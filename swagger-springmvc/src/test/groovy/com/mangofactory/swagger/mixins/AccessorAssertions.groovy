package com.mangofactory.swagger.mixins

class AccessorAssertions {

   def assertAccessor(target, String method, value){
      method = method.capitalize()
      target."set${method}"(value)
      return target."get${method}"() == value
   }

   def assertSetter(target, String field, value){
      def method = field.capitalize()
      target."set${method}"(value)
      return target.class.fields["${field}"] == value
   }
}