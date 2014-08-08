package com.mangofactory.swagger.models.property.constructor;

import com.fasterxml.classmate.members.ResolvedField;
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider;
import com.mangofactory.swagger.models.property.field.FieldModelProperty;

/**
 * @author fgaule
 * @since 17/07/2014
 */
public class ConstructorModelProperty extends FieldModelProperty {

  public ConstructorModelProperty(String name, ResolvedField childField, AlternateTypeProvider alternateTypeProvider) {
    super(name, childField, alternateTypeProvider);
  }
}
