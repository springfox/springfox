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

package springfox.documentation.spring.web.readers.parameter;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.members.ResolvedField;

public class ModelAttributeField {
  private final ResolvedType fieldType;
  private final ResolvedField field;

  public ModelAttributeField(ResolvedType fieldType, ResolvedField field) {
    this.fieldType = fieldType;
    this.field = field;
  }

  public ResolvedType getFieldType() {
    return fieldType;
  }

  public ResolvedField getField() {
    return field;
  }
}
