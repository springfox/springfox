package springdox.documentation.spi.schema;

import org.springframework.plugin.core.Plugin;
import springdox.documentation.spi.DocumentationType;

public interface TypeNameProviderPlugin extends Plugin<DocumentationType> {
  public String nameFor(Class<?> type);
}
