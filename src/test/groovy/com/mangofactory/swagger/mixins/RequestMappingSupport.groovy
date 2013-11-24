package com.mangofactory.swagger.mixins

import com.mangofactory.swagger.dummy.DummyClass
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

import static com.mangofactory.swagger.dummy.DummyClass.*

class RequestMappingSupport {

   def requestMappingInfo(String path, Map overrides = [:]){
      PatternsRequestCondition patternsRequestCondition = patternsRequestCondition(path)

      ConsumesRequestCondition consumesRequestCondition =
         null == overrides['consumesRequestCondition'] ? consumesRequestCondition() : overrides['consumesRequestCondition']
      ProducesRequestCondition producesRequestCondition =
         null == overrides['producesRequestCondition'] ? producesRequestCondition() : overrides['producesRequestCondition']


      new RequestMappingInfo(patternsRequestCondition, null, null, null, consumesRequestCondition, producesRequestCondition, null)
   }

   def dummyHandlerMethod(){
      def clazz = new DummyClass()
      Class c = clazz.getClass();
      new HandlerMethod(clazz, c.getMethod("dummyMethod", null))
   }

   def ignorableHandlerMethod(){
      def clazz = new DummyClass.ApiIgnorableClass()
      Class c = clazz.getClass();
      new HandlerMethod(clazz, c.getMethod("dummyMethod", null))
   }

   def patternsRequestCondition(String path){
      new PatternsRequestCondition([path] as String[])
   }

   def consumesRequestCondition(String ... conditions){
      new ConsumesRequestCondition(conditions)
   }

   def producesRequestCondition(String ... conditions){
      new ProducesRequestCondition(conditions)
   }

}
