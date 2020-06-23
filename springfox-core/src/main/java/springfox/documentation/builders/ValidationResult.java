package springfox.documentation.builders;

import org.slf4j.event.Level;

public class ValidationResult {
  private final Level level;
  private final String object;
  private final String field;
  private final String message;

  public ValidationResult(String object, String field, String message) {
    this(Level.ERROR, object, field, message);
  }

  public ValidationResult(Level level, String object, String field, String message) {
    this.level = level;
    this.object = object;
    this.field = field;
    this.message = message;
  }

  public Level getLevel() {
    return level;
  }

  public String getObject() {
    return object;
  }

  public String getField() {
    return field;
  }

  public String getMessage() {
    return message;
  }
}
