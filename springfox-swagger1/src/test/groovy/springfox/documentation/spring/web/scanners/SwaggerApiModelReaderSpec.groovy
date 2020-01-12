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

package springfox.documentation.spring.web.scanners

import com.fasterxml.classmate.TypeResolver
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
import springfox.documentation.schema.mixins.SchemaPluginsSupport
import springfox.documentation.service.ResourceGroup
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.TypeNameProviderPlugin
import springfox.documentation.spi.service.contexts.Defaults
import springfox.documentation.spi.service.contexts.RequestMappingContext
import springfox.documentation.spring.web.WebMvcRequestHandler
import springfox.documentation.spring.web.dummy.DummyClass
import springfox.documentation.spring.web.dummy.DummyModels
import springfox.documentation.spring.web.dummy.controllers.BusinessService
import springfox.documentation.spring.web.mixins.ModelProviderForServiceSupport
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.paths.DefaultPathProvider
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver
import springfox.documentation.swagger.mixins.SwaggerPluginsSupport
import springfox.documentation.swagger1.web.SwaggerDefaultConfiguration

import javax.servlet.http.HttpServletResponse
import java.util.function.Function
import java.util.stream.Collectors

import static springfox.documentation.spring.web.paths.Paths.*

class SwaggerApiModelReaderSpec
    extends DocumentationContextSpec
    implements RequestMappingSupport,
        SwaggerPluginsSupport,
        ModelProviderForServiceSupport {

  ApiModelReader sut
  ResourceGroup resourceGroup
  DocumentationPluginsManager pluginsManager
  def methodResolver = new HandlerMethodResolver(new TypeResolver())

  def setup() {

    pluginsManager = swaggerServicePlugins([
        new SwaggerDefaultConfiguration(
            new Defaults(),
            new TypeResolver(),
            new DefaultPathProvider())
    ])

    PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
        OrderAwarePluginRegistry.create(
            [new DefaultTypeNameProvider()])
    TypeNameExtractor typeNameExtractor = new TypeNameExtractor(
        new TypeResolver(),
        modelNameRegistry,
        new JacksonEnumTypeDeterminer())
    sut = new ApiModelReader(
        modelProvider(swaggerSchemaPlugins()),
        new TypeResolver(),
        pluginsManager,
        new JacksonEnumTypeDeterminer(),
        typeNameExtractor)
    resourceGroup = new ResourceGroup("businesses", DummyClass)
  }

  def requestMappingContext(HandlerMethod handlerMethod, String path) {
    return new RequestMappingContext(
        "0",
        documentationContext(),
        new WebMvcRequestHandler(
            ROOT,
            methodResolver,
            requestMappingInfo(path),
            handlerMethod))
  }

  Function<Model, String> toModelMap = new Function<Model, String>() {
    String apply(Model model) {
      return model.getName()
    }
  }

  def "Annotated model"() {
    given:
    HandlerMethod handlerMethod = dummyHandlerMethod('methodWithModelPropertyAnnotations')
    RequestMappingContext context = requestMappingContext(handlerMethod, '/somePath')
    when:
    def modelsMap = sut.read(context)

    then:
    modelsMap.containsKey("0_0")
    Map<String, Model> models = modelsMap.get("0_0").stream()
        .collect(Collectors.toMap(toModelMap,
            Function.identity()))
    models.size() == 1

    models.containsKey('AnnotatedBusinessModel')
    Model model = models.get('AnnotatedBusinessModel')
    model.id == '0_0_springfox.documentation.spring.web.dummy.DummyModels$AnnotatedBusinessModel'
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
    HandlerMethod handlerMethod = dummyHandlerMethod('methodApiResponseClass')
    RequestMappingContext context = requestMappingContext(handlerMethod, '/somePath')
    when:
    def modelsMap = sut.read(context)

    then:
    modelsMap.containsKey("0_1")
    Map<String, Model> models = modelsMap.get("0_1").stream()
        .collect(Collectors.toMap(toModelMap,
            Function.identity()))
    models.size() == 1

    models['FunkyBusiness'].getQualifiedType() == 'springfox.documentation.spring.web.dummy.DummyModels$FunkyBusiness'
  }

  def "Should pull models from operation's ApiResponse annotations"() {
    given:
    HandlerMethod handlerMethod = dummyHandlerMethod('methodAnnotatedWithApiResponse')
    RequestMappingContext context = requestMappingContext(handlerMethod, '/somePath')
    when:
    def modelsMap = sut.read(context)
    then:
    modelsMap.containsKey("0_2")
    Map<String, Model> models = modelsMap.get("0_2").stream()
        .collect(Collectors.toMap(toModelMap,
            Function.identity()))
    models.size() == 1
    models['RestError'].getQualifiedType() == 'springfox.documentation.spring.web.dummy.RestError'
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
    !modelsMap.containsKey("0_0")
    modelsMap.containsKey("0_1")
    modelsMap.containsKey("0_2")
    Map<String, Model> models_1 = modelsMap.get("0_1").stream()
        .collect(Collectors.toMap(toModelMap,
            Function.identity()))
    Map<String, Model> models_2 = modelsMap.get("0_2").stream()
        .collect(Collectors.toMap(toModelMap,
            Function.identity()))

    models_1.size() == 1
    models_1.containsKey("BusinessModel")

    models_2.size() == 1
    models_2.containsKey("RestError") // from class-level annotation.

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

  def "property description should be populated when type is used in response and request body"() {
    given:
    HandlerMethod handlerMethod = dummyHandlerMethod('methodWithSameAnnotatedModelInReturnAndRequestBodyParam',
        DummyModels.AnnotatedBusinessModel
    )
    RequestMappingContext context = requestMappingContext(handlerMethod, '/businesses/responseEntity/{businessId}')

    when:
    def modelsMap = sut.read(context)

    then:
    modelsMap.containsKey("0_0")
    modelsMap.containsKey("0_1")
    modelsMap.containsKey("0_2")
    Map<String, Model> models_0 = modelsMap.get("0_0").stream()
        .collect(Collectors.toMap(toModelMap,
            Function.identity()))
    Map<String, Model> models_1 = modelsMap.get("0_1").stream()
        .collect(Collectors.toMap(toModelMap,
            Function.identity()))
    Map<String, Model> models_2 = modelsMap.get("0_2").stream()
        .collect(Collectors.toMap(toModelMap,
            Function.identity()))

    models_0.size() == 1
    models_1.size() == 1

    String modelName = DummyModels.AnnotatedBusinessModel.class.simpleName

    models_0.containsKey(modelName)
    models_1.containsKey(modelName)

    Model model = models_1[modelName]

    model.equalsIgnoringName(models_0[modelName])

    Map modelProperties = model.getProperties()

    modelProperties.size() == 2
    modelProperties.containsKey('name')
    modelProperties.containsKey('numEmployees')

    ModelProperty nameProperty = modelProperties['name']
    nameProperty.getDescription() == 'The name of this business'

    ModelProperty numEmployeesProperty = modelProperties['numEmployees']
    numEmployeesProperty.getDescription() == 'Total number of current employees'

    models_2.size() == 1
    models_2.containsKey('RestError') // from class-level annotation.
  }
}
