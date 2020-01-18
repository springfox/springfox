package springfox.documentation.schema;

import com.fasterxml.classmate.ResolvedType;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractMap;
import java.util.Currency;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.Collections.*;
import static java.util.stream.Collectors.*;

public class ScalarTypes {
  private static final Map<Type, ScalarType> SCALAR_TYPE_LOOKUP = unmodifiableMap(Stream.of(
      new AbstractMap.SimpleEntry<>(Long.TYPE, ScalarType.LONG),
      new AbstractMap.SimpleEntry<>(Short.TYPE, ScalarType.INTEGER),
      new AbstractMap.SimpleEntry<>(Integer.TYPE, ScalarType.INTEGER),
      new AbstractMap.SimpleEntry<>(Double.TYPE, ScalarType.DOUBLE),
      new AbstractMap.SimpleEntry<>(Float.TYPE, ScalarType.FLOAT),
      new AbstractMap.SimpleEntry<>(Byte.TYPE, ScalarType.BYTE),
      new AbstractMap.SimpleEntry<>(Boolean.TYPE, ScalarType.BOOLEAN),
      new AbstractMap.SimpleEntry<>(Character.TYPE, ScalarType.STRING),

      new AbstractMap.SimpleEntry<>(Date.class, ScalarType.DATE),
      new AbstractMap.SimpleEntry<>(java.sql.Date.class, ScalarType.DATE_TIME),
      new AbstractMap.SimpleEntry<>(String.class, ScalarType.STRING),
      new AbstractMap.SimpleEntry<>(Long.class, ScalarType.LONG),
      new AbstractMap.SimpleEntry<>(Short.class, ScalarType.INTEGER),
      new AbstractMap.SimpleEntry<>(Integer.class, ScalarType.INTEGER),
      new AbstractMap.SimpleEntry<>(Double.class, ScalarType.DOUBLE),
      new AbstractMap.SimpleEntry<>(Float.class, ScalarType.FLOAT),
      new AbstractMap.SimpleEntry<>(Byte.class, ScalarType.BYTE),
      new AbstractMap.SimpleEntry<>(Boolean.class, ScalarType.BOOLEAN),
      new AbstractMap.SimpleEntry<>(Character.class, ScalarType.STRING),
      new AbstractMap.SimpleEntry<>(BigDecimal.class, ScalarType.BIGDECIMAL),
      new AbstractMap.SimpleEntry<>(BigInteger.class, ScalarType.BIGINTEGER),
      new AbstractMap.SimpleEntry<>(Currency.class, ScalarType.BIGDECIMAL),
      new AbstractMap.SimpleEntry<>(UUID.class, ScalarType.UUID),
      new AbstractMap.SimpleEntry<>(MultipartFile.class, ScalarType.BINARY))
      .collect(toMap(Map.Entry::getKey, Map.Entry::getValue)));

  private ScalarTypes() {
    throw new UnsupportedOperationException();
  }

  public static Optional<ScalarType> builtInScalarType(Type forType) {
    if (forType instanceof ResolvedType) {
      return builtInScalarTypeForResolvedType((ResolvedType) forType);
    } else {
      return Optional.ofNullable(SCALAR_TYPE_LOOKUP.getOrDefault(
          forType,
          null));
    }
  }

  private static Optional<ScalarType> builtInScalarTypeForResolvedType(ResolvedType forType) {
    return Optional.ofNullable(SCALAR_TYPE_LOOKUP.getOrDefault(
        forType.getErasedType(),
        null));
  }
}
