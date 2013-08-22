package com.mangofactory.swagger.spring;

import com.mangofactory.swagger.filters.Filter;
import com.mangofactory.swagger.filters.FilterContext;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.mangofactory.swagger.spring.filters.ApplicationFilter;
import com.wordnik.swagger.core.Documentation;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
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

@RunWith(MockitoJUnitRunner.class)
public class ApplicationDocumentationFilterTest {
    @Mock private ServletContext servletContext;
    private List<Filter<Documentation>> filters;
    private FilterContext<Documentation> context;
    private Documentation doc;

    class SampleController
    {
        @RequestMapping( value = "/no-classlevel-requestmapping", method = RequestMethod.GET )
        public void sampleMethod()
        {

        }
    }

    @Before
    @SneakyThrows
    public void setup() {
        SampleController sampleController = new SampleController();
        Method sampleMethod = sampleController.getClass().getMethod("sampleMethod");
        HandlerMethod handlerMethod = new HandlerMethod(sampleController, sampleMethod);
        doc = new Documentation();

        context = new FilterContext<Documentation>(doc);
        context.put("servletContext", servletContext);
        context.put("controllerClass", sampleController.getClass());
        context.put("handlerMethod", handlerMethod);
        SwaggerConfiguration config = new SwaggerConfiguration("2.0", "/some-path");
        context.put("swagger", config);

        filters = newArrayList();
        filters.add(new ApplicationFilter());
    }

    @Test
    @SneakyThrows
    public void whenServiceNameEndsWithController()
    {
        applyFilters(filters, context);

        assertThat(doc.getSwaggerVersion(), is(notNullValue()));
        assertThat(doc.swaggerVersion(), equalTo("1.0"));
        assertThat(doc.basePath(), is(notNullValue()));
        assertThat(doc.basePath(), equalTo("/some-path"));
        assertThat(doc.getModels(), is(nullValue()));
        assertThat(doc.getApis(), is(nullValue()));
    }

}
