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
import com.google.common.collect.Maps;
import java.util.function.Consumer
import com.google.common.base.Function
import com.google.common.collect.FluentIterable;
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
import springfox.documentation.schema.ModelReference
import springfox.documentation.schema.TypeNameExtractor
import springfox.documentation.schema.mixins.SchemaPluginsSupport
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
import springfox.documentation.spring.web.dummy.models.RecursiveTypeWithConditions
import springfox.documentation.spring.web.dummy.models.RecursiveTypeWithNonEqualsConditionsOuter
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

@Mixin([RequestMappingSupport, ModelProviderForServiceSupport, ServicePluginsSupport, SchemaPluginsSupport])
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

  def requestMappingContext(HandlerMethod handlerMethod, String path, String paramId = "0") {
    return new RequestMappingContext(
        paramId,
        documentationContext(),
        new WebMvcRequestHandler(methodResolver, requestMappingInfo('/somePath'),
            handlerMethod))
  }

  def Function<Model, String> toModelMap = new Function<Model, String>(){
          public String apply(Model model) {
              return model.getName();
          }};

  def "Method return type model"() {
    given:
      RequestMappingContext context = requestMappingContext(dummyHandlerMethod('methodWithConcreteResponseBody'), '/somePath')
    when:
      def modelsMap = sut.read(context)
    then:
      modelsMap.containsKey("0_0")
      Map<String, Model> models = Maps.uniqueIndex(modelsMap.get("0_0"), toModelMap)

      models.size() == 1
      Model model = models.get('BusinessModel')
      model.id == '0_0_springfox.documentation.spring.web.dummy.DummyModels$BusinessModel'
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

  def "should only generate models for request parameters that are annotated with Springs RequestBody"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodParameterWithRequestBodyAnnotation',
              DummyModels.BusinessModel,
              HttpServletResponse.class,
              DummyModels.AnnotatedBusinessModel.class
      )
      RequestMappingContext context = requestMappingContext(handlerMethod, '/somePath')
    when:
      def modelsMap = sut.read(context)

    then:
      modelsMap.containsKey("0_1")
      Map<String, Model> models = Maps.uniqueIndex(modelsMap.get("0_1"), toModelMap)

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
      RequestMappingContext context = requestMappingContext(handlerMethod, '/somePath')
    when:
      def modelsMap = sut.read(context)

    then:
      modelsMap.containsKey("0_1")
      Map<String, Model> models = Maps.uniqueIndex(modelsMap.get("0_1"), toModelMap)

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
      RequestMappingContext context = requestMappingContext(handlerMethod, '/somePath')
    when:
      def modelsMap = sut.read(context)

    then:
      modelsMap.isEmpty() // instead of 3

  }

  @Ignore("This needs to move to a swagger 1.2 test")
  def "Generates the correct models when there is a Map object in the input parameter"() {
    given:
      HandlerMethod handlerMethod = handlerMethodIn(PetService, 'echo', Map)
      RequestMappingContext context = requestMappingContext(handlerMethod)

    when:
      def modelsMap = sut.read(context)

    then:
      modelsMap.containsKey("0")
      Map<String, Model> models = Maps.uniqueIndex(modelsMap.get("0"), toModelMap)
    
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
      RequestMappingContext context = requestMappingContext(handlerMethod, '/businesses/responseEntity/{businessId}')
     
    when:
      def modelsMap = sut.read(context)

    then:
      modelsMap.isEmpty()

  }

  def "model should include property that is only visible during serialization"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodWithSerializeOnlyPropInReturnAndRequestBodyParam',
              DummyModels.ModelWithSerializeOnlyProperty
      )
      RequestMappingContext context = requestMappingContext(handlerMethod, '/somePath')

    when:
      def modelsMap = sut.read(context)

    then:
      modelsMap.containsKey("0_0")
      modelsMap.containsKey("0_1")
      Map<String, Model> models_1 = Maps.uniqueIndex(modelsMap.get("0_0"), toModelMap)
      Map<String, Model> models_2 = Maps.uniqueIndex(modelsMap.get("0_1"), toModelMap)

      models_1.size() == 1
      models_2.size() == 1

      String baseModelName = DummyModels.ModelWithSerializeOnlyProperty.class.simpleName

      models_1.containsKey(baseModelName)
      models_2.containsKey(baseModelName + '_1')

      Model model_1 = models_1.get(baseModelName)

      Map modelProperties_1 = model_1.getProperties()

      modelProperties_1.size() == 2
      modelProperties_1.containsKey('visibleForSerialize')
      modelProperties_1.containsKey('alwaysVisible')

      Model model_2 = models_2.get(baseModelName + '_1')

      Map modelProperties_2 = model_2.getProperties()

      modelProperties_2.size() == 1
      modelProperties_2.containsKey('alwaysVisible')

  }

  def "model should include snake_case property that is only visible during serialization when objectMapper has CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodWithSerializeOnlyPropInReturnAndRequestBodyParam',
              DummyModels.ModelWithSerializeOnlyProperty
      )
      RequestMappingContext context = requestMappingContext(handlerMethod, '/somePath')
    and:
      def snakeCaseReader = new ApiModelReader(modelProviderWithSnakeCaseNamingStrategy(),
              new TypeResolver(), defaultWebPlugins(), new JacksonEnumTypeDeterminer(), typeNameExtractor)
    when:
      def modelsMap = snakeCaseReader.read(context)

    then:    
      modelsMap.containsKey("0_0")
      modelsMap.containsKey("0_1")
      Map<String, Model> models_1 = Maps.uniqueIndex(modelsMap.get("0_0"), toModelMap)
      Map<String, Model> models_2 = Maps.uniqueIndex(modelsMap.get("0_1"), toModelMap)

      models_1.size() == 1
      models_2.size() == 1

      String baseModelName = DummyModels.ModelWithSerializeOnlyProperty.class.simpleName

      models_1.containsKey(baseModelName)
      models_2.containsKey(baseModelName + '_1')

      Model model_1 = models_1[baseModelName]

      Map modelProperties_1 = model_1.getProperties()

      modelProperties_1.size() == 2
      modelProperties_1.containsKey('visible_for_serialize')
      modelProperties_1.containsKey('always_visible')
      
      Model model_2 = models_2[baseModelName + '_1']

      Map modelProperties_2 = model_2.getProperties()

      modelProperties_2.size() == 1
      modelProperties_2.containsKey('always_visible')

  }

  def "Test to verify issue #283"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodToTestFoobarDto', FoobarDto)
      RequestMappingContext context = requestMappingContext(handlerMethod, '/somePath')

    when:
      def modelsMap = sut.read(context)

    then:
      modelsMap.containsKey("0_0")
      modelsMap.containsKey("0_1")
      Map<String, Model> models_1 = Maps.uniqueIndex(modelsMap.get("0_0"), toModelMap)
      Map<String, Model> models_2 = Maps.uniqueIndex(modelsMap.get("0_1"), toModelMap)

      models_1.size() == 1
      models_2.size() == 1

      String baseModelName = FoobarDto.simpleName

      models_1.containsKey(baseModelName)
      models_2.containsKey(baseModelName + '_1')

      Model model_1 = models_1[baseModelName]

      Map modelProperties_1 = model_1.getProperties()
      
      modelProperties_1.size() == 2
      modelProperties_1.containsKey('visibleForSerialize')
      modelProperties_1.containsKey('foobar')

      Model model_2 = models_2[baseModelName + '_1']

      Map modelProperties_2 = model_2.getProperties()

      modelProperties_2.size() == 1
      modelProperties_2.containsKey('foobar')

  }

  def "Test to verify issue #1196"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodToTestBidrectionalRecursiveTypes', Pirate)
      RequestMappingContext context = requestMappingContext(handlerMethod, '/somePath')

    when:
      def modelsMap = sut.read(context)

    then:
      modelsMap.containsKey("0_1")
      modelsMap.containsKey("0_0")
      Map<String, Model> models_1 = Maps.uniqueIndex(modelsMap.get("0_0"), toModelMap)
      Map<String, Model> models_2 = Maps.uniqueIndex(modelsMap.get("0_1"), toModelMap)
    and:  
      Model pirate_1 = models_1[Pirate.simpleName]
      Model monkey_1 = models_1[Monkey.simpleName]
    and:
      models_1.size() == 2
    and:
      pirate_1 != null
      monkey_1 != null
    and:
      pirate_1.getProperties().containsKey('monkey')
      ModelReference modelRef_1 = pirate_1.getProperties().get('monkey').getModelRef()
      modelRef_1.getModelId().get().equals("0_0_springfox.documentation.spring.web.dummy.models.Monkey")

      monkey_1.getProperties().containsKey('pirate')
      ModelReference modelRef_2 = monkey_1.getProperties().get('pirate').getModelRef()
      modelRef_2.getModelId().get().equals("0_0_springfox.documentation.spring.web.dummy.models.Pirate")

    and:
      Model pirate_2 = models_2[Pirate.simpleName]
      Model monkey_2 = models_2[Monkey.simpleName]
    and:
      models_2.size() == 2
    and:
      pirate_2 != null
      monkey_2 != null
    and:
      pirate_2.getProperties().containsKey('monkey')
      ModelReference modelRef_3 = pirate_2.getProperties().get('monkey').getModelRef()
      modelRef_3.getModelId().get().equals("0_1_springfox.documentation.spring.web.dummy.models.Monkey")

      monkey_2.getProperties().containsKey('pirate')
      ModelReference modelRef_4 = monkey_2.getProperties().get('pirate').getModelRef()
      modelRef_4.getModelId().get().equals("0_1_springfox.documentation.spring.web.dummy.models.Pirate")

  }

  def "Test to verify that recursive type same for serialization and deserialization"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodToTestBidrectionalRecursiveTypesWithConditions',
            RecursiveTypeWithConditions)
      RequestMappingContext context = requestMappingContext(handlerMethod, '/somePath')

    when:
      def modelsMap = sut.read(context)

    then:
      modelsMap.containsKey("0_1")
      modelsMap.containsKey("0_0")
      Map<String, Model> models_1 = Maps.uniqueIndex(modelsMap.get("0_0"), toModelMap)
      Map<String, Model> models_2 = Maps.uniqueIndex(modelsMap.get("0_1"), toModelMap)

    and:
      Model recursiveTypeWithConditions_1 = models_1[RecursiveTypeWithConditions.simpleName]
      Model monkey_1 = models_1[Monkey.simpleName]
      Model pirate_1 = models_1[Pirate.simpleName]
    and:
      models_1.size() == 3
    and:
      recursiveTypeWithConditions_1 != null
      pirate_1 != null
      monkey_1 != null
    and:
      recursiveTypeWithConditions_1.getProperties().containsKey('monkey')
      ModelReference modelRef_1 = recursiveTypeWithConditions_1.getProperties().get('monkey').getModelRef()
      modelRef_1.getModelId().get().equals("0_0_springfox.documentation.spring.web.dummy.models.Monkey")
      modelRef_1.getType().equals('Monkey')

      monkey_1.getProperties().containsKey('pirate')
      ModelReference modelRef_2 = monkey_1.getProperties().get('pirate').getModelRef()
      modelRef_2.getModelId().get().equals("0_0_springfox.documentation.spring.web.dummy.models.Pirate")
      modelRef_2.getType().equals('Pirate')

      pirate_1.getProperties().containsKey('monkey')
      ModelReference modelRef_3 = pirate_1.getProperties().get('monkey').getModelRef()
      modelRef_3.getModelId().get().equals("0_0_springfox.documentation.spring.web.dummy.models.Monkey")
      modelRef_3.getType().equals('Monkey')

    and:
      Model recursiveTypeWithConditions_2 = models_2[RecursiveTypeWithConditions.simpleName + '_1']
      Model monkey_2 = models_2[Monkey.simpleName]
      Model pirate_2 = models_2[Pirate.simpleName]
    and:
      models_2.size() == 3
    and:
      recursiveTypeWithConditions_2 != null
      pirate_2 != null
      monkey_2 != null
    and:
      recursiveTypeWithConditions_2.getProperties().containsKey('monkey')
      ModelReference modelRef_4 = recursiveTypeWithConditions_2.getProperties().get('monkey').getModelRef()
      modelRef_4.getModelId().get().equals("0_1_springfox.documentation.spring.web.dummy.models.Monkey")
      modelRef_4.getType().equals('Monkey')

      recursiveTypeWithConditions_2.getProperties().containsKey('conditionalProperty')

      monkey_2.getProperties().containsKey('pirate')
      ModelReference modelRef_5 = monkey_2.getProperties().get('pirate').getModelRef()
      modelRef_5.getModelId().get().equals("0_1_springfox.documentation.spring.web.dummy.models.Pirate")
      modelRef_5.getType().equals('Pirate')

      pirate_2.getProperties().containsKey('monkey')
      ModelReference modelRef_6 = pirate_2.getProperties().get('monkey').getModelRef()
      modelRef_6.getModelId().get().equals("0_1_springfox.documentation.spring.web.dummy.models.Monkey")
      modelRef_6.getType().equals('Monkey')

  }

  def "Test to verify that recursive type different for serialization and deserialization"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodToTestBidrectionalRecursiveTypesWithNonEqualsConditions',
            RecursiveTypeWithNonEqualsConditionsOuter)
      RequestMappingContext context = requestMappingContext(handlerMethod, '/somePath')

    when:
      def modelsMap = sut.read(context)

    then:
      modelsMap.containsKey("0_1")
      modelsMap.containsKey("0_0")
      Map<String, Model> models_1 = Maps.uniqueIndex(modelsMap.get("0_0"), toModelMap)
      Map<String, Model> models_2 = Maps.uniqueIndex(modelsMap.get("0_1"), toModelMap)

    and:
      Model recursiveTypeWithConditionsOuter_1 = models_1['RecursiveTypeWithNonEqualsConditionsOuter']
      Model recursiveTypeWithConditionsMiddle_1 = models_1['RecursiveTypeWithNonEqualsConditionsMiddle']
      Model recursiveTypeWithConditionsInner_1 = models_1['RecursiveTypeWithNonEqualsConditionsInner']
    and:
      models_1.size() == 3
    and:
      recursiveTypeWithConditionsOuter_1 != null
      recursiveTypeWithConditionsMiddle_1 != null
      recursiveTypeWithConditionsInner_1 != null

    and:
      recursiveTypeWithConditionsOuter_1.getProperties().size() == 1

      recursiveTypeWithConditionsOuter_1.getProperties().containsKey('recursiveTypeWithNonEqualsConditionsMiddle')
      ModelReference modelRef_1 = recursiveTypeWithConditionsOuter_1.getProperties()
            .get('recursiveTypeWithNonEqualsConditionsMiddle').getModelRef()
      modelRef_1.getModelId().get()
            .equals("0_0_springfox.documentation.spring.web.dummy.models.RecursiveTypeWithNonEqualsConditionsMiddle")
      modelRef_1.getType().equals('RecursiveTypeWithNonEqualsConditionsMiddle')

      recursiveTypeWithConditionsMiddle_1.getProperties().containsKey('recursiveTypeWithNonEqualsConditionsInner')
      ModelReference modelRef_2 = recursiveTypeWithConditionsMiddle_1.getProperties()
            .get('recursiveTypeWithNonEqualsConditionsInner').getModelRef()
      modelRef_2.getModelId().get()
           .equals("0_0_springfox.documentation.spring.web.dummy.models.RecursiveTypeWithNonEqualsConditionsInner")
      modelRef_2.getType().equals('RecursiveTypeWithNonEqualsConditionsInner')

      recursiveTypeWithConditionsInner_1.getProperties().containsKey('recursiveTypeWithNonEqualsConditionsOuter')
      ModelReference modelRef_3 = recursiveTypeWithConditionsInner_1.getProperties()
           .get('recursiveTypeWithNonEqualsConditionsOuter').getModelRef()
      modelRef_3.getModelId().get()
            .equals("0_0_springfox.documentation.spring.web.dummy.models.RecursiveTypeWithNonEqualsConditionsOuter")
      modelRef_3.getType().equals('RecursiveTypeWithNonEqualsConditionsOuter')

    and:
      Model recursiveTypeWithConditionsOuter_2 = models_2['RecursiveTypeWithNonEqualsConditionsOuter_1']
      Model recursiveTypeWithConditionsMiddle_2 = models_2['RecursiveTypeWithNonEqualsConditionsMiddle_1']
      Model recursiveTypeWithConditionsInner_2 = models_2['RecursiveTypeWithNonEqualsConditionsInner_1']
    and:
      models_2.size() == 3
    and:
      recursiveTypeWithConditionsOuter_2 != null
      recursiveTypeWithConditionsMiddle_2 != null
      recursiveTypeWithConditionsInner_2 != null

    and:
      recursiveTypeWithConditionsOuter_2.getProperties().size() == 2

      recursiveTypeWithConditionsOuter_2.getProperties().containsKey('recursiveTypeWithNonEqualsConditionsMiddle')
      ModelReference modelRef_4 = recursiveTypeWithConditionsOuter_2.getProperties()
            .get('recursiveTypeWithNonEqualsConditionsMiddle').getModelRef()
      modelRef_4.getModelId().get()
            .equals("0_1_springfox.documentation.spring.web.dummy.models.RecursiveTypeWithNonEqualsConditionsMiddle")
      modelRef_4.getType().equals('RecursiveTypeWithNonEqualsConditionsMiddle_1')

      recursiveTypeWithConditionsMiddle_2.getProperties().containsKey('recursiveTypeWithNonEqualsConditionsInner')
      ModelReference modelRef_5 = recursiveTypeWithConditionsMiddle_2.getProperties()
            .get('recursiveTypeWithNonEqualsConditionsInner').getModelRef()
      modelRef_5.getModelId().get()
           .equals("0_1_springfox.documentation.spring.web.dummy.models.RecursiveTypeWithNonEqualsConditionsInner")
      modelRef_5.getType().equals('RecursiveTypeWithNonEqualsConditionsInner_1')

      recursiveTypeWithConditionsInner_2.getProperties().containsKey('recursiveTypeWithNonEqualsConditionsOuter')
      ModelReference modelRef_6 = recursiveTypeWithConditionsInner_2.getProperties()
           .get('recursiveTypeWithNonEqualsConditionsOuter').getModelRef()
      modelRef_6.getModelId().get()
            .equals("0_1_springfox.documentation.spring.web.dummy.models.RecursiveTypeWithNonEqualsConditionsOuter")
      modelRef_6.getType().equals('RecursiveTypeWithNonEqualsConditionsOuter_1')
  }

  def "Test to verify that recursive type asame with known types"() {
    given:
      HandlerMethod handlerMethodFirst = dummyHandlerMethod('methodToTestBidrectionalRecursiveTypesWithConditions',
            RecursiveTypeWithConditions)
      RequestMappingContext contextFirst = requestMappingContext(handlerMethodFirst, '/somePath', '0')

      HandlerMethod handlerMethodSecond = dummyHandlerMethod('methodToTestBidrectionalRecursiveTypesWithNonEqualsConditions',
            RecursiveTypeWithNonEqualsConditionsOuter)
      RequestMappingContext contextSecond = requestMappingContext(handlerMethodSecond, '/somePath', '1')

      HandlerMethod handlerMethodThird = dummyHandlerMethod('methodToTestBidrectionalRecursiveTypesWithKnownTypes',
           RecursiveTypeWithConditions)
      RequestMappingContext contextThird = requestMappingContext(handlerMethodThird, '/somePath', "2")

    when:
      def modelsMap = newHashMap(sut.read(contextFirst))
      modelsMap.putAll(sut.read(contextSecond.withKnownModels(modelsMap)))
      modelsMap.putAll(sut.read(contextThird.withKnownModels(modelsMap)))

    then:
      modelsMap.containsKey("0_1")
      modelsMap.containsKey("0_0")
      Map<String, Model> models_1 = Maps.uniqueIndex(modelsMap.get("0_0"), toModelMap)
      Map<String, Model> models_2 = Maps.uniqueIndex(modelsMap.get("0_1"), toModelMap)
   
      modelsMap.containsKey("1_1")
      modelsMap.containsKey("1_0")
      Map<String, Model> models_3 = Maps.uniqueIndex(modelsMap.get("1_0"), toModelMap)
      Map<String, Model> models_4 = Maps.uniqueIndex(modelsMap.get("1_1"), toModelMap)

      modelsMap.containsKey("2_1")
      modelsMap.containsKey("2_0")
      Map<String, Model> models_5 = Maps.uniqueIndex(modelsMap.get("2_0"), toModelMap)
      Map<String, Model> models_6 = Maps.uniqueIndex(modelsMap.get("2_1"), toModelMap)

    and:
      Model recursiveTypeWithConditions_1 = models_1[RecursiveTypeWithConditions.simpleName]
      Model monkey_1 = models_1[Monkey.simpleName]
      Model pirate_1 = models_1[Pirate.simpleName]
      Model recursiveTypeWithConditions_2 = models_2[RecursiveTypeWithConditions.simpleName + '_1']
      Model monkey_2 = models_2[Monkey.simpleName]
      Model pirate_2 = models_2[Pirate.simpleName]

      Model recursiveTypeWithConditionsOuter_1 = models_3['RecursiveTypeWithNonEqualsConditionsOuter']
      Model recursiveTypeWithConditionsMiddle_1 = models_3['RecursiveTypeWithNonEqualsConditionsMiddle']
      Model recursiveTypeWithConditionsInner_1 = models_3['RecursiveTypeWithNonEqualsConditionsInner']
      Model recursiveTypeWithConditionsOuter_2 = models_4['RecursiveTypeWithNonEqualsConditionsOuter_1']
      Model recursiveTypeWithConditionsMiddle_2 = models_4['RecursiveTypeWithNonEqualsConditionsMiddle_1']
      Model recursiveTypeWithConditionsInner_2 = models_4['RecursiveTypeWithNonEqualsConditionsInner_1']

      Model recursiveTypeWithConditionsOuter_3 = models_5['RecursiveTypeWithNonEqualsConditionsOuter']
      Model recursiveTypeWithConditionsMiddle_3 = models_5['RecursiveTypeWithNonEqualsConditionsMiddle']
      Model recursiveTypeWithConditionsInner_3 = models_5['RecursiveTypeWithNonEqualsConditionsInner']
      Model recursiveTypeWithNonEqualsConditionsOuterWithSubTypes = 
                                       models_5["RecursiveTypeWithNonEqualsConditionsOuterWithSubTypes"]
      Model recursiveTypeWithConditions_3 = models_5[RecursiveTypeWithConditions.simpleName]
      Model monkey_3 = models_5[Monkey.simpleName]
      Model pirate_3 = models_5[Pirate.simpleName]

      Model recursiveTypeWithConditions_4 = models_6[RecursiveTypeWithConditions.simpleName + '_1']
      Model monkey_4 = models_6[Monkey.simpleName]
      Model pirate_4 = models_6[Pirate.simpleName]
    and:
      models_1.size() == 3
      models_2.size() == 3

      models_3.size() == 3
      models_4.size() == 3

      models_5.size() == 7
      models_6.size() == 3
    and:
      recursiveTypeWithConditions_1 != null
      pirate_1 != null
      monkey_1 != null
      recursiveTypeWithConditions_2 != null
      pirate_2 != null
      monkey_2 != null

      recursiveTypeWithConditionsOuter_1 != null
      recursiveTypeWithConditionsMiddle_1 != null
      recursiveTypeWithConditionsInner_1 != null
      recursiveTypeWithConditionsOuter_2 != null
      recursiveTypeWithConditionsMiddle_2 != null
      recursiveTypeWithConditionsInner_2 != null

      recursiveTypeWithConditionsOuter_3 != null
      recursiveTypeWithConditionsMiddle_3 != null
      recursiveTypeWithConditionsInner_3 != null
      recursiveTypeWithNonEqualsConditionsOuterWithSubTypes != null
      recursiveTypeWithConditions_3 != null
      pirate_3 != null
      monkey_3 != null

      recursiveTypeWithConditions_4 != null
      pirate_4 != null
      monkey_4 != null

    and:
      recursiveTypeWithConditionsOuter_3.equalsIgnoringName(recursiveTypeWithConditionsOuter_1)
      recursiveTypeWithConditionsMiddle_3.equalsIgnoringName(recursiveTypeWithConditionsMiddle_1)
      recursiveTypeWithConditionsInner_3.equalsIgnoringName(recursiveTypeWithConditionsInner_1)
      recursiveTypeWithNonEqualsConditionsOuterWithSubTypes.getProperties().size() == 1
      recursiveTypeWithNonEqualsConditionsOuterWithSubTypes.getSubTypes().size() == 1
      ModelReference modelRef = recursiveTypeWithNonEqualsConditionsOuterWithSubTypes.getSubTypes().get(0)
      modelRef.getModelId().get()
            .equals("2_0_springfox.documentation.spring.web.dummy.models.RecursiveTypeWithConditions")
      modelRef.getType().equals('RecursiveTypeWithConditions')
      recursiveTypeWithConditions_3.equalsIgnoringName(recursiveTypeWithConditions_1)

      recursiveTypeWithConditions_4.equalsIgnoringName(recursiveTypeWithConditions_2)
      pirate_3.equalsIgnoringName(pirate_1) && pirate_3.equalsIgnoringName(pirate_2)
      pirate_4.equalsIgnoringName(pirate_1) && pirate_4.equalsIgnoringName(pirate_2)
      monkey_3.equalsIgnoringName(monkey_1) && monkey_3.equalsIgnoringName(monkey_2)
      monkey_4.equalsIgnoringName(monkey_1) && monkey_4.equalsIgnoringName(monkey_2)
  }

  def "Test to verify that duplicate class names in different packages will be prodused as different models (#182)"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodToTestIssue182', Pet)
      RequestMappingContext context = requestMappingContext(handlerMethod, '/somePath')

    when:
      def modelsMap = sut.read(context)

    then:
      modelsMap.containsKey("0_0")
      modelsMap.containsKey("0_1")
      Map<String, Model> models_1 = Maps.uniqueIndex(modelsMap.get("0_0"), toModelMap)
      Map<String, Model> models_2 = Maps.uniqueIndex(modelsMap.get("0_1"), toModelMap)

      Model pet_1 = models_1["Pet"]
      Model pet_2 = models_2["Pet_1"]

      models_1.size() == 1
      models_2.size() == 1
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

  def "Test to verify that same class for serialization and deserialization will be produced as one model"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodToTestSerializationAndDeserialization', 
              Map)
      RequestMappingContext context = requestMappingContext(handlerMethod, '/somePath')

    when:
      def modelsMap = sut.read(context)

    then:
      modelsMap.containsKey("0_0")
      modelsMap.containsKey("0_1")
      Map<String, Model> models_1 = Maps.uniqueIndex(modelsMap.get("0_0"), toModelMap)
      Map<String, Model> models_2 = Maps.uniqueIndex(modelsMap.get("0_1"), toModelMap)

      Model fancyPet_1 = models_1["FancyPet"]
      Model category_1 = models_1["Category"]

      Model fancyPet_2 = models_2["FancyPet"]
      Model category_2 = models_2["Category"]

      models_1.size() == 2
      models_2.size() == 2
    and:
      fancyPet_1 != null
      fancyPet_1.qualifiedType.equals("springfox.documentation.spring.web.dummy.models.FancyPet");

      category_1 != null
      category_1.qualifiedType.equals("springfox.documentation.spring.web.dummy.models.Category");
    and:
      fancyPet_1.getProperties().size() == 4
      fancyPet_1.getProperties().containsKey('id')
      fancyPet_1.getProperties().containsKey('name')
      fancyPet_1.getProperties().containsKey('age')
      fancyPet_1.getProperties().containsKey('categories')
    and:
      ModelReference modelRef_1 = fancyPet_1.getProperties().get('categories').getModelRef()
      modelRef_1.isCollection();
      modelRef_1.itemModel().get()
          .getModelId().get().equals("0_0_springfox.documentation.spring.web.dummy.models.Category")
    and:
      category_1.getProperties().size() == 1
      category_1.getProperties().containsKey('name')

    and:
      fancyPet_2 != null
      fancyPet_1.qualifiedType.equals("springfox.documentation.spring.web.dummy.models.FancyPet");

      category_2 != null
      category_1.qualifiedType.equals("springfox.documentation.spring.web.dummy.models.Category");
    and:
      fancyPet_2.getProperties().size() == 4
      fancyPet_2.getProperties().containsKey('id')
      fancyPet_2.getProperties().containsKey('name')
      fancyPet_2.getProperties().containsKey('age')
      fancyPet_2.getProperties().containsKey('categories')
    and:
      ModelReference modelRef_2 = fancyPet_2.getProperties().get('categories').getModelRef()
      modelRef_2.isCollection();
      modelRef_2.itemModel().get()
          .getModelId().get().equals("0_1_springfox.documentation.spring.web.dummy.models.Category")
    and:
      category_2.getProperties().size() == 1
      category_2.getProperties().containsKey('name')

  }

  def "Test to verify that different class for serialization and deserialization will be produced as two models"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodToTestSameClassesWithDifferentProperties', SameFancyPet)
      RequestMappingContext context = requestMappingContext(handlerMethod, '/somePath')

    when:
      def modelsMap = sut.read(context)

    then:
      modelsMap.containsKey("0_0")
      modelsMap.containsKey("0_1")
      Map<String, Model> models_1 = Maps.uniqueIndex(modelsMap.get("0_0"), toModelMap)
      Map<String, Model> models_2 = Maps.uniqueIndex(modelsMap.get("0_1"), toModelMap)

      Model category_1 = models_1["SameCategory"]
      Model category_2 = models_2["SameCategory_1"]

      Model mapFancyPet = models_1["MapFancyPet"]

      Model fancyPet_1 = models_1["SameFancyPet"]
      Model fancyPet_2 = models_2["SameFancyPet_1"]

      models_1.size() == 3
      models_2.size() == 2
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
      RequestMappingContext context = requestMappingContext(handlerMethod, '/somePath')

    when:
      def modelsMap = sut.read(context)

    then:
      modelsMap.containsKey("0_0")
      modelsMap.containsKey("0_1")
      Map<String, Model> models_1 = Maps.uniqueIndex(modelsMap.get("0_0"), toModelMap)
      Map<String, Model> models_2 = Maps.uniqueIndex(modelsMap.get("0_1"), toModelMap)

      Model pet_1 = models_1["PetWithJsonView"]
      Model pet_2 = models_2["PetWithJsonView_1"]

      models_1.size() == 1
      models_2.size() == 1
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
      RequestMappingContext context = requestMappingContext(handlerMethod, '/somePath')

    when:
      def modelsMap = sutSpecial.read(context)

    then:
      modelsMap.containsKey("0_0")
      modelsMap.containsKey("0_1")
      Map<String, Model> models_1 = Maps.uniqueIndex(modelsMap.get("0_0"), toModelMap)
      Map<String, Model> models_2 = Maps.uniqueIndex(modelsMap.get("0_1"), toModelMap)

      Model pet_1 = models_1["PetWithJsonView"]
      Model pet_2 = models_2["PetWithJsonView_1"]

      models_1.size() == 1
      models_2.size() == 1
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
