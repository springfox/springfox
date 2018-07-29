/*
 *
 *  Copyright 2015-2019 the original author or authors.
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
import com.fasterxml.classmate.members.ResolvedMember;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.*;

public class ModelAttributeField {
  private final ResolvedType fieldType;
  private final String name;
  private final List<ResolvedMember<?>> resolvedMembers;

  public ModelAttributeField(
      ResolvedType fieldType,
      String name,
      ResolvedMember<?> primary,
      ResolvedMember<?> secondary) {
    this.fieldType = fieldType;
    this.name = name;
    this.resolvedMembers = new ArrayList<>();
    resolvedMembers.add(primary);
    if (secondary != null) {
      resolvedMembers.add(secondary);
    }
  }

  public ResolvedType getFieldType() {
    return fieldType;
  }

  public List<ResolvedMember<?>> getResolvedMembers() {
    return resolvedMembers;
  }

  public List<AnnotatedElement> annotatedElements() {
    return resolvedMembers.stream()
        .map(input -> (AnnotatedElement) input.getRawMember())
        .collect(toList());
  }

  public String getName() {
    return name;
  }
}
