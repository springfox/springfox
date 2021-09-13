package springfox.test.contract.oas.linkdedmodels.model;

import java.util.Objects;


public class Model06 extends Model  {

  private Model05 previous;

  private Model07 next;

  public Model05 getPrevious() {
    return previous;
  }

  public void setPrevious(Model05 previous) {
    this.previous = previous;
  }

  public Model07 getNext() {
    return next;
  }

  public void setNext(Model07 next) {
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
    Model06 model06 = (Model06) o;
    return Objects.equals(previous, model06.previous) &&
        Objects.equals(next, model06.next) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(previous, next, super.hashCode());
  }

  @Override
  public String toString() {
    return "Model06{" +
            "previous=" + previous +
            ", next=" + next +
            '}';
  }
}

