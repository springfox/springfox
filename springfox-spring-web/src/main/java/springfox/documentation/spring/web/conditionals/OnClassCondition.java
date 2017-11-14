/*
 *
 *  Copyright 2017 the original author or authors.
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
package springfox.documentation.spring.web.conditionals;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static org.springframework.util.ClassUtils.isPresent;

@Order(HIGHEST_PRECEDENCE)
class OnClassCondition implements Condition {

  @Override
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    String annotationName = ConditionalOnClass.class.getName();
    Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(annotationName, true);
    String[] classesToLookFor = (String[]) annotationAttributes.get("value");
    return areAllClassesInTheClasspath(classesToLookFor, context.getClassLoader());
  }

  private boolean areAllClassesInTheClasspath(String[] classesToLookFor, ClassLoader classLoader) {
    for (String classToCheck : classesToLookFor) {
      if (!isPresent(classToCheck, classLoader)) {
        return false;
      }
    }
    return true;
  }

}
