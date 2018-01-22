package springfox.documentation.springWrapper;

public interface NameValueExpression<T> {

	String getName();

    T getValue();

	boolean isNegated();

}
