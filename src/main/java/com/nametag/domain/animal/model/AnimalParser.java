package com.nametag.domain.animal.model;
import java.util.regex.*;
public final class AnimalParser { private AnimalParser(){} public record Birth(Integer year, boolean neonate){}
 public static Double parseKg(String value) { if(value==null) return null; Matcher m=Pattern.compile("^([\\d.,]+)\\(?kg\\)?",Pattern.CASE_INSENSITIVE).matcher(value.replaceAll("\\s", "")); if(!m.find()) return null; try { double n=Double.parseDouble(m.group(1).replace(',','.')); return n>0?n:null; } catch(NumberFormatException e){return null;} }
 public static Birth parseBirth(String value) { if(value==null) return new Birth(null,false); Matcher m=Pattern.compile("^(\\d{4})").matcher(value); return m.find()?new Birth(Integer.valueOf(m.group(1)),value.contains("60일미만")):new Birth(null,false); }
}
