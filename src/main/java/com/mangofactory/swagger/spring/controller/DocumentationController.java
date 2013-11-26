package com.mangofactory.swagger.spring.controller;

import com.mangofactory.swagger.ControllerDocumentation;
import com.mangofactory.swagger.DocumentationTransformer;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.mangofactory.swagger.spring.DocumentationReader;
import com.wordnik.swagger.core.Documentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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

    @Autowired private SwaggerConfiguration swaggerConfiguration;
    @Autowired private List<RequestMappingHandlerMapping> handlerMappings;

    private DocumentationReader apiReader;

    public SwaggerConfiguration getSwaggerConfiguration() {
        return swaggerConfiguration;
    }

    public DocumentationReader getApiReader() {
        return apiReader;
    }

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
        String fullUrl = String.valueOf(request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE));
        int indexOfApiName = fullUrl.indexOf("/", 1) + 1;
        DocumentationTransformer transformer = swaggerConfiguration.getDocumentationTransformer();
        return (ControllerDocumentation) transformer
                .applySorting(apiReader.getDocumentation(fullUrl.substring(indexOfApiName)));
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        apiReader = new DocumentationReader(swaggerConfiguration,
                WebApplicationContextUtils.getWebApplicationContext(servletContext), handlerMappings);
    }
}
