package springfox.test.contract.oas.linkdedmodels.model;

import java.util.Objects;


public class Model11 extends Model  {

  private Model10 previous;

  private Model12 next;

  public Model10 getPrevious() {
    return previous;
  }

  public void setPrevious(Model10 previous) {
    this.previous = previous;
  }

  public Model12 getNext() {
    return next;
  }

  public void setNext(Model12 next) {
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
    Model11 model11 = (Model11) o;
    return Objects.equals(previous, model11.previous) &&
        Objects.equals(next, model11.next) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(previous, next, super.hashCode());
  }

  @Override
  public String toString() {
    return "Model11{" +
            "previous=" + previous +
            ", next=" + next +
            '}';
  }
}

