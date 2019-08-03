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

import springfox.documentation.swagger.common.ClassUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This accesser accesses any CsrfToken instance through a reflection-styled way
 *
 * @author liuxy
 */
public class DefaultCsrfTokenAccesser implements CsrfTokenAccesser {

    public static final DefaultCsrfTokenAccesser WEB_MVC_ACCESSER =
            new DefaultCsrfTokenAccesser("org.springframework.security.web.csrf.CsrfToken");

    public static final DefaultCsrfTokenAccesser WEB_FLUX_ACCESSER =
            new DefaultCsrfTokenAccesser("org.springframework.security.web.server.csrf.CsrfToken");

    /**
     * The getter method that can access any csrfToken and get the token string
     */
    private final Method getter;

    /**
     * @param csrfTokenType The given fully qualified class name of the target csrf token
     */
    public DefaultCsrfTokenAccesser(String csrfTokenType) {
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
     * If this accessor available at this moment or not
     */
    @Override
    public boolean available() {
        return this.getter != null;
    }

    /**
     * Get the token string from a csrfToken instance
     *
     * @param csrfToken csrfToken instance
     * @return token string, or null
     */
    @Override
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
