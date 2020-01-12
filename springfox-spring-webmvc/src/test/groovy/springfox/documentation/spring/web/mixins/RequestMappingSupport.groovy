/*
 *
 *  Copyright 2016-2019 the original author or authors.
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

package springfox.documentation.spring.web.mixins

import com.fasterxml.classmate.TypeResolver
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition
import org.springframework.web.servlet.mvc.condition.HeadersRequestCondition
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import springfox.documentation.builders.OperationBuilder
import springfox.documentation.schema.Model
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spi.service.contexts.RequestMappingContext
import springfox.documentation.spring.web.WebMvcRequestHandler
import springfox.documentation.spring.web.dummy.DummyClass
import springfox.documentation.spring.web.dummy.DummyController
import springfox.documentation.spring.web.dummy.DummyControllerWithApiDescription
import springfox.documentation.spring.web.dummy.DummyControllerWithResourcePath
import springfox.documentation.spring.web.dummy.DummyControllerWithTags
import springfox.documentation.spring.web.dummy.DummyDeprecatedController
import springfox.documentation.spring.web.dummy.controllers.FancyPetService
import springfox.documentation.spring.web.dummy.controllers.PetGroomingService
import springfox.documentation.spring.web.dummy.controllers.PetService
import springfox.documentation.spring.web.dummy.models.FancyPet
import springfox.documentation.spring.web.paths.Paths
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver

import javax.servlet.ServletContext

trait RequestMappingSupport {

  RequestMappingInfo requestMappingInfo(String path, Map overrides = [:]) {
    PatternsRequestCondition singlePatternRequestCondition = patternsRequestCondition([path] as String[])
    ConsumesRequestCondition consumesRequestCondition =
        overrides['consumesRequestCondition'] ?: consumesRequestCondition()
    ProducesRequestCondition producesRequestCondition =
        overrides['producesRequestCondition'] ?: producesRequestCondition()
    PatternsRequestCondition patternsRequestCondition =
        overrides['patternsRequestCondition'] ?: singlePatternRequestCondition
    ParamsRequestCondition paramsRequestCondition =
        overrides["paramsCondition"] ?: paramsRequestCondition()
    HeadersRequestCondition headersRequestCondition =
        overrides["headersCondition"] ?: headersRequestCondition()
    RequestMethodsRequestCondition requestMethodsRequestCondition =
        overrides['requestMethodsRequestCondition'] ?: requestMethodsRequestCondition(RequestMethod.values())

    new RequestMappingInfo(
        patternsRequestCondition,
        requestMethodsRequestCondition,
        paramsRequestCondition,
        headersRequestCondition,
        consumesRequestCondition,
        producesRequestCondition,
        null)
  }

  HandlerMethod dummyHandlerMethod(
      String methodName = "dummyMethod",
      Class<?>... parameterTypes = null) {

    def clazz = new DummyClass()
    Class c = clazz.getClass()
    new HandlerMethod(clazz, c.getMethod(methodName, parameterTypes))
  }

  HandlerMethod dummyHandlerMethodIn(
      Class<?> aClass,
      String methodName = "dummyMethod",
      Class<?>... parameterTypes = null) {

    def clazz = aClass.newInstance()
    Class c = clazz.getClass()
    new HandlerMethod(clazz, c.getMethod(methodName, parameterTypes))
  }

  HandlerMethod handlerMethodIn(
      Class<?> aClass,
      String methodName = "dummyMethod",
      Class<?>... parameterTypes = null) {

    new HandlerMethod(aClass, aClass.getMethod(methodName, parameterTypes))
  }

  HandlerMethod dummyControllerHandlerMethod(
      String methodName = "dummyMethod",
      parameterTypes = null) {

    def clazz = new DummyController()
    Class c = clazz.getClass();
    new HandlerMethod(clazz, c.getMethod(methodName, parameterTypes))
  }

  HandlerMethod dummyOperationWithTags(
      String methodName = "dummyMethod",
      parameterTypes = null) {

    def clazz = new DummyControllerWithTags()
    Class c = clazz.getClass();
    new HandlerMethod(clazz, c.getMethod(methodName, parameterTypes))
  }

  HandlerMethod dummyControllerWithApiDescriptionHandlerMethod(
      String methodName = "dummyMethod",
      parameterTypes = null) {

    def clazz = new DummyControllerWithApiDescription()
    Class c = clazz.getClass();
    new HandlerMethod(clazz, c.getMethod(methodName, parameterTypes))
  }

  HandlerMethod dummyControllerWithResourcePath(
      String methodName = "dummyMethod",
      parameterTypes = null) {

    def clazz = new DummyControllerWithResourcePath()
    Class c = clazz.getClass();
    new HandlerMethod(clazz, c.getMethod(methodName, parameterTypes))
  }

  HandlerMethod petServiceHandlerMethod(String methodName = "getPetById", parameterTypes = String) {
    def clazz = new PetService()
    Class c = clazz.getClass()
    new HandlerMethod(clazz, c.getMethod(methodName, parameterTypes))
  }

  HandlerMethod fancyPetServiceHandlerMethod(
      String methodName = "createObject",
      parameterTypes = FancyPet) {

    def clazz = new FancyPetService()
    Class c = clazz.getClass()
    new HandlerMethod(clazz, c.getMethod(methodName, parameterTypes))
  }


  HandlerMethod multipleRequestMappingsHandlerMethod(
      String methodName = "canGroom",
      parameterTypes = String) {

    def clazz = new PetGroomingService()
    Class c = clazz.getClass()
    new HandlerMethod(clazz, c.getMethod(methodName, parameterTypes))
  }

  HandlerMethod dummyDeprecatedController(
      String methodName = "dummyMethod",
      Class<?>... parameterTypes = null) {

    def clazz = new DummyDeprecatedController()
    Class c = clazz.getClass()
    new HandlerMethod(clazz, c.getMethod(methodName, parameterTypes))
  }

  Class ignorableClass() {
    DummyClass.ApiIgnorableClass
  }

  def apiImplicitParamsClass() {
    DummyClass.ApiImplicitParamsClass.class;
  }

  def apiImplicitParamsAllowMultipleClass() {
    DummyClass.ApiImplicitParamsAllowMultipleClass.class;
  }

  HandlerMethod ignorableHandlerMethod() {
    def clazz = new DummyClass.ApiIgnorableClass()
    Class c = clazz.getClass();
    new HandlerMethod(clazz, c.getMethod("dummyMethod", null))
  }

  PatternsRequestCondition patternsRequestCondition(String... patterns) {
    new PatternsRequestCondition(patterns)
  }

  ParamsRequestCondition paramsRequestCondition(String... params) {
    new ParamsRequestCondition(params)
  }

  HeadersRequestCondition headersRequestCondition(String... params) {
    new HeadersRequestCondition(params)
  }

  ConsumesRequestCondition consumesRequestCondition(String... conditions) {
    new ConsumesRequestCondition(conditions)
  }

  ProducesRequestCondition producesRequestCondition(String... conditions) {
    new ProducesRequestCondition(conditions)
  }

  RequestMethodsRequestCondition requestMethodsRequestCondition(RequestMethod... requestMethods) {
    new RequestMethodsRequestCondition(requestMethods)
  }

  ServletContext servletContext() {
    [getContextPath: { return "/context-path" }] as ServletContext
  }

  def operationContext(
      context,
      handlerMethod,
      operationIndex = 0,
      requestMapping = requestMappingInfo("/somePath"),
      httpMethod = RequestMethod.GET,
      knownModels = new HashMap<String, List<Model>>()) {
    new OperationContext(
        new OperationBuilder(new CachingOperationNameGenerator()),
        httpMethod,
        new RequestMappingContext(
            "0",
            context,
            new WebMvcRequestHandler(
                Paths.ROOT,
                new HandlerMethodResolver(new TypeResolver()),
                requestMapping,
                handlerMethod)).withKnownModels(knownModels),
        operationIndex)
  }
}
