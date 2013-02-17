package com.mangofactory.swagger.spring.filters;

import com.mangofactory.swagger.ControllerDocumentation;
import com.mangofactory.swagger.filters.Filter;
import com.mangofactory.swagger.filters.FilterContext;
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

import static com.google.common.collect.Lists.newArrayList;
import static com.mangofactory.swagger.filters.Filters.Fn.*;
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

    @Before
    public void setup() {
        docParam = new DocumentationParameter();
        documentation = new ControllerDocumentation("1", "2", "", "");
        context = new FilterContext<DocumentationParameter>(docParam);
        context.put("controllerDocumentation", documentation);
        paramFilters = newArrayList();
        paramFilters.add(new ParameterFilter());
    }

    @Test
    @SneakyThrows
    public void whenParameterIsAPrimitive() {
        MethodParameter[] methodParameters = handlerMethod("methodWithSupportedPrimitives", byte.class, boolean.class,
                int.class, long.class, float.class, double.class, String.class, Date.class).getMethodParameters();
        for (int index = 0; index < methodParameters.length; index++) {
            DocumentationParameter docParam = new DocumentationParameter();
            ControllerDocumentation documentation = new ControllerDocumentation("1", "2", "", "");
            FilterContext context = new FilterContext<DocumentationParameter>(docParam);
            context.put("controllerDocumentation", documentation);
            context.put("methodParameter", methodParameters[index]);


            applyFilters(paramFilters, context);

            assertEquals(0, documentation.getModels().size());
            assertNull(docParam.allowableValues());

        }
    }

    @Test
    @SneakyThrows
    public void whenParameterIsAnEnum() {
        context.put("methodParameter", handlerMethod("methodWithEnum", SampleEnum.class).getMethodParameters()[0]);
        applyFilters(paramFilters, context);

        assertEquals(0, documentation.getModels().size());
        assertTrue(docParam.allowableValues() instanceof DocumentationAllowableListValues);
        assertEquals(2, ((DocumentationAllowableListValues) docParam.allowableValues()).getValues().size());

    }

    @Test
    @SneakyThrows
    public void whenParameterIsAType() {
        context.put("methodParameter", handlerMethod("methodWithType", SampleType.class).getMethodParameters()[0]);
        applyFilters(paramFilters, context);

        assertEquals(1, documentation.getModels().size());
        assertNull(docParam.allowableValues());
    }

    @Test
    @SneakyThrows
    public void whenParameterIsAGenericList() {
        context.put("methodParameter", handlerMethod("methodWithGenericList", List.class).getMethodParameters()[0]);
        applyFilters(paramFilters, context);

        assertEquals(0, documentation.getModels().size());
        assertNull(docParam.allowableValues());
    }

    @Test
    @SneakyThrows
    public void whenParameterIsGenericSet() {
        context.put("methodParameter", handlerMethod("methodWithGenericSet", Set.class).getMethodParameters()[0]);
        applyFilters(paramFilters, context);

        assertEquals(0, documentation.getModels().size());
        assertNull(docParam.allowableValues());
    }

    @Test
    @SneakyThrows
    public void whenArrayTypeIsAPrimitive() {
        context.put("methodParameter", handlerMethod("methodWithPrimitiveArray", String[].class)
                .getMethodParameters()[0]);
        applyFilters(paramFilters, context);

        assertEquals(0, documentation.getModels().size());
        assertNull(docParam.allowableValues());
    }

    @Test
    @SneakyThrows
    public void whenArrayTypeIsAClass() {
        context.put("methodParameter", handlerMethod("methodWithTypeArray", SampleType[].class)
                .getMethodParameters()[0]);
        applyFilters(paramFilters, context);

        assertEquals(1, documentation.getModels().size());
        assertNull(docParam.allowableValues());
    }

    @Test
    @SneakyThrows
    public void whenArrayTypeIsAnEnum() {
        context.put("methodParameter", handlerMethod("methodWithEnumArray", SampleEnum[].class)
                .getMethodParameters()[0]);

        applyFilters(paramFilters, context);

        assertEquals(0, documentation.getModels().size());
        assertNull(docParam.allowableValues());
    }

}
