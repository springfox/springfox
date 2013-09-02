package com.mangofactory.swagger.spring.controller;

import com.mangofactory.swagger.ControllerDocumentation;
import com.mangofactory.swagger.DocumentationTransformer;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.mangofactory.swagger.spring.DocumentationReader;
import com.wordnik.swagger.core.Documentation;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping('/' + DocumentationController.CONTROLLER_ENDPOINT)
public class DocumentationController implements ServletContextAware {

    public static final String CONTROLLER_ENDPOINT = "api-docs";
    @Autowired
    @Getter
    @Setter
    private SwaggerConfiguration swaggerConfiguration;

    @Autowired
    private List<RequestMappingHandlerMapping> handlerMappings;
    @Getter
    private DocumentationReader apiReader;

    @RequestMapping(method = RequestMethod.GET)
    public
    @ResponseBody
    Documentation getResourceListing() {
        Documentation documentation = apiReader.getDocumentation();
        DocumentationTransformer transformer = swaggerConfiguration.getDocumentationTransformer();
        return transformer.applySorting(transformer.applyTransformation(documentation));
    }

    @RequestMapping(value = "/**", method = RequestMethod.GET)
    public
    @ResponseBody
    ControllerDocumentation getApiDocumentation(HttpServletRequest request) {
        String apiName = extractPathFromPattern(request);
        DocumentationTransformer transformer = swaggerConfiguration.getDocumentationTransformer();
        return (ControllerDocumentation) transformer
                .applySorting(apiReader.getDocumentation(apiName));
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        apiReader = new DocumentationReader(swaggerConfiguration,
                WebApplicationContextUtils.getWebApplicationContext(servletContext), handlerMappings);
    }
    
    private static String extractPathFromPattern(final HttpServletRequest request){

      String path = (String) request.getAttribute(
              HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
      String bestMatchPattern = (String ) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
      
      return new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, path);
  }
}
