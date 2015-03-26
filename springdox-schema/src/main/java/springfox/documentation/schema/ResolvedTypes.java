package springfox.documentation.schema;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.types.ResolvedArrayType;
import com.fasterxml.classmate.types.ResolvedPrimitiveType;
import springfox.documentation.service.AllowableValues;

import java.lang.reflect.Type;

public class ResolvedTypes {

  private ResolvedTypes() {
    throw new UnsupportedOperationException();
  }

  public static String simpleQualifiedTypeName(ResolvedType type) {
    if (type instanceof ResolvedPrimitiveType) {
      Type primitiveType = type.getErasedType();
      return Types.typeNameFor(primitiveType);
    }
    if (type instanceof ResolvedArrayType) {
      return Types.typeNameFor(type.getArrayElementType().getErasedType());
    }

    return type.getErasedType().getName();
  }

  public static AllowableValues allowableValues(ResolvedType resolvedType) {
    return Enums.allowableValues(resolvedType.getErasedType());
  }
}
