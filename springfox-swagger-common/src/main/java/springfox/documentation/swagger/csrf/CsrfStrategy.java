package springfox.documentation.swagger.csrf;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import springfox.documentation.swagger.common.EnvIndicator;
import springfox.documentation.swagger.web.UiConfigurationBuilder;

/**
 * The csrf strategy that you are using.
 * <p>
 * By default, it follows spring-security's rules of csrf token
 * creation, storing and matching.
 * <p>
 * If you are not using spring-security's csrf support, this
 * strategy might still be compatible with your own csrf strategy,
 * but it's not guaranteed, and you need to configure your strategy
 * using {@link UiConfigurationBuilder#csrfStrategy(CsrfStrategy)}
 *
 * @author liuxy
 */
public class CsrfStrategy {

    /**
     * The csrf strategy of none strategy at all
     */
    public static final CsrfStrategy NONE =
            of(TokenStore.NONE, null, null, null);

    /**
     * The csrf strategy of spring-security's CsrfFilter with HttpSessionCsrfTokenRepository
     * for WebMvc or its CsrfWebFilter with WebSessionServerCsrfTokenRepository for WebFlux.
     * <p>
     * It depends.
     */
    public static final CsrfStrategy SESSION;

    static {
        String sessionKey;
        try {
            // Try to forName the webMvc's indicator
            Class.forName(EnvIndicator.WEB_MVC_INDICATOR);
            sessionKey = "org.springframework.security.web.csrf." +
                    "HttpSessionCsrfTokenRepository.CSRF_TOKEN";
        } catch (ClassNotFoundException ignored) {
            sessionKey = "org.springframework.security.web.server.csrf." +
                    "WebSessionServerCsrfTokenRepository.CSRF_TOKEN";
        }
        SESSION = of(TokenStore.SESSION,
                "_csrf",
                "X-CSRF-TOKEN",
                sessionKey);
    }

    /**
     * The default csrf strategy of both spring-security's CsrfFilter
     * with CookieCsrfTokenRepository and its CsrfWebFilter with
     * CookieServerCsrfTokenRepository
     */
    public static final CsrfStrategy COOKIE =
            of(TokenStore.COOKIE,
                    "_csrf",
                    "X-CSRF-TOKEN",
                    "XSRF-TOKEN");

    /**
     * The default strategy which is the `session` strategy by default
     */
    public static final CsrfStrategy DEFAULT_STRATEGY = SESSION;

    private final TokenStore tokenStore;

    private final String parameterName;

    private final String headerName;

    private final String keyName;

    /**
     * @param tokenStore    Specify the way of the actual csrf token being stored
     *                      by server-side. When it is {@link TokenStore#NONE}, it
     *                      means there's no csrf support being used at all.
     * @param parameterName This is the `key` of the csrf token which is sent from
     *                      the client and carried by a request. Also, it is the
     *                      same `key` by which a template engine can get the csrf
     *                      token from the context.
     * @param headerName    This is the http header name of the csrf token which is
     *                      sent from the client. At first, spring-security's
     *                      `CsrfFilter / CsrfWebFilter` would like to get the csrf
     *                      token carried by a request from header using this
     *                      headerName, but if it gets null, the `CsrfFilter /
     *                      CsrfWebFilter` will try to get the csrf token from
     *                      parameters using the `parameterName`. If you are not
     *                      using spring-security's csrf mechanism, you might need
     *                      to do the same thing on your own.
     * @param keyName       When the tokenStore is `session`, this is the session's
     *                      attribution key and when `cookie` the cookie's name
     * @return CsrfStrategy instance
     */
    public static CsrfStrategy of(TokenStore tokenStore,
                                  String parameterName,
                                  String headerName,
                                  String keyName) {
        return new CsrfStrategy(tokenStore, parameterName, headerName, keyName);
    }

    private CsrfStrategy(TokenStore tokenStore,
                         String parameterName,
                         String headerName,
                         String keyName) {
        this.tokenStore = tokenStore;
        this.parameterName = parameterName;
        this.headerName = headerName;
        this.keyName = keyName;
    }

    /**
     * Load the stored csrf token
     *
     * @param fetcher the request
     * @return csrf token
     */
    public <T> T loadCsrfToken(CsrfTokenLoader<T> fetcher) {
        switch (this.tokenStore) {
            case COOKIE:
                return fetcher.loadFromCookie(this);
            case SESSION:
                return fetcher.loadFromSession(this);
            case NONE:
            default:
                return null;
        }
    }

    @JsonProperty("tokenStore")
    public TokenStore getTokenStore() {
        return tokenStore;
    }

    @JsonProperty("parameterName")
    public String getParameterName() {
        return parameterName;
    }

    @JsonProperty("headerName")
    public String getHeaderName() {
        return headerName;
    }

    @JsonIgnore
    public String getKeyName() {
        return keyName;
    }

    public enum TokenStore {
        NONE,
        SESSION,
        COOKIE
    }
}
