package com.nametag;

import static org.assertj.core.api.Assertions.*;

import com.nametag.domain.animal.model.*;
import com.nametag.domain.match.model.*;
import java.util.*;
import org.junit.jupiter.api.*;

@DisplayName("이름표 규칙 엔진")
class RulesTest {
  @Test
  void 체중_파싱_success() {
    assertThat(AnimalParser.parseKg("0,25(Kg)")).isEqualTo(.25);
    assertThat(AnimalParser.parseKg("0(Kg)")).isNull();
  }

  @Test
  void 출생연도_파싱_success() {
    assertThat(AnimalParser.parseBirth("2025(60일미만)(년생)").neonate()).isTrue();
  }

  @Test
  void 긍정_경계는_주의가_아님_success() {
    Animal a = AnimalDeriver.derive(raw("사람경계 없음"), 2026);
    assertThat(a.keywords()).doesNotContain(Keyword.CAUTION);
  }

  @Test
  void 맹견과_아이주의기록은_제외_success() {
    Profile p = profile(Set.of(Profile.Housemate.CHILD_UNDER_7));
    assertThat(
            HardFilters.accepts(
                p,
                AnimalDeriver.derive(
                    new RawAnimal(
                        "x",
                        "핏불테리어",
                        "2022(년생)",
                        "8(Kg)",
                        "순함",
                        "보호중",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        ""),
                    2026)))
        .isFalse();
    assertThat(HardFilters.accepts(p, AnimalDeriver.derive(raw("입질"), 2026))).isFalse();
  }

  @Test
  void 플래그는_조건부_success() {
    Profile p = profile(Set.of(Profile.Housemate.PET));
    Animal a = AnimalDeriver.derive(raw("다른 개 싫어함, 활발"), 2026);
    assertThat(MatchEngine.match(p, List.of(a)).cards().get(0).grade())
        .isEqualTo(Grade.CONDITIONAL);
  }

  @Test
  void 규칙밖_이유는_제거_success() {
    assertThat(ReasonValidator.validate(List.of("없는 문장"), List.of("activity-fit"))).isEmpty();
  }

  static RawAnimal raw(String mark) {
    return new RawAnimal(
        "x",
        "믹스견",
        "2022(년생)",
        "8(Kg)",
        mark,
        "보호중",
        "20260920",
        "s",
        "t",
        "a",
        "http://x",
        "U",
        "M",
        "o");
  }

  static Profile profile(Set<Profile.Housemate> h) {
    return new Profile(
        Profile.Home.HOUSE,
        Profile.Limit.NONE,
        Profile.Tenure.OWN,
        Profile.Absence.UNDER_2,
        h,
        5,
        Profile.Experience.NONE,
        Profile.Expectation.WEEKS,
        "서울",
        "");
  }
}
