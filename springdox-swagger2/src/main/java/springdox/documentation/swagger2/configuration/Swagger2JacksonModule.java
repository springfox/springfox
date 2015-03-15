package springdox.documentation.swagger2.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.wordnik.swagger.models.Contact;
import com.wordnik.swagger.models.ExternalDocs;
import com.wordnik.swagger.models.Info;
import com.wordnik.swagger.models.Model;
import com.wordnik.swagger.models.Operation;
import com.wordnik.swagger.models.Path;
import com.wordnik.swagger.models.Response;
import com.wordnik.swagger.models.Scheme;
import com.wordnik.swagger.models.SecurityRequirement;
import com.wordnik.swagger.models.Swagger;
import com.wordnik.swagger.models.Tag;
import com.wordnik.swagger.models.Xml;
import com.wordnik.swagger.models.auth.SecuritySchemeDefinition;
import com.wordnik.swagger.models.parameters.Parameter;
import com.wordnik.swagger.models.properties.Property;

import static com.fasterxml.jackson.annotation.JsonInclude.*;

public class Swagger2JacksonModule extends SimpleModule {

  public static boolean isRegistered(ObjectMapper objectMapper) {
    return objectMapper.findMixInClassFor(Swagger.class) != null;
  }

  @Override
  public void setupModule(SetupContext context) {
    super.setupModule(context);
    context.setMixInAnnotations(Swagger.class, CustomizedSwaggerSerializer.class);
    context.setMixInAnnotations(Info.class, CustomizedSwaggerSerializer.class);
    context.setMixInAnnotations(Scheme.class, CustomizedSwaggerSerializer.class);
    context.setMixInAnnotations(SecurityRequirement.class, CustomizedSwaggerSerializer.class);
    context.setMixInAnnotations(SecuritySchemeDefinition.class, CustomizedSwaggerSerializer.class);
    context.setMixInAnnotations(Model.class, CustomizedSwaggerSerializer.class);
    context.setMixInAnnotations(Property.class, CustomizedSwaggerSerializer.class);
    context.setMixInAnnotations(Operation.class, CustomizedSwaggerSerializer.class);
    context.setMixInAnnotations(Path.class, CustomizedSwaggerSerializer.class);
    context.setMixInAnnotations(Response.class, CustomizedSwaggerSerializer.class);
    context.setMixInAnnotations(Parameter.class, CustomizedSwaggerSerializer.class);
    context.setMixInAnnotations(ExternalDocs.class, CustomizedSwaggerSerializer.class);
    context.setMixInAnnotations(Xml.class, CustomizedSwaggerSerializer.class);
    context.setMixInAnnotations(Tag.class, CustomizedSwaggerSerializer.class);
    context.setMixInAnnotations(Contact.class, CustomizedSwaggerSerializer.class);
  }

  @JsonAutoDetect
  @JsonInclude(value = Include.NON_EMPTY)
  private class CustomizedSwaggerSerializer {
  }

  @JsonAutoDetect
  @JsonInclude(value = Include.NON_EMPTY)
  @JsonPropertyOrder(alphabetic = true)
  private class OrderedSwaggerSerializer {
  }
  
}
