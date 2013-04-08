package com.mangofactory.swagger.models;

import com.google.common.base.Function;
import com.mangofactory.swagger.ControllerDocumentation;
import com.wordnik.swagger.core.DocumentationSchema;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class Models {
    public static class Fn {
        private Fn() {
            throw new UnsupportedOperationException();
        }
        public static Function<Model, Map<String, DocumentationSchema>>
            modelToSchema(final DocumentationSchemaProvider provider) {
            return new Function<Model, Map<String, DocumentationSchema>>() {
                @Override
                public Map<String, DocumentationSchema> apply(Model input) {
                    return provider.getModelMap(input);
                }
            };
        }
    }

    public static void maybeAddParameterTypeToModels(ControllerDocumentation controllerDocumentation,
                                                     Class<?> parameterType, String dataType, boolean isReturnType) {

        if (isKnownType(parameterType)) {
            return;
        }
        if (parameterType.isArray()) {
            String componentType = parameterType.getComponentType().getSimpleName();
            if (isComplexType(parameterType.getComponentType())) {
                controllerDocumentation.putModel(componentType, new Model(String.format("Array[%s]", componentType),
                        parameterType.getComponentType(), isReturnType));
            }
        } else {
            controllerDocumentation.putModel(dataType, new Model(dataType, parameterType, isReturnType));
        }
    }

    private static boolean isKnownType(Class<?> parameterType) {
        return parameterType.isAssignableFrom(List.class) ||
                parameterType.isAssignableFrom(Set.class) ||
                parameterType.isPrimitive() ||
                parameterType.isEnum() ||
                parameterType.isAssignableFrom(String.class) ||
                parameterType.isAssignableFrom(Date.class);
    }

    private static boolean isComplexType(Class<?> parameterType) {
        return !parameterType.isEnum() &&
                !parameterType.isPrimitive() &&
                !parameterType.isArray() &&
                !parameterType.isAssignableFrom(List.class) &&
                !parameterType.isAssignableFrom(Set.class) &&
                !parameterType.isAssignableFrom(String.class) &&
                !parameterType.isAssignableFrom(Date.class);
    }
}
