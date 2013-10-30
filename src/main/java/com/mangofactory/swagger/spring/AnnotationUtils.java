package com.mangofactory.swagger.spring;

import org.springframework.core.MethodParameter;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author John Crygier
 */
public class AnnotationUtils {

    /**
     * Find the annotation on this method parameter, traversing it's interfaces and super classes to find extra
     * annotations.  It will find the first instance walking up the hierarchy.
     *
     * @param methodParameter
     * @param annotationType
     * @param <A>
     * @return
     */
    public static <A extends Annotation> A findParameterAnnotation(MethodParameter methodParameter,
                                                                   Class<A> annotationType) {
        Annotation[] allAnnotations = getParameterAnnotations(methodParameter);
        for (Annotation a : allAnnotations) {
            if (a.annotationType().isAssignableFrom(annotationType)) {
                return (A) a;
            }
        }

        return null;
    }

    /**
     * Find All annotations on this method parameter, traversing it's interfaces and super classes to find extra
     * annotations.  All annotations will be included from super classes, only if the type doesn't exist on the lower
     * level class.
     *
     * @param methodParameter
     * @return
     */
    public static Annotation[] getParameterAnnotations(MethodParameter methodParameter) {
        return getParameterAnnotations(methodParameter.getMethod(),
                methodParameter.getParameterIndex(),
                methodParameter.getNestingLevel());
    }
    /**
     * Find All annotations on this method parameter, traversing it's interfaces and super classes to find extra
     * annotations.  All annotations will be included from super classes, only if the type doesn't exist on the lower
     * level class.
     *
     * @param method
     * @param parameterIndex
     * @param nestingLevel
     * @return
     */
    public static Annotation[] getParameterAnnotations(Method method, int parameterIndex, int nestingLevel) {
        final List<Annotation> answer = new ArrayList<Annotation>();

        visitParameters(method, parameterIndex, nestingLevel, new ParameterVisitor() {
            @Override
            public void visitParameter(MethodParameter mp) {
                answer.addAll(Arrays.asList(mp.getParameterAnnotations()));
            }
        });

        return (Annotation[]) answer.toArray(new Annotation[answer.size()]);
    }

    /**
     * Visits all super class and interface classes for this parameter
     */
    private static void visitParameters(MethodParameter methodParameter, ParameterVisitor parameterVisitor) {
        visitParameters(methodParameter.getMethod(),
                methodParameter.getParameterIndex(),
                methodParameter.getNestingLevel(),
                parameterVisitor);
    }

    /**
     * Visits all super class and interface classes for this parameter
     */
    private static void visitParameters(Method method, int parameterIndex,
                                        int nestingLevel, ParameterVisitor parameterVisitor) {
        if (method != null) {
            visitParameters(method.getDeclaringClass(), method.getName(),
                    method.getParameterTypes(), parameterIndex, nestingLevel, parameterVisitor);
        }
    }

    /**
     * Visits all super class and interface classes for this parameter
     */
    private static void visitParameters(Class<?> clazz, String methodName, Class<?>[] methodParameterTypes,
                                        int parameterIndex, int nestingLevel, ParameterVisitor parameterVisitor) {
        // First, find the method, and visit it - if it exists
        Method m = ReflectionUtils.findMethod(clazz, methodName, methodParameterTypes);
        if (m != null) {
            MethodParameter mp = new MethodParameter(m, parameterIndex, nestingLevel);
            parameterVisitor.visitParameter(mp);
        }

        // Now, check it's interfaces - and visit them
        for (Class<?> iface : clazz.getInterfaces()) {
            visitParameters(iface, methodName, methodParameterTypes, parameterIndex, nestingLevel, parameterVisitor);
        }

        // Finally, visit the super class - if it's not Object
        if (clazz.getSuperclass() != null && !Object.class.equals(clazz.getSuperclass())) {
            visitParameters(clazz.getSuperclass(), methodName, methodParameterTypes,
                    parameterIndex, nestingLevel, parameterVisitor);
        }
    }

    /**
     * Helper method to search a list to see if the annotation type exists already.
     *
     * @param annotationList
     * @param annotationType
     * @return
     */
    public static boolean containsAnnotationType(List<Annotation> annotationList,
                                                 Class<? extends Annotation> annotationType) {
        for (Annotation a : annotationList) {
            if (a.getClass().isAssignableFrom(annotationType)) {
                return true;
            }
        }

        return false;
    }

    private static abstract class ParameterVisitor {
        public abstract void visitParameter(MethodParameter mp);
    }

}
