package com.mangofactory.swagger.authorization
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.mangofactory.swagger.mixins.AuthSupport
import spock.lang.Specification

@Mixin(AuthSupport)
class AuthSerializationSpec extends Specification {
   final ObjectMapper mapper = new ObjectMapper();

   def setup() {
//      mapper.registerModule(new DefaultScalaModule())
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
