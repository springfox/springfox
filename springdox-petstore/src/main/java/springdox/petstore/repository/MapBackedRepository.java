package springdox.petstore.repository;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

import java.util.HashMap;
import java.util.List;
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
    return Iterables.getFirst(service.values(), null);
  }
  
  public List<V> where(Predicate<V> criteria) {
    return FluentIterable
            .from(service.values())
            .filter(criteria).toList();
  }
}
