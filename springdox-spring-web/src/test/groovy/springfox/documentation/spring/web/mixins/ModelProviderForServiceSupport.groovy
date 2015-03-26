package springfox.documentation.spring.web.mixins
import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import springfox.documentation.schema.DefaultModelProvider
import springfox.documentation.schema.configuration.ObjectMapperConfigured
import springfox.documentation.schema.plugins.SchemaPluginsManager
import springfox.documentation.schema.property.bean.AccessorsProvider
import springfox.documentation.schema.property.bean.BeanModelPropertyProvider
import springfox.documentation.schema.property.provider.DefaultModelPropertiesProvider
import springfox.documentation.schema.ModelDependencyProvider
import springfox.documentation.schema.ModelProvider
import springfox.documentation.schema.TypeNameExtractor
import springfox.documentation.schema.mixins.SchemaPluginsSupport
import springfox.documentation.schema.property.ObjectMapperBeanPropertyNamingStrategy
import springfox.documentation.schema.property.constructor.ConstructorModelPropertyProvider
import springfox.documentation.schema.property.field.FieldModelPropertyProvider
import springfox.documentation.schema.property.field.FieldProvider

@SuppressWarnings("GrMethodMayBeStatic")
@Mixin([ServicePluginsSupport, SchemaPluginsSupport])
class ModelProviderForServiceSupport {
  def typeNameExtractor() {
    new TypeNameExtractor(new TypeResolver(), defaultSchemaPlugins())
  }

  ModelProvider modelProvider(SchemaPluginsManager pluginsManager = defaultSchemaPlugins(),
                              TypeResolver typeResolver = new TypeResolver()) {

    def fields = new FieldProvider(typeResolver)
    def objectMapper = new ObjectMapper()
    def typeNameExtractor = typeNameExtractor()
    def namingStrategy = new ObjectMapperBeanPropertyNamingStrategy()
    namingStrategy.onApplicationEvent(new ObjectMapperConfigured(this, objectMapper))

    def modelPropertiesProvider = new DefaultModelPropertiesProvider(
            beanProperty(typeResolver, namingStrategy, pluginsManager, typeNameExtractor, objectMapper),
            fieldProperty(fields, namingStrategy, pluginsManager, typeNameExtractor, objectMapper),
            constructorProperty(fields, namingStrategy, pluginsManager, typeNameExtractor, objectMapper))
    def modelDependenciesProvider = new ModelDependencyProvider(typeResolver,
            modelPropertiesProvider, typeNameExtractor)
    new DefaultModelProvider(typeResolver, modelPropertiesProvider, modelDependenciesProvider,
            pluginsManager, typeNameExtractor)
  }

  def beanProperty(TypeResolver typeResolver, ObjectMapperBeanPropertyNamingStrategy namingStrategy, SchemaPluginsManager pluginsManager, TypeNameExtractor typeNameExtractor, ObjectMapper objectMapper) {
    def modelPropertyProvider = new BeanModelPropertyProvider(new AccessorsProvider(typeResolver), typeResolver
            , namingStrategy, pluginsManager, typeNameExtractor)
    modelPropertyProvider.onApplicationEvent(new ObjectMapperConfigured(this, objectMapper))
    modelPropertyProvider
  }

  def fieldProperty(FieldProvider fields, ObjectMapperBeanPropertyNamingStrategy namingStrategy, SchemaPluginsManager pluginsManager, TypeNameExtractor typeNameExtractor, ObjectMapper objectMapper) {
    def modelPropertyProvider = new FieldModelPropertyProvider(fields, namingStrategy,
            pluginsManager, typeNameExtractor)
    modelPropertyProvider.onApplicationEvent(new ObjectMapperConfigured(this, objectMapper))
    modelPropertyProvider
  }

  def constructorProperty(FieldProvider fields, ObjectMapperBeanPropertyNamingStrategy namingStrategy, SchemaPluginsManager pluginsManager, TypeNameExtractor typeNameExtractor, ObjectMapper objectMapper) {
    def modelPropertyProvider =
            new ConstructorModelPropertyProvider(fields, namingStrategy, pluginsManager, typeNameExtractor)
    modelPropertyProvider.onApplicationEvent(new ObjectMapperConfigured(this, objectMapper))
    modelPropertyProvider
  }

  ModelProvider modelProviderWithSnakeCaseNamingStrategy(SchemaPluginsManager pluginsManager = defaultSchemaPlugins(),
      TypeResolver typeResolver = new TypeResolver()) {

    def fields = new FieldProvider(typeResolver)
    def objectMapper = new ObjectMapper()
    def typeNameExtractor = typeNameExtractor()
    objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES)
    def namingStrategy = new ObjectMapperBeanPropertyNamingStrategy()
    namingStrategy.onApplicationEvent(new ObjectMapperConfigured(this, objectMapper))

    def modelPropertiesProvider = new DefaultModelPropertiesProvider(
            beanProperty(typeResolver, namingStrategy, pluginsManager, typeNameExtractor, objectMapper),
            fieldProperty(fields, namingStrategy, pluginsManager, typeNameExtractor, objectMapper),
            constructorProperty(fields, namingStrategy, pluginsManager, typeNameExtractor, objectMapper))
    def modelDependenciesProvider = new ModelDependencyProvider(typeResolver,
            modelPropertiesProvider, typeNameExtractor)
    new DefaultModelProvider(typeResolver, modelPropertiesProvider, modelDependenciesProvider,
            pluginsManager, typeNameExtractor)
  }


}
