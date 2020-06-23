/*
 *
 *  Copyright 2020 the original author or authors.
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
package springfox.test.contract.swagger.models;

import java.util.List;

public class City {

  private List<District> districts;

  private Region region;

  private Country country;

  public List<District> getDistricts() {
    return districts;
  }

  public void setDistricts(List<District> districts) {
    this.districts = districts;
  }

  public Region getRegion() {
    return region;
  }

  public void setRegion(Region region) {
    this.region = region;
  }

  public Country getCountry() {
    return country;
  }

  public void setCountry(Country country) {
    this.country = country;
  }

}
