package springfox.documentation.swagger.csrf;

import springfox.documentation.swagger.common.ClassUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This accesser accesses any CsrfToken instance through a reflection-styled way
 *
 * @author liuxy
 */
public class CsrfTokenAccesser {

    public static final CsrfTokenAccesser WEB_MVC_ACCESSER =
            new CsrfTokenAccesser("org.springframework.security.web.csrf.CsrfToken");

    public static final CsrfTokenAccesser WEB_FLUX_ACCESSER =
            new CsrfTokenAccesser("org.springframework.security.web.server.csrf.CsrfToken");

    /**
     * The getter method that can access any csrfToken and get the token string
     */
    private final Method getter;

    /**
     * @param csrfTokenType The given fully qualified class name of the target csrf token
     */
    public CsrfTokenAccesser(String csrfTokenType) {
        Method accessMethod = null;
        try {
            Class<?> csrfTokenClass =
                    ClassUtils.forName(csrfTokenType);
            if (csrfTokenClass != null) {
                accessMethod = csrfTokenClass.getMethod("getToken");
            }
        } catch (NoSuchMethodException ignored) {
        }
        this.getter = accessMethod;
    }

    /**
     * If this accessor can access the csrfToken or not
     */
    public boolean accessible() {
        return this.getter != null;
    }

    /**
     * Get the token string from a csrfToken instance
     *
     * @param csrfToken csrfToken instance
     * @return token string, or null
     */
    public String access(Object csrfToken) {
        if (getter == null) {
            return null;
        }
        try {
            return (String) getter.invoke(csrfToken);
        } catch (IllegalAccessException | InvocationTargetException ignored) {
        }
        return null;
    }

}
