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

import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;

import java.util.Set;

public class WebMvcPatternsRequestConditionWrapper
        implements springfox.documentation.spring.wrapper.PatternsRequestCondition<PatternsRequestCondition> {

    private PatternsRequestCondition condition;

    public WebMvcPatternsRequestConditionWrapper(PatternsRequestCondition condition) {
        this.condition = condition;
    }

    @Override
    public springfox.documentation.spring.wrapper.PatternsRequestCondition combine(
            springfox.documentation.spring.wrapper.PatternsRequestCondition<PatternsRequestCondition> other
    ) {
        if (other instanceof WebMvcPatternsRequestConditionWrapper && !this.equals(other)) {
            return new WebMvcPatternsRequestConditionWrapper(this.condition.combine(((WebMvcPatternsRequestConditionWrapper) other).condition));
        }
        return this;
    }

    @Override
    public Set<String> getPatterns() {
        return this.condition.getPatterns();
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof WebMvcPatternsRequestConditionWrapper) {
            return this.condition.equals(((WebMvcPatternsRequestConditionWrapper) o).condition);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.condition.hashCode();
    }


    @Override
    public String toString() {
        return this.condition.toString();
    }
}

