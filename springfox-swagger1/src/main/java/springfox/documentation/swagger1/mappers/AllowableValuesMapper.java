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

package springfox.documentation.swagger1.mappers;


import org.mapstruct.Mapper;
import springfox.documentation.swagger1.dto.AllowableListValues;
import springfox.documentation.swagger1.dto.AllowableRangeValues;

@Mapper
public abstract class AllowableValuesMapper {

  //Allowable values related
  public abstract AllowableListValues toSwaggerAllowableListValues(
          springfox.documentation.service.AllowableListValues from);

  public abstract AllowableRangeValues toSwaggerAllowableRangeValues(
          springfox.documentation.service.AllowableRangeValues from);

  public springfox.documentation.swagger1.dto.AllowableValues toSwaggerAllowableValues(
          springfox.documentation.service.AllowableValues original) {
    if (original == null) {
      return null;
    }

    if (original instanceof springfox.documentation.service.AllowableListValues) {
      return toSwaggerAllowableListValues((springfox.documentation.service.AllowableListValues) original);
    } else if (original instanceof springfox.documentation.service.AllowableRangeValues) {
      return toSwaggerAllowableRangeValues((springfox.documentation.service.AllowableRangeValues)
              original);
    }
    throw new UnsupportedOperationException();
  }
}