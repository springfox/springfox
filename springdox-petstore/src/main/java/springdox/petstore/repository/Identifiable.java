package springdox.petstore.repository;

public interface Identifiable<T> {
  T getIdentifier();
}
