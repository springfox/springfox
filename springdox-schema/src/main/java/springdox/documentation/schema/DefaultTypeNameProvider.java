package springdox.documentation.schema;


import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spi.schema.TypeNameProviderPlugin;

public class DefaultTypeNameProvider implements TypeNameProviderPlugin {

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  @Override
  public String nameFor(Class<?> type) {
    return type.getSimpleName();
  }
}
