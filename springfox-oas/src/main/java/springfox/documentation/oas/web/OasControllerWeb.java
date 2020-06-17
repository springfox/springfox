package springfox.documentation.oas.web;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import springfox.documentation.builders.ServerBuilder;
import springfox.documentation.oas.mappers.ServiceModelToOasMapper;
import springfox.documentation.service.Documentation;
import springfox.documentation.service.Server;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.json.Json;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.spring.web.plugins.Docket;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class OasControllerWeb {
  protected static final String HAL_MEDIA_TYPE = "application/hal+json";
  private final DocumentationCache documentationCache;
  private final ServiceModelToOasMapper mapper;
  private final JsonSerializer jsonSerializer;
  private final String oasPath;

  public OasControllerWeb(
      DocumentationCache documentationCache,
      ServiceModelToOasMapper mapper,
      JsonSerializer jsonSerializer,
      @Value("${springfox.documentation.resources.baseUrl:}${springfox.documentation.oas.v3.path:/v3/api-docs}")
          String oasPath) {
    this.documentationCache = documentationCache;
    this.mapper = mapper;
    this.jsonSerializer = jsonSerializer;
    this.oasPath = oasPath;
  }

  protected ResponseEntity<Json> toJsonResponse(
      String swaggerGroup,
      String requestUrl) {
    String groupName = Optional.ofNullable(swaggerGroup).orElse(Docket.DEFAULT_GROUP_NAME);
    Documentation documentation = documentationCache.documentationByGroup(groupName);
    if (documentation == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    documentation.addServer(inferredServer(oasPath, requestUrl));
    OpenAPI oas = mapper.mapDocumentation(documentation);
    Json json = jsonSerializer.toJson(oas);
    return new ResponseEntity<>(
        json,
        HttpStatus.OK);
  }

  protected Server inferredServer(
      String apiDocsUrl,
      String requestUrl) {
    return new ServerBuilder()
        .url(requestUrl.substring(0, requestUrl.length() - apiDocsUrl.length()))
        .description("Inferred Url")
        .build();
  }

  protected String decode(String requestURI) {
    try {
      return URLDecoder.decode(requestURI, StandardCharsets.UTF_8.toString());
    } catch (UnsupportedEncodingException e) {
      return requestURI;
    }
  }
}
