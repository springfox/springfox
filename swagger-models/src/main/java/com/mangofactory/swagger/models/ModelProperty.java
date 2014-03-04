package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;
import com.wordnik.swagger.model.AllowableValues;
import scala.Option;

public interface ModelProperty {
    String getName();
    ResolvedType getType();
    String typeName(ModelContext modelContext);
    String qualifiedTypeName();
    AllowableValues allowableValues();
    Option<String> propertyDescription();
    boolean isRequired();
}
