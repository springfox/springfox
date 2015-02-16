package com.mangofactory.petstore.repository;

public interface Identifiable<T> {
  T getIdentifier();
}
