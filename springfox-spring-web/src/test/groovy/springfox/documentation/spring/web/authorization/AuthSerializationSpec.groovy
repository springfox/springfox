/*
 *
 *  Copyright 2015 the original author or authors.
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

package springfox.documentation.spring.web.authorization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import spock.lang.Specification
import springfox.documentation.spring.web.mixins.AuthSupport

class AuthSerializationSpec extends Specification implements AuthSupport {
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
