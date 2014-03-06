package com.mangofactory.swagger.mixins

import com.mangofactory.swagger.dummy.DummyClass
import com.mangofactory.swagger.dummy.DummyController
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

   def dummyHandlerMethod(String methodName = "dummyMethod", parameterTypes = null ) {
      def clazz = new DummyClass()
      Class c = clazz.getClass();
      new HandlerMethod(clazz, c.getMethod(methodName, parameterTypes))
   }

   def dummyHandlerMethod(String methodName = "dummyMethod", Class<?>... parameterTypes ) {
       def clazz = new DummyClass()
       Class c = clazz.getClass();
       new HandlerMethod(clazz, c.getMethod(methodName, parameterTypes))
   }

   def dummyControllerHandlerMethod(String methodName = "dummyMethod", parameterTypes = null ) {
      def clazz = new DummyController()
      Class c = clazz.getClass();
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
