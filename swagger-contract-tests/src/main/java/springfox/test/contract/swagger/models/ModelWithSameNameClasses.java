/*
 *
 *  Copyright 2017 the original author or authors.
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
import java.util.Map;

public class ModelWithSameNameClasses {

  private FancyPet fancyPet;

  private SameFancyPet moreFancyPet;

  private List<FancyPet> fancyPets;

  private Map<String, SameFancyPet> fancyPetsMap;

  private List<List<List<FancyPet[]>>> crazyFancyPets;

  private List<List<List<SameFancyPet[]>>> weirdFancyPets;

  private Map<String, List<List<SameFancyPet>>> madFancyPets;

  public FancyPet getFancyPet() {
    return fancyPet;
  }

  public void setFancyPet(FancyPet fancyPet) {
    this.fancyPet = fancyPet;
  }

  public SameFancyPet getMoreFancyPet() {
    return moreFancyPet;
  }

  public void setMoreFancyPet(SameFancyPet moreFancyPet) {
    this.moreFancyPet = moreFancyPet;
  }

  public List<FancyPet> getFancyPets() {
    return fancyPets;
  }

  public void setFancyPets(List<FancyPet> fancyPets) {
    this.fancyPets = fancyPets;
  }

  public Map<String, SameFancyPet> getFancyPetsMap() {
    return fancyPetsMap;
  }

  public void setFancyPetsMap(Map<String, SameFancyPet> fancyPetsMap) {
    this.fancyPetsMap = fancyPetsMap;
  }

  public List<List<List<FancyPet[]>>> getCrazyFancyPets() {
    return crazyFancyPets;
  }

  public void setCrazyFancyPets(List<List<List<FancyPet[]>>> crazyFancyPets) {
    this.crazyFancyPets = crazyFancyPets;
  }

  public List<List<List<SameFancyPet[]>>> getWeirdFancyPets() {
    return weirdFancyPets;
  }

  public void setWeirdFancyPets(
      List<List<List<SameFancyPet[]>>> weirdFancyPets) {
    this.weirdFancyPets = weirdFancyPets;
  }

  public Map<String, List<List<SameFancyPet>>> getMadFancyPets() {
    return madFancyPets;
  }

  public void setMadFancyPets(
      Map<String, List<List<SameFancyPet>>> madFancyPets) {
    this.madFancyPets = madFancyPets;
  }
}
