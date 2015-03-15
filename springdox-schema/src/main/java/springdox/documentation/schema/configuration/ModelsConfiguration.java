package springdox.documentation.schema.configuration;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.plugin.core.config.EnablePluginRegistries;
import springdox.documentation.spi.schema.ModelBuilderPlugin;
import springdox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springdox.documentation.spi.schema.TypeNameProviderPlugin;

@Configuration
@ComponentScan(basePackages = {
        "springdox.documentation.schema"
})
@EnablePluginRegistries({
        ModelBuilderPlugin.class,
        ModelPropertyBuilderPlugin.class,
        TypeNameProviderPlugin.class
})
public class ModelsConfiguration {
  @Bean
  public TypeResolver typeResolver() {
    return new TypeResolver();
  }

}
