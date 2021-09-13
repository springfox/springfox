package springfox.test.contract.oas.linkdedmodels.model;

import java.util.Objects;


public class Model07 extends Model  {

  private Model06 previous;

  private Model08 next = null;

  public Model06 getPrevious() {
    return previous;
  }

  public void setPrevious(Model06 previous) {
    this.previous = previous;
  }

  public Model08 getNext() {
    return next;
  }

  public void setNext(Model08 next) {
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
    Model07 model07 = (Model07) o;
    return Objects.equals(previous, model07.previous) &&
        Objects.equals(next, model07.next) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(previous, next, super.hashCode());
  }

  @Override
  public String toString() {
    return "Model07{" +
            "previous=" + previous +
            ", next=" + next +
            '}';
  }
}

