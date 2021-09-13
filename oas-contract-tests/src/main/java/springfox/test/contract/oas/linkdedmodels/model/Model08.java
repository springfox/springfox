package springfox.test.contract.oas.linkdedmodels.model;

import java.util.Objects;


public class Model08 extends Model  {

  private Model07 previous;

  private Model09 next;

  public Model07 getPrevious() {
    return previous;
  }

  public void setPrevious(Model07 previous) {
    this.previous = previous;
  }

  public Model09 getNext() {
    return next;
  }

  public void setNext(Model09 next) {
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
    Model08 model08 = (Model08) o;
    return Objects.equals(previous, model08.previous) &&
        Objects.equals(next, model08.next) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(previous, next, super.hashCode());
  }

  @Override
  public String toString() {
    return "Model08{" +
            "previous=" + previous +
            ", next=" + next +
            '}';
  }
}

