package springfox.boot.starter.autoconfigure;

import org.springframework.core.io.Resource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.resource.ResourceTransformer;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SwaggerUiWebMvcTransformer implements ResourceTransformer {
  private final String baseUrl;

  public SwaggerUiWebMvcTransformer(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  @Override
  public Resource transform(
      HttpServletRequest request,
      Resource resource,
      ResourceTransformerChain transformerChain) throws IOException {
    AntPathMatcher antPathMatcher = new AntPathMatcher();
    boolean isIndexFound = antPathMatcher.match("**/springfox.js", resource.getURL().toString());
    if (isIndexFound) {
      String html = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
      html = replaceBaseUrl(html);
      return new TransformedResource(resource, html.getBytes());
    } else {
      return resource;
    }
  }

  private String replaceBaseUrl(String html) {
    return html.replace("return/(.*)\\/swagger-ui.html.*/.exec(window.location.href)[1]",
        "return '" + baseUrl + "';");
  }
}
