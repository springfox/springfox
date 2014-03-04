package com.mangofactory.swagger.mixins

import com.mangofactory.swagger.dummy.DummyClass
import org.springframework.web.method.HandlerMethod

class HandlerMethodsSupport {
  HandlerMethod methodWithChild() {
    def clazz = new DummyClass.MethodsWithSameName()
    Class c = clazz.getClass();
    new HandlerMethod(clazz, c.getMethod("methodToTest", Integer, DummyClass.Child))
  }


  HandlerMethod methodWithParent() {
    def clazz = new DummyClass.MethodsWithSameName()
    Class c = clazz.getClass();
    new HandlerMethod(clazz, c.getMethod("methodToTest", Integer, DummyClass.Parent))
  }
}
