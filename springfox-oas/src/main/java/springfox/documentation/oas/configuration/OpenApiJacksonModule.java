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

package springfox.documentation.oas.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Encoding;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.XML;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import springfox.documentation.spring.web.json.JacksonModuleRegistrar;

import static com.fasterxml.jackson.annotation.JsonInclude.*;

public class OpenApiJacksonModule extends SimpleModule implements JacksonModuleRegistrar {

  public void maybeRegisterModule(ObjectMapper mapper) {
    if (mapper.findMixInClassFor(OpenAPI.class) == null) {
      mapper.registerModule(this);
      mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
      mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
      mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
      mapper.setSerializationInclusion(Include.NON_NULL);
    }
  }

  @Override
  public void setupModule(SetupContext context) {
    super.setupModule(context);
    context.setMixInAnnotations(OpenAPI.class, NonEmptyMixin.class);
    context.setMixInAnnotations(Info.class, NonEmptyMixin.class);
    context.setMixInAnnotations(License.class, NonEmptyMixin.class);
    context.setMixInAnnotations(Schema.class, NonEmptyMixin.class);
    context.setMixInAnnotations(PathItem.class, NonEmptyMixin.class);
    context.setMixInAnnotations(Content.class, NonEmptyMixin.class);
    context.setMixInAnnotations(MediaType.class, NonEmptyMixin.class);
    context.setMixInAnnotations(SecurityScheme.class, NonEmptyMixin.class);
    context.setMixInAnnotations(Operation.class, NonEmptyMixin.class);
    context.setMixInAnnotations(ApiResponses.class, NonEmptyMixin.class);
    context.setMixInAnnotations(ApiResponse.class, NonEmptyMixin.class);
    context.setMixInAnnotations(Parameter.class, NonEmptyMixin.class);
    context.setMixInAnnotations(RequestBody.class, NonEmptyMixin.class);
    context.setMixInAnnotations(Encoding.class, NonEmptyMixin.class);
    context.setMixInAnnotations(Components.class, NonEmptyMixin.class);
    context.setMixInAnnotations(Contact.class, NonEmptyMixin.class);
    context.setMixInAnnotations(Server.class, NonEmptyMixin.class);
//    context.setMixInAnnotations(ExternalDocs.class, CustomizedSwaggerSerializer.class);
    context.setMixInAnnotations(XML.class, NonEmptyMixin.class);
    context.setMixInAnnotations(Tag.class, NonEmptyMixin.class);
    context.setMixInAnnotations(Contact.class, NonEmptyMixin.class);
    context.setMixInAnnotations(Example.class, NonEmptyMixin.class);

  }

  @JsonAutoDetect
  @JsonInclude(value = Include.NON_EMPTY)
  private class NonEmptyMixin {
  }
}
