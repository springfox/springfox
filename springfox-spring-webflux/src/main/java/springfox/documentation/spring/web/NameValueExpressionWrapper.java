package springfox.documentation.spring.web;

import springfox.documentation.springWrapper.NameValueExpression;

import java.util.HashSet;
import java.util.Set;

public class NameValueExpressionWrapper<T> implements NameValueExpression {
    private org.springframework.web.reactive.result.condition.NameValueExpression<T> e;

    public static <T> Set<NameValueExpression<T>> from(Set<org.springframework.web.reactive.result.condition.NameValueExpression<T>> springSet) {
        Set<NameValueExpression<T>> wrapped = new HashSet<NameValueExpression<T>>();

        for (org.springframework.web.reactive.result.condition.NameValueExpression e: springSet) {
            wrapped.add(new NameValueExpressionWrapper<T>(e));
        }

        return wrapped;
    }

    public NameValueExpressionWrapper(org.springframework.web.reactive.result.condition.NameValueExpression<T> e) {
        this.e = e;
    }

    @Override
    public String getName() {
        return this.e.getName();
    }

    @Override
    public Object getValue() {
        return this.e.getValue();
    }

    @Override
    public boolean isNegated() {
        return this.e.isNegated();
    }
}
