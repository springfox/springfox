package springfox.test.contract.oas.linkdedmodels.model;

import java.util.Objects;


public class Model16 extends Model {

    private Model15 previous;

    private Model17 next;

    public Model15 getPrevious() {
        return previous;
    }

    public void setPrevious(Model15 previous) {
        this.previous = previous;
    }

    public Model17 getNext() {
        return next;
    }

    public void setNext(Model17 next) {
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
        Model16 model16 = (Model16) o;
        return Objects.equals(previous, model16.previous) &&
                Objects.equals(next, model16.next) &&
                super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(previous, next, super.hashCode());
    }

    @Override
    public String toString() {
        return "Model16{" +
                "previous=" + previous +
                ", next=" + next +
                '}';
    }
}

