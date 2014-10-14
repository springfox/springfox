package com.mangofactory.swagger.models;

import java.util.Date;

public class ArraysContainer {
  private ComplexType[] complexTypes;
  private ExampleEnum[] enums;
  private int[] integers;
  private Integer[] integerObjs;
  private String[] strings;
  private float[] floats;
  private Float[] floatObjs;
  private double[] doubles;
  private Double[] doubleObjs;
  private long[] longs;
  private Long[] longObjs;
  private short[] shorts;
  private Short[] shortObjs;
  private Date[] dates;
  private boolean[] booleans;
  private Boolean[] booleanObjs;
  private int[][] intArrayOfArrays;

  private Object[] objects;
  private int nonArrayInt;
  ArraysContainer(ComplexType[] complexTypes) {
    this.complexTypes = complexTypes;
  }

  public ComplexType[] getComplexTypes() {
    return complexTypes;
  }

  public void setComplexTypes(ComplexType[] complexTypes) {
    this.complexTypes = complexTypes;
  }

  public ExampleEnum[] getEnums() {
    return enums;
  }

  public void setEnums(ExampleEnum[] enums) {
    this.enums = enums;
  }

  public int[] getAliasOfIntegers() {
    return integers;
  }

  public void setAliasOfIntegers(int[] years) {
    this.integers = years;
  }

  public String[] getStrings() {
    return strings;
  }

  public void setStrings(String[] strings) {
    this.strings = strings;
  }

  public Object[] getObjects() {
    return objects;
  }

  public void setObjects(Object[] objects) {
    this.objects = objects;
  }

  public Integer[] getIntegerObjs() {
    return integerObjs;
  }

  public void setIntegerObjs(Integer[] integerObjs) {
    this.integerObjs = integerObjs;
  }

  public float[] getFloats() {
    return floats;
  }

  public void setFloats(float[] floats) {
    this.floats = floats;
  }

  public Float[] getFloatObjs() {
    return floatObjs;
  }

  public void setFloatObjs(Float[] floatObjs) {
    this.floatObjs = floatObjs;
  }

  public double[] getDoubles() {
    return doubles;
  }

  public void setDoubles(double[] doubles) {
    this.doubles = doubles;
  }

  public Double[] getDoubleObjs() {
    return doubleObjs;
  }

  public void setDoubleObjs(Double[] doubleObjs) {
    this.doubleObjs = doubleObjs;
  }

  public long[] getLongs() {
    return longs;
  }

  public void setLongs(long[] longs) {
    this.longs = longs;
  }

  public Long[] getLongObjs() {
    return longObjs;
  }

  public void setLongObjs(Long[] longObjs) {
    this.longObjs = longObjs;
  }

  public short[] getShorts() {
    return shorts;
  }

  public void setShorts(short[] shorts) {
    this.shorts = shorts;
  }

  public Short[] getShortObjs() {
    return shortObjs;
  }

  public void setShortObjs(Short[] shortObjs) {
    this.shortObjs = shortObjs;
  }

  public Date[] getDates() {
    return dates;
  }

  public void setDates(Date[] dates) {
    this.dates = dates;
  }

  public boolean[] getBooleans() {
    return booleans;
  }

  public void setBooleans(boolean[] booleans) {
    this.booleans = booleans;
  }

  public Boolean[] getBooleanObjs() {
    return booleanObjs;
  }

  public void setBooleanObjs(Boolean[] booleanObjs) {
    this.booleanObjs = booleanObjs;
  }

  public int[][] getIntArrayOfArrays() {
    return intArrayOfArrays;
  }

  public void setIntArrayOfArrays(int[][] intArrayOfArrays) {
    this.intArrayOfArrays = intArrayOfArrays;
  }
}

