package springfox.test.contract.oas.linkdedmodels.model;

import java.util.Objects;

public class Model02 extends Model  {

  private Model01 previous;

  private Model03 next;

  public Model01 getPrevious() {
    return previous;
  }

  public void setPrevious(Model01 previous) {
    this.previous = previous;
  }

  public Model03 getNext() {
    return next;
  }

  public void setNext(Model03 next) {
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
    Model02 model02 = (Model02) o;
    return Objects.equals(previous, model02.previous) &&
        Objects.equals(next, model02.next) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(previous, next, super.hashCode());
  }

  @Override
  public String toString() {
    return "Model02{" +
            "previous=" + previous +
            ", next=" + next +
            '}';
  }

}

