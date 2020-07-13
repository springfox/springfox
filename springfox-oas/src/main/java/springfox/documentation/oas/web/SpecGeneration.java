package springfox.documentation.oas.web;

import io.swagger.v3.oas.models.servers.Server;
import org.slf4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static org.slf4j.LoggerFactory.*;

public class SpecGeneration {
  private static final Logger LOGGER = getLogger(SpecGeneration.class);
  public static final String OPEN_API_SPECIFICATION_PATH
      = "${springfox.documentation.open-api.v3.path:/v3/api-docs}";
  protected static final String HAL_MEDIA_TYPE = "application/hal+json";

  private SpecGeneration() {
    throw new UnsupportedOperationException();
  }

  public static Server inferredServer(
      String requestPrefix,
      String requestUrl) {
    String serverUrl = requestUrl.replace(requestPrefix, "");
    try {
      URI url = new URI(requestUrl);
      serverUrl = String.format("%s://%s:%s", url.getScheme(), url.getHost(), url.getPort());
    } catch (URISyntaxException e) {
      LOGGER.error("Unable to parse request url:" + requestUrl);
    }
    return new Server()
        .url(serverUrl)
        .description("Inferred Url");
  }

  public static String decode(String requestURI) {
    try {
      return URLDecoder.decode(requestURI, StandardCharsets.UTF_8.toString());
    } catch (UnsupportedEncodingException e) {
      return requestURI;
    }
  }
}
