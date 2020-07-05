package springfox.documentation.oas.web;

import io.swagger.v3.oas.models.OpenAPI;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import springfox.documentation.builders.ServerBuilder;
import springfox.documentation.oas.mappers.ServiceModelToOpenApiMapper;
import springfox.documentation.service.Documentation;
import springfox.documentation.service.Server;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.json.Json;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.spring.web.plugins.Docket;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.slf4j.LoggerFactory.*;

public class OpenApiControllerWeb {
  private static final Logger LOGGER = getLogger(OpenApiControllerWeb.class);
  public static final String OPEN_API_SPECIFICATION_PATH
      = "${springfox.documentation.open-api.v3.path:/v3/api-docs}";
  protected static final String HAL_MEDIA_TYPE = "application/hal+json";
  private final DocumentationCache documentationCache;
  private final ServiceModelToOpenApiMapper mapper;
  private final JsonSerializer jsonSerializer;
  private final String requestPrefix;

  public OpenApiControllerWeb(
      DocumentationCache documentationCache,
      ServiceModelToOpenApiMapper mapper,
      JsonSerializer jsonSerializer,
      String oasPath) {
    this.documentationCache = documentationCache;
    this.mapper = mapper;
    this.jsonSerializer = jsonSerializer;
    this.requestPrefix = StringUtils.trimTrailingCharacter(oasPath, '/');
  }

  protected ResponseEntity<Json> toJsonResponse(
      String swaggerGroup,
      String requestUrl) {
    String groupName = Optional.ofNullable(swaggerGroup).orElse(Docket.DEFAULT_GROUP_NAME);
    Documentation documentation = documentationCache.documentationByGroup(groupName);
    if (documentation == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    documentation.addServer(inferredServer(requestUrl));
    OpenAPI oas = mapper.mapDocumentation(documentation);
    Json json = jsonSerializer.toJson(oas);
    return new ResponseEntity<>(json, HttpStatus.OK);
  }

  protected Server inferredServer(String requestUrl) {
    String serverUrl = requestUrl.replace(requestPrefix, "");
    try {
      URI url = new URI(requestUrl);
      serverUrl = String.format("%s://%s:%s", url.getScheme(), url.getHost(), url.getPort());
    } catch (URISyntaxException e) {
      LOGGER.error("Unable to parse request url:" + requestUrl);
    }
    return new ServerBuilder()
        .url(serverUrl)
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
