package springfox.test.contract.oas.linkdedmodels.model;

import java.util.Objects;


public class Model20 extends Model {

    private Model19 previous;

    public Model19 getPrevious() {
        return previous;
    }

    public void setPrevious(Model19 previous) {
        this.previous = previous;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Model20 model20 = (Model20) o;
        return Objects.equals(previous, model20.previous) &&
                super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(previous, super.hashCode());
    }

    @Override
    public String toString() {
        return "Model20{" +
                "previous=" + previous +
                '}';
    }
}

