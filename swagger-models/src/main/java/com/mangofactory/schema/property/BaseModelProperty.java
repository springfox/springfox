package com.mangofactory.schema.property;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Optional;
import com.mangofactory.schema.ResolvedTypes;
import com.mangofactory.schema.alternates.AlternateTypeProvider;
import com.mangofactory.service.model.AllowableValues;

import static com.mangofactory.schema.ResolvedTypes.*;

public abstract class BaseModelProperty implements ModelProperty {

  private final String name;
  private final AlternateTypeProvider alternateTypeProvider;

  public BaseModelProperty(String name, AlternateTypeProvider alternateTypeProvider) {
    this.name = name;
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
  public AllowableValues allowableValues() {
    Optional<AllowableValues> allowableValues = Optional.fromNullable(ResolvedTypes.allowableValues(getType()));
    //Preference to inferred allowable values over list values via ApiModelProperty
    if (allowableValues.isPresent()) {
      return allowableValues.get();
    }
    return null;
  }

  @Override
  public boolean isRequired() {
    return false;
  }


  @Override
  public String propertyDescription() {
    return null;
  }

  @Override
  public int position() {
    return 0;
  }
}
