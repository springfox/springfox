package springdox.documentation.schema;

import org.springframework.stereotype.Component;

/**
 * Strategy that uses \u00ab, \u00bb, and comma in generic type names
 */
@Component
public class DefaultGenericTypeNamingStrategy implements GenericTypeNamingStrategy {
  private static final String OPEN = "«";
  private static final String CLOSE = "»";
  private static final String DELIM = ",";

  @Override
  public String getOpenGeneric() {
    return OPEN;
  }

  @Override
  public String getCloseGeneric() {
    return CLOSE;
  }

  @Override
  public String getTypeListDelimiter() {
    return DELIM;
  }
}
