package com.nametag.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nametag.domain.animal.model.RawAnimal;
import com.nametag.domain.match.model.Profile;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AnimalProtectionClient {
  private static final String DOG_KIND_CODE = "417000";

  private final ObjectMapper mapper;
  private final HttpClient httpClient;
  private final String baseUrl;
  private final String serviceKey;

  public AnimalProtectionClient(
      ObjectMapper mapper,
      @Value("${nametag.public-api.base-url}") String baseUrl,
      @Value("${nametag.public-api.service-key:}") String serviceKey) {
    this.mapper = mapper;
    this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    this.baseUrl = baseUrl;
    this.serviceKey = serviceKey;
  }

  public List<Region> sido() {
    return items("sido_v2").stream()
        .map(item -> new Region(text(item, "orgCd"), text(item, "orgCd"), text(item, "orgdownNm")))
        .toList();
  }

  public List<Region> sigungu(String uprCd) {
    return items("sigungu_v2", "upr_cd", uprCd).stream()
        .map(item -> new Region(text(item, "uprCd"), text(item, "orgCd"), text(item, "orgdownNm")))
        .toList();
  }

  public List<RawAnimal> protectedDogs(Profile profile) {
    Region sido =
        sido().stream()
            .filter(region -> profile.sido().equals(region.orgdownNm()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("선택한 시·도를 찾지 못했습니다."));
    Region sigungu =
        sigungu(sido.uprCd()).stream()
            .filter(region -> profile.sigungu().equals(region.orgdownNm()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("선택한 시·군·구를 찾지 못했습니다."));

    return items(
            "abandonmentPublic_v2",
            "upkind",
            DOG_KIND_CODE,
            "upr_cd",
            sido.uprCd(),
            "org_cd",
            sigungu.orgCd(),
            "state",
            "protect",
            "numOfRows",
            "1000")
        .stream()
        .map(this::toRawAnimal)
        .toList();
  }

  private List<JsonNode> items(String path, String... parameters) {
    StringBuilder query = new StringBuilder("serviceKey=").append(encodedServiceKey());
    query.append("&_type=json");
    for (int index = 0; index < parameters.length; index += 2) {
      query
          .append('&')
          .append(parameters[index])
          .append('=')
          .append(URLEncoder.encode(parameters[index + 1], StandardCharsets.UTF_8));
    }

    try {
      HttpRequest request =
          HttpRequest.newBuilder(URI.create(baseUrl + "/" + path + "?" + query))
              .timeout(Duration.ofSeconds(20))
              .GET()
              .build();
      HttpResponse<String> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() < 200 || response.statusCode() >= 300) {
        throw new IllegalStateException("국가동물보호정보시스템 요청이 실패했습니다.");
      }

      JsonNode root = mapper.readTree(response.body()).path("response");
      JsonNode header = root.path("header");
      if (!"00".equals(text(header, "resultCode"))) {
        throw new IllegalStateException("국가동물보호정보시스템: " + text(header, "resultMsg"));
      }

      JsonNode item = root.path("body").path("items").path("item");
      List<JsonNode> results = new ArrayList<>();
      if (item.isArray()) item.forEach(results::add);
      else if (!item.isMissingNode() && !item.isNull()) results.add(item);
      return results;
    } catch (IOException exception) {
      throw new IllegalStateException("국가동물보호정보시스템 응답을 읽지 못했습니다.", exception);
    } catch (InterruptedException exception) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("국가동물보호정보시스템 요청이 중단되었습니다.", exception);
    }
  }

  private RawAnimal toRawAnimal(JsonNode item) {
    return new RawAnimal(
        text(item, "desertionNo"),
        text(item, "kindNm"),
        text(item, "age"),
        text(item, "weight"),
        text(item, "specialMark"),
        text(item, "processState"),
        text(item, "noticeEdt"),
        text(item, "careNm"),
        text(item, "careTel"),
        text(item, "careAddr"),
        text(item, "popfile1"),
        text(item, "neuterYn"),
        text(item, "sexCd"),
        text(item, "orgNm"));
  }

  private String encodedServiceKey() {
    if (serviceKey.isBlank()) {
      throw new IllegalStateException("DATA_GO_KR_SERVICE_KEY를 설정해야 운영 API를 사용할 수 있습니다.");
    }
    return serviceKey.contains("%")
        ? serviceKey
        : URLEncoder.encode(serviceKey, StandardCharsets.UTF_8);
  }

  private static String text(JsonNode node, String name) {
    return node.path(name).isNull() ? null : node.path(name).asText(null);
  }

  public record Region(String uprCd, String orgCd, String orgdownNm) {}
}
