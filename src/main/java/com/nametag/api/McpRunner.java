package com.nametag.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;

@Configuration
@Profile("mcp")
public class McpRunner {
  @Bean
  CommandLineRunner mcp(NametagService service) {
    return args -> {
      ObjectMapper m = new ObjectMapper();
      try (BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
          PrintWriter w = new PrintWriter(System.out, true)) {
        String line;
        while ((line = r.readLine()) != null) {
          Map<?, ?> q = m.readValue(line, Map.class);
          Object id = q.get("id");
          String method = (String) q.get("method");
          if ("tools/list".equals(method)) {
            List<Map<String, Object>> tools =
                List.of(
                        "search_animals",
                        "match",
                        "shelter_questions",
                        "settle_plan",
                        "evaluate_match")
                    .stream()
                    .map(
                        n ->
                            Map.<String, Object>of(
                                "name",
                                n,
                                "description",
                                "정착 " + n,
                                "inputSchema",
                                Map.of("type", "object")))
                    .toList();
            w.println(
                m.writeValueAsString(
                    Map.of("jsonrpc", "2.0", "id", id, "result", Map.of("tools", tools))));
          } else if ("tools/call".equals(method)) {
            Map<?, ?> params = (Map<?, ?>) q.get("params");
            Object arg = params.get("arguments");
            Map<?, ?> a = arg instanceof Map<?, ?> map ? map : Map.of();
            String name = String.valueOf(params.get("name"));
            Object data =
                switch (name) {
                  case "search_animals" -> service.animals();
                  case "evaluate_match" -> service.evaluate();
                  case "shelter_questions" ->
                      service.questions(String.valueOf(a.get("desertionNo")));
                  case "match" ->
                      service.match(
                          m.convertValue(
                              a.get("profile"), com.nametag.domain.match.model.Profile.class));
                  case "settle_plan" ->
                      service.settle(
                          m.convertValue(
                              a.get("profile"), com.nametag.domain.match.model.Profile.class));
                  default -> Map.of("error", "알 수 없는 도구");
                };
            String text = m.writeValueAsString(data);
            w.println(
                m.writeValueAsString(
                    Map.of(
                        "jsonrpc",
                        "2.0",
                        "id",
                        id,
                        "result",
                        Map.of("content", List.of(Map.of("type", "text", "text", text))))));
          } else if ("initialize".equals(method))
            w.println(
                m.writeValueAsString(
                    Map.of(
                        "jsonrpc",
                        "2.0",
                        "id",
                        id,
                        "result",
                        Map.of(
                            "protocolVersion",
                            "2024-11-05",
                            "serverInfo",
                            Map.of("name", "jeongchak", "version", "0.1.0"),
                            "capabilities",
                            Map.of("tools", Map.of())))));
        }
      }
    };
  }
}
