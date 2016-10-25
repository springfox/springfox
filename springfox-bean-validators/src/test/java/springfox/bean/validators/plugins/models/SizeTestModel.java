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

import javax.validation.constraints.Size;

public class SizeTestModel {

  private String noAnnotation;
  @Size
  private String defaultSize;
  @Size(min = -1, max = 10)
  private String belowZero;
  @Size(min = 10, max = Integer.MAX_VALUE + 1)
  private String aboveMax;
  @Size(min = Integer.MAX_VALUE, max = 0)
  private String inverted;
  @Size(min = -5, max = -10)
  private String bothNegative;
  @Size(min = 0, max = 0)
  private String bothZero;
  @Size(min = Integer.MAX_VALUE)
  private String bothMax;

  public String getNoAnnotation() {
    return noAnnotation;
  }

  public void setNoAnnotation(String noAnnotation) {
    this.noAnnotation = noAnnotation;
  }

  public String getDefaultSize() {
    return defaultSize;
  }

  public void setDefaultSize(String defaultSize) {
    this.defaultSize = defaultSize;
  }

  public String getBelowZero() {
    return belowZero;
  }

  public void setBelowZero(String belowZero) {
    this.belowZero = belowZero;
  }

  public String getAboveMax() {
    return aboveMax;
  }

  public void setAboveMax(String aboveMax) {
    this.aboveMax = aboveMax;
  }

  public String getInverted() {
    return inverted;
  }

  public void setInverted(String inverted) {
    this.inverted = inverted;
  }

  public String getBothNegative() {
    return bothNegative;
  }

  public void setBothNegative(String bothNegative) {
    this.bothNegative = bothNegative;
  }

  public String getBothZero() {
    return bothZero;
  }

  public void setBothZero(String bothZero) {
    this.bothZero = bothZero;
  }

  public String getBothMax() {
    return bothMax;
  }

  public void setBothMax(String bothMax) {
    this.bothMax = bothMax;
  }
}
