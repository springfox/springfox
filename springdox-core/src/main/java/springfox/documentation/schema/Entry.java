package springfox.documentation.schema;

public class Entry<K, V> {
  private V key;

  public V getKey() {
    return key;
  }

  public void setKey(V key) {
    this.key = key;
  }
}
