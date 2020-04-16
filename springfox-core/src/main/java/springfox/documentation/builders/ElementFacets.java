package springfox.documentation.builders;

import org.slf4j.Logger;

import java.lang.reflect.Constructor;
import java.util.function.Function;

import static org.slf4j.LoggerFactory.*;

public class ElementFacets {
  private static final Logger LOGGER = getLogger(ElementFacets.class);

  private ElementFacets() {
    throw new UnsupportedOperationException();
  }

  public static <T extends ElementFacetBuilder> Function<Class<?>, ElementFacetBuilder> builderFactory(
      Object parent,
      Class<T> clazz) {
    return t -> {
      try {
        Constructor<T> constructor = clazz.getConstructor(Object.class);
        return constructor.newInstance(parent);
      } catch (Exception e) {
        LOGGER.error("Unable to create builder of type {}", clazz);
        return null;
      }
    };
  }
}
