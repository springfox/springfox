package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.members.ResolvedField;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider;
import com.wordnik.swagger.annotations.ApiModelProperty;
import com.wordnik.swagger.model.AllowableValues;
import scala.Option;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import static com.mangofactory.swagger.models.ResolvedTypes.*;

public class FieldModelProperty implements ModelProperty {
    private final String name;
    private final ResolvedField childField;
    private final AlternateTypeProvider alternateTypeProvider;

    public FieldModelProperty(String name, ResolvedField childField, AlternateTypeProvider alternateTypeProvider) {
        this.name = name;
        this.childField = childField;
        this.alternateTypeProvider = alternateTypeProvider;
    }

    private ResolvedType realType(ResolvedField field) {
        return field.getType();
    }

    @Override
    public String typeName(ModelContext modelContext) {
        return ResolvedTypes.typeName(getType());
    }

    @Override
    public String qualifiedTypeName() {
        ResolvedType resolvedType = getType();
        if (resolvedType.getTypeParameters().size() > 0) {
            return resolvedType.toString();
        }
        return simpleQualifiedTypeName(resolvedType);
    }

    @Override
    public AllowableValues allowableValues() {
        return ResolvedTypes.allowableValues(getType());
    }

    @Override
    public Option<String> propertyDescription() {
        Optional<ApiModelProperty> modelPropertyAnnotation = modelPropertyAnnotation(childField.getRawMember());
        if (modelPropertyAnnotation.isPresent()) {
            ApiModelProperty annotation = modelPropertyAnnotation.get();
            if (!Strings.isNullOrEmpty(annotation.value())) {
                return Option.apply(annotation.value());
            } else if (!Strings.isNullOrEmpty(annotation.notes())) {
                return Option.apply(annotation.notes());
            }
        }
        return Option.apply(null);
    }

    @Override
    public boolean isRequired() {
        Optional<ApiModelProperty> modelPropertyAnnotation = modelPropertyAnnotation(childField.getRawMember());
        return modelPropertyAnnotation.isPresent() && modelPropertyAnnotation.get().required();
    }

    private Optional<ApiModelProperty> modelPropertyAnnotation(AnnotatedElement annotated) {
        for (Annotation annotation : annotated.getDeclaredAnnotations()) {
            if (annotation instanceof ApiModelProperty) {
                return Optional.of((ApiModelProperty) annotation);
            }
        }
        return Optional.absent();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ResolvedType getType() {
       return alternateTypeProvider.alternateFor(realType(childField));
    }
}
