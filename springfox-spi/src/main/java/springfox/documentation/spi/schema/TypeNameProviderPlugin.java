package springfox.documentation.spi.schema;

import org.springframework.plugin.core.Plugin;
import springfox.documentation.spi.DocumentationType;

public interface TypeNameProviderPlugin extends Plugin<DocumentationType> {
  public String nameFor(Class<?> type);
}
