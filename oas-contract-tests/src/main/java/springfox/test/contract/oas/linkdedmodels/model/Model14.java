package springfox.test.contract.oas.linkdedmodels.model;

import java.util.Objects;


public class Model14 extends Model  {

  private Model13 previous;

  private Model15 next;

  public Model13 getPrevious() {
    return previous;
  }

  public void setPrevious(Model13 previous) {
    this.previous = previous;
  }

  public Model15 getNext() {
    return next;
  }

  public void setNext(Model15 next) {
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
    Model14 model14 = (Model14) o;
    return Objects.equals(previous, model14.previous) &&
        Objects.equals(next, model14.next) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(previous, next, super.hashCode());
  }

  @Override
  public String toString() {
    return "Model14{" +
            "previous=" + previous +
            ", next=" + next +
            '}';
  }
}

