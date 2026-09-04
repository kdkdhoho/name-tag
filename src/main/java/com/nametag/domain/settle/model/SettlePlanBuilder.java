package com.nametag.domain.settle.model;

import com.nametag.domain.match.model.Profile;
import java.util.*;

public final class SettlePlanBuilder {
  private SettlePlanBuilder() {}

  public static SettlePlan build(Profile p) {
    String first =
        p.expectation() == Profile.Expectation.DAYS
            ? "첫 며칠의 배변 실수와 짖음은 적응 과정일 수 있어요."
            : "처음 몇 주는 새 환경에 적응할 시간을 주세요.";
    List<String> d =
        List.of(
            "1일차: " + first,
            "2일차: 공동주택 이웃에게 인사하고 관리규약을 확인하세요.",
            "3일차: 임대 동의는 문자로 남기세요.",
            "7일차: 짧고 예측 가능한 산책 루틴을 만드세요.",
            "14일차: 보호소와 적응 상태를 점검하세요.");
    List<String> w = new ArrayList<>();
    if (p.absence() == Profile.Absence.OVER_10) w.add("10시간 초과 부재일에는 펫시터 또는 산책 도우미를 마련하세요.");
    w.add("식사 거부, 반복 공격성, 지속 짖음은 보호소·수의사에게 상담하세요.");
    return new SettlePlan(
        d, w, List.of("배변과 식사는 안정됐나요?", "혼자 있을 때 어떤 반응인가요?", "사람·동물과 만남은 안전했나요?"));
  }
}
