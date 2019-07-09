package springfox.documentation.swagger.common;

/**
 * Indicators that are used to sense the current environment
 * @author liuxy
 */
public class EnvIndicator {

    public static final String WEB_FLUX_INDICATOR = "org.springframework.web.reactive.BindingContext";

    public static final String WEB_MVC_INDICATOR = "javax.servlet.http.HttpServletRequest";
}
