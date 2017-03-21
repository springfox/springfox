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
package springfox.documentation.schema.property.bean;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedParameterizedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import springfox.documentation.schema.property.BaseModelProperty;
import springfox.documentation.spi.schema.AlternateTypeProvider;

public class ParameterModelProperty extends BaseModelProperty {

  private final AnnotatedParameter parameter;
  private final ResolvedParameterizedMember constructor;

  public ParameterModelProperty(
      String name,
      AnnotatedParameter parameter,
      ResolvedParameterizedMember constructor,
      TypeResolver resolver,
      AlternateTypeProvider alternateTypeProvider,
      BeanPropertyDefinition jacksonProperty) {
    super(name, resolver, alternateTypeProvider, jacksonProperty);
    this.parameter = parameter;
    this.constructor = constructor;
  }

  @Override
  public String qualifiedTypeName() {
      return getType().toString();
  }

  @Override
  public int position() {
    return parameter.getIndex();
  }

  @Override
  protected ResolvedType realType() {
    return constructor.getArgumentType(parameter.getIndex());
  }
}
