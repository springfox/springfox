package springfox.documentation.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EnumerationFacet implements ElementFacet {
  private final List<String> allowedValues = new ArrayList<>();

  public EnumerationFacet(Collection<String> allowedValues) {
    this.allowedValues.addAll(allowedValues);
  }

  public List<?> getAllowedValues() {
    return allowedValues;
  }
}
