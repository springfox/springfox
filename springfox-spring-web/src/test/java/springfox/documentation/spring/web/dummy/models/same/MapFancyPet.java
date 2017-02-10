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

package springfox.documentation.spring.web.dummy.models.same;

import java.util.Map;

public class MapFancyPet {
  private Map<String, FancyPet> fancyPets;
  
  private FancyPet fancyPet;

  public Map<String, FancyPet> getFancyPets() {
    return fancyPets;
  }

  public void setFancyPets(Map<String, FancyPet> fancyPets) {
    this.fancyPets = fancyPets;
  }

  public FancyPet getFancyPet() {
    return fancyPet;
  }

  public void setFancyPet(FancyPet fancyPet) {
    this.fancyPet = fancyPet;
  }
}
