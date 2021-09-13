package springfox.test.contract.oas.linkdedmodels.model;

import java.util.Objects;


public class Model09 extends Model  {

  private Model08 previous;

  private Model10 next;

  public Model08 getPrevious() {
    return previous;
  }

  public void setPrevious(Model08 previous) {
    this.previous = previous;
  }

  public Model10 getNext() {
    return next;
  }

  public void setNext(Model10 next) {
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
    Model09 model09 = (Model09) o;
    return Objects.equals(previous, model09.previous) &&
        Objects.equals(next, model09.next) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(previous, next, super.hashCode());
  }

  @Override
  public String toString() {
    return "Model09{" +
            "previous=" + previous +
            ", next=" + next +
            '}';
  }
}

