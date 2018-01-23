package springfox.documentation.spring.web;

import org.springframework.web.reactive.result.condition.PatternsRequestCondition;
import org.springframework.web.util.pattern.PathPattern;

import java.util.HashSet;
import java.util.Set;

public class PatternsRequestConditionWrapper implements springfox.documentation.springWrapper.PatternsRequestCondition<PatternsRequestCondition> {

    private org.springframework.web.reactive.result.condition.PatternsRequestCondition condition;

    public PatternsRequestConditionWrapper(org.springframework.web.reactive.result.condition.PatternsRequestCondition condition) {
        this.condition = condition;
    }

    @Override
    public springfox.documentation.springWrapper.PatternsRequestCondition combine(springfox.documentation.springWrapper.PatternsRequestCondition<PatternsRequestCondition> other) {
        if (other instanceof PatternsRequestConditionWrapper) {
            return new PatternsRequestConditionWrapper(this.condition.combine(((PatternsRequestConditionWrapper) other).condition));
        }
        return this;
    }

    @Override
    public Set<String> getPatterns() {
        return PatternUtil.toListString(this.condition.getPatterns());
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof PatternsRequestConditionWrapper) {
            return this.condition.equals(((PatternsRequestConditionWrapper) o).condition);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.condition.hashCode();
    }



}

