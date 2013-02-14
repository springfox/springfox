package com.mangofactory.swagger.spring.controller;

import com.mangofactory.swagger.ControllerDocumentation;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.mangofactory.swagger.spring.DocumentationReader;
import com.wordnik.swagger.core.Documentation;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping('/' + DocumentationController.CONTROLLER_ENDPOINT)
public class DocumentationController implements InitializingBean {

    public static final String CONTROLLER_ENDPOINT = "api-docs";
    @Autowired
    @Getter
    @Setter
    private SwaggerConfiguration swaggerConfiguration;
    @Autowired
    private WebApplicationContext wac;
    @Getter
    private DocumentationReader apiReader;

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    Documentation getResourceListing() {
        return apiReader.getDocumentation();
    }

    @RequestMapping(value = "/**", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    ControllerDocumentation getApiDocumentation(HttpServletRequest request) {
        String fullUrl = String.valueOf(request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE));
        int indexOfApiName = fullUrl.indexOf("/", 2) + 1;
        return apiReader.getDocumentation(fullUrl.substring(indexOfApiName));
    }

    // TODO :
    // Initializing apiReader here so that consumers only have
    // to declare a single bean, rather than many.
    // A better approach would be to use a custom xml declaration
    // and parser - like <swagger:documentation ... />
    public void afterPropertiesSet() throws Exception {
        apiReader = new DocumentationReader(wac, swaggerConfiguration);
    }

}
