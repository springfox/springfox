package com.mangofactory.swagger.spring.filters;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.swagger.ControllerDocumentation;
import com.mangofactory.swagger.filters.Filter;
import com.mangofactory.swagger.filters.FilterContext;
import com.mangofactory.swagger.models.DocumentationSchemaProvider;
import com.wordnik.swagger.core.DocumentationAllowableListValues;
import com.wordnik.swagger.core.DocumentationParameter;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.*;
import static com.mangofactory.swagger.filters.Filters.Fn.*;
import static com.mangofactory.swagger.models.ResolvedTypes.*;
import static org.junit.Assert.*;

public class ParameterFilterTest {

    class SampleType {
        private SampleEnum sampleEnum;

        public SampleEnum getSampleEnum() {
            return sampleEnum;
        }

        public void setSampleEnum(SampleEnum sampleEnum) {
            this.sampleEnum = sampleEnum;
        }
    }

    enum SampleEnum {
        ONE,
        TWO
    }

    class SampleController
    {
        public void methodWithSupportedPrimitives(byte a, boolean b, int c, long d, float e, double f, String g,
                                               Date h) {

        }

        public void methodWithEnum(SampleEnum sample)  {

        }

        public void methodWithType(SampleType sample)  {

        }

        public void methodWithGenericList(List<String> stringList)  {

        }

        public void methodWithGenericSet(Set<String> stringSet)  {

        }

        public void methodWithPrimitiveArray(String[] stringSet)  {

        }

        public void methodWithTypeArray(SampleType[] stringSet)  {

        }

        public void methodWithEnumArray(SampleEnum[] stringSet)  {

        }

    }

    private HandlerMethod handlerMethod(String methodName, Class... paramTypes) throws NoSuchMethodException {
        SampleController sampleController = new SampleController();
        Method sampleMethod = sampleController.getClass().getMethod(methodName, paramTypes);
        return new HandlerMethod(sampleController, sampleMethod);
    }

    private DocumentationParameter docParam;
    private ControllerDocumentation documentation;
    private FilterContext<DocumentationParameter> context;
    private List<Filter<DocumentationParameter>> paramFilters;
    private static final TypeResolver typeResolver = new TypeResolver();


    @Before
    public void setup() {
        docParam = new DocumentationParameter();
        documentation = new ControllerDocumentation("1", "2", "", "",
                new DocumentationSchemaProvider(new TypeResolver()));
        context = new FilterContext<DocumentationParameter>(docParam);
        context.put("controllerDocumentation", documentation);
        paramFilters = newArrayList();
        paramFilters.add(new ParameterFilter());
    }

/*    @Test
    @SneakyThrows
    public void whenParameterIsAPrimitive() {
        HandlerMethod handlerMethod = handlerMethod("methodWithSupportedPrimitives", byte.class, boolean.class,
                int.class, long.class, float.class, double.class, String.class, Date.class);
        MethodParameter[] methodParams = handlerMethod.getMethodParameters();
        List<ResolvedType> parameterTypes = methodParameters(typeResolver, handlerMethod.getMethod());
        for (int index = 0; index < methodParams.length; index++) {
            DocumentationParameter docParam = new DocumentationParameter();
            ControllerDocumentation documentation = new ControllerDocumentation("1", "2", "", "",
                    new DocumentationSchemaProvider(new TypeResolver()));
            FilterContext context = new FilterContext<DocumentationParameter>(docParam);
            context.put("controllerDocumentation", documentation);
            context.put("methodParameter", methodParams[index]);
            context.put("parameterType", parameterTypes.get(index));


            applyFilters(paramFilters, context);

            assertEquals(0, documentation.getModels().size());
            assertNull(docParam.allowableValues());

        }
    }

    @Test
    @SneakyThrows
    public void whenParameterIsAnEnum() {
        HandlerMethod handlerMethod = handlerMethod("methodWithEnum", SampleEnum.class);
        context.put("methodParameter", handlerMethod.getMethodParameters()[0]);
        context.put("parameterType", methodParameters(typeResolver, handlerMethod.getMethod()).get(0));
        applyFilters(paramFilters, context);

        assertEquals(1, documentation.getModels().size());
        assertTrue(docParam.allowableValues() instanceof DocumentationAllowableListValues);
        assertEquals(2, ((DocumentationAllowableListValues) docParam.allowableValues()).getValues().size());

    }

    @Test
    @SneakyThrows
    public void whenParameterIsAType() {
        HandlerMethod handlerMethod = handlerMethod("methodWithType", SampleType.class);
        context.put("methodParameter", handlerMethod.getMethodParameters()[0]);
        context.put("parameterType", methodParameters(typeResolver, handlerMethod.getMethod()).get(0));
        applyFilters(paramFilters, context);

        assertEquals(2, documentation.getModels().size());
        assertNull(docParam.allowableValues());
    }

    @Test
    @SneakyThrows
    public void whenParameterIsAGenericList() {
        HandlerMethod handlerMethod = handlerMethod("methodWithGenericList", List.class);
        context.put("methodParameter", handlerMethod.getMethodParameters()[0]);
        context.put("parameterType", methodParameters(typeResolver, handlerMethod.getMethod()).get(0));
        applyFilters(paramFilters, context);

        assertEquals(0, documentation.getModels().size());
        assertNull(docParam.allowableValues());
    }

    @Test
    @SneakyThrows
    public void whenParameterIsGenericSet() {
        HandlerMethod handlerMethod = handlerMethod("methodWithGenericSet", Set.class);
        context.put("methodParameter", handlerMethod.getMethodParameters()[0]);
        context.put("parameterType", methodParameters(typeResolver, handlerMethod.getMethod()).get(0));
        applyFilters(paramFilters, context);

        assertEquals(0, documentation.getModels().size());
        assertNull(docParam.allowableValues());
    }

    @Test
    @SneakyThrows
    public void whenArrayTypeIsAPrimitive() {
        HandlerMethod handlerMethod = handlerMethod("methodWithPrimitiveArray", String[].class);
        context.put("methodParameter", handlerMethod.getMethodParameters()[0]);
        context.put("parameterType", methodParameters(typeResolver, handlerMethod.getMethod()).get(0));
        applyFilters(paramFilters, context);

        assertEquals(0, documentation.getModels().size());
        assertNull(docParam.allowableValues());
    }

    @Test
    @SneakyThrows
    public void whenArrayTypeIsAClass() {
        HandlerMethod handlerMethod = handlerMethod("methodWithTypeArray", SampleType[].class);
        context.put("methodParameter", handlerMethod.getMethodParameters()[0]);
        context.put("parameterType", methodParameters(typeResolver, handlerMethod.getMethod()).get(0));
        applyFilters(paramFilters, context);

        assertEquals(2, documentation.getModels().size());
        assertNull(docParam.allowableValues());
    }

    @Test
    @SneakyThrows
    public void whenArrayTypeIsAnEnum() {
        HandlerMethod handlerMethod = handlerMethod("methodWithEnumArray", SampleEnum[].class);
        context.put("methodParameter", handlerMethod.getMethodParameters()[0]);
        context.put("parameterType", methodParameters(typeResolver, handlerMethod.getMethod()).get(0));

        applyFilters(paramFilters, context);

        assertEquals(1, documentation.getModels().size());
        assertNull(docParam.allowableValues());
    }*/

}
