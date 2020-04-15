package springfox.documentation.common;

import java.util.Objects;
import java.util.Optional;

public class Compatibility<T, U> {
  private final T legacy;
  private final U modern;

  public Compatibility(T legacy, U modern) {
    this.legacy = legacy;
    this.modern = modern;
  }

  public Optional<T> getLegacy() {
    return Optional.ofNullable(legacy);
  }

  public Optional<U> getModern() {
    return Optional.ofNullable(modern);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Compatibility<?, ?> that = (Compatibility<?, ?>) o;
    return Objects.equals(legacy, that.legacy) &&
        Objects.equals(modern, that.modern);
  }

  @Override
  public int hashCode() {
    return Objects.hash(legacy, modern);
  }
}
