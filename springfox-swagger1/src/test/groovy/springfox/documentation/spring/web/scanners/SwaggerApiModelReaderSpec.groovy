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

package springfox.documentation.spring.web.scanners
import com.fasterxml.classmate.TypeResolver
import com.google.common.base.Function
import com.google.common.collect.Maps
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import org.springframework.plugin.core.OrderAwarePluginRegistry
import org.springframework.plugin.core.PluginRegistry
import org.springframework.web.method.HandlerMethod
import springfox.documentation.schema.DefaultTypeNameProvider
import springfox.documentation.schema.JacksonEnumTypeDeterminer
import springfox.documentation.schema.Model
import springfox.documentation.schema.ModelProperty
import springfox.documentation.schema.TypeNameExtractor
import springfox.documentation.schema.TypeNameIndexingAdapter
import springfox.documentation.service.ResourceGroup
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.UniqueTypeNameAdapter;
import springfox.documentation.spi.schema.TypeNameProviderPlugin
import springfox.documentation.spi.service.contexts.Defaults
import springfox.documentation.spi.service.contexts.RequestMappingContext
import springfox.documentation.spring.web.WebMvcRequestHandler
import springfox.documentation.spring.web.dummy.DummyClass
import springfox.documentation.spring.web.dummy.DummyModels
import springfox.documentation.spring.web.dummy.controllers.BusinessService
import springfox.documentation.spring.web.mixins.ModelProviderForServiceSupport
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager
import springfox.documentation.swagger.mixins.SwaggerPluginsSupport
import springfox.documentation.swagger1.web.SwaggerDefaultConfiguration

import javax.servlet.ServletContext
import javax.servlet.http.HttpServletResponse

import static com.google.common.collect.Maps.newHashMap;

@Mixin([RequestMappingSupport, ModelProviderForServiceSupport, SwaggerPluginsSupport])
class SwaggerApiModelReaderSpec extends DocumentationContextSpec {

  ApiModelReader sut
  ResourceGroup resourceGroup;
  DocumentationPluginsManager pluginsManager

  def setup() {
    pluginsManager = swaggerServicePlugins([new SwaggerDefaultConfiguration(new Defaults(), new TypeResolver(),
            Mock(ServletContext))])
    PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
        OrderAwarePluginRegistry.create(
            [new DefaultTypeNameProvider()])
    TypeNameExtractor typeNameExtractor = new TypeNameExtractor(
        new TypeResolver(),
        modelNameRegistry,
        new JacksonEnumTypeDeterminer())
    sut = new ApiModelReader(modelProvider(swaggerSchemaPlugins()), new TypeResolver(), pluginsManager, typeNameExtractor)
    resourceGroup = new ResourceGroup("businesses", DummyClass)
  }

  def "Annotated model"() {
    given:
      def listingContext = apiListingContext(dummyHandlerMethod('methodWithModelPropertyAnnotations'), '/somePath')
    when:
      def modelsMap = sut.read(listingContext)

    then:
      modelsMap.containsKey(resourceGroup)
      List<Model> modelsList = modelsMap.get(resourceGroup)
      modelsList.size() == 2

      def models = Maps.uniqueIndex(modelsList, new Function<Model,String>() {
        public String apply(Model model) {
            return model.getName();
        }});

      models.containsKey('AnnotatedBusinessModel')
      Model model = models.get('AnnotatedBusinessModel')
      model.id == '-1161799015'
      model.getName() == 'AnnotatedBusinessModel'
      model.getQualifiedType() == 'springfox.documentation.spring.web.dummy.DummyModels$AnnotatedBusinessModel'

      Map<String, ModelProperty> modelProps = model.getProperties()
      ModelProperty prop = modelProps.name
      prop.type.erasedType == String
      prop.getDescription() == 'The name of this business'
      prop.isRequired()

      modelProps.numEmployees.getDescription() == 'Total number of current employees'
      !modelProps.numEmployees.isRequired()
  }

  def "Should pull models from Api Operation response class"() {
    given:
      def listingContext = apiListingContext(dummyHandlerMethod('methodApiResponseClass'), '/somePath')
    when:
      def modelsMap = sut.read(listingContext)

    then:
      modelsMap.containsKey(resourceGroup)
      List<Model> modelsList = modelsMap.get(resourceGroup)
      modelsList.size() == 2

      def models = Maps.uniqueIndex(modelsList, new Function<Model,String>() {
        public String apply(Model model) {
            return model.getName();
        }});

      models['FunkyBusiness'].getQualifiedType() == 'springfox.documentation.spring.web.dummy.DummyModels$FunkyBusiness'
  }

  def "Should pull models from operation's ApiResponse annotations"() {
    given:
     def listingContext = apiListingContext(dummyHandlerMethod('methodAnnotatedWithApiResponse'), '/somePath')
    when:
      def modelsMap = sut.read(listingContext)
    then:
      modelsMap.containsKey(resourceGroup)
      List<Model> modelsList = modelsMap.get(resourceGroup)
      modelsList.size() == 1

      def models = Maps.uniqueIndex(modelsList, new Function<Model,String>() {
        public String apply(Model model) {
            return model.getName();
        }});

      models.size() == 1
      models['RestError'].getQualifiedType() == 'springfox.documentation.spring.web.dummy.RestError'
  }

  def apiListingContext(HandlerMethod handlerMethod, String path) {
    def requestMappingContext = new RequestMappingContext(
        context(),
        new WebMvcRequestHandler(requestMappingInfo(path), handlerMethod),
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
      def listingContext = apiListingContext(handlerMethod, '/somePath')
    when:
      def modelsMap = sut.read(listingContext)

    then:
      modelsMap.containsKey(resourceGroup)
      List<Model> modelsList = modelsMap.get(resourceGroup)
      modelsList.size() == 2

      def models = Maps.uniqueIndex(modelsList, new Function<Model,String>() {
        public String apply(Model model) {
            return model.getName();
        }});
    
      models.size() == 2 // instead of 3
      models.containsKey("BusinessModel")
      models.containsKey("RestError") // from class-level annotation.

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
      def listingContext = apiListingContext(handlerMethod, '/businesses/responseEntity/{businessId}')
    when:
      def modelsMap = sut.read(listingContext)

    then:
      modelsMap.containsKey(resourceGroup)
      List<Model> modelsList = modelsMap.get(resourceGroup)
      modelsList.size() == 0

      def models = Maps.uniqueIndex(modelsList, new Function<Model,String>() {
        public String apply(Model model) {
            return model.getName();
        }});
    
      models.size() == 0

  }

  def "property description should be populated when type is used in response and request body"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodWithSameAnnotatedModelInReturnAndRequestBodyParam',
              DummyModels.AnnotatedBusinessModel
      )
      def listingContext = apiListingContext(handlerMethod, '/businesses/responseEntity/{businessId}')

    when:
      def modelsMap = sut.read(listingContext)

    then:
      modelsMap.containsKey(resourceGroup)
      List<Model> modelsList = modelsMap.get(resourceGroup)
      modelsList.size() == 3

      def models = newHashMap();
      for (Model model: modelsList) {
        models.put(model.getName(), model);
      }

      models.size() == 2
      models.containsKey('RestError') // from class-level annotation.

      String modelName = DummyModels.AnnotatedBusinessModel.class.simpleName

      models.containsKey(modelName)

      Model model = models[modelName]

      Map modelProperties = model.getProperties()

      modelProperties.size() == 2
      modelProperties.containsKey('name')
      modelProperties.containsKey('numEmployees')

      ModelProperty nameProperty = modelProperties['name']
      nameProperty .getDescription().equals('The name of this business')
      
      ModelProperty numEmployeesProperty = modelProperties['numEmployees']
      numEmployeesProperty .getDescription().equals('Total number of current employees')

  }

}
