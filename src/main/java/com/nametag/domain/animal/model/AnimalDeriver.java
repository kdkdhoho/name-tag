package com.nametag.domain.animal.model;

import java.util.*;

public final class AnimalDeriver {
  private AnimalDeriver() {}

  public static Animal derive(RawAnimal raw, int year) {
    String mark = raw.specialMark() == null ? "" : raw.specialMark();
    String kind = raw.kindNm() == null ? "" : raw.kindNm();
    Set<Keyword> k = EnumSet.noneOf(Keyword.class);
    if (mark.matches(".*(사람좋아|순함|경계 없음|경계없음|온순|애교).*")) k.add(Keyword.POSITIVE);
    if (mark.matches(".*(입질|사나움|공격|겁많|견제|경계).*")
        && !mark.contains("경계 없음")
        && !mark.contains("경계없음")) k.add(Keyword.CAUTION);
    if (mark.contains("짖")) k.add(Keyword.BARKING);
    if (mark.contains("분리불안")) k.add(Keyword.SEPARATION_ANXIETY);
    if (mark.matches(".*(다른 개 싫|개를 싫).*")) k.add(Keyword.DOG_UNFRIENDLY);
    if (mark.matches(".*(사교|다른 개 좋아).*")) k.add(Keyword.SOCIABLE);
    Double kg = AnimalParser.parseKg(raw.weight());
    AnimalParser.Birth b = AnimalParser.parseBirth(raw.age());
    Integer age = b.year() == null ? null : year - b.year();
    boolean unknown = kg == null || b.neonate() || (age != null && age <= 1 && kind.contains("믹스"));
    NeedLevel n =
        (age != null && age >= 8) || mark.matches(".*(얌전|조용|노령).*")
            ? NeedLevel.LOW
            : ((age != null && age >= 1 && age <= 3 && kg != null && kg >= 10)
                    || mark.matches(".*(활발|에너지).*")
                ? NeedLevel.HIGH
                : NeedLevel.MID);
    boolean dangerous = kind.matches(".*(도사|핏불|스태퍼드셔|로트와일러).*");
    boolean lacking = mark.isBlank() || mark.matches(".*(피부|골절|심장사상충|치료).*");
    String photo =
        raw.popfile1() == null ? null : raw.popfile1().replaceFirst("^http://", "https://");
    return new Animal(
        raw,
        kg,
        b.year(),
        b.neonate(),
        age,
        unknown,
        n,
        k,
        dangerous,
        lacking ? InfoLevel.LACKING : InfoLevel.ENOUGH,
        photo);
  }
}
