package com.mangofactory.swagger.spring;

import com.mangofactory.swagger.spring.test.InheritedService;
import com.wordnik.swagger.annotations.ApiParam;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author John Crygier
 */
public class AnnotationUtilsTest {

    @Test
    public void testGetParameterAnnotationsFromInterface() {
        Method m = ReflectionUtils.findMethod(InheritedService.InheritedServiceImpl.class, "getSomething", String.class);
        MethodParameter methodParameter = new MethodParameter(m, 0);
        Annotation[] allAnnotations = AnnotationUtils.getParameterAnnotations(methodParameter);

        assert allAnnotations.length == 1;
        assert allAnnotations[0] instanceof ApiParam;
        assert ((ApiParam) allAnnotations[0]).value().equals("The parameter to do stuff with");
        assert ((ApiParam) allAnnotations[0]).internalDescription().equals("The Coolest Parameter");

        ApiParam apiParam = AnnotationUtils.findParameterAnnotation(methodParameter, ApiParam.class);
        assert apiParam.value().equals("The parameter to do stuff with");
        assert apiParam.internalDescription().equals("The Coolest Parameter");
    }

    @Test
    public void testGetParameterAnnotationsFromSuperClass() {
        Method m = ReflectionUtils.findMethod(InheritedService.InheritedServiceImpl.class, "getSomethingElse", int.class);
        MethodParameter methodParameter = new MethodParameter(m, 0);
        Annotation[] allAnnotations = AnnotationUtils.getParameterAnnotations(methodParameter);

        assert allAnnotations.length == 1;
        assert allAnnotations[0] instanceof ApiParam;
        assert ((ApiParam) allAnnotations[0]).value().equals("Another Parameter");

        ApiParam apiParam = AnnotationUtils.findParameterAnnotation(methodParameter, ApiParam.class);
        assert apiParam.value().equals("Another Parameter");
    }

}
