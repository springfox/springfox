package springfox.test.contract.oas.linkdedmodels.model;

import java.util.Objects;


public class Model04 extends Model  {

  private Model03 previous;

  private Model05 next;

  public Model03 getPrevious() {
    return previous;
  }

  public void setPrevious(Model03 previous) {
    this.previous = previous;
  }

  public Model05 getNext() {
    return next;
  }

  public void setNext(Model05 next) {
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
    Model04 model04 = (Model04) o;
    return Objects.equals(previous, model04.previous) &&
        Objects.equals(next, model04.next) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(previous, next, super.hashCode());
  }

  @Override
  public String toString() {
    return "Model04{" +
            "previous=" + previous +
            ", next=" + next +
            '}';
  }
}

