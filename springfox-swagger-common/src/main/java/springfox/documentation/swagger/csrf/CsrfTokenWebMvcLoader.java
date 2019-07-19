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
    private static final ThreadLocal<HttpServletRequest> req = new ThreadLocal<>();

    private final CsrfTokenAccesser accesser;

    public CsrfTokenWebMvcLoader(CsrfTokenAccesser accesser) {
        this.accesser = accesser;
    }

    public static CsrfTokenWebMvcLoader defaultOne() {
        return new CsrfTokenWebMvcLoader(DefaultCsrfTokenAccesser.WEB_MVC_ACCESSER);
    }

    public CsrfTokenWebMvcLoader wrap(HttpServletRequest request) {
        req.set(request);
        return this;
    }

    @Override
    public boolean isCorsRequest() {
        return CorsUtils.isCorsRequest(req.get());
    }

    @Override
    public MirrorCsrfToken loadEmptiness() {
        return MirrorCsrfToken.EMPTY;
    }

    @Override
    public MirrorCsrfToken loadFromCookie(CsrfStrategy strategy) {
        Cookie cookie = WebUtils.getCookie(req.get(), strategy.getKeyName());
        if (cookie == null || !StringUtils.hasText(cookie.getValue())) {
            return tryLoadFromRequest(strategy);
        }
        return createMirrorCsrfToken(strategy, cookie.getValue());

    }

    @Override
    public MirrorCsrfToken loadFromSession(CsrfStrategy strategy) {
        Object csrfToken = req.get().getSession(true)
                .getAttribute(strategy.getKeyName());
        if (csrfToken == null) {
            return tryLoadFromRequest(strategy);
        } else {
            return createMirrorCsrfToken(strategy, accesser.access(csrfToken));
        }
    }

    private MirrorCsrfToken tryLoadFromRequest(CsrfStrategy strategy) {
        String token = tryAccess(req.get(), strategy.getBackupKeyName());
        if (StringUtils.isEmpty(token)) {
            return this.loadEmptiness();
        }
        return createMirrorCsrfToken(strategy, token);
    }

    /**
     * Try to access the csrfToken from the request where the csrfToken is
     * guaranteed to be stored according to spring-security's csrf mechanism.
     *
     * @param request the HttpServletRequest
     * @return The token string, or null
     */
    private String tryAccess(HttpServletRequest request, String backupKeyName) {
        if (!accesser.available()) {
            return null; // fail fast
        }
        Object lazyToken = request.getAttribute(backupKeyName);
        if (lazyToken == null) {
            return null;
        }
        return accesser.access(lazyToken);
    }
}
