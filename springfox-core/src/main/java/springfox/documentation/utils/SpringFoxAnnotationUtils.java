/*
 *
 *  Copyright 2019 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package springfox.documentation.utils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Extension of Spring's AnnotationUtils
 */
public abstract class SpringFoxAnnotationUtils {

    /**
     * Traverses whole class hierarchy post-order and returns all annotations of given type
     */
    public static <A extends Annotation, T> List<A> getAnnotationsForClassHierarchy(Class<T> clz, Class<A> annotationType) {

        List<Class<?>> interfaces = Arrays.asList(clz.getInterfaces());
        Class<?> superclass = clz.getSuperclass();

        List<Class<?>> classesToTraverse = new ArrayList<>();
        if (superclass != null) {
            classesToTraverse.add(superclass);
        }
        classesToTraverse.addAll(interfaces);

        List<A> annotations = classesToTraverse
                .stream()
                .flatMap(c -> getAnnotationsForClassHierarchy(c, annotationType).stream())
                .collect(Collectors.toList());

        A currentClassAnnotation = clz.getDeclaredAnnotation(annotationType);

        if (currentClassAnnotation != null) {
            annotations.add(currentClassAnnotation);
        }

        return annotations;

    }



}
