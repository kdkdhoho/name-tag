package com.nametag.domain.evaluate.model;

import java.util.List;
import java.util.Map;

public record Evaluation(
    int personas,
    int unsupportedReasons,
    int endedAnimals,
    int hardFilterViolations,
    int goodFlagViolations,
    double featureCoverage,
    Map<String, Integer> grades,
    List<String> violations) {}
