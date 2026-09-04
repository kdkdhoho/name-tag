package com.nametag.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nametag.domain.animal.model.*;
import com.nametag.domain.evaluate.model.*;
import com.nametag.domain.match.model.*;
import com.nametag.domain.settle.model.*;
import java.nio.file.*;
import java.time.Year;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NametagService {
  private final ObjectMapper mapper;
  private final AnimalProtectionClient animalProtectionClient;
  private final boolean fixture;
  private final List<RawAnimal> animals;
  private final List<Profile> personas;

  public NametagService(
      ObjectMapper mapper,
      AnimalProtectionClient animalProtectionClient,
      @Value("${nametag.fixture:true}") boolean fixture,
      @Value("${nametag.fixture-dir:./fixtures}") String dir)
      throws Exception {
    this.mapper = mapper;
    this.animalProtectionClient = animalProtectionClient;
    this.fixture = fixture;
    this.animals = fixture ? readAnimals(dir) : List.of();
    this.personas = fixture ? readPersonas(dir) : List.of();
  }

  public MatchOutput match(Profile p) {
    if (p.tenure() == Profile.Tenure.RENT_DENIED) return MatchOutput.landlordDeniedResult();
    return MatchEngine.match(
        p,
        matchAnimals(p).stream()
            .map(animal -> AnimalDeriver.derive(animal, Year.now().getValue()))
            .toList());
  }

  public SettlePlan settle(Profile p) {
    return SettlePlanBuilder.build(p);
  }

  public Evaluation evaluate() {
    return Evaluator.evaluate(personas, animals);
  }

  public List<RawAnimal> animals() {
    return animals;
  }

  public boolean isFixture() {
    return fixture;
  }

  public List<AnimalProtectionClient.Region> sido() {
    if (!fixture) return animalProtectionClient.sido();
    return List.of(
        new AnimalProtectionClient.Region("6110000", "6110000", "서울"),
        new AnimalProtectionClient.Region("6410000", "6410000", "경기"));
  }

  public List<AnimalProtectionClient.Region> sigungu(String uprCd) {
    if (!fixture) return animalProtectionClient.sigungu(uprCd);
    return List.of(new AnimalProtectionClient.Region(uprCd, uprCd + "001", "강남구"));
  }

  public List<String> questions(String id) {
    return animals.stream()
        .filter(a -> a.desertionNo().equals(id))
        .findFirst()
        .map(a -> ShelterQuestions.forAnimal(AnimalDeriver.derive(a, 2026)))
        .orElse(List.of());
  }

  private List<RawAnimal> matchAnimals(Profile profile) {
    return fixture ? animals : animalProtectionClient.protectedDogs(profile);
  }

  private List<RawAnimal> readAnimals(String dir) throws Exception {
    return mapper.readValue(
        Files.readString(Path.of(dir, "animals.json")), new TypeReference<>() {});
  }

  private List<Profile> readPersonas(String dir) throws Exception {
    return mapper.readValue(
        Files.readString(Path.of(dir, "personas.json")), new TypeReference<>() {});
  }
}
