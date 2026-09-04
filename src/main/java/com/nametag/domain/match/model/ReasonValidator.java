package com.nametag.domain.match.model;

import java.util.*;

public final class ReasonValidator {
  private ReasonValidator() {}

  public static List<String> validate(List<String> sentences, List<String> ids) {
    Set<String> allowed =
        Rules.ALL.stream()
            .filter(r -> ids.contains(r.id()))
            .map(Rule::reason)
            .collect(java.util.stream.Collectors.toSet());
    return sentences.stream().filter(allowed::contains).toList();
  }
}
