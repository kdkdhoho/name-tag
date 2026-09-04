package com.nametag.domain.match.model;

import com.nametag.domain.animal.model.*;
import java.util.*;

public final class Rules {
  private Rules() {}

  static int activity(Profile p) {
    return p.activeDays() <= 1 ? 0 : p.activeDays() <= 3 ? 1 : 2;
  }

  static boolean novice(Profile p) {
    return p.experience() == Profile.Experience.NONE || p.experience() == Profile.Experience.FAMILY;
  }

  public static final List<Rule> ALL =
      List.of(
          new Rule(
              "activity-fit",
              (p, a) -> activity(p) >= a.needLevel().ordinal(),
              2,
              "필요 활동량을 채울 수 있습니다"),
          new Rule(
              "activity-gap",
              (p, a) -> activity(p) + 2 <= a.needLevel().ordinal(),
              -2,
              "활동량 차이가 커 산책 계획이 필요합니다"),
          new Rule(
              "absence-young",
              (p, a) ->
                  p.absence().ordinal() >= 3
                      && (a.neonate() || (a.ageApprox() != null && a.ageApprox() <= 1)),
              -2,
              "어린 개는 사람 시간이 필요합니다"),
          new Rule(
              "absence-senior",
              (p, a) -> p.absence().ordinal() >= 3 && a.ageApprox() != null && a.ageApprox() >= 8,
              1,
              "성견 생활 리듬을 보호소에 확인하세요"),
          new Rule(
              "absence-behavior",
              (p, a) ->
                  p.absence().ordinal() >= 3
                      && (a.keywords().contains(Keyword.BARKING)
                          || a.keywords().contains(Keyword.SEPARATION_ANXIETY)),
              -1,
              "혼자 있을 때 행동을 확인하세요"),
          new Rule(
              "pet-social",
              (p, a) ->
                  p.housemates().contains(Profile.Housemate.PET)
                      && a.keywords().contains(Keyword.SOCIABLE),
              1,
              "다른 개와의 사회성 기록이 있습니다"),
          new Rule(
              "novice-young",
              (p, a) -> novice(p) && a.ageApprox() != null && a.ageApprox() <= 1,
              -1,
              "첫 입양에는 성견의 정보가 더 예측 가능합니다"),
          new Rule(
              "novice-large",
              (p, a) -> novice(p) && a.kg() != null && a.kg() >= 25,
              -1,
              "큰 체구는 돌봄 부담을 확인해야 합니다"),
          new Rule(
              "novice-high",
              (p, a) -> novice(p) && a.needLevel() == NeedLevel.HIGH,
              -1,
              "높은 활동량을 감당할 계획이 필요합니다"),
          new Rule(
              "novice-adult",
              (p, a) ->
                  novice(p) && a.ageApprox() != null && a.ageApprox() >= 2 && a.ageApprox() <= 7,
              1,
              "성견은 생활 리듬을 확인하기 좋습니다"),
          new Rule(
              "novice-positive",
              (p, a) -> novice(p) && a.keywords().contains(Keyword.POSITIVE),
              1,
              "보호소 기록에 사람 친화적 특징이 있습니다"),
          new Rule(
              "studio-large",
              (p, a) -> p.home() == Profile.Home.STUDIO && a.kg() != null && a.kg() >= 25,
              -2,
              "공용공간 이동 규약을 확인하세요"),
          new Rule(
              "apartment-large",
              (p, a) ->
                  (p.home() == Profile.Home.APARTMENT || p.home() == Profile.Home.VILLA)
                      && a.kg() != null
                      && a.kg() >= 25,
              -1,
              "공동주택 규약을 확인하세요"),
          new Rule(
              "shared-barking",
              (p, a) -> p.home() != Profile.Home.HOUSE && a.keywords().contains(Keyword.BARKING),
              -1,
              "짖음 기록과 이웃 환경을 확인하세요"));
}
