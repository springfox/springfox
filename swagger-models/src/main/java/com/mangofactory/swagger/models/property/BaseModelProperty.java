package com.mangofactory.swagger.models.property;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Optional;
import com.mangofactory.swagger.models.ModelContext;
import com.mangofactory.swagger.models.ResolvedTypes;
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider;
import com.wordnik.swagger.annotations.ApiModelProperty;
import com.wordnik.swagger.model.AllowableListValues;
import com.wordnik.swagger.model.AllowableValues;
import scala.Option;

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
    Optional<AllowableListValues> listValues = apiModelProperty.transform(toAllowableList());
    //Preference to inferred allowable values over list values via ApiModelProperty
    if (allowableValues.isPresent()) {
      return allowableValues.get();
    }
    if (allowableValuesIsEmpty(listValues)) {
      return null;
    }
    return listValues.orNull();
  }

  private boolean allowableValuesIsEmpty(Optional<AllowableListValues> listValues) {
    return !listValues.isPresent() || listValues.get().values().size() == 0;
  }

  @Override
  public boolean isRequired() {
    return apiModelProperty.transform(toIsRequired()).or(false);
  }


  @Override
  public Option<String> propertyDescription() {
    String description = getApiModelProperty().transform(toDescription()).orNull();
    return Option.apply(description);
  }

  protected Optional<ApiModelProperty> getApiModelProperty() {
    return apiModelProperty;
  }


}
