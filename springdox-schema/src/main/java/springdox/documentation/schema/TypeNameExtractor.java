package springdox.documentation.schema;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.types.ResolvedArrayType;
import com.fasterxml.classmate.types.ResolvedObjectType;
import com.fasterxml.classmate.types.ResolvedPrimitiveType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import springdox.documentation.schema.plugins.SchemaPluginsManager;
import springdox.documentation.spi.schema.GenericTypeNamingStrategy;
import springdox.documentation.spi.schema.contexts.ModelContext;

import java.lang.reflect.Type;

import static com.google.common.base.Optional.*;
import static springdox.documentation.schema.Types.*;

@Component
public class TypeNameExtractor {
  private final TypeResolver typeResolver;
  private final SchemaPluginsManager pluginsManager;

  @Autowired
  public TypeNameExtractor(TypeResolver typeResolver, SchemaPluginsManager pluginsManager) {

    this.typeResolver = typeResolver;
    this.pluginsManager = pluginsManager;
  }

  public String typeName(ModelContext context) {
    ResolvedType type = asResolved(context.getType());
    if (Collections.isContainerType(type)) {
      return Collections.containerType(type);
    }
    return innerTypeName(type, context);
  }

  private ResolvedType asResolved(Type type) {
    return typeResolver.resolve(type);
  }

  private String genericTypeName(ResolvedType resolvedType, ModelContext context) {
    Class<?> erasedType = resolvedType.getErasedType();
    GenericTypeNamingStrategy namingStrategy = context.getGenericNamingStrategy();
    ModelNameContext nameContext = new ModelNameContext(resolvedType.getErasedType(), context.getDocumentationType());
    String simpleName = fromNullable(typeNameFor(erasedType)).or(pluginsManager.typeName(nameContext));
    StringBuilder sb = new StringBuilder(String.format("%s%s", simpleName, namingStrategy.getOpenGeneric()));
    boolean first = true;
    for (int index = 0; index < erasedType.getTypeParameters().length; index++) {
      ResolvedType typeParam = resolvedType.getTypeParameters().get(index);
      if (first) {
        sb.append(innerTypeName(typeParam, context));
        first = false;
      } else {
        sb.append(String.format("%s%s", namingStrategy.getTypeListDelimiter(),
                innerTypeName(typeParam, context)));
      }
    }
    sb.append(namingStrategy.getCloseGeneric());
    return sb.toString();
  }

  private String innerTypeName(ResolvedType type, ModelContext context) {
    if (type.getTypeParameters().size() > 0 && type.getErasedType().getTypeParameters().length > 0) {
      return genericTypeName(type, context);
    }
    return simpleTypeName(type, context);
  }

  private String simpleTypeName(ResolvedType type, ModelContext context) {
    Class<?> erasedType = type.getErasedType();
    if (type instanceof ResolvedPrimitiveType) {
      return typeNameFor(erasedType);
    } else if (erasedType.isEnum()) {
      return "string";
    } else if (type instanceof ResolvedArrayType) {
      GenericTypeNamingStrategy namingStrategy = context.getGenericNamingStrategy();
      return String.format("Array%s%s%s", namingStrategy.getOpenGeneric(),
              simpleTypeName(type.getArrayElementType(), context), namingStrategy.getCloseGeneric());
    } else if (type instanceof ResolvedObjectType) {
      String typeName = typeNameFor(erasedType);
      if (typeName != null) {
        return typeName;
      }
    }
    return pluginsManager.typeName(new ModelNameContext(type.getErasedType(), context.getDocumentationType()));
  }
}
