package com.mangofactory.swagger.authorization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.mangofactory.swagger.configuration.SwaggerAuthorizationJsonSerializer
import com.mangofactory.swagger.configuration.SwaggerAuthorizationTypeJsonSerializer
import com.mangofactory.swagger.mixins.AuthSupport
import com.wordnik.swagger.model.Authorization
import com.wordnik.swagger.model.AuthorizationType
import groovy.json.JsonSlurper
import spock.lang.Specification

@Mixin(AuthSupport)
class AuthSerializationSpec extends Specification {
   final ObjectMapper mapper = new ObjectMapper();

   def setup() {
      mapper.registerModule(new DefaultScalaModule())
   }

   def "Basic write"() {
    expect:
      mapper.writeValueAsString("String") == '"String"'
   }

   def "Custom String serializer"() {
    given:
      SimpleModule stringModule = new SimpleModule("SimpleModule")
      stringModule.addSerializer(String.class, stringPrependSerializer())
      mapper.registerModule(stringModule)

    when:
      def result = mapper.writeValueAsString("myString")

    then:
      result == '"prefix-myString"'
   }

   def "serialize AuthorizationTypes using swaggers serializer"() {
    given:
      SimpleModule module = new SimpleModule("SimpleModule")
      module.addSerializer(AuthorizationType.class, new SwaggerAuthorizationTypeJsonSerializer())
      mapper.registerModule(module)

    when:
      StringWriter stringWriter = new StringWriter()
      mapper.writeValue(stringWriter, authorizationTypes())
      def jsonString = stringWriter.toString()
      def json = new JsonSlurper().parseText(jsonString)
    then:
      json.type == "oauth2"
      json.scopes[0].scope == "global"
      json.scopes[0].description == "access all"
      json.grantTypes.implicit.loginEndpoint.url == "https://logmein.com"
      json.grantTypes.implicit.tokenName == "AccessToken"
   }

   def "should serialize Authorization types"() {
    given:
      SimpleModule mod = new SimpleModule("AuthMod")
      mod.addSerializer(Authorization.class, new SwaggerAuthorizationJsonSerializer())
      mapper.registerModule(mod)

    when:
      StringWriter stringWriter = new StringWriter()
      mapper.writeValue(stringWriter, defaultAuth())
      def jsonString = stringWriter.toString()
      def json = new JsonSlurper().parseText(jsonString)
    then:
      println json
   }

   def stringPrependSerializer() {
      JsonSerializer<String> prepSerializer = new JsonSerializer<String>() {
         @Override
         void serialize(String value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeString("prefix-" + value)
         }
      }
      prepSerializer
   }
}
