package springfox.petstore.repository;

public interface Identifiable<T> {
  T getIdentifier();
}
