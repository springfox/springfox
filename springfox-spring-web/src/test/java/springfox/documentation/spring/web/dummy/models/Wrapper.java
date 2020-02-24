package springfox.documentation.spring.web.dummy.models;

public class Wrapper<T> {

  private T object;

  public T getObject() {
    return object;
  }

  public void setObject(T object) {
    this.object = object;
  }

}
