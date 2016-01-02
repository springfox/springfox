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

package springfox.documentation.spi.service.contexts;


import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.spi.DocumentationType;

import java.lang.reflect.Field;

public class ParameterExpansionContext {

  private final String dataTypeName;
  private final String parentName;
  private final Field field;
  private final DocumentationType documentationType;
  private ParameterBuilder parameterBuilder;

  public ParameterExpansionContext(
      String dataTypeName,
      String parentName,
      Field field,
      DocumentationType documentationType,
      ParameterBuilder parameterBuilder) {

    this.dataTypeName = dataTypeName;
    this.parentName = parentName;
    this.field = field;
    this.documentationType = documentationType;
    this.parameterBuilder = parameterBuilder;
  }

  public String getDataTypeName() {
    return dataTypeName;
  }

  public String getParentName() {
    return parentName;
  }

  public Field getField() {
    return field;
  }

  public DocumentationType getDocumentationType() {
    return documentationType;
  }

  public ParameterBuilder getParameterBuilder() {
    return parameterBuilder;
  }


}
