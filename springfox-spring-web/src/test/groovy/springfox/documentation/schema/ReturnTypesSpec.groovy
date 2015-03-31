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

package springfox.documentation.schema

import com.fasterxml.classmate.GenericType
import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.type.SimpleType
import spock.lang.Specification
import springfox.documentation.schema.mixins.SchemaPluginsSupport
import springfox.documentation.spring.web.dummy.DummyModels
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.mixins.ServicePluginsSupport
import springfox.documentation.spring.web.HandlerMethodReturnTypes

import static springfox.documentation.spi.DocumentationType.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

@Mixin([RequestMappingSupport, ServicePluginsSupport, SchemaPluginsSupport, AlternateTypesSupport])
class ReturnTypesSpec extends Specification {
  TypeNameExtractor sut

  def setup() {
    sut = new TypeNameExtractor(new TypeResolver(), defaultSchemaPlugins())
  }

   def "model types"() {
    expect:
      def type = HandlerMethodReturnTypes.handlerReturnType(new TypeResolver(), handlerMethod)
      type.getErasedType() == expectedType

    where:
      handlerMethod                                            | expectedType
      dummyHandlerMethod("methodWithConcreteResponseBody")     | DummyModels.BusinessModel.class
      dummyHandlerMethod("methodWithConcreteCorporationModel") | DummyModels.CorporationModel.class
   }

  def "Get response class name from ResolvedType"(){
    expect:
      def namingStrategy = new DefaultGenericTypeNamingStrategy()
      def modelResponseClass = sut.typeName(
              returnValue(new TypeResolver().resolve(GenericType.class, clazz), SWAGGER_12, alternateTypeProvider(),
                      namingStrategy))
      modelResponseClass == expectedResponseClassName

    where:
      clazz       | expectedResponseClassName
      SimpleType  | "GenericType«SimpleType»"
      Integer     | "GenericType«int»"
  }

  def "Cannot instantiate HandlerMethodReturnTypes helper class" () {
    when:
      new HandlerMethodReturnTypes()
    then:
      thrown(UnsupportedOperationException)
  }

}