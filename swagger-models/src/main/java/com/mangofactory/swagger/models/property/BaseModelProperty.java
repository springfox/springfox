package com.mangofactory.swagger.models.property;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Optional;
import com.mangofactory.swagger.models.ModelContext;
import com.mangofactory.swagger.models.ResolvedTypes;
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

import static com.mangofactory.swagger.models.ResolvedTypes.*;
import static com.mangofactory.swagger.models.property.ApiModelProperties.*;

public abstract class BaseModelProperty implements ModelProperty {

  private final Optional<ApiModelProperty> apiModelProperty;
  private final String name;
  private final AlternateTypeProvider alternateTypeProvider;

  public BaseModelProperty(String name, AlternateTypeProvider alternateTypeProvider,
                           Optional<ApiModelProperty> apiModelProperty) {
    this.name = name;
    this.apiModelProperty = apiModelProperty;
    this.alternateTypeProvider = alternateTypeProvider;
  }

  protected abstract ResolvedType realType();

  @Override
  public ResolvedType getType() {
    return alternateTypeProvider.alternateFor(realType());
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String qualifiedTypeName() {
    if (getType().getTypeParameters().size() > 0) {
      return getType().toString();
    }
    return simpleQualifiedTypeName(getType());
  }

  @Override
  public String typeName(ModelContext modelContext) {
    return ResolvedTypes.typeName(getType());
  }

  @Override
  public Optional<List<String>> allowableValues() {
    Optional<List<String>> allowableValues = ResolvedTypes.allowableValues(getType());
    Optional<List<String>> listValues = apiModelProperty.transform(toAllowableList());
    //Preference to inferred allowable values over list values via ApiModelProperty
    if (allowableValues.isPresent()) {
      return allowableValues;
    }
    if (listValues.isPresent()) {
      return listValues;
    }
    return Optional.absent();
  }

  @Override
  public boolean isRequired() {
    return apiModelProperty.transform(toIsRequired()).or(false);
  }


  @Override
  public Optional<String> propertyDescription() {
    String description = getApiModelProperty().transform(toDescription()).orNull();
    return Optional.fromNullable(description);
  }

  protected Optional<ApiModelProperty> getApiModelProperty() {
    return apiModelProperty;
  }


}
