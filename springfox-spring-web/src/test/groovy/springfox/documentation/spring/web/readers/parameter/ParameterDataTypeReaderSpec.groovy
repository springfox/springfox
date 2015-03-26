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

package springfox.documentation.spring.web.readers.parameter
import com.fasterxml.classmate.TypeResolver
import org.springframework.core.MethodParameter
import org.springframework.web.method.HandlerMethod
import org.springframework.web.multipart.MultipartFile
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.dummy.DummyModels
import springfox.documentation.spring.web.mixins.ServicePluginsSupport
import springfox.documentation.schema.TypeNameExtractor
import springfox.documentation.schema.mixins.SchemaPluginsSupport
import springfox.documentation.service.ResolvedMethodParameter
import springfox.documentation.spi.service.contexts.ParameterContext
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

@Mixin([RequestMappingSupport, ServicePluginsSupport, SchemaPluginsSupport])
class ParameterDataTypeReaderSpec extends DocumentationContextSpec {
  HandlerMethod handlerMethod = Stub(HandlerMethod)
  MethodParameter methodParameter = Stub(MethodParameter)
  def typeNameExtractor =
          new TypeNameExtractor(new TypeResolver(), defaultSchemaPlugins())
  
  ParameterDataTypeReader sut = new ParameterDataTypeReader(typeNameExtractor, new TypeResolver())

  def "Should support all documentation types"() {
    expect:
      sut.supports(DocumentationType.SPRING_WEB)
      sut.supports(DocumentationType.SWAGGER_12)
      sut.supports(DocumentationType.SWAGGER_2)
  }

  def "Parameter types"() {
    given:
      ResolvedMethodParameter resolvedMethodParameter = new ResolvedMethodParameter(methodParameter,
              new TypeResolver().resolve(paramType))
      def namingStrategy = new DefaultGenericTypeNamingStrategy()
      ParameterContext parameterContext =
              new ParameterContext(resolvedMethodParameter, new ParameterBuilder(), context(), namingStrategy)
      methodParameter.getParameterType() >> paramType

    when:
      sut.apply(parameterContext)
    then:
      parameterContext.parameterBuilder().build().modelRef.type == expected
    where:
      paramType                       | expected
      char.class                      | "string"
      String.class                    | "string"
      Integer.class                   | "int"
      int.class                       | "int"
      Long.class                      | "long"
      BigInteger.class                | "long"
      long.class                      | "long"
      Float.class                     | "float"
      float.class                     | "float"
      Double.class                    | "double"
      double.class                    | "double"
      Byte.class                      | "byte"
      BigDecimal.class                | "double"
      byte.class                      | "byte"
      Boolean.class                   | "boolean"
      boolean.class                   | "boolean"
      Date.class                      | "date-time"
      DummyModels.FunkyBusiness.class | "FunkyBusiness"
      Void.class                      | "Void"
      MultipartFile.class             | "File"
  }

  def "Container Parameter types"() {
    given:
      ResolvedMethodParameter resolvedMethodParameter = Mock(ResolvedMethodParameter)
    def namingStrategy = new DefaultGenericTypeNamingStrategy()
      ParameterContext parameterContext =
              new ParameterContext(resolvedMethodParameter, new ParameterBuilder(), context(), namingStrategy)
      resolvedMethodParameter.getResolvedParameterType() >> new TypeResolver().resolve(List, String)
      resolvedMethodParameter.getMethodParameter() >> methodParameter
      methodParameter.getParameterType() >> List

    when:
      def typeNameExtractor =
              new TypeNameExtractor(new TypeResolver(), defaultSchemaPlugins())
      def sut = new ParameterDataTypeReader(typeNameExtractor, new TypeResolver())
      sut.apply(parameterContext)
    then:
      parameterContext.parameterBuilder().build().modelRef.type == "List"
      parameterContext.parameterBuilder().build().modelRef.itemType == "string"

  }

}
