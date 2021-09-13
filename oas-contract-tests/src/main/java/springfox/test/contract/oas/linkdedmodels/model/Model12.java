package springfox.test.contract.oas.linkdedmodels.model;

import java.util.Objects;


public class Model12 extends Model  {

  private Model11 previous;

  private Model13 next;

  public Model11 getPrevious() {
    return previous;
  }

  public void setPrevious(Model11 previous) {
    this.previous = previous;
  }

  public Model13 getNext() {
    return next;
  }

  public void setNext(Model13 next) {
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
    Model12 model12 = (Model12) o;
    return Objects.equals(previous, model12.previous) &&
        Objects.equals(next, model12.next) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(previous, next, super.hashCode());
  }

  @Override
  public String toString() {
    return "Model12{" +
            "previous=" + previous +
            ", next=" + next +
            '}';
  }
}

