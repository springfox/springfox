package springfox.test.contract.oas.linkdedmodels.model;

import java.util.Objects;


public class Model15 extends Model {
    private Model14 previous;

    private Model16 next;

    public Model14 getPrevious() {
        return previous;
    }

    public void setPrevious(Model14 previous) {
        this.previous = previous;
    }

    public Model16 getNext() {
        return next;
    }

    public void setNext(Model16 next) {
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
        Model15 model15 = (Model15) o;
        return Objects.equals(previous, model15.previous) &&
                Objects.equals(next, model15.next) &&
                super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(previous, next, super.hashCode());
    }

    @Override
    public String toString() {
        return "Model15{" +
                "previous=" + previous +
                ", next=" + next +
                '}';
    }
}

