package springfox.test.contract.oas.linkdedmodels.model;

import java.util.Objects;


public class Model17 extends Model {

    private Model16 previous;

    private Model18 next;

    public Model16 getPrevious() {
        return previous;
    }

    public void setPrevious(Model16 previous) {
        this.previous = previous;
    }

    public Model18 getNext() {
        return next;
    }

    public void setNext(Model18 next) {
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
        Model17 model17 = (Model17) o;
        return Objects.equals(previous, model17.previous) &&
                Objects.equals(next, model17.next) &&
                super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(previous, next, super.hashCode());
    }

    @Override
    public String toString() {
        return "Model17{" +
                "previous=" + previous +
                ", next=" + next +
                '}';
    }
}

