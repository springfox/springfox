package com.mangofactory.petstore.repository;

import java.util.HashMap;
import java.util.Map;

public class MapBackedRepository<K, V extends Identifiable<K>> {
  Map<K, V> service = new HashMap<K, V>();

  public void delete(K key) {
    service.remove(key);
  }

  public boolean exists(K key) {
    return service.containsKey(key);
  }

  public void add(V model) {
    service.put(model.getIdentifier(), model);
  }

  public V get(K key) {
    return service.get(key);
  }

  public V first() {
    return service.entrySet().iterator().next().getValue();
  }
}
