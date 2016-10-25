/*
 *
 *  Copyright 2016 the original author or authors.
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
package springfox.bean.validators.plugins.models;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;

public class DecimalMinMaxTestModel {
  private int noAnnotation;
  @DecimalMin("10.5")
  private int onlyMin;
  @DecimalMax("20.5")
  private int onlyMax;
  @DecimalMin("10.5")
  @DecimalMax("20.5")
  private int both;
  @DecimalMin(value = "10.5", inclusive = false)
  private int minExclusive;
  @DecimalMax(value = "20.5", inclusive = false)
  private int maxExclusive;
  @DecimalMin(value = "10.5", inclusive = false)
  @DecimalMax(value = "20.5", inclusive = false)
  private int bothExclusive;

  public int getNoAnnotation() {
    return noAnnotation;
  }

  public void setNoAnnotation(int noAnnotation) {
    this.noAnnotation = noAnnotation;
  }

  public int getOnlyMin() {
    return onlyMin;
  }

  public void setOnlyMin(int onlyMin) {
    this.onlyMin = onlyMin;
  }

  public int getOnlyMax() {
    return onlyMax;
  }

  public void setOnlyMax(int onlyMax) {
    this.onlyMax = onlyMax;
  }

  public int getBoth() {
    return both;
  }

  public void setBoth(int both) {
    this.both = both;
  }

  public int getMinExclusive() {
    return minExclusive;
  }

  public void setMinExclusive(int minExclusive) {
    this.minExclusive = minExclusive;
  }

  public int getMaxExclusive() {
    return maxExclusive;
  }

  public void setMaxExclusive(int maxExclusive) {
    this.maxExclusive = maxExclusive;
  }

  public int getBothExclusive() {
    return bothExclusive;
  }

  public void setBothExclusive(int bothExclusive) {
    this.bothExclusive = bothExclusive;
  }
}
