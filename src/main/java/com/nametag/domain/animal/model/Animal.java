package com.nametag.domain.animal.model;

import java.util.Set;

public record Animal(
    RawAnimal raw,
    Double kg,
    Integer birthYear,
    boolean neonate,
    Integer ageApprox,
    boolean adultSizeUnknown,
    NeedLevel needLevel,
    Set<Keyword> keywords,
    boolean dangerousBreed,
    InfoLevel infoLevel,
    String photoUrl) {}
