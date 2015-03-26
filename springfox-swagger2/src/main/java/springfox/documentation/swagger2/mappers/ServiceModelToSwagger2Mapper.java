package springfox.documentation.swagger2.mappers;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;
import com.wordnik.swagger.models.Contact;
import com.wordnik.swagger.models.Info;
import com.wordnik.swagger.models.Operation;
import com.wordnik.swagger.models.Path;
import com.wordnik.swagger.models.Response;
import com.wordnik.swagger.models.Scheme;
import com.wordnik.swagger.models.Swagger;
import com.wordnik.swagger.models.Tag;
import com.wordnik.swagger.models.properties.Property;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiListing;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Documentation;
import springfox.documentation.service.ResponseMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.FluentIterable.*;
import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static springfox.documentation.swagger2.mappers.ModelMapper.*;

@Mapper(uses = { ModelMapper.class, ParameterMapper.class, SecurityMapper.class, LicenseMapper.class })
public abstract class ServiceModelToSwagger2Mapper {

  @Mappings({
      @Mapping(target = "info", source = "resourceListing.info"),
      @Mapping(target = "paths", source = "apiListings"),
      @Mapping(target = "basePath", source = "basePath"),
      @Mapping(target = "tags", source = "tags"),
      @Mapping(target = "schemes", source = "from.schemes"),
      @Mapping(target = "produces", source = "produces"),
      @Mapping(target = "consumes", source = "consumes"),
      @Mapping(target = "definitions", source = "apiListings"),
      @Mapping(target = "securityDefinitions", source = "resourceListing"),
      @Mapping(target = "swagger", ignore = true),
      @Mapping(target = "parameters", ignore = true),
      @Mapping(target = "host", ignore = true),
      @Mapping(target = "externalDocs", ignore = true)
  })
  public abstract Swagger mapDocumentation(Documentation from);

  @Mappings({
      @Mapping(target = "license", source = "from",
          qualifiedBy = { LicenseMapper.LicenseTranslator.class, LicenseMapper.License.class }),
      @Mapping(target = "contact", source = "from.contact"),
      @Mapping(target = "termsOfService", source = "termsOfServiceUrl"),
      @Mapping(target = "vendorExtensions", ignore = true)
  })
  protected abstract Info mapApiInfo(ApiInfo from);

  @Mappings({
      @Mapping(target = "description", source = "notes"),
      @Mapping(target = "operationId", source = "nickname"),
      @Mapping(target = "schemes", source = "protocol"),
      @Mapping(target = "produces", source = "produces"),
      @Mapping(target = "consumes", source = "consumes"),
      @Mapping(target = "parameters", source = "parameters"),
      @Mapping(target = "security", source = "authorizations"),
      @Mapping(target = "responses", source = "responseMessages"),
      @Mapping(target = "tags", source = "tags"),
      @Mapping(target = "vendorExtensions", ignore = true),
      @Mapping(target = "externalDocs", ignore = true)
  })
  protected abstract Operation mapOperation(springfox.documentation.service.Operation from);

  @Mappings({
      @Mapping(target = "description", source = "description"),
      @Mapping(target = "name", source = "name"),
      @Mapping(target = "externalDocs", ignore = true)
  })
  protected abstract Tag mapTag(springfox.documentation.service.Tag from);

  protected List<Scheme> mapSchemes(List<String> from) {
    return FluentIterable.from(from).transform(toScheme()).toList();
  }

  protected Contact mapContact(String contact) {
    return new Contact().name(contact);
  }

  protected List<Map<String, List<String>>> mapAuthorizations(
      Map<String, List<AuthorizationScope>> from) {
    List<Map<String, List<String>>> security = newArrayList();
    for (Map.Entry<String, List<AuthorizationScope>> each : from.entrySet()) {
      Map<String, List<String>> newEntry = newHashMap();
      newEntry.put(each.getKey(), from(each.getValue()).transform(scopeToString()).toList());
      security.add(newEntry);
    }
    return security;
  }

  protected Map<String, Response> mapResponseMessages(Set<ResponseMessage> from) {
    HashMap<String, Response> responses = newHashMap();
    for (ResponseMessage responseMessage : from) {
      Property responseProperty;
      ModelRef modelRef = responseMessage.getResponseModel();
      responseProperty = modelRefToProperty(modelRef);
      Response response = new Response()
          .description(responseMessage.getMessage())
          .schema(responseProperty);
      response.setExamples(Maps.<String, String>newHashMap());
      response.setHeaders(Maps.<String, Property>newHashMap());
      responses.put(String.valueOf(responseMessage.getCode()), response);
    }
    return responses;
  }

  protected Map<String, Path> mapApiListings(Map<String, ApiListing> apiListings) {
    Map<String, Path> paths = newHashMap();
    for (ApiListing each : apiListings.values()) {
      for (ApiDescription api : each.getApis()) {
        paths.put(api.getPath(), mapOperations(api, Optional.fromNullable(paths.get(api.getPath()))));
      }
    }
    return paths;
  }

  private Function<AuthorizationScope, String> scopeToString() {
    return new Function<AuthorizationScope, String>() {
      @Override
      public String apply(AuthorizationScope input) {
        return input.getScope();
      }
    };
  }

  private Path mapOperations(ApiDescription api, Optional<Path> existingPath) {
    Path path = existingPath.or(new Path());
    for (springfox.documentation.service.Operation each : api.getOperations()) {
      Operation operation = mapOperation(each);
      path.set(each.getMethod().toLowerCase(), operation);
    }
    return path;
  }


  private Function<String, Scheme> toScheme() {
    return new Function<String, Scheme>() {
      @Override
      public Scheme apply(String input) {
        return Scheme.forValue(input);
      }
    };
  }

}
