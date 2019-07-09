package springfox.documentation.swagger.csrf;

import org.springframework.http.HttpCookie;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.Map;

public class CsrfTokenWebFluxLoader implements CsrfTokenLoader<Mono<MirrorCsrfToken>> {

    private final ServerWebExchange exchange;

    public static final CsrfTokenAccesser accesser =
            new CsrfTokenAccesser("org.springframework.security.web.server.csrf.CsrfToken");

    private CsrfTokenWebFluxLoader(ServerWebExchange exchange) {
        this.exchange = exchange;
    }

    public static CsrfTokenWebFluxLoader wrap(ServerWebExchange exchange) {
        return new CsrfTokenWebFluxLoader(exchange);
    }

    @Override
    public boolean isCorsRequest() {
        return CorsUtils.isCorsRequest(exchange.getRequest());
    }

    @Override
    public Mono<MirrorCsrfToken> loadFromCookie(CsrfStrategy strategy) {
        HttpCookie cookie = exchange.getRequest()
                .getCookies().getFirst(strategy.getKeyName());
        if (cookie == null || !StringUtils.hasText(cookie.getValue())) {
            return Mono.empty();
        }
        return Mono.just(this.createMirrorCsrfToken(strategy, cookie.getValue()));
    }

    @Override
    public Mono<MirrorCsrfToken> loadFromSession(CsrfStrategy strategy) {
        return exchange.getSession()
                .map(WebSession::getAttributes)
                .flatMap(a ->
                        this.fromAttributes(a, strategy.getKeyName(), strategy))
                .switchIfEmpty(this.fromAttributes(
                        exchange.getAttributes(),
                        "org.springframework.security.web.server.csrf.CsrfToken",
                        strategy));
    }

    /**
     * Get and transform the csrf token stored in the given attributes
     *
     * @param attributes    The attributes map
     * @param attributeName The key name of the csrf token
     * @param strategy      The CsrfStrategy instance
     * @return The mono of a newly created MirrorCsrfToken instance
     */
    public Mono<MirrorCsrfToken> fromAttributes(Map<String, Object> attributes,
                                                String attributeName,
                                                CsrfStrategy strategy) {
        return Mono.just(attributes)
                .filter(a -> a.containsKey(attributeName))
                .map(a -> a.get(attributeName))
                // branching
                .map(a -> Tuples.of(a instanceof Mono, a))
                .flatMap(tup -> tup.getT1()
                        ? (Mono<?>) tup.getT2()
                        : Mono.just(tup.getT2()))
                .map(accesser::access)
                .map(t -> createMirrorCsrfToken(strategy, (String) t));
    }
}
