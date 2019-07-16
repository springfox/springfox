/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

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
    private final CsrfTokenAccesser accesser;

    public CsrfTokenWebFluxLoader(ServerWebExchange exchange, CsrfTokenAccesser accesser) {
        this.exchange = exchange;
        this.accesser = accesser;
    }

    public static CsrfTokenWebFluxLoader wrap(ServerWebExchange exchange) {
        return new CsrfTokenWebFluxLoader(exchange, CsrfTokenAccesser.WEB_FLUX_ACCESSER);
    }

    @Override
    public boolean isCorsRequest() {
        return CorsUtils.isCorsRequest(exchange.getRequest());
    }

    @Override
    public Mono<MirrorCsrfToken> loadEmptiness() {
        return Mono.just(MirrorCsrfToken.EMPTY);
    }

    @Override
    public Mono<MirrorCsrfToken> loadFromCookie(CsrfStrategy strategy) {
        HttpCookie cookie = exchange.getRequest()
                .getCookies().getFirst(strategy.getKeyName());
        if (cookie == null || !StringUtils.hasText(cookie.getValue())) {
            return this.fromAttributes(exchange.getAttributes(),
                    strategy.getBackupKeyName(),
                    strategy).switchIfEmpty(this.loadEmptiness());
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
                        strategy.getBackupKeyName(),
                        strategy))
                .switchIfEmpty(this.loadEmptiness());
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
        if (!accesser.accessible()) {
            return Mono.empty(); // fail fast
        }
        return Mono.just(attributes)
                .filter(a -> a.containsKey(attributeName))
                .map(a -> a.get(attributeName))
                // branching
                .map(a -> Tuples.of(a instanceof Mono, a))
                .flatMap(tup -> tup.getT1()
                        ? (Mono<?>) tup.getT2()
                        : Mono.just(tup.getT2()))
                .map(accesser::access)
                .map(t -> createMirrorCsrfToken(strategy, t))
                .switchIfEmpty(Mono.empty());
    }
}
