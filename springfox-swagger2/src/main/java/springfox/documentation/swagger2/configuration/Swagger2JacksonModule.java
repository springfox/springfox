/*
 *
 *  Copyright 2015-2019 the original author or authors.
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

package springfox.documentation.swagger2.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.swagger.models.Contact;
import io.swagger.models.ExternalDocs;
import io.swagger.models.Info;
import io.swagger.models.License;
import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Response;
import io.swagger.models.Scheme;
import io.swagger.models.SecurityRequirement;
import io.swagger.models.Swagger;
import io.swagger.models.Tag;
import io.swagger.models.Xml;
import io.swagger.models.auth.SecuritySchemeDefinition;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.Property;
import springfox.documentation.spring.web.json.JacksonModuleRegistrar;

import java.io.IOException;
import java.util.regex.Pattern;

import static com.fasterxml.jackson.annotation.JsonInclude.*;

public class Swagger2JacksonModule extends SimpleModule implements JacksonModuleRegistrar {

  public void maybeRegisterModule(ObjectMapper objectMapper) {
    if (objectMapper.findMixInClassFor(Swagger.class) == null) {
      objectMapper.registerModule(this);
      objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }
  }

  @Override
  public void setupModule(SetupContext context) {
    super.setupModule(context);
    context.setMixInAnnotations(Swagger.class, CustomizedSwaggerSerializer.class);
    context.setMixInAnnotations(Info.class, CustomizedSwaggerSerializer.class);
    context.setMixInAnnotations(License.class, CustomizedSwaggerSerializer.class);
    context.setMixInAnnotations(Scheme.class, CustomizedSwaggerSerializer.class);
    context.setMixInAnnotations(SecurityRequirement.class, CustomizedSwaggerSerializer.class);
    context.setMixInAnnotations(SecuritySchemeDefinition.class, CustomizedSwaggerSerializer.class);
    context.setMixInAnnotations(Model.class, CustomizedSwaggerSerializer.class);
    context.setMixInAnnotations(Operation.class, CustomizedSwaggerSerializer.class);
    context.setMixInAnnotations(Path.class, CustomizedSwaggerSerializer.class);
    context.setMixInAnnotations(Response.class, ResponseSerializer.class);
    context.setMixInAnnotations(Parameter.class, CustomizedSwaggerSerializer.class);
    context.setMixInAnnotations(ExternalDocs.class, CustomizedSwaggerSerializer.class);
    context.setMixInAnnotations(Xml.class, CustomizedSwaggerSerializer.class);
    context.setMixInAnnotations(Tag.class, CustomizedSwaggerSerializer.class);
    context.setMixInAnnotations(Contact.class, CustomizedSwaggerSerializer.class);

    context.setMixInAnnotations(Property.class, PropertyExampleSerializerMixin.class);
  }

  @JsonAutoDetect
  @JsonInclude(value = Include.NON_EMPTY)
  private class CustomizedSwaggerSerializer {
  }

  @JsonAutoDetect
  @JsonInclude(value = Include.NON_EMPTY)
  @JsonIgnoreProperties("responseSchema")
  private class ResponseSerializer {
  }

  @JsonAutoDetect
  @JsonInclude(value = Include.NON_EMPTY)
  private interface PropertyExampleSerializerMixin {

    @JsonSerialize(using = PropertyExampleSerializer.class)
    Object getExample();

    class PropertyExampleSerializer extends StdSerializer<Object> {

      @SuppressWarnings("java:S4784")
      private static final Pattern JSON_NUMBER_PATTERN =
          Pattern.compile("-?(?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+-]?\\d+)?");

      @SuppressWarnings("unused")
      PropertyExampleSerializer() {
        this(Object.class);
      }

      PropertyExampleSerializer(Class<Object> t) {
        super(t);
      }

      @Override
      public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (canConvertToString(value)) {
          String stringValue = (value instanceof String) ? ((String) value).trim() : value.toString().trim();
          if (isStringLiteral(stringValue)) {
            String cleanedUp = stringValue.replaceAll("^\"", "")
                .replaceAll("\"$", "")
                .replaceAll("^'", "")
                .replaceAll("'$", "");
            gen.writeString(cleanedUp);
          } else if (isNotJsonString(stringValue)) {
            gen.writeRawValue(stringValue);
          } else {
            gen.writeString(stringValue);
          }
        } else {
          gen.writeObject(value);
        }
      }

      private boolean canConvertToString(Object value) {
        return value instanceof Boolean
            || value instanceof Character
            || value instanceof String
            || value instanceof Byte
            || value instanceof Short
            || value instanceof Integer
            || value instanceof Long
            || value instanceof Float
            || value instanceof Double
            || value instanceof Void;
      }

      boolean isStringLiteral(String value) {
        return (value.startsWith("\"") && value.endsWith("\""))
            || (value.startsWith("'") && value.endsWith("'"));
      }

      boolean isNotJsonString(final String value) {
        // strictly speaking, should also test for equals("null") since {"example": null} would be valid JSON
        // but swagger2 does not support null values
        // and an example value of "null" probably does not make much sense anyway
        return value.startsWith("{")                          // object
            || value.startsWith("[")                          // array
            || "true".equals(value)                           // true
            || "false".equals(value)                          // false
            || JSON_NUMBER_PATTERN.matcher(value).matches();  // number
      }

      @Override
      public boolean isEmpty(SerializerProvider provider, Object value) {
        return internalIsEmpty(value);
      }

      @SuppressWarnings("deprecation")
      @Override
      public boolean isEmpty(Object value) {
        return internalIsEmpty(value);
      }

      private boolean internalIsEmpty(Object value) {
        return value == null || value.toString().trim().length() == 0;
      }
    }
  }

}
