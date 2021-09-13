package springfox.test.contract.oas.linkdedmodels.model;

import java.util.Objects;


public class Model18 extends Model {

    private Model17 previous;

    private Model19 next;

    public Model17 getPrevious() {
        return previous;
    }

    public void setPrevious(Model17 previous) {
        this.previous = previous;
    }

    public Model19 getNext() {
        return next;
    }

    public void setNext(Model19 next) {
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
        Model18 model18 = (Model18) o;
        return Objects.equals(previous, model18.previous) &&
                Objects.equals(next, model18.next) &&
                super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(previous, next, super.hashCode());
    }

    @Override
    public String toString() {
        return "Model18{" +
                "previous=" + previous +
                ", next=" + next +
                '}';
    }
}

