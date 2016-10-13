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

package springfox.documentation.service;

public class AllowableRangeValues implements AllowableValues {
  private final String min;
  private final String max;
  private final Boolean exclusiveMin;
  private final Boolean exclusiveMax;

  public AllowableRangeValues(String min, String max) {
    this(min, null, max, null);
  }

  public AllowableRangeValues(String min, Boolean exclusiveMin, String max, Boolean exclusiveMax) {
    this.min = min;
    this.max = max;
    this.exclusiveMin = exclusiveMin;
    this.exclusiveMax = exclusiveMax;
  }

  public String getMin() {
    return min;
  }

  public String getMax() {
    return max;
  }

  public Boolean getExclusiveMin() {
    return exclusiveMin;
  }

  public Boolean getExclusiveMax() {
    return exclusiveMax;
  }
}
