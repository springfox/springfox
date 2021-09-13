package springfox.test.contract.oas.linkdedmodels.model;

import java.util.Objects;


public class Model05 extends Model  {

  private Model04 previous;

  private Model06 next;

  public Model04 getPrevious() {
    return previous;
  }

  public void setPrevious(Model04 previous) {
    this.previous = previous;
  }

  public Model06 getNext() {
    return next;
  }

  public void setNext(Model06 next) {
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
    Model05 model05 = (Model05) o;
    return Objects.equals(previous, model05.previous) &&
        Objects.equals(next, model05.next) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(previous, next, super.hashCode());
  }

  @Override
  public String toString() {
    return "Model05{" +
            "previous=" + previous +
            ", next=" + next +
            '}';
  }
}

