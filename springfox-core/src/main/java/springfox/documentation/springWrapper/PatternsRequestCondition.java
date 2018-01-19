package springfox.documentation.springWrapper;

import java.util.Set;

public interface PatternsRequestCondition<T> {
    PatternsRequestCondition combine(T other);

    Set<String> getPatterns();
}
