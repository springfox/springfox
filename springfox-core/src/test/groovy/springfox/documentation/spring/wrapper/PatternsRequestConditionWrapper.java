package springfox.documentation.spring.wrapper;

import springfox.documentation.springWrapper.PatternsRequestCondition;

import java.util.Set;

public class PatternsRequestConditionWrapper implements PatternsRequestCondition<org.springframework.web.servlet.mvc.condition.PatternsRequestCondition> {

    private org.springframework.web.servlet.mvc.condition.PatternsRequestCondition condition;

    public PatternsRequestConditionWrapper(org.springframework.web.servlet.mvc.condition.PatternsRequestCondition condition) {
        this.condition = condition;
    }

    @Override
    public PatternsRequestCondition combine(PatternsRequestCondition<org.springframework.web.servlet.mvc.condition.PatternsRequestCondition> other) {
        if (other instanceof PatternsRequestConditionWrapper) {
            return new PatternsRequestConditionWrapper(this.condition.combine(((PatternsRequestConditionWrapper) other).condition));
        }
        return this;
    }

    @Override
    public Set<String> getPatterns() {
        return this.condition.getPatterns();
    }
}
