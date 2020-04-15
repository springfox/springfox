/*
 *
 *  Copyright 2015-2017 the original author or authors.
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
package springfox.documentation.swagger.readers.parameter

import com.fasterxml.classmate.TypeResolver
import com.fasterxml.classmate.members.ResolvedField
import io.swagger.annotations.ApiModelProperty
import io.swagger.annotations.ApiParam
import org.springframework.mock.env.MockEnvironment
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.builders.RequestParameterBuilder
import springfox.documentation.schema.ExampleEnum
import springfox.documentation.schema.JacksonEnumTypeDeterminer
import springfox.documentation.schema.property.field.FieldProvider
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.ParameterExpansionContext
import springfox.documentation.spring.web.DescriptionResolver
import springfox.documentation.spring.web.readers.parameter.ModelAttributeParameterMetadataAccessor

class SwaggerExpandedParameterBuilderSpec extends Specification {
  @Shared
  def resolver = new TypeResolver()

  @Unroll
  def "Swagger parameter expander reads field #field.name as #expectedName "() {
    given:
    def env = new DescriptionResolver(new MockEnvironment())
    SwaggerExpandedParameterBuilder sut = new SwaggerExpandedParameterBuilder(env, new JacksonEnumTypeDeterminer())

    and:
    def builderWithDefaultName = new ParameterBuilder().name(field.name)

    and:
    ParameterExpansionContext context = new ParameterExpansionContext(
        "Test",
        "",
        "",
        new ModelAttributeParameterMetadataAccessor(
            [field.rawMember],
            field.type,
            field.name),
        DocumentationType.SWAGGER_12,
        builderWithDefaultName,
        new RequestParameterBuilder())

    when:
    sut.apply(context)
    def param = context.parameterBuilder.build()

    then:
    param != null
    param.name == expectedName

    and:
    sut.supports(DocumentationType.SWAGGER_12)
    sut.supports(DocumentationType.SWAGGER_2)
    !sut.supports(DocumentationType.SPRING_WEB)

    where:
    field      | expectedName
    named("a") | "a"
    named("b") | "b1"
    named("c") | "c2"
    named("d") | "d3"
    named("f") | "f4"
  }

  def named(String name) {
    def resolver = new TypeResolver()
    FieldProvider fieldProvider = new FieldProvider(resolver)
    for (ResolvedField field : fieldProvider.in(resolver.resolve(A))) {
      if (field.name == name) {
        return field
      }
    }
  }

  class A {
    public String a;

    @ApiModelProperty(name = "b1")
    public String b;

    @ApiParam(name = "c2", allowableValues = "a, b, c")
    public String c;

    @ApiModelProperty(name = "d3")
    public D d;

    @ApiModelProperty(name = "f4")
    public ExampleEnum f;
  }

  class D {
    @ApiModelProperty(name = "e")
    public String e;
  }
}
