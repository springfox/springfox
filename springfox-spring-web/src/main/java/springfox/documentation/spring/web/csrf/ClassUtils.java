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
 * * Indicators that are used to sense the current environment
 * <p>
 * * Common static method unifies all `forName` calls
 * <p>
 * * Encapsuled `isMvc` and `isFlux` methods
 *
 * @author liuxy
 */
public class ClassUtils {

    private ClassUtils() {
        throw new UnsupportedOperationException();
    }

    public static final String WEB_FLUX_INDICATOR = "org.springframework.web.reactive.BindingContext";

    public static final String WEB_MVC_INDICATOR = "org.springframework.web.servlet.DispatcherServlet";

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
