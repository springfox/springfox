package com.mangofactory.swagger.core
import com.fasterxml.classmate.GenericType
import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.type.SimpleType
import com.mangofactory.schema.DefaultGenericTypeNamingStrategy
import com.mangofactory.schema.TypeNameExtractor
import com.mangofactory.schema.plugins.DocumentationType
import com.mangofactory.spring.web.HandlerMethodReturnTypes
import com.mangofactory.swagger.dummy.DummyModels
import com.mangofactory.swagger.mixins.ModelProviderSupport
import com.mangofactory.swagger.mixins.RequestMappingSupport
import spock.lang.Specification

import static com.mangofactory.schema.ResolvedTypes.*
import static com.mangofactory.schema.plugins.ModelContext.*

@Mixin([RequestMappingSupport, ModelProviderSupport])
class ModelUtilsSpec extends Specification {
  TypeNameExtractor typeNameExtractor

  def setup() {
    typeNameExtractor =
            new TypeNameExtractor(new TypeResolver(), new DefaultGenericTypeNamingStrategy(), pluginsManager())
  }

   def "model types"() {
    expect:
      def type = HandlerMethodReturnTypes.handlerReturnType(new TypeResolver(), handlerMethod)
      println "TYPE: $type"
      type.getErasedType() == expectedType
      typeNameExtractor.typeName(inputParam(asResolved(new TypeResolver(), String.class),
              DocumentationType.SWAGGER_12))

    where:
      handlerMethod                                            | expectedType
      dummyHandlerMethod("methodWithConcreteResponseBody")     | DummyModels.BusinessModel.class
      dummyHandlerMethod("methodWithConcreteCorporationModel") | DummyModels.CorporationModel.class
   }

  def "Get response class name from ResolvedType"(){
    expect:
      def modelResponseClass = typeNameExtractor.typeName(
              returnValue(new TypeResolver().resolve(GenericType.class, clazz), DocumentationType.SWAGGER_12))
      modelResponseClass == expectedResponseClassName

    where:
      clazz       | expectedResponseClassName
      SimpleType  | "GenericType«SimpleType»"
      Integer     | "GenericType«int»"
  }

}