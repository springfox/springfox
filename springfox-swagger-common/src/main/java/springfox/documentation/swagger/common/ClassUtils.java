package springfox.documentation.swagger.common;

/**
 * * Indicators that are used to sense the current environment
 * <p>
 * * Common static method unifies all `forName` calls
 * <p>
 * * Encapsuled `isMvc` and `isFlux` methods
 *
 * @author liuxy
 */
public class ClassUtils {

    public static final String WEB_FLUX_INDICATOR = "org.springframework.web.reactive.BindingContext";

    public static final String WEB_MVC_INDICATOR = "javax.servlet.http.HttpServletRequest";

    public static Class<?> forName(String qualifiedName) {
        try {
            return Class.forName(qualifiedName);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static boolean isMvc() {
        return forName(WEB_MVC_INDICATOR) != null;
    }

    public static boolean isFlux() {
        return forName(WEB_FLUX_INDICATOR) != null;
    }
}
