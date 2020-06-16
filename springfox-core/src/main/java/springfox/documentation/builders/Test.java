package springfox.documentation.builders;

import java.util.function.Consumer;
import java.util.function.Function;

public class Test {
  public Function<Consumer<Integer>, Integer> addOne(Integer toAdd) {
    return a -> {
      a.accept(toAdd);
      return toAdd + 1;
    };
  }

}
