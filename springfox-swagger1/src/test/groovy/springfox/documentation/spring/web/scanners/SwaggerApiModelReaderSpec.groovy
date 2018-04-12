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

package springfox.documentation.spring.web.scanners

import com.fasterxml.classmate.TypeResolver
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.method.HandlerMethod
import springfox.documentation.schema.Model
import springfox.documentation.schema.ModelProperty
import springfox.documentation.schema.mixins.SchemaPluginsSupport
import springfox.documentation.spi.service.contexts.Defaults
import springfox.documentation.spi.service.contexts.RequestMappingContext
import springfox.documentation.spring.web.WebMvcRequestHandler
import springfox.documentation.spring.web.dummy.DummyModels
import springfox.documentation.spring.web.dummy.controllers.BusinessService
import springfox.documentation.spring.web.mixins.ModelProviderForServiceSupport
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver
import springfox.documentation.swagger.mixins.SwaggerPluginsSupport
import springfox.documentation.swagger1.web.SwaggerDefaultConfiguration

import javax.servlet.ServletContext
import javax.servlet.http.HttpServletResponse

@Mixin([RequestMappingSupport, ModelProviderForServiceSupport, SwaggerPluginsSupport, SchemaPluginsSupport])
class SwaggerApiModelReaderSpec extends DocumentationContextSpec {

  ApiModelReader sut
  DocumentationPluginsManager pluginsManager
  def methodResolver = new HandlerMethodResolver(new TypeResolver())

  def setup() {
    pluginsManager = swaggerServicePlugins([new SwaggerDefaultConfiguration(new Defaults(), new TypeResolver(),
        Mock(ServletContext))])
    sut = new ApiModelReader(
        modelProvider(swaggerSchemaPlugins()),
        new TypeResolver()
        ,
        pluginsManager)
  }

  def "Annotated model"() {
    given:
    RequestMappingContext context = context(dummyHandlerMethod('methodWithModelPropertyAnnotations'))

    when:
    def models = sut.read(context)

    then:
    Model model = models['AnnotatedBusinessModel']
    model.id == 'AnnotatedBusinessModel'
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
    RequestMappingContext context = context(dummyHandlerMethod('methodApiResponseClass'))

    when:
    def models = sut.read(context)

    then:
    models['FunkyBusiness'].getQualifiedType() == 'springfox.documentation.spring.web.dummy.DummyModels$FunkyBusiness'
  }

  def "Should pull models from operation's ApiResponse annotations"() {
    given:
    RequestMappingContext context = context(dummyHandlerMethod('methodAnnotatedWithApiResponse'))

    when:
    def models = sut.read(context)

    then:
    models.size() == 1
    models['RestError'].getQualifiedType() == 'springfox.documentation.spring.web.dummy.RestError'
  }

  def context(HandlerMethod handlerMethod) {
    return new RequestMappingContext(
        context(),
        new WebMvcRequestHandler(methodResolver,
            requestMappingInfo('/somePath'),
            handlerMethod))
  }

  def "should only generate models for request parameters that are annotated with Springs RequestBody"() {
    given:
    HandlerMethod handlerMethod = dummyHandlerMethod('methodParameterWithRequestBodyAnnotation',
        DummyModels.BusinessModel,
        HttpServletResponse.class,
        DummyModels.AnnotatedBusinessModel.class
    )
    RequestMappingContext context = new RequestMappingContext(
        context(),
        new WebMvcRequestHandler(methodResolver,
            requestMappingInfo('/somePath'),
            handlerMethod))

    when:
    def models = sut.read(context)

    then:
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
    def pluginContext = contextBuilder.build()

    and:
    HandlerMethod handlerMethod = handlerMethodIn(BusinessService, 'getResponseEntity', String)
    RequestMappingContext context =
        new RequestMappingContext(
            pluginContext,
            new WebMvcRequestHandler(
                methodResolver,
                requestMappingInfo('/businesses/responseEntity/{businessId}'),
                handlerMethod))

    when:
    def models = sut.read(context)

    then:
    models.size() == 0

  }

  def "property description should be populated when type is used in response and request body"() {
    given:
    HandlerMethod handlerMethod = dummyHandlerMethod('methodWithSameAnnotatedModelInReturnAndRequestBodyParam',
        DummyModels.AnnotatedBusinessModel
    )
    RequestMappingContext context = new RequestMappingContext(
        context(),
        new WebMvcRequestHandler(
            methodResolver,
            requestMappingInfo('/somePath'),
            handlerMethod))

    when:
    def models = sut.read(context)

    then:
    models.size() == 2
    models.containsKey('RestError') // from class-level annotation.

    String modelName = DummyModels.AnnotatedBusinessModel.class.simpleName
    models.containsKey(modelName)

    Model model = models[modelName]
    Map modelProperties = model.getProperties()
    modelProperties.containsKey('name')

    ModelProperty nameProperty = modelProperties['name']
    !nameProperty.getDescription().isEmpty()
  }
}
