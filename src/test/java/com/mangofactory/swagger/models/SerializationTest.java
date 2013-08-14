package com.mangofactory.swagger.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY, fieldVisibility = JsonAutoDetect
        .Visibility.PUBLIC_ONLY)
@JsonIgnoreProperties(value = {"ignoreProperty1"})
public class SerializationTest {
    private String privateField;
    public String publicField;
    @JsonIgnore
    public String ignoredPublicField;
    @JsonProperty
    private String includedPrivateField;

    private String ignoreProperty1;

//        @JsonProperty
    @JsonProperty(value = "otherName")
    private String renamedPrivateField;

    private String privateProperty;
    private String deserializationProperty;

    public SerializationTest() {
    }

    public SerializationTest(String privateField, String publicField, String ignoredPublicField,
                             String includedPrivateField, String privateProperty, String renamedPrivateField) {
        this.privateField = privateField;
        this.publicField = publicField;
        this.ignoredPublicField = ignoredPublicField;
        this.includedPrivateField = includedPrivateField;
        this.privateProperty = privateProperty;
        this.renamedPrivateField = renamedPrivateField;
    }

    public String getPrivateProperty() {
        return privateProperty;
    }

    void setPrivateProperty(String privateProperty) {
        this.privateProperty = privateProperty;
    }

    String getDeserializationProperty() {
        return deserializationProperty;
    }

    @JsonProperty
    void setDeserializationProperty(String deserializationProperty) {
        this.deserializationProperty = deserializationProperty;
    }

    public String getIgnoreProperty1() {
        return ignoreProperty1;
    }

    public void setIgnoreProperty1(String ignoreProperty1) {
        this.ignoreProperty1 = ignoreProperty1;
    }
}
