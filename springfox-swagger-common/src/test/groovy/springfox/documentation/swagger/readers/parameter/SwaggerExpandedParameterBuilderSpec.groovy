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
import spock.lang.Specification
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.schema.ExampleEnum
import springfox.documentation.schema.JacksonEnumTypeDeterminer
import springfox.documentation.schema.property.field.FieldProvider
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.ParameterExpansionContext
import springfox.documentation.spring.web.DescriptionResolver

class SwaggerExpandedParameterBuilderSpec extends Specification {
  def "Swagger parameter expander expands as expected" () {
    given:
      def env = new DescriptionResolver(new MockEnvironment())
      SwaggerExpandedParameterBuilder sut = new SwaggerExpandedParameterBuilder(env, new JacksonEnumTypeDeterminer())
    and:
      ParameterExpansionContext context = new ParameterExpansionContext("Test", "", field,
          DocumentationType.SWAGGER_12, new ParameterBuilder())
    when:
      sut.apply(context)
      def param = context.parameterBuilder.build()
    then:
      param != null //TODO: add more fidelity to this test
    and:
      sut.supports(DocumentationType.SWAGGER_12)
      sut.supports(DocumentationType.SWAGGER_2)
      !sut.supports(DocumentationType.SPRING_WEB)
    where:
      field << [named("a"), named("b"), named("c"), named("d"), named("f") ]
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

    @ApiModelProperty(name = "b")
    public String b;

    @ApiParam(name = "c", allowableValues = "a, b, c")
    public String c;

    @ApiModelProperty(name = "d")
    public D d;

    @ApiModelProperty(name = "f")
    public ExampleEnum f;
  }

  class D {
    @ApiModelProperty(name = "e")
    public String e;
  }
}
