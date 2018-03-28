/*
 *
 *  Copyright 2016 the original author or authors.
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

package springfox.documentation.spring.web.readers.operation.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Downsized version of io.swagger.util.ReflectionUtils
 */
public class ReflectionUtils {

    /**
     * Returns overridden method from superclass if it exists. If method was not found returns null.
     *
     * @param method is method to find
     * @return overridden method from superclass
     */
    public static Method getOverriddenMethod(Method method) {
        Class<?> declaringClass = method.getDeclaringClass();
        Class<?> superClass = declaringClass.getSuperclass();
        Method result = null;
        if (superClass != null && !(superClass.equals(Object.class))) {
            result = findMethod(method, superClass);
        }
        if (result == null) {
            for (Class<?> anInterface : declaringClass.getInterfaces()) {
                result = findMethod(method, anInterface);
                if (result != null) {
                    return result;
                }
            }
        }
        return result;
    }

    /**
     * Searches the method methodToFind in given class cls. If the method is found returns it, else return null.
     *
     * @param methodToFind is the method to search
     * @param cls          is the class or interface where to search
     * @return method if it is found
     */
    public static Method findMethod(Method methodToFind, Class<?> cls) {
        if (cls == null) {
            return null;
        }
        String methodToSearch = methodToFind.getName();
        Class<?>[] soughtForParameterType = methodToFind.getParameterTypes();
        Type[] soughtForGenericParameterType = methodToFind.getGenericParameterTypes();
        for (Method method : cls.getMethods()) {
            if (method.getName().equals(methodToSearch) && method.getReturnType().isAssignableFrom(methodToFind.getReturnType())) {
                Class<?>[] srcParameterTypes = method.getParameterTypes();
                Type[] srcGenericParameterTypes = method.getGenericParameterTypes();
                if (soughtForParameterType.length == srcParameterTypes.length &&
                        soughtForGenericParameterType.length == srcGenericParameterTypes.length) {
                    if (hasIdenticalParameters(srcParameterTypes, soughtForParameterType, srcGenericParameterTypes, soughtForGenericParameterType)) {
                        return method;
                    }
                }
            }
        }
        return null;
    }

    private static boolean hasIdenticalParameters(Class<?>[] srcParameterTypes, Class<?>[] soughtForParameterType,
                                                  Type[] srcGenericParameterTypes, Type[] soughtForGenericParameterType) {
        for (int j = 0; j < soughtForParameterType.length; j++) {
            Class<?> parameterType = soughtForParameterType[j];
            if (!(srcParameterTypes[j].equals(parameterType) || (!srcGenericParameterTypes[j].equals(soughtForGenericParameterType[j]) &&
                    srcParameterTypes[j].isAssignableFrom(parameterType)))) {
                return false;
            }
        }
        return true;
    }

    public static Annotation[][] getParameterAnnotations(Method method) {
        Annotation[][] methodAnnotations = method.getParameterAnnotations();
        Method overriddenmethod = getOverriddenMethod(method);

        if (overriddenmethod != null) {
            Annotation[][] overriddenAnnotations = overriddenmethod
                    .getParameterAnnotations();

            for (int i = 0; i < methodAnnotations.length; i++) {
                List<Type> types = new ArrayList<Type>();
                for (int j = 0; j < methodAnnotations[i].length; j++) {
                    types.add(methodAnnotations[i][j].annotationType());
                }
                for (int j = 0; j < overriddenAnnotations[i].length; j++) {
                    if (!types.contains(overriddenAnnotations[i][j]
                            .annotationType())) {
                        methodAnnotations[i] = ArrayUtils.add(
                                methodAnnotations[i],
                                overriddenAnnotations[i][j]);
                    }
                }

            }
        }
        return methodAnnotations;
    }
}
