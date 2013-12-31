package com.mangofactory.swagger.spring.filters;

import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.swagger.ControllerDocumentation;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.mangofactory.swagger.filters.FilterContext;
import com.mangofactory.swagger.models.AlternateTypeProcessingRule;
import com.mangofactory.swagger.models.DocumentationSchemaProvider;
import com.wordnik.swagger.core.DocumentationOperation;
import com.wordnik.swagger.core.DocumentationSchema;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.net.URL;

import static com.google.common.collect.Lists.*;
import static com.mangofactory.swagger.models.IgnorableTypeRule.ignorable;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OperationFilterTest {

    public class SomeType {
        int foo;

        public int getFoo() {
            return foo;
        }

        public void setFoo(int foo) {
            this.foo = foo;
        }
    }

    public class MyUrl {
        private String host;
        private String port;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }
    }

    public class Methods {
        public SomeType normalMethod() {
            return null;
        }
        public ResponseEntity ignorableMethod() {
            return null;
        }
        public URL alternateTypeMethod() {
            return null;
        }
    }
    private ControllerDocumentation controllerDocumentation;

    private FilterContext<DocumentationOperation> context;
    @Mock private MethodParameter ignoredMethodParameter;
    @Mock private MethodParameter notIgnoredMethodParameter;

    @Before
    public void setup() throws Exception {
        DocumentationOperation operation = new DocumentationOperation();
        controllerDocumentation= new ControllerDocumentation("", "", "", "",
                new DocumentationSchemaProvider(new TypeResolver(), new SwaggerConfiguration("1.1", "/")));
        context = new FilterContext<DocumentationOperation>(operation);
        context.put("controllerDocumentation", controllerDocumentation);

        SwaggerConfiguration configuration = new SwaggerConfiguration("2.0", "/some-path");
        configuration.getTypeProcessingRules().addAll(newArrayList(ignorable(ResponseEntity.class),
                new AlternateTypeProcessingRule(URL.class, MyUrl.class)));
        context.put("swaggerConfiguration", configuration);

        when(ignoredMethodParameter.getParameterType()).thenReturn((Class) ResponseEntity.class);
        when(notIgnoredMethodParameter.getParameterType()).thenReturn((Class) SomeType.class);
    }

    private void setupHandlerMethod(String methodName) throws NoSuchMethodException {
        Methods instance = new Methods();
        Method method = Methods.class.getMethod(methodName);
        HandlerMethod handlerMethod = new HandlerMethod(instance, method);
        context.put("handlerMethod", handlerMethod);
    }

    @Test
    public void whenReturnParameterIsAnIgnorableType() throws NoSuchMethodException {
        OperationFilter filter = new OperationFilter();
        setupHandlerMethod("ignorableMethod");
        filter.apply(context);
        assertEquals(0, controllerDocumentation.getModels().size());
    }

    @Test
    public void whenReturnParameterIsAnAlternateType() throws NoSuchMethodException {
        OperationFilter filter = new OperationFilter();
        setupHandlerMethod("alternateTypeMethod");
        filter.apply(context);
        assertEquals(1, controllerDocumentation.getModels().size());
        assertThat(controllerDocumentation.getModels(), hasKey("MyUrl"));
        DocumentationSchema myUrl = controllerDocumentation.getModels().get("MyUrl");
        assertEquals(myUrl.getProperties().size(), 2);
        assertThat(myUrl.getProperties(), hasKey("host"));
        assertThat(myUrl.getProperties(), hasKey("port"));
    }

    @Test
    public void whenReturnParameterIsANormalType() throws NoSuchMethodException {
        OperationFilter filter = new OperationFilter();
        setupHandlerMethod("normalMethod");

        filter.apply(context);
        assertEquals(1, controllerDocumentation.getModels().size());
    }
}
