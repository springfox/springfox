package com.mangofactory.swagger.mixins
import com.mangofactory.swagger.dummy.DummyClass
import com.mangofactory.swagger.dummy.DummyController
import com.mangofactory.swagger.dummy.controllers.FancyPetService
import com.mangofactory.swagger.dummy.controllers.PetGroomingService
import com.mangofactory.swagger.dummy.controllers.PetService
import com.mangofactory.swagger.dummy.models.FancyPet
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition
import org.springframework.web.servlet.mvc.method.RequestMappingInfo

import javax.servlet.ServletContext

class RequestMappingSupport {

  def requestMappingInfo(String path, Map overrides = [:]) {
    PatternsRequestCondition singlePatternRequestCondition = patternsRequestCondition([path] as String[])
    ConsumesRequestCondition consumesRequestCondition = overrides['consumesRequestCondition'] ?: consumesRequestCondition()
    ProducesRequestCondition producesRequestCondition = overrides['producesRequestCondition'] ?: producesRequestCondition()
    PatternsRequestCondition patternsRequestCondition = overrides['patternsRequestCondition'] ?: singlePatternRequestCondition
    RequestMethodsRequestCondition requestMethodsRequestCondition =
            overrides['requestMethodsRequestCondition'] ?: requestMethodsRequestCondition(RequestMethod.values())
    new RequestMappingInfo(patternsRequestCondition, requestMethodsRequestCondition, null, null, consumesRequestCondition, producesRequestCondition, null)
  }

  def dummyHandlerMethod(String methodName = "dummyMethod", Class<?>... parameterTypes = null) {
    def clazz = new DummyClass()
    Class c = clazz.getClass();
    new HandlerMethod(clazz, c.getMethod(methodName, parameterTypes))
  }

  def handlerMethodIn(Class<?> aClass, String methodName = "dummyMethod", Class<?>... parameterTypes = null) {
    new HandlerMethod(aClass, aClass.getMethod(methodName, parameterTypes))
  }

  def dummyControllerHandlerMethod(String methodName = "dummyMethod", parameterTypes = null) {
    def clazz = new DummyController()
    Class c = clazz.getClass();
    new HandlerMethod(clazz, c.getMethod(methodName, parameterTypes))
  }


  def petServiceHandlerMethod(String methodName = "getPetById", parameterTypes = String) {
    def clazz = new PetService()
    Class c = clazz.getClass()
    new HandlerMethod(clazz, c.getMethod(methodName, parameterTypes))
  }

  def fancyPetServiceHandlerMethod(String methodName = "createObject", parameterTypes = FancyPet) {
    def clazz = new FancyPetService()
    Class c = clazz.getClass()
    new HandlerMethod(clazz, c.getMethod(methodName, parameterTypes))
  }


  def multipleRequestMappingsHandlerMethod(String methodName = "canGroom", parameterTypes = String) {
    def clazz = new PetGroomingService()
    Class c = clazz.getClass()
    new HandlerMethod(clazz, c.getMethod(methodName, parameterTypes))
  }

  def ignorableHandlerMethod() {
    def clazz = new DummyClass.ApiIgnorableClass()
    Class c = clazz.getClass();
    new HandlerMethod(clazz, c.getMethod("dummyMethod", null))
  }

  def patternsRequestCondition(String... patterns) {
    new PatternsRequestCondition(patterns)
  }

  def consumesRequestCondition(String... conditions) {
    new ConsumesRequestCondition(conditions)
  }

  def producesRequestCondition(String... conditions) {
    new ProducesRequestCondition(conditions)
  }

  def requestMethodsRequestCondition(RequestMethod... requestMethods) {
    new RequestMethodsRequestCondition(requestMethods)
  }

  def servletContext() {
    [getContextPath: { return "/context-path" }] as ServletContext
  }
}
