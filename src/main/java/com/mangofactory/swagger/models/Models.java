package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.types.ResolvedArrayType;
import com.google.common.base.Function;
import com.mangofactory.swagger.ControllerDocumentation;
import com.wordnik.swagger.core.DocumentationSchema;

import java.util.Date;
import java.util.Map;

import static com.mangofactory.swagger.models.ResolvedTypes.*;

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

    public static boolean maybeAddParameterTypeToModels(ControllerDocumentation controllerDocumentation,
            ResolvedType parameterType, String dataType, boolean isReturnType) {

        if (isPrimitive(parameterType.getErasedType()) || isObject(parameterType.getErasedType())) {
            return false;
        }
        ResolvedTypeMemberSource member = new ResolvedTypeMemberSource(parameterType);
        if (parameterType.isArray()) {
            ResolvedArrayType arrayType = (ResolvedArrayType) parameterType;
            String componentType = modelName(arrayType.getArrayElementType());
            if (!isPrimitive(arrayType.getArrayElementType().getErasedType())) {
                controllerDocumentation.putModel(componentType, new Model(String.format("Array[%s]", componentType),
                        arrayType.getArrayElementType(), isReturnType));
                return true;
            }
        } else if (ResolvedCollection.isList(member)) {
            ResolvedType elementType = ResolvedCollection.listElementType(member);
            String componentType = modelName(elementType);
            if (!isPrimitive(elementType.getErasedType())) {
                controllerDocumentation.putModel(componentType, new Model(String.format("List[%s]", componentType),
                        elementType, isReturnType));
                return true;
            }
        } else if (ResolvedCollection.isSet(member)) {
            ResolvedType elementType = ResolvedCollection.setElementType(member);
            String componentType = modelName(elementType);
            if (!isPrimitive(elementType.getErasedType())) {
                controllerDocumentation.putModel(componentType, new Model(String.format("Set[%s]", componentType),
                        elementType, isReturnType));
                return true;
            }
        } else {
            controllerDocumentation.putModel(dataType, new Model(dataType, parameterType, isReturnType));
            return true;
        }
        return false;
    }

    private static boolean isObject(Class<?> erasedType) {
        return erasedType == Object.class;
    }

    public static boolean isPrimitive(Class<?> parameterType) {
        return  parameterType.isPrimitive() ||
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
