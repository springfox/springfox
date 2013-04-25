package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.types.ResolvedArrayType;
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
                                                     ResolvedType parameterType, String dataType, boolean isReturnType) {

        if (isKnownType(parameterType.getErasedType())) {
            return;
        }
        if (parameterType.isArray()) {
            ResolvedArrayType arrayType = (ResolvedArrayType) parameterType;
            String componentType = arrayType.getArrayElementType().getBriefDescription();
            if (isComplexType(arrayType.getArrayElementType().getErasedType())) {
                controllerDocumentation.putModel(componentType, new Model(String.format("Array[%s]", componentType),
                        arrayType.getArrayElementType(), isReturnType));
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

    public static boolean isPrimitive(Class<?> parameterType) {
        return parameterType.isEnum() ||
                parameterType.isPrimitive() ||
                String.class.isAssignableFrom(parameterType) ||
                Date.class.isAssignableFrom(parameterType) ||
                Byte.class.isAssignableFrom(parameterType) ||
                Boolean.class.isAssignableFrom(parameterType) ||
                Integer.class.isAssignableFrom(parameterType) ||
                Long.class.isAssignableFrom(parameterType) ||
                Float.class.isAssignableFrom(parameterType) ||
                Double.class.isAssignableFrom(parameterType);
    }
}
