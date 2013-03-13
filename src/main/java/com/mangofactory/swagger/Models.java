package com.mangofactory.swagger;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import com.google.common.base.Function;
import com.wordnik.swagger.core.DocumentationSchema;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

class Models {
    public static class Fn {
        private Fn() {
            throw new UnsupportedOperationException();
        }
        public static Function<Model, DocumentationSchema> modelToSchema() {
            return new Function<Model, DocumentationSchema>() {
                @Override
                public DocumentationSchema apply(Model input) {
                    ObjectMapper mapper = new ObjectMapper();
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
}
