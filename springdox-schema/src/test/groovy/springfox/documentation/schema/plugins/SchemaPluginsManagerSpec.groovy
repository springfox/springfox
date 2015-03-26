package springfox.documentation.schema.plugins

import org.springframework.plugin.core.OrderAwarePluginRegistry
import org.springframework.plugin.core.PluginRegistry
import spock.lang.Specification
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.schema.ModelNameContext
import springfox.documentation.schema.TypeForTestingPropertyNames
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin
import springfox.documentation.builders.ModelPropertyBuilder
import springfox.documentation.spi.schema.AlternateTypeProvider
import springfox.documentation.spi.schema.ModelBuilderPlugin
import springfox.documentation.spi.schema.TypeNameProviderPlugin
import springfox.documentation.spi.schema.contexts.ModelContext
import springfox.documentation.spi.schema.contexts.ModelPropertyContext

import java.lang.reflect.AnnotatedElement

import static com.google.common.collect.Lists.*
import static springfox.documentation.spi.DocumentationType.SPRING_WEB

class SchemaPluginsManagerSpec extends Specification {
  SchemaPluginsManager sut
  def propertyPlugin = Mock(ModelPropertyBuilderPlugin)
  def modelPlugin = Mock(ModelBuilderPlugin)
  def namePlugin = Mock(TypeNameProviderPlugin)

  def setup() {
    PluginRegistry<ModelPropertyBuilderPlugin, DocumentationType> propRegistry =
            OrderAwarePluginRegistry.create(newArrayList(propertyPlugin))
    propertyPlugin.supports(SPRING_WEB) >> true

    PluginRegistry<ModelBuilderPlugin, DocumentationType> modelRegistry =
            OrderAwarePluginRegistry.create(newArrayList(modelPlugin))
    modelPlugin.supports(SPRING_WEB) >> true

    PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
            OrderAwarePluginRegistry.create(newArrayList(namePlugin))
    namePlugin.supports(SPRING_WEB) >> true

    sut = new SchemaPluginsManager(propRegistry, modelRegistry, modelNameRegistry)
  }

  def "enriches model property when plugins are found"() {
    given:
      def context = new ModelPropertyContext(Mock(ModelPropertyBuilder), Mock(AnnotatedElement), SPRING_WEB)
    when:
      sut.property(context)
    then:
      1 * propertyPlugin.apply(context)
  }

  def "enriches model when plugins are found"() {
    given:
      def namingStrategy = new DefaultGenericTypeNamingStrategy()
      def context = ModelContext.inputParam(TypeForTestingPropertyNames, SPRING_WEB, new AlternateTypeProvider([]), namingStrategy)
    and:
      context.documentationType >> SPRING_WEB
    when:
      sut.model(context)
    then:
      1 * modelPlugin.apply(context)
  }

  def "enriches model name when plugins are found"() {
    given:
      def context = Mock(ModelNameContext)
    and:
      context.documentationType >> SPRING_WEB
    when:
      sut.typeName(context)
    then:
      1 * namePlugin.nameFor(_)
  }
}
