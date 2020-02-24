package springfox.documentation.spring.web.dummy.models;

public class Holder<T> {

  private T content;

  private Wrapper<T> wrapper;

  public T getContent() {
    return content;
  }

  public void setContent(T content) {
    this.content = content;
  }

  public Wrapper<T> getWrapper() {
    return wrapper;
  }

  public void setWrapper(Wrapper<T> wrapper) {
    this.wrapper = wrapper;
  }

}
