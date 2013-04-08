package com.mangofactory.swagger.spring.filters;

import com.mangofactory.swagger.ControllerDocumentation;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.mangofactory.swagger.SwaggerConfigurationExtension;
import com.mangofactory.swagger.filters.FilterContext;
import com.mangofactory.swagger.models.DocumentationSchemaProvider;
import com.wordnik.swagger.core.DocumentationOperation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

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

    public class Methods {
        public SomeType normalMethod() {
            return null;
        }
        public ResponseEntity ignorableMethod() {
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
        controllerDocumentation= new ControllerDocumentation("", "", "", "", new DocumentationSchemaProvider());
        context = new FilterContext<DocumentationOperation>(operation);
        context.put("controllerDocumentation", controllerDocumentation);

        SwaggerConfiguration configuration = new SwaggerConfiguration();
        SwaggerConfigurationExtension extensions = new SwaggerConfigurationExtension();
        extensions.setIgnorableParameterTypes(newArrayList(String.class, ResponseEntity.class));
        configuration.setExtensions(extensions);
        configuration.afterPropertiesSet();
        context.put("swaggerConfiguration", configuration);

        when(ignoredMethodParameter.getParameterType()).thenReturn((Class) ResponseEntity.class);
        when(notIgnoredMethodParameter.getParameterType()).thenReturn((Class)SomeType.class);
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
    public void whenReturnParameterIsANormalType() throws NoSuchMethodException {
        OperationFilter filter = new OperationFilter();
        setupHandlerMethod("normalMethod");

        filter.apply(context);
        assertEquals(1, controllerDocumentation.getModels().size());
    }
}
