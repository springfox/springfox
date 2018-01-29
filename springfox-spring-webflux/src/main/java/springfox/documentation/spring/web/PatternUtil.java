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

package springfox.documentation.spring.web;

import org.springframework.web.util.pattern.PathPattern;

import java.util.HashSet;
import java.util.Set;

public class PatternUtil {

    public static Set<String> toListString(Set<PathPattern> patterns) {
        Set<String> paths = new HashSet<String>();
        for (PathPattern p: patterns) {
            paths.add(p.getPatternString());
        }
        return paths;
    }
}
