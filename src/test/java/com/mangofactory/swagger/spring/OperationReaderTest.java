package com.mangofactory.swagger.spring;

import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.swagger.ControllerDocumentation;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.mangofactory.swagger.annotations.ApiError;
import com.mangofactory.swagger.annotations.ApiErrors;
import com.mangofactory.swagger.models.DocumentationSchemaProvider;
import com.mangofactory.swagger.spring.sample.Pet;
import com.mangofactory.swagger.spring.sample.configuration.ServicesTestConfiguration;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.core.DocumentationError;
import com.wordnik.swagger.core.DocumentationOperation;
import com.wordnik.swagger.core.DocumentationParameter;
import com.wordnik.swagger.sample.exception.BadRequestException;
import com.wordnik.swagger.sample.exception.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.server.test.context.WebContextLoader;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.NameValueExpression;
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition;

import java.lang.reflect.Method;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = WebContextLoader.class, classes = ServicesTestConfiguration.class)
public class OperationReaderTest {

    @Autowired private SwaggerConfiguration swaggerConfiguration;
    private HandlerMethod handlerMethod;
    private HandlerMethod handlerMethod2;
    private HandlerMethod handlerMethodWithNoParam;
    private OperationReader methodReader;
    private ControllerDocumentation controllerDocumentation;

    @Before
    public void setup() throws NoSuchMethodException {
        SampleClass instance = new SampleClass();
        Method method = instance.getClass().getMethod("sampleMethod", String.class, String.class, String.class,
                String.class, String.class);
        handlerMethod = new HandlerMethod(instance, method);
        methodReader = new OperationReader(swaggerConfiguration);

        Method method2 = instance.getClass().getMethod("sampleMethod2", Pet.class);
        handlerMethod2 = new HandlerMethod(instance, method2);

        Method methodWithNoParam = instance.getClass().getMethod("methodWithNoParametersWithExpression");
        handlerMethodWithNoParam = new HandlerMethod(instance, methodWithNoParam);

        controllerDocumentation = new ControllerDocumentation(swaggerConfiguration.getApiVersion(),
                swaggerConfiguration.getSwaggerVersion(), swaggerConfiguration.getBasePath(),
                swaggerConfiguration.getDocumentationBasePath(),
                new DocumentationSchemaProvider(new TypeResolver(), swaggerConfiguration));
    }

    @Test
    public void paramDataTypeDetectedCorrectly() {

        DocumentationOperation operation = methodReader.readOperation(controllerDocumentation, handlerMethod,
                new ParamsRequestCondition(), RequestMethod.GET);
        List<DocumentationParameter> parameters = operation.getParameters();
        assertThat(parameters.get(0).dataType(), equalToIgnoringCase("string"));
        assertThat(parameters.get(1).dataType(), equalToIgnoringCase("string"));
    }

    @Test
    public void setsNickanameCorrectly() {
        DocumentationOperation operation = methodReader.readOperation(controllerDocumentation, handlerMethod,
                new ParamsRequestCondition(), RequestMethod.GET);
        assertThat(operation.getNickname(), equalTo("sampleMethod"));
    }

    @Test
    public void apiParamNameTakesFirstPriority() {
        DocumentationOperation operation = methodReader.readOperation(controllerDocumentation, handlerMethod,
                new ParamsRequestCondition(), RequestMethod.GET);
        List<DocumentationParameter> parameters = operation.getParameters();
        assertThat(parameters.size(), equalTo(5));
        assertThat(parameters.get(0).getName(), equalTo("documentationNameA"));
        assertThat(parameters.get(1).getName(), equalTo("mvcNameB"));
        assertThat(parameters.get(2).getName(), equalTo("modelAttributeC"));
        assertThat(parameters.get(4).getName(), equalTo("requestParam1"));

        // Only available if debug data compiled in, so test excluded.
        //		assertThat(parameters.get(3).getName(), equalTo("variableD"));
    }

    @Test
    public void detectsErrorsUsingSwaggerDeclaration() throws NoSuchMethodException {
        DocumentationOperation operation  = getExceptionMethod("exceptionMethodB");
        List<DocumentationError> errors = operation.getErrorResponses();
        assertThat(errors.size(), equalTo(2));
        DocumentationError error = errors.get(0);
        assertThat(error.code(), equalTo(302));
        assertThat(error.reason(), equalTo("Malformed request"));
    }

    @Test
    public void detectsErrorsUsingSpringMVCDeclaration() throws NoSuchMethodException {
        DocumentationOperation operation  = getExceptionMethod("exceptionMethodA");
        List<DocumentationError> errors = operation.getErrorResponses();
        assertThat(errors.size(), equalTo(2));
        DocumentationError error = errors.get(0);
        assertThat(error.code(), equalTo(404));
        assertThat(error.reason(), equalToIgnoringCase("Invalid ID supplied"));
    }

    @Test
    public void detectsErrorsUsingThrowsDeclaration() throws NoSuchMethodException {
        DocumentationOperation operation  = getExceptionMethod("exceptionMethodC");
        List<DocumentationError> errors = operation.getErrorResponses();
        assertThat(errors.size(), equalTo(1));
        DocumentationError error = errors.get(0);
        assertThat(error.code(), equalTo(404));
        assertThat(error.reason(), equalToIgnoringCase("Invalid ID supplied"));
    }

    @Test
    public void detectsApiErrorsDeclaredWithSpringMvcApiErrorList() throws NoSuchMethodException {
        DocumentationOperation operation  = getExceptionMethod("exceptionMethodD");
        List<DocumentationError> errors = operation.getErrorResponses();
        assertThat(errors.size(), equalTo(2));
        DocumentationError error = errors.get(0);
        assertThat(error.code(), equalTo(302));
        assertThat(error.reason(), equalTo("Malformed request"));
    }

    @Test
    public void responseClass() {
        DocumentationOperation operation = methodReader.readOperation(controllerDocumentation, handlerMethod,
                new ParamsRequestCondition(), RequestMethod.GET);
        assertThat(operation.getResponseClass(), equalToIgnoringCase("Pet"));
    }

    @Test
    public void methodWithNoParametersWithExpression() {
        ParamsRequestCondition paramsCondition = new ParamsRequestCondition();
        paramsCondition.getExpressions().add(new NameValueExpression<String>() {
            @Override
            public String getName() {
                return "test";
            }

            @Override
            public String getValue() {
                return "testValue";
            }

            @Override
            public boolean isNegated() {
               return false;
            }
        });
        DocumentationOperation operation = methodReader.readOperation(controllerDocumentation,
                handlerMethodWithNoParam, paramsCondition, RequestMethod.GET);
        assertThat(operation.getResponseClass(), equalTo("Void"));
    }

    @Test
    public void requestParamRequired() {
        DocumentationOperation operation = methodReader.readOperation(controllerDocumentation, handlerMethod,
                new ParamsRequestCondition(), RequestMethod.GET);
        assertEquals(false, operation.getParameters().get(4).getRequired());
    }

    @Test
    public void paramType1() {
        DocumentationOperation operation = methodReader.readOperation(controllerDocumentation, handlerMethod,
                new ParamsRequestCondition(), RequestMethod.GET);
        assertEquals("path", operation.getParameters().get(0).getParamType());
        assertEquals("path", operation.getParameters().get(1).getParamType());
        assertEquals("body", operation.getParameters().get(2).getParamType());
        assertEquals("query", operation.getParameters().get(4).getParamType());
    }

    @Test
    public void paramType() {
        DocumentationOperation operation = methodReader.readOperation(controllerDocumentation, handlerMethod2,
                new ParamsRequestCondition(), RequestMethod.POST);
        assertEquals("body", operation.getParameters().get(0).getParamType());
    }

    /// TEST SUPPORT
    private DocumentationOperation getExceptionMethod(String methodName) throws NoSuchMethodException {
        SampleClass instance = new SampleClass();
        Method method = instance.getClass().getMethod(methodName);
        handlerMethod = new HandlerMethod(instance, method);
        return new OperationReader(swaggerConfiguration).readOperation(controllerDocumentation, handlerMethod,
                new ParamsRequestCondition(), RequestMethod.GET);
    }


    @SuppressWarnings("unused")
    private final class SampleClass {
        public
        @ResponseBody
        Pet sampleMethod(
                @ApiParam(name = "documentationNameA") @PathVariable("mvcNameA") String variableA,
                @PathVariable("mvcNameB") String variableB,
                @ModelAttribute("modelAttributeC") String variableC,
                String variableD, @RequestParam(value = "requestParam1", required = false) String variableE) {
            return new Pet();
        }

        public void sampleMethod2(@ApiParam(name = "com.mangofactory.swagger.spring.sample.Pet") @RequestBody Pet pet) {
        }

        public void methodWithNoParametersWithExpression() {
        }

        @ApiErrors({ NotFoundException.class, BadRequestException.class })
        public void exceptionMethodA() {
        }

		@com.wordnik.swagger.annotations.ApiErrors({
			@com.wordnik.swagger.annotations.ApiError(code=302,reason="Malformed request"),
			@com.wordnik.swagger.annotations.ApiError(code=404,reason="Not found")}
        )
        public void exceptionMethodB() {
        }


        public void exceptionMethodC() throws NotFoundException {
        }

        @ApiErrors(errors = {
                @ApiError(code = 302, reason = "Malformed request"),
                @ApiError(code = 404, reason = "Not found")
        })
        public void exceptionMethodD() {
        }
    }
}
