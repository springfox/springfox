package springfox.documentation.springWrapper;

import com.sun.istack.internal.Nullable;

public interface NameValueExpression<T> {

	String getName();

	@Nullable
    T getValue();

	boolean isNegated();

}
