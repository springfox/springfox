/*
 *
 *  Copyright 2017-2019 the original author or authors.
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
package springfox.documentation.schema;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ClassUtils;

import static org.springframework.util.ClassUtils.forName;

public abstract class ClassPresentInClassPathCondition implements Condition {
  @Override
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    return isPresent(getClassName(), context.getClassLoader());
  }

  protected abstract String getClassName();

  private static boolean isPresent(String className, ClassLoader classLoader) {
    if (classLoader == null) {
      classLoader = ClassUtils.getDefaultClassLoader();
    }
    try {
      forName(className, classLoader);
      return true;
    } catch (Throwable ex) {
      return false;
    }
  }
}
