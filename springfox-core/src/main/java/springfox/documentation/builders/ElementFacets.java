package springfox.documentation.builders;

import org.slf4j.Logger;

import java.util.function.Function;

import static org.slf4j.LoggerFactory.*;

public class ElementFacets {
  private static final Logger LOGGER = getLogger(ElementFacets.class);

  private ElementFacets() {
    throw new UnsupportedOperationException();
  }

  public static <T extends ElementFacetBuilder> Function<Class<?>, ElementFacetBuilder> builderFactory(
      Class<T> clazz) {
    return t -> {
      try {
        return clazz.getDeclaredConstructor().newInstance();
      } catch (Exception e) {
        LOGGER.error("Unable to create builder of type {}", clazz);
        throw new RuntimeException(String.format("Unable to create builder of type %s", clazz), e);
      }
    };
  }
}
