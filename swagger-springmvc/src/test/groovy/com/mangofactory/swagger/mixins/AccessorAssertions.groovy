package com.mangofactory.swagger.mixins

import org.codehaus.groovy.reflection.CachedClass

class AccessorAssertions {

   def assertAccessor(target, String method, value){
      method = method.capitalize()
      target."set${method}"(value)
      return target."get${method}"() == value
   }
}