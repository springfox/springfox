package springfox.documentation.builders;

import org.slf4j.Logger;
import org.slf4j.event.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.*;

public class NoopValidator<T> implements Validator<T> {
  private static final Logger LOGGER = getLogger("Validator");

  @Override
  public List<ValidationResult> validate(T builder) {
    return new ArrayList<>();
  }

  static List<ValidationResult> logProblems(List<ValidationResult> results) {
    List<ValidationResult> problems = results.stream()
        .filter(v -> v.getLevel().toInt() > Level.INFO.toInt())
        .collect(Collectors.toList());
    problems.forEach(v -> LOGGER.debug("{} [{}.{}] {}", v.getLevel(), v.getObject(), v.getField(), v.getMessage()));
    return problems;
  }
}
