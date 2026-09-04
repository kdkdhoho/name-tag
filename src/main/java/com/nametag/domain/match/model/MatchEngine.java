package com.nametag.domain.match.model;

import com.nametag.domain.animal.model.*;
import java.util.*;

public final class MatchEngine {
  private MatchEngine() {}

  public static MatchOutput match(Profile p, List<Animal> animals) {
    if (p.tenure() == Profile.Tenure.RENT_DENIED) return MatchOutput.landlordDeniedResult();
    List<MatchCard> c = new ArrayList<>();
    for (Animal a : animals)
      if (HardFilters.accepts(p, a)) {
        ScoreResult s = Scorer.score(p, a);
        List<String> f = CheckFlags.of(p, a);
        c.add(
            new MatchCard(
                a,
                Grader.grade(a, s, f),
                s.score(),
                f,
                ReasonTemplates.forRules(s),
                ShelterQuestions.forAnimal(a)));
      }
    c.sort(
        Comparator.comparing((MatchCard x) -> x.grade().ordinal())
            .thenComparing(MatchCard::score)
            .reversed());
    return new MatchOutput(false, c);
  }
}
