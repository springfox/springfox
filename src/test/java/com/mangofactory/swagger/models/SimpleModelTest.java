package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.wordnik.swagger.core.DocumentationSchema;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.Map;

import static com.google.common.collect.Maps.*;
import static com.mangofactory.swagger.models.ResolvedTypes.*;
import static org.junit.Assert.*;

public class SimpleModelTest {
    private Map<String, DocumentationSchema> modelMap;

    class SimpleType {
        byte aByte;
        boolean aBoolean;
        short aShort;
        int anInt;
        long aLong;
        float aFloat;
        double aDouble;
        String aString;
        Date date;
        Object anObject;

        public short getaShort() {
            return aShort;
        }

        public void setaShort(short aShort) {
            this.aShort = aShort;
        }

        public byte getaByte() {
            return aByte;
        }

        public void setaByte(byte aByte) {
            this.aByte = aByte;
        }

        public boolean isaBoolean() {
            return aBoolean;
        }

        public void setaBoolean(boolean aBoolean) {
            this.aBoolean = aBoolean;
        }

        public int getAnInt() {
            return anInt;
        }

        public void setAnInt(int anInt) {
            this.anInt = anInt;
        }

        public long getaLong() {
            return aLong;
        }

        public void setaLong(long aLong) {
            this.aLong = aLong;
        }

        public float getaFloat() {
            return aFloat;
        }

        public void setaFloat(float aFloat) {
            this.aFloat = aFloat;
        }

        public double getaDouble() {
            return aDouble;
        }

        public void setaDouble(double aDouble) {
            this.aDouble = aDouble;
        }

        public String getaString() {
            return aString;
        }

        public void setaString(String aString) {
            this.aString = aString;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public Object getAnObject() {
            return anObject;
        }

        public void setAnObject(Object anObject) {
            this.anObject = anObject;
        }
    }

    @Before
    public void setup() {
        modelMap = newHashMap();
        DocumentationSchemaProvider provider = new DocumentationSchemaProvider(new TypeResolver(),
                new SwaggerConfiguration("1.1", "/"));
        modelMap = provider.getModelMap(new Model("SimpleType", asResolvedType(SimpleType.class)));
    }

    @Test
    public void hasExpectedModels() {
        assertEquals(1, modelMap.size());
    }

    @Test
    public void hasASimpleTypeModel() {
        assertTrue(modelMap.containsKey("SimpleType"));
        DocumentationSchema simpleType = modelMap.get("SimpleType");
        assertNotNull(simpleType.getProperties());
        assertEquals(10, simpleType.getProperties().size());
    }

    @Test
    public void schemaHasAStringProperty() {
        DocumentationSchema schema = modelMap.get("SimpleType");
        assertTrue(schema.getProperties().containsKey("aString"));
        DocumentationSchema stringProperty = schema.getProperties().get("aString");
        assertNotNull(stringProperty);
        assertEquals("string", stringProperty.getType());
    }

    @Test
    public void schemaHasAByteProperty() {
        DocumentationSchema schema = modelMap.get("SimpleType");
        assertTrue(schema.getProperties().containsKey("aByte"));
        DocumentationSchema stringProperty = schema.getProperties().get("aByte");
        assertNotNull(stringProperty);
        assertEquals("byte", stringProperty.getType());
    }


    @Test
    public void schemaHasABooleanProperty() {
        DocumentationSchema schema = modelMap.get("SimpleType");
        assertTrue(schema.getProperties().containsKey("aBoolean"));
        DocumentationSchema stringProperty = schema.getProperties().get("aBoolean");
        assertNotNull(stringProperty);
        assertEquals("boolean", stringProperty.getType());
    }

    @Test
    public void schemaHasAIntProperty() {
        DocumentationSchema schema = modelMap.get("SimpleType");
        assertTrue(schema.getProperties().containsKey("anInt"));
        DocumentationSchema stringProperty = schema.getProperties().get("anInt");
        assertNotNull(stringProperty);
        assertEquals("int", stringProperty.getType());
    }


    @Test
    public void schemaHasALongProperty() {
        DocumentationSchema schema = modelMap.get("SimpleType");
        assertTrue(schema.getProperties().containsKey("aLong"));
        DocumentationSchema stringProperty = schema.getProperties().get("aLong");
        assertNotNull(stringProperty);
        assertEquals("long", stringProperty.getType());
    }

    @Test
    public void schemaHasAShortProperty() {
        DocumentationSchema schema = modelMap.get("SimpleType");
        assertTrue(schema.getProperties().containsKey("aShort"));
        DocumentationSchema stringProperty = schema.getProperties().get("aShort");
        assertNotNull(stringProperty);
        assertEquals("int", stringProperty.getType());
    }

    @Test
    public void schemaHasAFloatProperty() {
        DocumentationSchema schema = modelMap.get("SimpleType");
        assertTrue(schema.getProperties().containsKey("aFloat"));
        DocumentationSchema stringProperty = schema.getProperties().get("aFloat");
        assertNotNull(stringProperty);
        assertEquals("float", stringProperty.getType());
    }


    @Test
    public void schemaHasADoubleProperty() {
        DocumentationSchema schema = modelMap.get("SimpleType");
        assertTrue(schema.getProperties().containsKey("aDouble"));
        DocumentationSchema stringProperty = schema.getProperties().get("aDouble");
        assertNotNull(stringProperty);
        assertEquals("double", stringProperty.getType());
    }

    @Test
    public void schemaHasADateProperty() {
        DocumentationSchema schema = modelMap.get("SimpleType");
        assertTrue(schema.getProperties().containsKey("date"));
        DocumentationSchema stringProperty = schema.getProperties().get("date");
        assertNotNull(stringProperty);
        assertEquals("Date", stringProperty.getType());
    }

    @Test
    public void schemaHasAnObjectProperty() {
        DocumentationSchema schema = modelMap.get("SimpleType");
        assertTrue(schema.getProperties().containsKey("anObject"));
        DocumentationSchema stringProperty = schema.getProperties().get("anObject");
        assertNotNull(stringProperty);
        assertEquals("any", stringProperty.getType());
    }

    @Test
    public void resolver() {
        ResolvedType a = new TypeResolver().resolve(Map.class);

    }
}
