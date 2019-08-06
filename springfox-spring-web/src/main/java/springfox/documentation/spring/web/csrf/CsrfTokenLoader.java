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

package springfox.documentation.spring.web.csrf;

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
     * Load an empty MirrorCsrfToken instance
     */
    T loadEmptiness();

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

    CsrfTokenLoader<T> wrap(Object requestOrExchange);

    /**
     * <p>
     * The default csrf token loader to use
     * </p>
     * <p>
     * It can be registered via `set` and retrieved via `get`
     * There is and should be *NO* need to carry multiple loaders
     * at the same time.
     * </p>
     */
    class DefaultOne {

        private DefaultOne() {
            throw new UnsupportedOperationException();
        }

        private static CsrfTokenLoader<?> defaultOne = null;

        public static void set(CsrfTokenLoader<?> loader) {
            defaultOne = loader;
        }

        public static CsrfTokenLoader<?> get() {
            return defaultOne;
        }
    }
}
