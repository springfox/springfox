package springdox.documentation.swagger.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Qualifier;
import springdox.documentation.schema.ModelRef;
import springdox.documentation.swagger.dto.DataType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Mapper
public class DataTypeMapper {
  @ResponseTypeName
  public String responseTypeName(ModelRef modelRef) {
    if (modelRef == null) {
      return null;
    }
    if (modelRef.isCollection()) {
      return "array";
    }
    return modelRef.getType();
  }

  @OperationType
  public DataType operationTypeFromModelRef(ModelRef modelRef) {
    if (modelRef !=null) {
      return new DataType(operationTypeName(modelRef));
    }
    return null;
  }

  @Type
  public DataType typeFromModelRef(ModelRef modelRef) {
    if (modelRef != null) {
      if (modelRef.isCollection()) {
        return new DataType(String.format("%s[%s]", modelRef.getType(), modelRef.getItemType()));
      }
      return new DataType(modelRef.getType());
    }
    return null;
  }

  private String operationTypeName(ModelRef modelRef) {
    if (modelRef == null) {
      return null;
    }
    if (modelRef.isCollection()) {
      return String.format("%s[%s]", modelRef.getType(), modelRef.getItemType());
    }
    return modelRef.getType();
  }

  @Qualifier
  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.SOURCE)
  public @interface OperationType {
  }

  @Qualifier
  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.SOURCE)
  public @interface Type {
  }

  @Qualifier
  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.SOURCE)
  public @interface ResponseTypeName {
  }

  @Qualifier
  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.SOURCE)
  public @interface ItemType {
  }
}
