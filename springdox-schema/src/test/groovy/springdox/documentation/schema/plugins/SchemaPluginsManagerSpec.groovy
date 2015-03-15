package springdox.documentation.schema.plugins

import org.springframework.plugin.core.OrderAwarePluginRegistry
import org.springframework.plugin.core.PluginRegistry
import spock.lang.Specification
import springdox.documentation.builders.ModelPropertyBuilder
import springdox.documentation.schema.ModelNameContext
import springdox.documentation.schema.TypeForTestingPropertyNames
import springdox.documentation.spi.DocumentationType
import springdox.documentation.spi.schema.AlternateTypeProvider
import springdox.documentation.spi.schema.ModelBuilderPlugin
import springdox.documentation.spi.schema.ModelPropertyBuilderPlugin
import springdox.documentation.spi.schema.TypeNameProviderPlugin
import springdox.documentation.spi.schema.contexts.ModelContext
import springdox.documentation.spi.schema.contexts.ModelPropertyContext

import java.lang.reflect.AnnotatedElement

import static com.google.common.collect.Lists.*
import static springdox.documentation.spi.DocumentationType.*

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
      def context = ModelContext.inputParam(TypeForTestingPropertyNames, SPRING_WEB, new AlternateTypeProvider([]))
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
