package springfox.test.contract.oas.linkdedmodels.model;

import java.util.Objects;


public class Model13 extends Model  {

  private Model12 previous;

  private Model14 next;

  public Model12 getPrevious() {
    return previous;
  }

  public void setPrevious(Model12 previous) {
    this.previous = previous;
  }

  public Model14 getNext() {
    return next;
  }

  public void setNext(Model14 next) {
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
    Model13 model13 = (Model13) o;
    return Objects.equals(previous, model13.previous) &&
        Objects.equals(next, model13.next) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(previous, next, super.hashCode());
  }

  @Override
  public String toString() {
    return "Model13{" +
            "previous=" + previous +
            ", next=" + next +
            '}';
  }
}

