package springfox.documentation.spring.web.plugins;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.DefaultsProviderPlugin;
import springfox.documentation.spi.service.contexts.DocumentationContextBuilder;

import java.util.ArrayList;
import java.util.List;

import static springfox.documentation.schema.AlternateTypeRules.*;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebfluxDefaultsProvider implements DefaultsProviderPlugin {

  private final TypeResolver resolver;

  public WebfluxDefaultsProvider(TypeResolver resolver) {
    this.resolver = resolver;
  }

  @Override
  public DocumentationContextBuilder create(DocumentationType documentationType) {
    throw new UnsupportedOperationException();
  }

  @Override
  public DocumentationContextBuilder apply(DocumentationContextBuilder builder) {
    List<AlternateTypeRule> rules = new ArrayList<>();
    rules.add(newRule(resolver.resolve(Mono.class, resolver.resolve(ResponseEntity.class, WildcardType.class)),
        resolver.resolve(WildcardType.class), Ordered.HIGHEST_PRECEDENCE + 20));
    rules.add(newRule(resolver.resolve(Flux.class, resolver.resolve(ResponseEntity.class, WildcardType.class)),
        resolver.resolve(List.class, WildcardType.class), Ordered.HIGHEST_PRECEDENCE + 20));
    rules.add(newRule(resolver.resolve(ResponseEntity.class, resolver.resolve(Flux.class, WildcardType.class)),
        resolver.resolve(List.class, WildcardType.class), Ordered.HIGHEST_PRECEDENCE + 20));
    rules.add(newRule(resolver.resolve(ResponseEntity.class, resolver.resolve(Mono.class, WildcardType.class)),
        resolver.resolve(WildcardType.class), Ordered.HIGHEST_PRECEDENCE + 20));
    rules.add(newRule(resolver.resolve(Flux.class, WildcardType.class),
        resolver.resolve(List.class, WildcardType.class), Ordered.HIGHEST_PRECEDENCE + 20));
    rules.add(newRule(resolver.resolve(Mono.class, WildcardType.class),
        resolver.resolve(WildcardType.class), Ordered.HIGHEST_PRECEDENCE + 20));
    return builder.rules(rules);
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }
}
