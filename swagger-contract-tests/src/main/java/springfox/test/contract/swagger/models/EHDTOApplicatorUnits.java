/*
 *
 *  Copyright 2017-2019 the original author or authors.
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "ApplicatorUnits")
@XmlAccessorType(XmlAccessType.FIELD)
public class EHDTOApplicatorUnits {
  @XmlElement(name = "UnitGroup")
  private List<EHDTOUnitGroup> ehdtoUnitGroups;

  public List<EHDTOUnitGroup> getEhdtoUnitGroups() {
    return ehdtoUnitGroups;
  }

  public void setEhdtoUnitGroups(List<EHDTOUnitGroup> ehdtoUnitGroups) {
    this.ehdtoUnitGroups = ehdtoUnitGroups;
  }
}