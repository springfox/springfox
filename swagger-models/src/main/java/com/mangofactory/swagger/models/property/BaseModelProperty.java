package com.mangofactory.swagger.models.property;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Optional;
import com.mangofactory.swagger.models.ModelContext;
import com.mangofactory.swagger.models.ResolvedTypes;
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider;
import com.wordnik.swagger.annotations.ApiModelProperty;
import com.mangofactory.swagger.models.dto.AllowableListValues;
import com.mangofactory.swagger.models.dto.AllowableValues;

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
  public AllowableValues allowableValues() {
    Optional<AllowableValues> allowableValues = Optional.fromNullable(ResolvedTypes.allowableValues(getType()));
    Optional<AllowableValues> listValues = apiModelProperty.transform(toAllowableValues());
    //Preference to inferred allowable values over list values via ApiModelProperty
    if (allowableValues.isPresent()) {
      return allowableValues.get();
    }
    if (allowableValuesIsEmpty(listValues)) {
      return null;
    }
    return listValues.orNull();
  }

  private boolean allowableValuesIsEmpty(Optional<AllowableValues> allowableValues) {
    if (allowableValues.isPresent()) {
      AllowableValues allowable = allowableValues.get();
      return allowable instanceof AllowableListValues && ((AllowableListValues) allowable).getValues().size() == 0;
    }
    return true;
  }

  @Override
  public boolean isRequired() {
    return apiModelProperty.transform(toIsRequired()).or(false);
  }


  @Override
  public boolean isHidden() {
    return apiModelProperty.transform(toHidden()).or(false);
  }

  @Override
  public String propertyDescription() {
    return getApiModelProperty().transform(toDescription()).orNull();
  }

  protected Optional<ApiModelProperty> getApiModelProperty() {
    return apiModelProperty;
  }

  @Override
  public int position() {
    return apiModelProperty.transform(toPosition()).or(0);
  }
}
