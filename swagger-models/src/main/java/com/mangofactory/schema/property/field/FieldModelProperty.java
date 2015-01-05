package com.mangofactory.schema.property.field;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.members.ResolvedField;
import com.mangofactory.schema.alternates.AlternateTypeProvider;
import com.mangofactory.schema.property.BaseModelProperty;

public class FieldModelProperty extends BaseModelProperty {

  private final ResolvedField childField;

  public FieldModelProperty(String fieldName,
      ResolvedField childField,
      AlternateTypeProvider alternateTypeProvider) {

    super(fieldName, alternateTypeProvider);
    this.childField = childField;
  }

  @Override
  protected ResolvedType realType() {
    return childField.getType();
  }
}
