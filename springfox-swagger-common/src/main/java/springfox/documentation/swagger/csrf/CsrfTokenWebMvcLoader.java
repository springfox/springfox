package springfox.documentation.swagger.csrf;

import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * The csrf token loader for web-mvc
 *
 * @author liuxy
 */
public class CsrfTokenWebMvcLoader implements CsrfTokenLoader<MirrorCsrfToken> {

    /**
     * The request
     */
    private final HttpServletRequest request;

    public static final CsrfTokenAccesser accesser =
            new CsrfTokenAccesser("org.springframework.security.web.csrf.CsrfToken");

    private CsrfTokenWebMvcLoader(HttpServletRequest request) {
        this.request = request;
    }

    public static CsrfTokenWebMvcLoader wrap(HttpServletRequest request) {
        return new CsrfTokenWebMvcLoader(request);
    }

    @Override
    public boolean isCorsRequest() {
        return CorsUtils.isCorsRequest(request);
    }

    @Override
    public MirrorCsrfToken loadFromCookie(CsrfStrategy strategy) {
        Cookie cookie = WebUtils.getCookie(request, strategy.getKeyName());
        if (cookie == null || !StringUtils.hasText(cookie.getValue())) {
            return createMirrorCsrfToken(strategy,
                    tryAccess(request, strategy.getParameterName()));
        }
        return createMirrorCsrfToken(strategy, cookie.getValue());

    }

    @Override
    public MirrorCsrfToken loadFromSession(CsrfStrategy strategy) {
        Object csrfToken = request.getSession(true)
                .getAttribute(strategy.getKeyName());
        if (csrfToken == null) {
            return createMirrorCsrfToken(strategy, tryAccess(request, strategy.getParameterName()));
        } else {
            return createMirrorCsrfToken(strategy, accesser.access(csrfToken));
        }
    }

    /**
     * Try to access the csrfToken from the request where the csrfToken is
     * guaranteed to be stored according to spring-security's csrf mechanism.
     *
     * @param request the HttpServletRequest
     * @return The token string, or null
     */
    private String tryAccess(HttpServletRequest request, String parameterName) {
        if (!accesser.accessible()) {
            return null; // fail fast
        }
        Object lazyToken = request.getAttribute(parameterName);
        if (lazyToken == null) {
            return null;
        }
        return accesser.access(lazyToken);
    }
}
