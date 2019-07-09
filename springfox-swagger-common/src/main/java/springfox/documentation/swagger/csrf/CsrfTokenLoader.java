package springfox.documentation.swagger.csrf;

/**
 * The csrf token loader
 *
 * @author liuxy
 */
public interface CsrfTokenLoader<T> {

    /**
     * The default method to create a `MirrorCsrfToken` instance
     *
     * @param strategy The csrf strategy
     * @param token    The token
     * @return A MirrorCsrfToken
     */
    default MirrorCsrfToken createMirrorCsrfToken(CsrfStrategy strategy, String token) {
        return new MirrorCsrfToken(
                strategy.getHeaderName(),
                strategy.getParameterName(),
                token);
    }

    /**
     * Is current request or exchange a cors request or not
     */
    boolean isCorsRequest();

    /**
     * Load the csrf token from cookie using the given csrf strategy.
     * By default, the client always load the csrf token from cookie
     * locally, but we still provide an approach for future use.
     *
     * @param strategy The csrf strategy
     * @return csrf token or null
     */
    T loadFromCookie(CsrfStrategy strategy);

    /**
     * Load the csrf token from session using the given csrf strategy.
     *
     * @param strategy The csrf strategy
     * @return csrf token or null
     */
    T loadFromSession(CsrfStrategy strategy);

}
