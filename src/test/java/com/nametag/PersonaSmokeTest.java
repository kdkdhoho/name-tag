package com.nametag;

import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nametag.domain.animal.model.RawAnimal;
import com.nametag.domain.evaluate.model.*;
import com.nametag.domain.match.model.Profile;
import java.nio.file.*;
import java.util.*;
import org.junit.jupiter.api.*;

@Tag("smoke")
class PersonaSmokeTest {
  @Test
  void 페르소나_20명_불변식_success() throws Exception {
    ObjectMapper m = new ObjectMapper();
    List<RawAnimal> a =
        m.readValue(Files.readString(Path.of("fixtures/animals.json")), new TypeReference<>() {});
    List<Profile> p =
        m.readValue(Files.readString(Path.of("fixtures/personas.json")), new TypeReference<>() {});
    Evaluation e = Evaluator.evaluate(p, a);
    Files.createDirectories(Path.of("build"));
    m.writeValue(Path.of("build/evaluation.json").toFile(), e);
    assertThat(p).hasSize(20);
    assertThat(e.hardFilterViolations()).isZero();
    assertThat(e.goodFlagViolations()).isZero();
    assertThat(e.endedAnimals()).isZero();
    assertThat(e.unsupportedReasons()).isZero();
  }
}
