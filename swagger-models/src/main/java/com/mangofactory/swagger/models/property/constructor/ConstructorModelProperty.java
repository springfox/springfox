package com.mangofactory.swagger.models.property.constructor;

import com.fasterxml.classmate.members.ResolvedField;
import com.mangofactory.swagger.models.NamingStrategy;
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider;
import com.mangofactory.swagger.models.property.field.FieldModelProperty;

public class ConstructorModelProperty extends FieldModelProperty {

  public ConstructorModelProperty(String name, ResolvedField childField, AlternateTypeProvider alternateTypeProvider,
                                  NamingStrategy namingStrategy                               ) {
    super(name, childField, alternateTypeProvider, namingStrategy);
  }
}
