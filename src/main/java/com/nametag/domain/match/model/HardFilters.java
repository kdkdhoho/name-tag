package com.nametag.domain.match.model;

import com.nametag.domain.animal.model.*;

public final class HardFilters {
  private HardFilters() {}

  public static boolean accepts(Profile p, Animal a) {
    if (a.dangerousBreed()) return false;
    if (p.sizeLimit() == Profile.Limit.UNDER_10 && a.kg() != null && a.kg() >= 10) return false;
    if (p.sizeLimit() == Profile.Limit.UNDER_25 && a.kg() != null && a.kg() >= 25) return false;
    return !(p.housemates().contains(Profile.Housemate.CHILD_UNDER_7)
        && a.keywords().contains(Keyword.CAUTION));
  }
}
