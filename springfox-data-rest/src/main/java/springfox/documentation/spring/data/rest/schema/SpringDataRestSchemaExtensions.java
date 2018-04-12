package springfox.documentation.spring.data.rest.schema;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.RelProvider;
import springfox.documentation.schema.TypeNameExtractor;

@Configuration
public class SpringDataRestSchemaExtensions {

  @Bean
  public ResourcesModelProvider resourcesModelProvider(
      TypeResolver resolver,
      TypeNameExtractor typeNameExtractor) {
    return new ResourcesModelProvider(resolver, typeNameExtractor);
  }

  @Bean
  public EmbeddedCollectionModelProvider embeddedCollectionProvider(
      TypeResolver resolver,
      @Qualifier("_relProvider")
          RelProvider relProvider,
      TypeNameExtractor typeNameExtractor) {
    return new EmbeddedCollectionModelProvider(resolver, relProvider, typeNameExtractor);
  }

  @Bean
  public LinkModelProvider linkProvider(
      TypeResolver resolver,
      TypeNameExtractor typeNameExtractor) {
    return new LinkModelProvider(resolver, typeNameExtractor);
  }
}
