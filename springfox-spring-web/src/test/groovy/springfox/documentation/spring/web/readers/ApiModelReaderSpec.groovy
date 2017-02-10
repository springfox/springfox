/*
 *
 *  Copyright 2015-2016 the original author or authors.
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
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.method.HandlerMethod
import spock.lang.Ignore
import springfox.documentation.schema.Model
import springfox.documentation.schema.ModelProperty
import springfox.documentation.spi.service.contexts.RequestMappingContext
import springfox.documentation.spring.web.WebMvcRequestHandler
import springfox.documentation.spring.web.dummy.DummyModels
import springfox.documentation.spring.web.dummy.controllers.BusinessService
import springfox.documentation.spring.web.dummy.controllers.PetService
import springfox.documentation.spring.web.dummy.models.FoobarDto
import springfox.documentation.spring.web.dummy.models.Monkey
import springfox.documentation.spring.web.dummy.models.Pirate
import springfox.documentation.spring.web.dummy.models.FancyPet
import springfox.documentation.spring.web.mixins.ModelProviderForServiceSupport
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.mixins.ServicePluginsSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager
import springfox.documentation.spring.web.scanners.ApiModelReader

import javax.print.attribute.Size2DSyntax
import javax.servlet.http.HttpServletResponse

@Mixin([RequestMappingSupport, ModelProviderForServiceSupport, ServicePluginsSupport])
class ApiModelReaderSpec extends DocumentationContextSpec {

  ApiModelReader sut
  DocumentationPluginsManager pluginsManager

  def setup() {
    pluginsManager = defaultWebPlugins()
    sut = new ApiModelReader(modelProvider(), new TypeResolver(), pluginsManager)
  }


  def "Method return type model"() {
    given:
      RequestMappingContext context = requestMappingContext(dummyHandlerMethod('methodWithConcreteResponseBody'))

    when:
      def models = sut.read(context)

    then:
      Model model = models['BusinessModel']
      model.id == 'BusinessModel'
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

  def requestMappingContext(HandlerMethod handlerMethod) {
    return new RequestMappingContext(
        context(),
        new WebMvcRequestHandler(requestMappingInfo('/somePath'),
        handlerMethod))
  }

  def "should only generate models for request parameters that are annotated with Springs RequestBody"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodParameterWithRequestBodyAnnotation',
              DummyModels.BusinessModel,
              HttpServletResponse.class,
              DummyModels.AnnotatedBusinessModel.class
      )
      RequestMappingContext context = requestMappingContext(handlerMethod)
    when:
      def models = sut.read(context)

    then:
      models.size() == 1 // instead of 3
      models.containsKey("BusinessModel")

  }


    def "should only generate models for request parameters that are annotated with Springs RequestPart"() {
        given:
        HandlerMethod handlerMethod = dummyHandlerMethod('methodParameterWithRequestPartAnnotation',
                DummyModels.BusinessModel,
                HttpServletResponse.class,
                DummyModels.AnnotatedBusinessModel.class
        )
        RequestMappingContext context = requestMappingContext(handlerMethod)
        when:
        def models = sut.read(context)

        then:
        models.size() == 1 // instead of 3
        models.containsKey("BusinessModel")

    }

    def "should not generate models for simple type request parameters that are annotated with Springs RequestPart"() {
        given:
        HandlerMethod handlerMethod = dummyHandlerMethod('methodParameterWithRequestPartAnnotationOnSimpleType',
                String.class,
                HttpServletResponse.class,
                DummyModels.AnnotatedBusinessModel.class
        )
        RequestMappingContext context = requestMappingContext(handlerMethod)
        when:
        def models = sut.read(context)

        then:
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
      def pluginContext =  contextBuilder.build()
    and:
      HandlerMethod handlerMethod = handlerMethodIn(BusinessService, 'getResponseEntity', String)
      RequestMappingContext context =
              new RequestMappingContext(pluginContext,
                  new WebMvcRequestHandler(
                      requestMappingInfo('/businesses/responseEntity/{businessId}'),
                      handlerMethod))
    when:
      def models = sut.read(context)

    then:
      models.size() == 0

  }

  def "ApiModelReader should considermodels with properties that is only visible during serialization"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodWithSerializeOnlyPropInReturnAndRequestBodyParam',
              DummyModels.ModelWithSerializeOnlyProperty
      )
      RequestMappingContext context = requestMappingContext(handlerMethod)

    when:
      def models = sut.read(context)

    then:
      models.size() == 2

      String modelName = DummyModels.ModelWithSerializeOnlyProperty.class.simpleName
      models.containsKey(modelName)
      models.containsKey(modelName + "_1")

      Model model1 = models[modelName + "_1"]
      Model model2 = models[modelName]
      
      Map modelProperties1 = model1.getProperties()
      Map modelProperties2 = model2.getProperties()
      
      if (modelProperties1.size() == 1) {
        modelProperties1.containsKey('always_visible')
            
        modelProperties2.size() == 2
        modelProperties2.containsKey('visible_for_serialize')
        modelProperties2.containsKey('always_visible')
      } else {
          modelProperties1.containsKey('always_visible')
          modelProperties1.containsKey('visible_for_serialize')
            
          modelProperties2.size() == 1
          modelProperties2.containsKey('always_visible')
      }

  }


  def "ApiModelReader should consider models that include snake_case property and is only visible during serialization when objectMapper has CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodWithSerializeOnlyPropInReturnAndRequestBodyParam',
              DummyModels.ModelWithSerializeOnlyProperty
      )
      RequestMappingContext context = requestMappingContext(handlerMethod)
    and:
      def snakeCaseReader = new ApiModelReader(modelProviderWithSnakeCaseNamingStrategy(),
              new TypeResolver(), defaultWebPlugins())
    when:
      def models = snakeCaseReader.read(context)

    then:
      models.size() == 2

      String modelName = DummyModels.ModelWithSerializeOnlyProperty.class.simpleName
      models.containsKey(modelName)
      models.containsKey(modelName + "_1")

      Model model1 = models[modelName + "_1"]
      Model model2 = models[modelName]
     
      Map modelProperties1 = model1.getProperties()
      Map modelProperties2 = model2.getProperties()
      
      if (modelProperties1.size() == 1) {
        modelProperties1.containsKey('always_visible')
          
        modelProperties2.size() == 2
        modelProperties2.containsKey('visible_for_serialize')
        modelProperties2.containsKey('always_visible')
      } else {
          modelProperties1.containsKey('always_visible')
          modelProperties1.containsKey('visible_for_serialize')
          
          modelProperties2.size() == 1
          modelProperties2.containsKey('always_visible')
      }

  }

  def "Test to verify issue #283"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodToTestFoobarDto', FoobarDto)
      RequestMappingContext context = requestMappingContext(handlerMethod)

    when:
      def models = sut.read(context)

    then:
      models.size() == 2

      String modelName = FoobarDto.simpleName
      models.containsKey(modelName + "_1")
      models.containsKey(modelName)

      Model model1 = models[modelName]
      Model model2 = models[modelName + "_1"]
      Map modelProperties1 = model1.getProperties()
      modelProperties1.containsKey('visibleForSerialize')
      Map modelProperties2 = model2.getProperties()
      !modelProperties2.containsKey('visibleForSerialize')

  }
  def "Test to verify issue #1196"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodToTestBidrectionalRecursiveTypes', Pirate)
      RequestMappingContext context = requestMappingContext(handlerMethod)

    when:
      def models = sut.read(context)
      Model pirate = models[Pirate.simpleName]
      Model monkey = models[Monkey.simpleName]

    then:
      models.size() == 2
    and:
      pirate != null
      monkey != null
    and:
      pirate.getProperties().containsKey('monkey')
      monkey.getProperties().containsKey('pirate')

  }
  def "Test to verify issue #182, #807, #895, #1356"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodToTestSameClassesWithDifferentProperties', FancyPet)
      RequestMappingContext context = requestMappingContext(handlerMethod)
  
    when:
      def models = sut.read(context)
      Model Category = models["Category"]
      Model FancyPet = models["FancyPet"]
        
      Model Category_1 = models["Category_1"]
      Model FancyPet_1 = models["FancyPet_1"]
  
    then:
      models.size() == 5
    and:
      Category != null
      FancyPet != null
        
      Category_1 != null
      FancyPet_1 != null
    and:
      (Category.getProperties().size() == 2 && Category_1.getProperties().size() == 1) ||
      (Category.getProperties().size() == 1 && Category_1.getProperties().size() == 2)
      
      (FancyPet.getProperties().size() == 5 && FancyPet_1.getProperties().size() == 4) ||
      (FancyPet.getProperties().size() == 4 && FancyPet_1.getProperties().size() == 5)
    and:
      if (Category.getProperties().size() == 2) {
        Category.getProperties().containsKey('id')
        Category.getProperties().containsKey('name')
      } else {
          Category.getProperties().containsKey('name')
        }

      if (Category_1.getProperties().size() == 2) {
        Category_1.getProperties().containsKey('id')
        Category_1.getProperties().containsKey('name')
      } else {
          Category_1.getProperties().containsKey('name')
        }
        
      if (FancyPet.getProperties().size() == 5) {
        FancyPet.getProperties().containsKey('id')
        FancyPet.getProperties().containsKey('age')
        FancyPet.getProperties().containsKey('name')
        FancyPet.getProperties().containsKey('categories')
      } else {
          FancyPet.getProperties().containsKey('id')
          FancyPet.getProperties().containsKey('age')
          FancyPet.getProperties().containsKey('name')
          FancyPet.getProperties().containsKey('color')
          FancyPet.getProperties().containsKey('extendedCategories')
        }
    
      if (FancyPet_1.getProperties().size() == 5) {
        FancyPet.getProperties().containsKey('id')
        FancyPet.getProperties().containsKey('age')
        FancyPet.getProperties().containsKey('name')
        FancyPet.getProperties().containsKey('categories')
      } else {
          FancyPet.getProperties().containsKey('id')
          FancyPet.getProperties().containsKey('age')
          FancyPet.getProperties().containsKey('name')
          FancyPet.getProperties().containsKey('color')
          FancyPet.getProperties().containsKey('extendedCategories')
        }

  }
  
}
