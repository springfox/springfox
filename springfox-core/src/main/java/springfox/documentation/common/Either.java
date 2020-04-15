package springfox.documentation.common;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class Either<L, R> {
  private final L left;
  private final R right;

  public Either(L left) {
    this(left, null);
  }

  public Either(L left, R right) {
    ensureOnlyOneIsNonNull(left, right);
    this.left = left;
    this.right = right;
  }

  public Optional<L> getLeft() {
    return Optional.ofNullable(left);
  }

  public Optional<R> getRight() {
    return Optional.ofNullable(right);
  }

  private void ensureOnlyOneIsNonNull(
      Object... specs) {
    long specCount = Arrays.stream(specs)
        .filter(Objects::nonNull)
        .count();
    if (specCount != 1) {
      throw new IllegalArgumentException("Only one of the values should be non null");
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Either<?, ?> either = (Either<?, ?>) o;
    return Objects.equals(left, either.left) &&
        Objects.equals(right, either.right);
  }

  @Override
  public int hashCode() {
    return Objects.hash(left, right);
  }
}
