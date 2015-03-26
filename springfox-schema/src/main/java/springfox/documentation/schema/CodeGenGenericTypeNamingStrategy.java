package springfox.documentation.schema;

import springfox.documentation.spi.schema.GenericTypeNamingStrategy;

public class CodeGenGenericTypeNamingStrategy implements GenericTypeNamingStrategy {
  private static final String OPEN = "Of";
  private static final String CLOSE = "";
  private static final String DELIM = "And";

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
