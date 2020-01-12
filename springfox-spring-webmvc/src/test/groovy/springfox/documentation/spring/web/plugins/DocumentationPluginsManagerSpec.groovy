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

package springfox.documentation.spring.web.plugins

import spock.lang.Specification
import springfox.documentation.builders.OperationBuilder
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.DocumentationPlugin
import springfox.documentation.spi.service.OperationBuilderPlugin
import springfox.documentation.spi.service.ParameterBuilderPlugin
import springfox.documentation.spi.service.ResourceGroupingStrategy
import springfox.documentation.spi.service.contexts.DocumentationContext
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spi.service.contexts.ParameterContext
import springfox.documentation.spi.service.contexts.PathContext
import springfox.documentation.spring.web.SpringGroupingStrategy
import springfox.documentation.spring.web.mixins.ServicePluginsSupport
import springfox.documentation.spring.web.paths.DefaultPathProvider
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator

import static java.util.Optional.*

class DocumentationPluginsManagerSpec extends Specification implements ServicePluginsSupport {
  def "default documentation plugin always exists" () {
    given:
      def sut = defaultWebPlugins()
    expect:
      sut.documentationPlugins.size() == 0
      sut.documentationPlugins().size() == 1
  }

  def "Resource grouping strategy is defaulted to use SpringResourceGroupingStrategy" () {
    given:
      def sut = defaultWebPlugins()
    expect:
      sut.resourceGroupingStrategy(DocumentationType.SPRING_WEB) instanceof SpringGroupingStrategy
      sut.resourceGroupingStrategy(DocumentationType.SWAGGER_12) instanceof SpringGroupingStrategy
  }

  def "When documentation plugins are explicitly defined" () {
    given:
      def mockPlugin = Mock(DocumentationPlugin)
    and:
      mockPlugin.groupName >> "default"
      def sut = customWebPlugins([mockPlugin])
    expect:
      sut.documentationPlugins.size() == 1
      sut.documentationPlugins().first() == mockPlugin
  }

  def "When resource grouping strategy has been defined" () {
    given:
      def mockStrategy = Mock(ResourceGroupingStrategy)
    and:
      def sut = customWebPlugins([], [mockStrategy])
      mockStrategy.supports(_) >> true
    expect:
      sut.resourceGroupingStrategy(DocumentationType.SPRING_WEB) == mockStrategy
      sut.resourceGroupingStrategy(DocumentationType.SWAGGER_12) == mockStrategy
  }

  def "Even when no operation plugins are applied an empty operation is returned" () {
    given:
      def operationContext = Mock(OperationContext)
    and:
      operationContext.operationBuilder() >> new OperationBuilder(new CachingOperationNameGenerator())
      operationContext.documentationType >> DocumentationType.SWAGGER_2
    when:
      def sut = customWebPlugins()
      def operation = sut.operation(operationContext)
    then:
      operation != null
  }

  def "Operation plugins are applied" () {
    given:
      def operationPlugin = Mock(OperationBuilderPlugin)
      def operationContext = Mock(OperationContext)
    and:
      operationContext.operationBuilder() >> new OperationBuilder(new CachingOperationNameGenerator())
      operationContext.documentationType >> DocumentationType.SPRING_WEB
      operationPlugin.supports(_) >> true
    when:
      def sut = customWebPlugins([], [], [operationPlugin])
      def operation = sut.operation(operationContext)
    then:
      operation != null
      operationPlugin.apply(operationContext)
  }

  def "Even when no parameter plugins are applied an empty operation is returned" () {
    given:
      def paramContext = Mock(ParameterContext)
    and:
      paramContext.parameterBuilder() >> new ParameterBuilder()
      paramContext.documentationType >> DocumentationType.SWAGGER_2
    when:
      def sut = customWebPlugins()
      def parameter = sut.parameter(paramContext)
    then:
      parameter != null
  }

  def "Parameter plugins are applied" () {
    given:
      def paramPlugin = Mock(ParameterBuilderPlugin)
      def paramContext = Mock(ParameterContext)
    and:
      paramContext.parameterBuilder() >> new ParameterBuilder()
      paramContext.documentationType >> DocumentationType.SPRING_WEB
      paramPlugin.supports(_) >> true
    when:
      def sut = customWebPlugins([], [], [], [paramPlugin])
      def parameter = sut.parameter(paramContext)
    then:
      parameter != null
      paramPlugin.apply(paramContext)
  }

  def "Path decorator plugins are applied" () {
    given:
      def pathContext = Mock(PathContext)
      def context = Mock(DocumentationContext)
    and:
      pathContext.pathProvider() >> new DefaultPathProvider()
      pathContext.documentationContext() >> context
      context.getPathMapping() >> empty()
      pathContext.parameters >> []
    when:
      def sut = defaultWebPlugins()
      def decorator = sut.decorator(pathContext)
    then:
      decorator != null
      decorator.apply("") == "/"
  }
}
