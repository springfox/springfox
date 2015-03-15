package springdox.documentation.builders;

import com.fasterxml.classmate.ResolvedType;
import springdox.documentation.schema.Enums;
import springdox.documentation.schema.ModelProperty;
import springdox.documentation.schema.ModelRef;
import springdox.documentation.service.AllowableListValues;
import springdox.documentation.service.AllowableValues;

public class ModelPropertyBuilder {
  private ResolvedType type;
  private String qualifiedType;
  private int position;
  private Boolean required;
  private String description;
  private AllowableValues allowableValues;
  private ModelRef modelRef;
  private String name;
  private boolean isHidden;

  public ModelPropertyBuilder name(String name) {
    this.name = BuilderDefaults.defaultIfAbsent(name, this.name);
    return this;
  }

  public ModelPropertyBuilder type(ResolvedType type) {
    this.type = BuilderDefaults.defaultIfAbsent(type, this.type);
    return this;
  }

  public ModelPropertyBuilder qualifiedType(String qualifiedType) {
    this.qualifiedType = BuilderDefaults.defaultIfAbsent(qualifiedType, this.qualifiedType);
    return this;
  }

  public ModelPropertyBuilder position(int position) {
    this.position = position;
    return this;
  }

  public ModelPropertyBuilder required(boolean required) {
    this.required = required;
    return this;
  }

  public ModelPropertyBuilder description(String description) {
    this.description = BuilderDefaults.defaultIfAbsent(description, this.description);
    return this;
  }

  public ModelPropertyBuilder allowableValues(AllowableValues allowableValues) {
    if (allowableValues != null) {
      if (allowableValues instanceof AllowableListValues) {
        this.allowableValues = Enums.emptyListValuesToNull((AllowableListValues) allowableValues);
      } else {
        this.allowableValues = allowableValues;
      }
    }
    return this;
  }

  public ModelPropertyBuilder modelRef(ModelRef modelRef) {
    this.modelRef = BuilderDefaults.defaultIfAbsent(modelRef, this.modelRef);
    return this;
  }

  public ModelPropertyBuilder isHidden(boolean isHidden) {
    this.isHidden = isHidden;
    return this;
  }

  public ModelProperty build() {
    return new ModelProperty(name, type, qualifiedType, position, required, isHidden, description, allowableValues,
            modelRef);
  }
}