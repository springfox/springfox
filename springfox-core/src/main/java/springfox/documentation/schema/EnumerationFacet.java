package springfox.documentation.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EnumerationFacet<T> implements ElementFacet {
  private final List<T> allowedValues = new ArrayList<>();

  public EnumerationFacet(Collection<T> allowedValues) {
    this.allowedValues.addAll(allowedValues);
  }

  public List<T> getAllowedValues() {
    return allowedValues;
  }
}
