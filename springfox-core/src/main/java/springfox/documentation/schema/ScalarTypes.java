package springfox.documentation.schema;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.types.ResolvedArrayType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ScalarTypes {
  private static final Map<Type, ScalarType> SCALAR_TYPE_LOOKUP = new HashMap<>();

  static {
    SCALAR_TYPE_LOOKUP.put(Long.TYPE, ScalarType.LONG);
    SCALAR_TYPE_LOOKUP.put(Short.TYPE, ScalarType.INTEGER);
    SCALAR_TYPE_LOOKUP.put(Integer.TYPE, ScalarType.INTEGER);
    SCALAR_TYPE_LOOKUP.put(Double.TYPE, ScalarType.DOUBLE);
    SCALAR_TYPE_LOOKUP.put(Float.TYPE, ScalarType.FLOAT);
    SCALAR_TYPE_LOOKUP.put(Byte.TYPE, ScalarType.BYTE);
    SCALAR_TYPE_LOOKUP.put(Boolean.TYPE, ScalarType.BOOLEAN);
    SCALAR_TYPE_LOOKUP.put(Character.TYPE, ScalarType.STRING);
    SCALAR_TYPE_LOOKUP.put(Date.class, ScalarType.DATE_TIME);
    SCALAR_TYPE_LOOKUP.put(java.sql.Date.class, ScalarType.DATE);
    SCALAR_TYPE_LOOKUP.put(String.class, ScalarType.STRING);
    SCALAR_TYPE_LOOKUP.put(Long.class, ScalarType.LONG);
    SCALAR_TYPE_LOOKUP.put(Short.class, ScalarType.INTEGER);
    SCALAR_TYPE_LOOKUP.put(Integer.class, ScalarType.INTEGER);
    SCALAR_TYPE_LOOKUP.put(Double.class, ScalarType.DOUBLE);
    SCALAR_TYPE_LOOKUP.put(Float.class, ScalarType.FLOAT);
    SCALAR_TYPE_LOOKUP.put(Byte.class, ScalarType.BYTE);
    SCALAR_TYPE_LOOKUP.put(Boolean.class, ScalarType.BOOLEAN);
    SCALAR_TYPE_LOOKUP.put(Character.class, ScalarType.STRING);
    SCALAR_TYPE_LOOKUP.put(BigDecimal.class, ScalarType.BIGDECIMAL);
    SCALAR_TYPE_LOOKUP.put(BigInteger.class, ScalarType.BIGINTEGER);
    SCALAR_TYPE_LOOKUP.put(Currency.class, ScalarType.BIGDECIMAL);
    SCALAR_TYPE_LOOKUP.put(UUID.class, ScalarType.UUID);
    SCALAR_TYPE_LOOKUP.put(MultipartFile.class, ScalarType.BINARY);
    SCALAR_TYPE_LOOKUP.put(FilePart.class, ScalarType.BINARY);
    SCALAR_TYPE_LOOKUP.put(File.class, ScalarType.BINARY);
    SCALAR_TYPE_LOOKUP.put(URL.class, ScalarType.URL);
    SCALAR_TYPE_LOOKUP.put(URI.class, ScalarType.URI);
    SCALAR_TYPE_LOOKUP.put(Object.class, ScalarType.OBJECT);
  }

  private ScalarTypes() {
    throw new UnsupportedOperationException();
  }

  public static Optional<ScalarType> builtInScalarType(Type forType) {
    if (forType instanceof ResolvedType) {
      return builtInScalarTypeForResolvedType((ResolvedType) forType);
    } else {
      return Optional.ofNullable(SCALAR_TYPE_LOOKUP.get(forType));
    }
  }

  private static Optional<ScalarType> builtInScalarTypeForResolvedType(ResolvedType forType) {
    if (forType instanceof ResolvedArrayType) {
      if (forType.getArrayElementType().getErasedType() == Byte.class
          || forType.getArrayElementType().getErasedType() == byte.class) {
        return Optional.of(ScalarType.BYTE);
      }
    }
    return Optional.ofNullable(SCALAR_TYPE_LOOKUP.get(forType.getErasedType()));
  }
}
