/*
 *
 *  Copyright 2015-2018 the original author or authors.
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

package springfox.documentation.spring.web.readers

import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import org.springframework.plugin.core.OrderAwarePluginRegistry
import org.springframework.plugin.core.PluginRegistry
import org.springframework.web.method.HandlerMethod
import spock.lang.Ignore
import springfox.documentation.schema.DefaultTypeNameProvider
import springfox.documentation.schema.JacksonEnumTypeDeterminer
import springfox.documentation.schema.Model
import springfox.documentation.schema.ModelProperty
import springfox.documentation.schema.TypeNameExtractor
import springfox.documentation.schema.TypeNameIndexingAdapter
import springfox.documentation.service.ResourceGroup
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.TypeNameProviderPlugin
import springfox.documentation.spi.service.contexts.RequestMappingContext
import springfox.documentation.spring.web.WebMvcRequestHandler
import springfox.documentation.spring.web.dummy.DummyClass
import springfox.documentation.spring.web.dummy.DummyModels
import springfox.documentation.spring.web.dummy.controllers.BusinessService
import springfox.documentation.spring.web.dummy.controllers.PetService
import springfox.documentation.spring.web.dummy.models.FoobarDto
import springfox.documentation.spring.web.dummy.models.Monkey
import springfox.documentation.spring.web.dummy.models.PetWithJsonView
import springfox.documentation.spring.web.dummy.models.Pirate
import springfox.documentation.spring.web.dummy.models.SameFancyPet
import springfox.documentation.spring.web.dummy.models.same.Pet
import springfox.documentation.spring.web.mixins.ModelProviderForServiceSupport
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.mixins.ServicePluginsSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver
import springfox.documentation.spring.web.scanners.ApiListingScanningContext
import springfox.documentation.spring.web.scanners.ApiModelReader

import javax.servlet.http.HttpServletResponse

import static com.google.common.collect.Maps.*

@Mixin([RequestMappingSupport, ModelProviderForServiceSupport, ServicePluginsSupport])
//TODO: Rewrite this test PR#2056. Should not have ids that are hashcode values
class ApiModelReaderSpec extends DocumentationContextSpec {

  ApiModelReader sut
  ApiModelReader sutSpecial
  DocumentationPluginsManager pluginsManager
  ResourceGroup resourceGroup;
  TypeNameExtractor typeNameExtractor
  def methodResolver = new HandlerMethodResolver(new TypeResolver())

  def setup() {
    TypeResolver typeResolver = new TypeResolver()
    PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
            OrderAwarePluginRegistry.create([new DefaultTypeNameProvider()])
    typeNameExtractor = new TypeNameExtractor(
        typeResolver,
        modelNameRegistry,
        new JacksonEnumTypeDeterminer())
    pluginsManager = defaultWebPlugins()
    sut = new ApiModelReader(
        modelProvider(),
        new TypeResolver(),
        pluginsManager,
        new JacksonEnumTypeDeterminer(),
        typeNameExtractor)
    ObjectMapper mapper = new ObjectMapper()
    mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
    sutSpecial = new ApiModelReader(
            modelProvider(defaultSchemaPlugins(), new TypeResolver(), new JacksonEnumTypeDeterminer(), mapper),
            new TypeResolver(),
            pluginsManager,
            new JacksonEnumTypeDeterminer(),
            typeNameExtractor)
    resourceGroup = new ResourceGroup("businesses", DummyClass)
  }

  def "Method return type model"() {
    given:
      def listingContext = apiListingContext(dummyHandlerMethod('methodWithConcreteResponseBody'), '/somePath')

    when:
      def modelsMap = sut.read(listingContext)

    then:
      modelsMap.containsKey(resourceGroup)
      Map<String, Model> models = modelsMap.get(resourceGroup)
      models.size() == 1
      Model model = models.get('BusinessModel')
      model.id == '-229259865'
      model.getName() == 'BusinessModel'
      model.getQualifiedType() == 'springfox.documentation.spring.web.dummy.DummyModels$BusinessModel'

      Map<String, ModelProperty> modelProperties = model.getProperties()
      modelProperties.size() == 2

      ModelProperty nameProp = modelProperties['name']
      nameProp.type.erasedType == String
      nameProp.getQualifiedType() == 'java.lang.String'
      nameProp.getPosition() == 0
      !nameProp.isRequired()
      nameProp.getDescription() == null
      def item = nameProp.getModelRef()
      item.type == "string"
      !item.collection
      item.itemType == null
  }

  def apiListingContext(HandlerMethod handlerMethod, String path) {
    def requestMappingContext = new RequestMappingContext(
        context(),
        new WebMvcRequestHandler(
            methodResolver,
            requestMappingInfo(path), handlerMethod),
        new TypeNameIndexingAdapter())

    def resourceGroupRequestMappings = newHashMap()
    resourceGroupRequestMappings.put(resourceGroup, [requestMappingContext])
    
    return new ApiListingScanningContext(context(), resourceGroupRequestMappings)
  }

  def "should only generate models for request parameters that are annotated with Springs RequestBody"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodParameterWithRequestBodyAnnotation',
              DummyModels.BusinessModel,
              HttpServletResponse.class,
              DummyModels.AnnotatedBusinessModel.class
      )
      ApiListingScanningContext listingContext = apiListingContext(handlerMethod, '/somePath')
    when:
      def modelsMap = sut.read(listingContext)

    then:
      modelsMap.containsKey(resourceGroup)
      Map<String, Model> models = modelsMap.get(resourceGroup)
      models.size() == 1 // instead of 3
      Model model = models.get("BusinessModel")
      model.getName().equals("BusinessModel")

  }

  def "should only generate models for request parameters that are annotated with Springs RequestPart"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodParameterWithRequestPartAnnotation',
              DummyModels.BusinessModel,
              HttpServletResponse.class,
              DummyModels.AnnotatedBusinessModel.class
      )
      ApiListingScanningContext listingContext = apiListingContext(handlerMethod, '/somePath')
    when:
      def modelsMap = sut.read(listingContext)

    then:
      modelsMap.containsKey(resourceGroup)
      Map<String, Model> models = modelsMap.get(resourceGroup)
      models.size() == 1 // instead of 3
      Model model = models.get("BusinessModel")
      model.getName().equals("BusinessModel")

  }

  def "should not generate models for simple type request parameters that are annotated with Springs RequestPart"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodParameterWithRequestPartAnnotationOnSimpleType',
              String.class,
              HttpServletResponse.class,
              DummyModels.AnnotatedBusinessModel.class
      )
      ApiListingScanningContext listingContext = apiListingContext(handlerMethod, '/somePath')
    when:
      def modelsMap = sut.read(listingContext)

    then:
      modelsMap.containsKey(resourceGroup)
      Map<String, Model> models = modelsMap.get(resourceGroup)
      models.size() == 0 // instead of 3

  }

  @Ignore("This needs to move to a swagger 1.2 test")
  def "Generates the correct models when there is a Map object in the input parameter"() {
    given:
      HandlerMethod handlerMethod = handlerMethodIn(PetService, 'echo', Map)
      RequestMappingContext context = requestMappingContext(handlerMethod)

    when:
      def models = sut.read(context)

    then:
      models.size() == 2
      models.containsKey("Entry«string,Pet»")
      models.containsKey("Pet")

  }

  def "Generates the correct models when alternateTypeProvider returns an ignoreable or base parameter type"() {
    given:
      plugin
              .genericModelSubstitutes(ResponseEntity, HttpEntity)
              .configure(contextBuilder)

    and:
      HandlerMethod handlerMethod = handlerMethodIn(BusinessService, 'getResponseEntity', String)
      ApiListingScanningContext listingContext = apiListingContext(handlerMethod, '/businesses/responseEntity/{businessId}')
     
    when:
      def modelsMap = sut.read(listingContext)

    then:
      modelsMap.containsKey(resourceGroup)
      def models = modelsMap.get(resourceGroup)
      models.size() == 0

  }

  def "model should include property that is only visible during serialization"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodWithSerializeOnlyPropInReturnAndRequestBodyParam',
              DummyModels.ModelWithSerializeOnlyProperty
      )
      ApiListingScanningContext listingContext = apiListingContext(handlerMethod, '/somePath')

    when:
      def modelsMap = sut.read(listingContext)

    then:
      modelsMap.containsKey(resourceGroup)
      def models = modelsMap.get(resourceGroup)
      models.size() == 2

      String baseModelName = DummyModels.ModelWithSerializeOnlyProperty.class.simpleName

      models.containsKey(baseModelName + '_1')
      models.containsKey(baseModelName + '_2')

      Model model_1 = models.get(baseModelName + '_1')

      Map modelProperties_1 = model_1.getProperties()

      modelProperties_1.size() == 2
      modelProperties_1.containsKey('visibleForSerialize')
      modelProperties_1.containsKey('alwaysVisible')

      Model model_2 = models.get(baseModelName + '_2')

      Map modelProperties_2 = model_2.getProperties()

      modelProperties_2.size() == 1
      modelProperties_2.containsKey('alwaysVisible')

  }

  def "model should include snake_case property that is only visible during serialization when objectMapper has CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodWithSerializeOnlyPropInReturnAndRequestBodyParam',
              DummyModels.ModelWithSerializeOnlyProperty
      )
      ApiListingScanningContext listingContext = apiListingContext(handlerMethod, '/somePath')
    and:
      def snakeCaseReader = new ApiModelReader(modelProviderWithSnakeCaseNamingStrategy(),
              new TypeResolver(), defaultWebPlugins(), new JacksonEnumTypeDeterminer(), typeNameExtractor)
    when:
      def modelsMap = snakeCaseReader.read(listingContext)

    then:    
      modelsMap.containsKey(resourceGroup)
      def models = modelsMap.get(resourceGroup)

      models.size() == 2

      String baseModelName = DummyModels.ModelWithSerializeOnlyProperty.class.simpleName

      models.containsKey(baseModelName + '_1')
      models.containsKey(baseModelName + '_2')

      Model model_1 = models[baseModelName + '_1']

      Map modelProperties_1 = model_1.getProperties()

      modelProperties_1.size() == 2
      modelProperties_1.containsKey('visible_for_serialize')
      modelProperties_1.containsKey('always_visible')
      
      Model model_2 = models[baseModelName + '_2']

      Map modelProperties_2 = model_2.getProperties()

      modelProperties_2.size() == 1
      modelProperties_2.containsKey('always_visible')

  }

  def "Test to verify issue #283"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodToTestFoobarDto', FoobarDto)
      ApiListingScanningContext listingContext = apiListingContext(handlerMethod, '/somePath')

    when:
      def modelsMap = sut.read(listingContext)

    then:
      modelsMap.containsKey(resourceGroup)
      def models = modelsMap.get(resourceGroup)

      models.size() == 2

      String baseModelName = FoobarDto.simpleName

      models.containsKey(baseModelName + '_1')
      models.containsKey(baseModelName + '_2')

      Model model_1 = models[baseModelName + '_1']

      Map modelProperties_1 = model_1.getProperties()
      
      modelProperties_1.size() == 2
      modelProperties_1.containsKey('visibleForSerialize')
      modelProperties_1.containsKey('foobar')

      Model model_2 = models[baseModelName + '_2']

      Map modelProperties_2 = model_2.getProperties()

      modelProperties_2.size() == 1
      modelProperties_2.containsKey('foobar')

  }

  def "Test to verify issue #1196"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodToTestBidrectionalRecursiveTypes', Pirate)
      ApiListingScanningContext listingContext = apiListingContext(handlerMethod, '/somePath')

    when:
      def modelsMap = sut.read(listingContext)
      
    then:
      modelsMap.containsKey(resourceGroup)
      def models = modelsMap.get(resourceGroup)
    and:  
      Model pirate = models[Pirate.simpleName]
      Model monkey = models[Monkey.simpleName]
    and:
      models.size() == 2
    and:
      pirate != null
      monkey != null
    and:
      pirate.getProperties().containsKey('monkey')
      monkey.getProperties().containsKey('pirate')

  }

  def "Test to verify that duplicate class names in different packages will be prodused as different models (#182)"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodToTestIssue182', Pet)
      ApiListingScanningContext listingContext = apiListingContext(handlerMethod, '/somePath')

    when:
      def modelsMap = sut.read(listingContext)

    then:
      modelsMap.containsKey(resourceGroup)
      def models = modelsMap.get(resourceGroup)

      Model pet_1 = models["Pet_1"]
      Model pet_2 = models["Pet_2"]

      models.size() == 2
    and:
      pet_1 != null
      pet_1.qualifiedType.equals("springfox.documentation.spring.web.dummy.models.Pet");

      pet_2 != null
      pet_2.qualifiedType.equals("springfox.documentation.spring.web.dummy.models.same.Pet");
    and:
      pet_1.getProperties().size() == 3
      pet_1.getProperties().containsKey('id')
      pet_1.getProperties().containsKey('name')
      pet_1.getProperties().containsKey('age')
    and:
      pet_2.getProperties().size() == 3
      pet_2.getProperties().containsKey('id')
      pet_2.getProperties().containsKey('name')
      pet_2.getProperties().containsKey('age')
  }

  @Ignore("Rewrite this test PR #2056")
  def "Test to verify that same class for serialization and deserialization will be produced as one model"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodToTestSerializationAndDeserialization', 
              Map)
      ApiListingScanningContext listingContext = apiListingContext(handlerMethod, '/somePath')

    when:
      def modelsMap = sut.read(listingContext)

    then:
      modelsMap.containsKey(resourceGroup)
      def models = modelsMap.get(resourceGroup)

      Model fancyPet = models["FancyPet"]
      Model category = models["Category"]

      models.size() == 2
    and:
      fancyPet != null
      fancyPet.qualifiedType.equals("springfox.documentation.spring.web.dummy.models.FancyPet");

      category != null
      category.qualifiedType.equals("springfox.documentation.spring.web.dummy.models.Category");
    and:
      fancyPet.getProperties().size() == 4
      fancyPet.getProperties().containsKey('id')
      fancyPet.getProperties().containsKey('name')
      fancyPet.getProperties().containsKey('age')
      fancyPet.getProperties().containsKey('categories')
    and:
      category.getProperties().size() == 1
      category.getProperties().containsKey('name')

  }

  def "Test to verify that different class for serialization and deserialization will be produced as two models"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodToTestSameClassesWithDifferentProperties', SameFancyPet)
      ApiListingScanningContext listingContext = apiListingContext(handlerMethod, '/somePath')

    when:
      def modelsMap = sut.read(listingContext)

    then:
      modelsMap.containsKey(resourceGroup)
      def models = modelsMap.get(resourceGroup)

      Model category_1 = models["SameCategory_1"]
      Model category_2 = models["SameCategory_2"]

      Model mapFancyPet = models["MapFancyPet"]

      Model fancyPet_1 = models["SameFancyPet_1"]
      Model fancyPet_2 = models["SameFancyPet_2"]

      models.size() == 5
    and:
      category_1 != null
      category_2 != null

      fancyPet_1 != null
      fancyPet_2 != null

      mapFancyPet != null

    and:
      category_1.getProperties().size() == 2
      category_1.getProperties().containsKey('name')
      category_1.getProperties().containsKey('_type')
    and:
      category_2.getProperties().size() == 2
      category_2.getProperties().containsKey('id')
      category_2.getProperties().containsKey('name')
    and:
      fancyPet_1.getProperties().size() == 5
      fancyPet_1.getProperties().containsKey('id')
      fancyPet_1.getProperties().containsKey('age')
      fancyPet_1.getProperties().containsKey('name')
      fancyPet_1.getProperties().containsKey('color')
      fancyPet_1.getProperties().containsKey('extendedCategory')
    and:
      fancyPet_2.getProperties().size() == 6
      fancyPet_2.getProperties().containsKey('id')
      fancyPet_2.getProperties().containsKey('age')
      fancyPet_2.getProperties().containsKey('name')
      fancyPet_2.getProperties().containsKey('color')
      fancyPet_2.getProperties().containsKey('pet_weight')
      fancyPet_2.getProperties().containsKey('extendedCategory')

  }

  def "Test to verify that @JsonView works correctly with DEFAULT_VIEW_INCLUSION (issues #563, #807, #895"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodToTestJsonView', PetWithJsonView)
      ApiListingScanningContext listingContext = apiListingContext(handlerMethod, '/somePath')

    when:
      def modelsMap = sut.read(listingContext)

    then:
      modelsMap.containsKey(resourceGroup)
      def models = modelsMap.get(resourceGroup)

      Model pet_1 = models["PetWithJsonView_1"]
      Model pet_2 = models["PetWithJsonView_2"]

      models.size() == 2
    and:
      pet_1 != null
      pet_2 != null
    and:
      pet_1.getProperties().size() == 2
      pet_1.getProperties().containsKey('age')
      pet_1.getProperties().containsKey('color')
    and:
      pet_2.getProperties().size() == 3
      pet_2.getProperties().containsKey('id')
      pet_2.getProperties().containsKey('name')
      pet_2.getProperties().containsKey('age')

  }

  def "Test to verify that @JsonView works correctly without DEFAULT_VIEW_INCLUSION (issues #563, #807, #895"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodToTestJsonView', PetWithJsonView)
      ApiListingScanningContext listingContext = apiListingContext(handlerMethod, '/somePath')

    when:
      def modelsMap = sutSpecial.read(listingContext)

    then:
      modelsMap.containsKey(resourceGroup)
      def models = modelsMap.get(resourceGroup)

      Model pet_1 = models["PetWithJsonView_1"]
      Model pet_2 = models["PetWithJsonView_2"]

      models.size() == 2
    and:
      pet_1 != null
      pet_2 != null
    and:
      pet_1.getProperties().size() == 1
      pet_1.getProperties().containsKey('color')
    
    and:
      pet_2.getProperties().size() == 2
      pet_2.getProperties().containsKey('id')
      pet_2.getProperties().containsKey('name')

  }
}
