package springfox.documentation.schema;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static springfox.documentation.schema.WildcardType.*;

public class TwoDimensionalArraySubstitutionRule extends AlternateTypeRule {
  private static final Map<Type, Class> nonPrimitiveVersion = ImmutableMap.<Type, Class>builder()
      .put(Long.TYPE, Long.class)
      .put(Short.TYPE, Short.class)
      .put(Integer.TYPE, Integer.class)
      .put(Double.TYPE, Double.class)
      .put(Float.TYPE, Float.class)
      .put(Byte.TYPE, Byte.class)
      .put(Boolean.TYPE, Boolean.class)
      .put(Character.TYPE, Character.class)
      .build();
  private final TypeResolver resolver;

  public TwoDimensionalArraySubstitutionRule(TypeResolver resolver) {
    super(resolver.arrayType(resolver.arrayType(WildcardType.class)),
        resolver.resolve(List.class, resolver.resolve(List.class, WildcardType.class)));
    this.resolver = resolver;
  }

  @Override
  public ResolvedType alternateFor(ResolvedType type) {
    if (appliesTo(type)) {
      Optional<ResolvedArray> elementType = arrayElementType(0, type);
      ResolvedType arrayElement = elementType.get().getElementType();
      if (arrayElement.isPrimitive()) {
        return resolver.resolve(List.class,
            resolver.resolve(List.class, nonPrimitiveVersion.get(arrayElement.getErasedType())));
      }
      return resolver.resolve(List.class,resolver.resolve(List.class, arrayElement));
    }
    return type;
  }

  @Override
  public boolean appliesTo(ResolvedType type) {
    Optional<ResolvedArray> elementType = arrayElementType(0, type);
    return elementType.isPresent() && elementType.get().getDimensions() == 2;
  }
}
