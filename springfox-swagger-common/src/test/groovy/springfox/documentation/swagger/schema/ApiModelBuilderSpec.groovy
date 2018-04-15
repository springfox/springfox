/*
 *
 *  Copyright 2016-2018 the original author or authors.
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
package springfox.documentation.swagger.schema

import com.fasterxml.classmate.TypeResolver
import com.google.common.collect.ImmutableSet
import io.swagger.annotations.ApiModel
import spock.lang.Shared
import spock.lang.Specification
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.schema.TypeNameExtractor
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.AlternateTypeProvider
import springfox.documentation.spi.schema.contexts.ModelContext

class ApiModelBuilderSpec extends Specification {
  @Shared def resolver = new TypeResolver()

  def "Should all swagger documentation types"() {
    given:
      def sut = new ApiModelBuilder(resolver, Mock(TypeNameExtractor))
    expect:
      !sut.supports(DocumentationType.SPRING_WEB)
      sut.supports(DocumentationType.SWAGGER_12)
      sut.supports(DocumentationType.SWAGGER_2)
  }

  def "Api model builder parses ApiModel annotation as expected" () {
    given:
      ApiModelBuilder sut = new ApiModelBuilder(resolver, Mock(TypeNameExtractor))
      ModelContext context = ModelContext.inputParam(
          "group",
          type,
          DocumentationType.SWAGGER_12,
          new AlternateTypeProvider([]),
          new DefaultGenericTypeNamingStrategy(),
          ImmutableSet.builder().build())
    when:
      sut.apply(context)
    then:
      context.builder.build().description == expected
    where:
      type                | expected
      String              | null
      AnnotatedTest       | "description"

  }

  @ApiModel(description = "description")
  class AnnotatedTest {

  }
}
