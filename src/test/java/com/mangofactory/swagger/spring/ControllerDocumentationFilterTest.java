package com.mangofactory.swagger.spring;

import com.mangofactory.swagger.SwaggerConfiguration;
import com.mangofactory.swagger.filters.Filter;
import com.mangofactory.swagger.filters.FilterContext;
import com.mangofactory.swagger.spring.filters.EndPointFilter;
import com.wordnik.swagger.core.DocumentationEndPoint;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.ServletContext;
import java.lang.reflect.Method;
import java.util.List;

import static com.google.common.collect.Lists.*;
import static com.mangofactory.swagger.filters.Filters.Fn.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class ControllerDocumentationFilterTest {
    @Mock private ServletContext servletContext;
    private List<Filter<DocumentationEndPoint>> filters;
    private FilterContext<DocumentationEndPoint> context;
    private NoClassLevelRequestMappingController noClassLevelRequestMappingController;
    private HandlerMethod noClassLevelReuestMappingHandlerMethod;
    private WithClassLevelRequestMappingController withClassLevelRequestMappingController;
    private HandlerMethod withClassLevelReuestMappingHandlerMethod;

    class NoClassLevelRequestMappingController
    {
        @RequestMapping( value = "/no-classlevel-requestmapping", method = RequestMethod.GET )
        public void sampleMethod()
        {

        }
    }

    @RequestMapping(value = "/test")
    class WithClassLevelRequestMappingController
    {
        @RequestMapping( value = "/with-classlevel-requestmapping", method = RequestMethod.GET )
        public void sampleMethod()
        {

        }
    }

    @Before
    public void setup() throws NoSuchMethodException {
        noClassLevelRequestMappingController = new NoClassLevelRequestMappingController();
        Method noClassLevelRequestMappingMethod = noClassLevelRequestMappingController.getClass()
                .getMethod("sampleMethod");
        noClassLevelReuestMappingHandlerMethod
                = new HandlerMethod(noClassLevelRequestMappingController, noClassLevelRequestMappingMethod);

        withClassLevelRequestMappingController = new WithClassLevelRequestMappingController();
        Method withClassLevelRequestMappingMethod = withClassLevelRequestMappingController.getClass()
                .getMethod("sampleMethod");
        withClassLevelReuestMappingHandlerMethod
                = new HandlerMethod(withClassLevelRequestMappingController, withClassLevelRequestMappingMethod);

    }

    @Test
    public void whenControllerHasNoRequestMappingSpecified() {
        DocumentationEndPoint endpoint = new DocumentationEndPoint("/no-classlevel-requestmapping", "");
        context = new FilterContext<DocumentationEndPoint>(endpoint);
        context.put("servletContext", servletContext);
        context.put("controllerClass", noClassLevelRequestMappingController.getClass());
        context.put("handlerMethod", noClassLevelReuestMappingHandlerMethod);
        SwaggerConfiguration swaggerConfiguration = new SwaggerConfiguration("2.0", "/some-path");
        context.put("swagger", swaggerConfiguration);
        filters = newArrayList();
        filters.add(new EndPointFilter());

        applyFilters(filters, context);

        assertThat(endpoint.getDescription(), is(notNullValue()));
        assertThat(endpoint.getDescription(), equalTo("No Class Level Request Mapping Controller"));
        assertThat(endpoint.getPath(), is(notNullValue()));
        assertThat(endpoint.getPath(), equalTo("/no-classlevel-requestmapping"));
    }


    @Test
    public void whenControllerHasRequestMappingSpecified() {
        DocumentationEndPoint endpoint = new DocumentationEndPoint("/test/with-classlevel-requestmapping", "");
        context = new FilterContext<DocumentationEndPoint>(endpoint);
        context.put("servletContext", servletContext);
        context.put("controllerClass", withClassLevelRequestMappingController.getClass());
        context.put("handlerMethod", withClassLevelReuestMappingHandlerMethod);
        SwaggerConfiguration swaggerConfiguration = new SwaggerConfiguration("2.0", "/some-path");
        context.put("swagger", swaggerConfiguration);
        filters = newArrayList();
        filters.add(new EndPointFilter());

        applyFilters(filters, context);

        assertThat(endpoint.getDescription(), is(notNullValue()));
        assertThat(endpoint.getDescription(), equalTo("With Class Level Request Mapping Controller"));
        assertThat(endpoint.getPath(), is(notNullValue()));
        assertThat(endpoint.getPath(), equalTo("/test/with-classlevel-requestmapping"));
    }
}
