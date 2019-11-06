/*
 *
 *  Copyright 2015-2016 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.schema;

import java.util.Currency;
import java.util.Date;
import java.util.UUID;

public class SimpleType {
  private byte aByte;
  private boolean aBoolean;
  private short aShort;
  private int anInt;
  private long aLong;
  private float aFloat;
  private double aDouble;
  private String aString;
  private Date date;
  private Object anObject;
  private Byte anObjectByte;
  private Boolean anObjectBoolean;
  private Short anObjectShort;
  private Integer anObjectInt;
  private Long anObjectLong;
  private Float anObjectFloat;
  private Double anObjectDouble;
  private Currency currency;
  private UUID uuid;
  private Date aDate;
  private java.sql.Date aSqlDate;

  public Date getaDate() {
    return aDate;
  }

  public void setaDate(Date aDate) {
    this.aDate = aDate;
  }

  public java.sql.Date getaSqlDate() {
    return aSqlDate;
  }

  public void setaSqlDate(java.sql.Date aSqlDate) {
    this.aSqlDate = aSqlDate;
  }

  public Byte getAnObjectByte() {
    return anObjectByte;
  }

  public void setAnObjectByte(Byte anObjectByte) {
    this.anObjectByte = anObjectByte;
  }

  public Boolean getAnObjectBoolean() {
    return anObjectBoolean;
  }

  public void setAnObjectBoolean(Boolean anObjectBoolean) {
    this.anObjectBoolean = anObjectBoolean;
  }

  public Short getAnObjectShort() {
    return anObjectShort;
  }

  public void setAnObjectShort(Short anObjectShort) {
    this.anObjectShort = anObjectShort;
  }

  public Integer getAnObjectInt() {
    return anObjectInt;
  }

  public void setAnObjectInt(Integer anObjectInt) {
    this.anObjectInt = anObjectInt;
  }

  public Long getAnObjectLong() {
    return anObjectLong;
  }

  public void setAnObjectLong(Long anObjectLong) {
    this.anObjectLong = anObjectLong;
  }

  public Float getAnObjectFloat() {
    return anObjectFloat;
  }

  public void setAnObjectFloat(Float anObjectFloat) {
    this.anObjectFloat = anObjectFloat;
  }

  public Double getAnObjectDouble() {
    return anObjectDouble;
  }

  public void setAnObjectDouble(Double anObjectDouble) {
    this.anObjectDouble = anObjectDouble;
  }

  public short getaShort() {
    return aShort;
  }

  public void setaShort(short aShort) {
    this.aShort = aShort;
  }

  public byte getaByte() {
    return aByte;
  }

  public void setaByte(byte aByte) {
    this.aByte = aByte;
  }

  public boolean isaBoolean() {
    return aBoolean;
  }

  public void setaBoolean(boolean aBoolean) {
    this.aBoolean = aBoolean;
  }

  public int getAnInt() {
    return anInt;
  }

  public void setAnInt(int anInt) {
    this.anInt = anInt;
  }

  public long getaLong() {
    return aLong;
  }

  public void setaLong(long aLong) {
    this.aLong = aLong;
  }

  public float getaFloat() {
    return aFloat;
  }

  public void setaFloat(float aFloat) {
    this.aFloat = aFloat;
  }

  public double getaDouble() {
    return aDouble;
  }

  public void setaDouble(double aDouble) {
    this.aDouble = aDouble;
  }

  public String getaString() {
    return aString;
  }

  public void setaString(String aString) {
    this.aString = aString;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public Object getAnObject() {
    return anObject;
  }

  public void setAnObject(Object anObject) {
    this.anObject = anObject;
  }

  public Currency getCurrency() {
    return currency;
  }

  public void setCurrency(Currency currency) {
    this.currency = currency;
  }

  public UUID getUuid() {
    return uuid;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }
}
