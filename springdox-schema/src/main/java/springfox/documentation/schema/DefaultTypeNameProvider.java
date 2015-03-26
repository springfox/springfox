package springfox.documentation.schema;


import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.TypeNameProviderPlugin;

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
