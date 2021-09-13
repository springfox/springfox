package springfox.test.contract.oas.linkdedmodels.model;

import java.util.Objects;


public class Model03 extends Model  {

  private Model02 previous;

  private Model04 next;

  public Model02 getPrevious() {
    return previous;
  }

  public void setPrevious(Model02 previous) {
    this.previous = previous;
  }

  public Model04 getNext() {
    return next;
  }

  public void setNext(Model04 next) {
    this.next = next;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Model03 model03 = (Model03) o;
    return Objects.equals(previous, model03.previous) &&
        Objects.equals(next, model03.next) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(previous, next, super.hashCode());
  }

  @Override
  public String toString() {
    return "Model03{" +
            "previous=" + previous +
            ", next=" + next +
            '}';
  }
}

