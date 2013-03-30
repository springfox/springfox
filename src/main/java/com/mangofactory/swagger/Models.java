package com.mangofactory.swagger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import com.google.common.base.Function;
import com.wordnik.swagger.core.DocumentationSchema;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Slf4j
public class Models {
    public static class Fn {
        private Fn() {
            throw new UnsupportedOperationException();
        }
        public static Function<Model, DocumentationSchema> modelToSchema() {
            return new Function<Model, DocumentationSchema>() {
                @Override
                public DocumentationSchema apply(Model input) {
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JsonSchema jsonSchema;
                    DocumentationSchema schema = new DocumentationSchema();
                    schema.setId(input.getName());
                    try {
                        jsonSchema = mapper.generateJsonSchema(input.getType());
                        ObjectWriter writer = mapper.writer();
                        Writer stringWriter = new StringWriter();
                        writer.writeValue(stringWriter, jsonSchema);
                        String schemaAsString = stringWriter.toString();
                        schema.setItems(mapper.readValue(schemaAsString, DocumentationSchema.class));
                        return fixup(schema);
                    } catch (IOException e) {
                        return schema;
                    } catch (StackOverflowError e) {
                        log.error(String.format("Unable to serialize: %s -> %s", input.getName(), input.getType()));
                        return schema;
                    }
                }
            };
        }

        private static DocumentationSchema fixup(DocumentationSchema schema) {
            DocumentationSchema fixup = new DocumentationSchema();
            fixup.setId(schema.getId());
            fixup.setProperties(schema.getItems().getProperties());
            return fixup;
        }
    }

    public static void maybeAddParameterTypeToModels(ControllerDocumentation controllerDocumentation,
                                              Class<?> parameterType, String dataType) {

        if (isKnownType(parameterType)) {
            return;
        }
        if (parameterType.isArray()) {
            String componentType = parameterType.getComponentType().getSimpleName();
            if (isComplexType(parameterType.getComponentType())) {
                controllerDocumentation.putModel(componentType, new Model(String.format("Array[%s]", componentType),
                        parameterType.getComponentType()));
            }
        } else {
            controllerDocumentation.putModel(dataType, new Model(dataType, parameterType));
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
