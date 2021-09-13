package springfox.test.contract.oas.linkdedmodels.model;

import java.util.Objects;


public class Model19 extends Model {

    private Model18 previous;

    private Model20 next;

    public Model18 getPrevious() {
        return previous;
    }

    public void setPrevious(Model18 previous) {
        this.previous = previous;
    }

    public Model20 getNext() {
        return next;
    }

    public void setNext(Model20 next) {
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
        Model19 model19 = (Model19) o;
        return Objects.equals(previous, model19.previous) &&
                Objects.equals(next, model19.next) &&
                super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(previous, next, super.hashCode());
    }

    @Override
    public String toString() {
        return "Model19{" +
                "previous=" + previous +
                ", next=" + next +
                '}';
    }
}

