package springdox.documentation.schema.mixins
import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.ObjectMapper
import springdox.documentation.schema.DefaultGenericTypeNamingStrategy
import springdox.documentation.schema.DefaultModelProvider
import springdox.documentation.schema.ModelDependencyProvider
import springdox.documentation.schema.ModelProvider
import springdox.documentation.schema.TypeNameExtractor
import springdox.documentation.schema.configuration.ObjectMapperConfigured
import springdox.documentation.schema.plugins.SchemaPluginsManager
import springdox.documentation.schema.property.ObjectMapperBeanPropertyNamingStrategy
import springdox.documentation.schema.property.bean.AccessorsProvider
import springdox.documentation.schema.property.bean.BeanModelPropertyProvider
import springdox.documentation.schema.property.constructor.ConstructorModelPropertyProvider
import springdox.documentation.schema.property.field.FieldModelPropertyProvider
import springdox.documentation.schema.property.field.FieldProvider
import springdox.documentation.schema.property.provider.DefaultModelPropertiesProvider

@SuppressWarnings("GrMethodMayBeStatic")
@Mixin([SchemaPluginsSupport])
class ModelProviderSupport {

  ModelProvider defaultModelProvider(ObjectMapper objectMapper = new ObjectMapper(),
                                     TypeResolver typeResolver = new TypeResolver()) {

    def fields = new FieldProvider(typeResolver)

    def pluginsManager = defaultSchemaPlugins()
    TypeNameExtractor typeNameExtractor = new TypeNameExtractor(typeResolver, new DefaultGenericTypeNamingStrategy(), pluginsManager)
    def namingStrategy = new ObjectMapperBeanPropertyNamingStrategy()
    namingStrategy.onApplicationEvent(new ObjectMapperConfigured(this, objectMapper))

    def modelPropertiesProvider = new DefaultModelPropertiesProvider(
            beanProperty(typeResolver, namingStrategy, pluginsManager, typeNameExtractor, objectMapper),
            fieldProperty(fields, namingStrategy, pluginsManager, typeNameExtractor, objectMapper),
            constructorProperty(fields, namingStrategy, pluginsManager, typeNameExtractor, objectMapper))
    def modelDependenciesProvider = modelDependencyProvider(typeResolver,
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

  ModelDependencyProvider modelDependencyProvider(TypeResolver resolver,
      DefaultModelPropertiesProvider modelPropertiesProvider, 
      TypeNameExtractor typeNameExtractor) {
    new ModelDependencyProvider(resolver, modelPropertiesProvider, typeNameExtractor)
  }

  ModelDependencyProvider defaultModelDependencyProvider() {
    def typeResolver = new TypeResolver()
    def fields = new FieldProvider(typeResolver)

    def pluginsManager = defaultSchemaPlugins()
    TypeNameExtractor typeNameExtractor = new TypeNameExtractor(typeResolver, new DefaultGenericTypeNamingStrategy(),
            pluginsManager)
    def objectMapper = new ObjectMapper()
    def namingStrategy = new ObjectMapperBeanPropertyNamingStrategy()
    namingStrategy.onApplicationEvent(new ObjectMapperConfigured(this, objectMapper))

    def modelPropertiesProvider = new DefaultModelPropertiesProvider(
            beanProperty(typeResolver, namingStrategy, pluginsManager, typeNameExtractor, objectMapper),
            fieldProperty(fields, namingStrategy, pluginsManager, typeNameExtractor, objectMapper),
            constructorProperty(fields, namingStrategy, pluginsManager, typeNameExtractor, objectMapper))
    modelDependencyProvider(typeResolver, modelPropertiesProvider, typeNameExtractor)
  }

}
