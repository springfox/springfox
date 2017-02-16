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
import org.springframework.plugin.core.OrderAwarePluginRegistry
import org.springframework.plugin.core.PluginRegistry
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import spock.lang.Unroll
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.schema.DefaultTypeNameProvider
import springfox.documentation.schema.TypeNameExtractor
import springfox.documentation.schema.mixins.SchemaPluginsSupport
import springfox.documentation.service.AllowableListValues
import springfox.documentation.service.ResolvedMethodParameter
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.TypeNameProviderPlugin
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spi.service.contexts.ParameterContext
import springfox.documentation.spring.web.dummy.DummyModels
import springfox.documentation.spring.web.dummy.models.Business
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.mixins.ServicePluginsSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

@Mixin([RequestMappingSupport, ServicePluginsSupport, SchemaPluginsSupport])
class ParameterDataTypeReaderSpec extends DocumentationContextSpec {
  PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
      OrderAwarePluginRegistry.create([new DefaultTypeNameProvider()])
  def typeNameExtractor = new TypeNameExtractor(new TypeResolver(),  modelNameRegistry)

  ParameterDataTypeReader sut = new ParameterDataTypeReader(typeNameExtractor, new TypeResolver())

  def "Should support all documentation types"() {
    expect:
      sut.supports(DocumentationType.SPRING_WEB)
      sut.supports(DocumentationType.SWAGGER_12)
      sut.supports(DocumentationType.SWAGGER_2)
  }

  @Unroll
  def "Parameter types #paramType"() {
    given:
      ResolvedMethodParameter resolvedMethodParameter =
          new ResolvedMethodParameter(0, "", annotations, new TypeResolver().resolve(paramType))
      def namingStrategy = new DefaultGenericTypeNamingStrategy()
      ParameterContext parameterContext =
              new ParameterContext(resolvedMethodParameter, new ParameterBuilder(), context(), namingStrategy,
                  Mock(OperationContext))

    when:
      sut.apply(parameterContext)

    then:
      def modelRef = parameterContext.parameterBuilder().build().modelRef
      modelRef.type == expected
      if ("object".equals(expected)) {
        assert modelRef.itemType == "string"
        def allowable = modelRef.allowableValues as AllowableListValues
        assert allowable.values.size() == 2
      }
    where:
      paramType                   | annotations          | expected
      char                        | []                   | "string"
      String                      | []                   | "string"
      Integer                     | []                   | "int"
      int                         | []                   | "int"
      Long                        | []                   | "long"
      Long                        | [Mock(PathVariable)] | "long"
      Long                        | [Mock(RequestParam)] | "long"
      Long[]                      | [Mock(PathVariable)] | "string"
      Long[]                      | [Mock(RequestParam)] | "Array"
      BigInteger                  | []                   | "biginteger"
      long                        | []                   | "long"
      Float                       | []                   | "float"
      float                       | []                   | "float"
      Double                      | []                   | "double"
      double                      | []                   | "double"
      Byte                        | []                   | "byte"
      BigDecimal                  | []                   | "bigdecimal"
      byte                        | []                   | "byte"
      Boolean                     | []                   | "boolean"
      boolean                     | []                   | "boolean"
      Date                        | []                   | "date-time"
      DummyModels.FunkyBusiness   | []                   | "FunkyBusiness"
      DummyModels.FunkyBusiness   | [Mock(PathVariable)] | "string"
      DummyModels.FunkyBusiness   | [Mock(RequestParam)] | "string"
      DummyModels.FunkyBusiness[] | [Mock(PathVariable)] | "string"
      DummyModels.FunkyBusiness[] | [Mock(RequestParam)] | "string"
      Void                        | []                   | "void"
      MultipartFile               | []                   | "__file"
      Business.BusinessType       | []                   | "string"
      Business.BusinessType       | [Mock(PathVariable)] | "string"
      Business.BusinessType       | [Mock(RequestParam)] | "string"
      Business.BusinessType[]     | [Mock(PathVariable)] | "string"
      Business.BusinessType[]     | [Mock(RequestParam)] | "Array"
  }

  def "RequestParam Map types"() {
    given:
      ResolvedMethodParameter resolvedMethodParameter =
          new ResolvedMethodParameter(0, "", [Mock(RequestParam)], new TypeResolver().resolve(Map, String, String))
      def namingStrategy = new DefaultGenericTypeNamingStrategy()
      ParameterContext parameterContext =
          new ParameterContext(resolvedMethodParameter, new ParameterBuilder(), context(), namingStrategy,
              Mock(OperationContext))

    when:
      sut.apply(parameterContext)

    then:
      def modelRef = parameterContext.parameterBuilder().build().modelRef
      modelRef.type == ""
      modelRef.isMap()
      modelRef.itemType == "string"
  }

  def "Container Parameter types"() {
    given:
      ResolvedMethodParameter resolvedMethodParameter =
          new ResolvedMethodParameter(0, "", [], new TypeResolver().resolve(List, String))
      def namingStrategy = new DefaultGenericTypeNamingStrategy()
      ParameterContext parameterContext =
              new ParameterContext(resolvedMethodParameter, new ParameterBuilder(), context(), namingStrategy,
                  Mock(OperationContext))

    when:
      PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
        OrderAwarePluginRegistry.create([new DefaultTypeNameProvider()])
      def typeNameExtractor = new TypeNameExtractor(new TypeResolver(),  modelNameRegistry)
      def sut = new ParameterDataTypeReader(typeNameExtractor, new TypeResolver())
      sut.apply(parameterContext)
    then:
      parameterContext.parameterBuilder().build().modelRef.type == "List"
      parameterContext.parameterBuilder().build().modelRef.itemType == "string"

  }

}
