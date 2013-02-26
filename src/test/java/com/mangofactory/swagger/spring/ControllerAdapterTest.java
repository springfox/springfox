package com.mangofactory.swagger.spring;

import com.wordnik.swagger.core.Documentation;
import lombok.SneakyThrows;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Method;

import static com.mangofactory.swagger.spring.UriExtractor.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class ControllerAdapterTest
{
	class SampleController
	{
		@RequestMapping( value = "/no-classlevel-requestmapping", method = RequestMethod.GET )
		public void sampleMethod()
		{

		}
	}

    class Example {

    }

    @Controller
    @RequestMapping("api/examples")
    public class ExampleServiceController {

        private static final String EFFECTIVE = "/effective";

        @RequestMapping(value = EFFECTIVE, method = RequestMethod.GET)
        @ResponseBody
        public ResponseEntity<Example> getEffective(UriComponentsBuilder builder) {
            return null;
        }

    }

	@Test
	@SneakyThrows
	public void assertHandleNoClassLevelRequestMapping()
	{
		SampleController sampleController = new SampleController();
		Method sampleMethod = sampleController.getClass().getMethod("sampleMethod");
		HandlerMethod handlerMethod = new HandlerMethod(sampleController, sampleMethod);

        String methodLevelUri = getMethodLevelUri(sampleController.getClass(), handlerMethod);

		assertThat(methodLevelUri, is(notNullValue()));
        assertThat(methodLevelUri, is(equalTo("/no-classlevel-requestmapping")));

        String classLevelUri = getClassLevelUri(sampleController.getClass());

        assertThat(classLevelUri, is(notNullValue()));
        assertThat(classLevelUri, is(equalTo("/")));

	}

    @Test
    @SneakyThrows
    public void assertThatExampleServiceWorksAsExpected()
    {
        ExampleServiceController controller = new ExampleServiceController();
        Method sampleMethod = controller.getClass().getMethod("getEffective", UriComponentsBuilder.class);
        HandlerMethod handlerMethod = new HandlerMethod(controller, sampleMethod);

        String classLevelUri = getClassLevelUri(controller.getClass());
        String methodLevelUri = getMethodLevelUri(controller.getClass(), handlerMethod);

        assertThat(classLevelUri, is(notNullValue()));
        assertThat(classLevelUri, is(equalTo("/api/examples")));

        assertThat(methodLevelUri, is(notNullValue()));
        assertThat(methodLevelUri, is(equalTo("/api/examples/effective")));
    }

}
