package springfox.documentation.builders;

import java.util.List;

@FunctionalInterface
public interface Validator<T> {
  List<ValidationResult> validate(T builder);
}
