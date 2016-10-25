/*
 *
 *  Copyright 2015 the original author or authors.
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

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class MinMaxTestModel {
  private int noAnnotation;
  @Min(10)
  private int onlyMin;
  @Max(20)
  private int onlyMax;
  @Min(10)
  @Max(20)
  private int both;

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
}
