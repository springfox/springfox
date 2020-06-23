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

package springfox.documentation.schema.mixins
import com.fasterxml.classmate.MemberResolver
import com.fasterxml.classmate.ResolvedTypeWithMembers
import com.fasterxml.classmate.TypeResolver
import com.fasterxml.classmate.members.ResolvedField
import com.fasterxml.classmate.members.ResolvedMethod
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition
import com.fasterxml.jackson.databind.type.TypeFactory
import spock.lang.Shared

import static springfox.documentation.schema.property.bean.Accessors.*

trait ModelPropertyLookupSupport {
  @Shared ObjectMapper mapper = new ObjectMapper();

  ResolvedMethod accessorMethod(def typeToTest, String methodName) {
    TypeResolver resolver = new TypeResolver()
    MemberResolver memberResolver = new MemberResolver(resolver);
    memberResolver.setIncludeLangObject(false);

    ResolvedTypeWithMembers typeWithMembers = memberResolver.resolve(resolver.resolve(typeToTest), null, null);
    typeWithMembers.memberMethods.find { it.name == methodName}
  }

  ResolvedField field(def typeToTest, String fieldName) {
    TypeResolver resolver = new TypeResolver()
    MemberResolver memberResolver = new MemberResolver(resolver);
    memberResolver.setIncludeLangObject(false);

    ResolvedTypeWithMembers typeWithMembers = memberResolver.resolve(resolver.resolve(typeToTest), null, null);
    typeWithMembers.memberFields.find { it.name == fieldName}
  }

  BeanPropertyDefinition beanPropertyDefinition(Class typeToTest, def methodName) {
    JavaType type = TypeFactory.defaultInstance().constructType(typeToTest)
    BeanDescription beanDescription = mapper.getDeserializationConfig().introspectForBuilder(type)
    Map<String, BeanPropertyDefinition> propertyDefinitionsByInternalName =
            beanDescription.findProperties()
                    .collectEntries {[ it.getInternalName(), it ]}
    return propertyDefinitionsByInternalName[propertyName(typeToTest.methods.find { it.name == methodName })]
  }

  BeanPropertyDefinition beanPropertyDefinitionByField(def typeToTest, def fieldName) {
    JavaType type = TypeFactory.defaultInstance().constructType(typeToTest)
    BeanDescription beanDescription = mapper.getDeserializationConfig().introspect(type)
    Map<String, BeanPropertyDefinition> propertyDefinitionsByInternalName =
            beanDescription.findProperties()
                    .collectEntries {[ it.getInternalName(), it ]}
    return propertyDefinitionsByInternalName[fieldName]
  }
}
