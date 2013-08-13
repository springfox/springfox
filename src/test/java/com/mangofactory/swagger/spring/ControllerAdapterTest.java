package com.mangofactory.swagger.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.mangofactory.swagger.annotations.ApiIgnore;
import com.mangofactory.swagger.annotations.ApiInclude;
import com.wordnik.swagger.core.Documentation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Map;

import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static com.mangofactory.swagger.spring.UriExtractor.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@Slf4j
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

    class ExampleKey {

        int getI() {
            return i;
        }

        private final int i;

        public ExampleKey(int i) {

            this.i = i;
        }

        @Override
        public String toString() {
            return "ExampleKey{" +
                    "i=" + i +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            ExampleKey that = (ExampleKey) o;

            if (i != that.i) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            return i;
        }
    }

    class ExampleValue {

        private int i;

        ExampleValue() {
        }

        public ExampleValue(int i) {

            this.i = i;
        }

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            ExampleValue that = (ExampleValue) o;

            if (i != that.i) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            return i;
        }

        @Override
        public String toString() {
            return "ExampleValue{" +
                    "i=" + i +
                    '}';
        }
    }


    @Test
    @SneakyThrows
    public void sometest() {
        Map<ExampleKey, ExampleValue> toSerialize =  newHashMap();
        toSerialize.put(new ExampleKey(1), new ExampleValue(1));
        toSerialize.put(new ExampleKey(2), new ExampleValue(2));
        ObjectMapper mapper = new ObjectMapper();
        try {
            StringWriter writer = new StringWriter();
            mapper.writeValue(writer, toSerialize);
            writer.flush();
            ControllerAdapterTest.log.info(writer.toString());

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    @Test
    @SneakyThrows
    public void sometest2() {
        Map<Integer, ExampleValue> toSerialize =  newHashMap();
        toSerialize.put(1, new ExampleValue(1));
        toSerialize.put(2, new ExampleValue(2));
        ObjectMapper mapper = new ObjectMapper();
        try {
            StringWriter writer = new StringWriter();
            mapper.writeValue(writer, toSerialize);
            writer.flush();
            ControllerAdapterTest.log.info(writer.toString());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    @Test
    @SneakyThrows
    public void sometest3() {
        Map<String, Object> toSerialize =  newHashMap();
        toSerialize.put("1", new ExampleValue(1));
        toSerialize.put("2", new ExampleValue(2));
        ObjectMapper mapper = new ObjectMapper();
        try {
            StringWriter writer = new StringWriter();
            mapper.writeValue(writer, toSerialize);
            writer.flush();
            ControllerAdapterTest.log.info(writer.toString());

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Test
    @SneakyThrows
    public void sometest4() {
        Map<String, Object> toSerialize =  newHashMap();
        toSerialize.put("1", new ExampleValue(1));
        toSerialize.put("2", new ExampleValue(2));
        ObjectMapper mapper = new ObjectMapper();
        try {
            StringWriter writer = new StringWriter();
            mapper.writeValue(writer, toSerialize.entrySet());
            writer.flush();
            ControllerAdapterTest.log.info(writer.toString());

        } catch (Throwable e) {
            e.printStackTrace();
        }
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

        @ApiInclude
        @RequestMapping(value = EFFECTIVE, method = RequestMethod.GET)
        @ResponseBody
        public ResponseEntity<Example> included(UriComponentsBuilder builder) {
            return null;
        }

        @ApiIgnore
        @RequestMapping(value = EFFECTIVE, method = RequestMethod.GET)
        @ResponseBody
        public ResponseEntity<Example> ignored(UriComponentsBuilder builder) {
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

        String methodLevelUri = getFirst(methodUris(sampleController.getClass(), handlerMethod), null);

		assertThat(methodLevelUri, is(notNullValue()));
        assertThat(methodLevelUri, is(equalTo("/no-classlevel-requestmapping")));

        String classLevelUri = getFirst(controllerUris(sampleController.getClass()), null);

        assertThat(classLevelUri, is(notNullValue()));
        assertThat(classLevelUri, is(equalTo("/sample-controller")));

	}

    @Test
    @SneakyThrows
    public void assertThatExampleServiceWorksAsExpected()
    {
        ExampleServiceController controller = new ExampleServiceController();
        Method sampleMethod = controller.getClass().getMethod("getEffective", UriComponentsBuilder.class);
        HandlerMethod handlerMethod = new HandlerMethod(controller, sampleMethod);

        String classLevelUri = getFirst(controllerUris(controller.getClass()), null);
        String methodLevelUri = getFirst(methodUris(controller.getClass(), handlerMethod), null);

        assertThat(classLevelUri, is(notNullValue()));
        assertThat(classLevelUri, is(equalTo("/api/examples")));

        assertThat(methodLevelUri, is(notNullValue()));
        assertThat(methodLevelUri, is(equalTo("/api/examples/effective")));
    }


    @Test
    @SneakyThrows
    public void doesNotSkipDocumentationWhenIncludeAnnotationIsAddedToAMethodOfAnExcludedController() {
        ExampleServiceController controller = new ExampleServiceController();
        Method sampleMethod = controller.getClass().getMethod("included", UriComponentsBuilder.class);
        HandlerMethod handlerMethod = new HandlerMethod(controller, sampleMethod);
        SwaggerConfiguration config = new SwaggerConfiguration("2.0", "/some-path");
        config.getExcludedResources().addAll(newArrayList(controllerUris(controller.getClass())));

        ControllerAdapter adapter = new ControllerAdapter(new Documentation(), handlerMethod, config);
        assertFalse(adapter.shouldSkipDocumentation());

    }

    @Test
    @SneakyThrows
    public void doesNotSkipDocumentationWhenIncludeAnnotationIsAdded() {
        ExampleServiceController controller = new ExampleServiceController();
        Method sampleMethod = controller.getClass().getMethod("included", UriComponentsBuilder.class);
        HandlerMethod handlerMethod = new HandlerMethod(controller, sampleMethod);
        SwaggerConfiguration config = new SwaggerConfiguration("2.0", "/some-path");

        ControllerAdapter adapter = new ControllerAdapter(new Documentation(), handlerMethod, config);
        assertFalse(adapter.shouldSkipDocumentation());
    }

    @Test
    @SneakyThrows
    public void skipsDocumentationWhenIgnoreAnnotationIsAddedToAMethodOfAnExcludedController() {
        ExampleServiceController controller = new ExampleServiceController();
        Method sampleMethod = controller.getClass().getMethod("ignored", UriComponentsBuilder.class);
        HandlerMethod handlerMethod = new HandlerMethod(controller, sampleMethod);
        SwaggerConfiguration config = new SwaggerConfiguration("2.0", "/some-path");
        config.getExcludedResources().addAll(newArrayList(controllerUris(controller.getClass())));

        ControllerAdapter adapter = new ControllerAdapter(new Documentation(), handlerMethod, config);
        assertTrue(adapter.shouldSkipDocumentation());
    }

    @Test
    @SneakyThrows
    public void skipsDocumentationWhenNoAnnotationIsAddedToAMethodOfAnExcludedController() {
        ExampleServiceController controller = new ExampleServiceController();
        Method sampleMethod = controller.getClass().getMethod("getEffective", UriComponentsBuilder.class);
        HandlerMethod handlerMethod = new HandlerMethod(controller, sampleMethod);
        SwaggerConfiguration config = new SwaggerConfiguration("2.0", "/some-path");
        config.getExcludedResources().addAll(newArrayList(controllerUris(controller.getClass())));

        ControllerAdapter adapter = new ControllerAdapter(new Documentation(), handlerMethod, config);
        assertTrue(adapter.shouldSkipDocumentation());
    }


    @Test
    @SneakyThrows
    public void skipsDocumentationWhenIgnoreAnnotationIsAddedToAMethod() {
        ExampleServiceController controller = new ExampleServiceController();
        Method sampleMethod = controller.getClass().getMethod("ignored", UriComponentsBuilder.class);
        HandlerMethod handlerMethod = new HandlerMethod(controller, sampleMethod);
        SwaggerConfiguration config = new SwaggerConfiguration("2.0", "/some-path");
        ControllerAdapter adapter = new ControllerAdapter(new Documentation(), handlerMethod, config);
        assertTrue(adapter.shouldSkipDocumentation());
    }

}
