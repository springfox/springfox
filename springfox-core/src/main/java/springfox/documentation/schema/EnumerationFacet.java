package springfox.documentation.schema;

import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.AllowableValues;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class EnumerationFacet<T> implements ElementFacet {
  private final List<T> allowedValues = new ArrayList<>();

  private EnumerationFacet(Collection<T> allowedValues) {
    this.allowedValues.addAll(allowedValues);
  }

  public static Optional<ElementFacet> from(AllowableValues allowableValues) {
    if (allowableValues instanceof AllowableListValues) {
      return Optional.of(new EnumerationFacet<>(((AllowableListValues) allowableValues).getValues()));
    }
    return Optional.empty();
  }

  public List<T> getAllowedValues() {
    return allowedValues;
  }
}
