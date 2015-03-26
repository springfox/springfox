package springfox.documentation.schema.property.field;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.members.ResolvedField;
import springfox.documentation.schema.property.BaseModelProperty;
import springfox.documentation.spi.schema.AlternateTypeProvider;

public class FieldModelProperty extends BaseModelProperty {

  private final ResolvedField childField;

  public FieldModelProperty(String fieldName,
                            ResolvedField childField, AlternateTypeProvider alternateTypeProvider) {

    super(fieldName, alternateTypeProvider);
    this.childField = childField;
  }

  @Override
  protected ResolvedType realType() {
    return childField.getType();
  }
}
