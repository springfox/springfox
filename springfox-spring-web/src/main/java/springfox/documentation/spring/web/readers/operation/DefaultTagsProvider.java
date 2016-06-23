/*
 *
 *  Copyright 2015-2016 the original author or authors.
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
package springfox.documentation.spring.web.readers.operation;

import com.google.common.collect.ImmutableSet;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.service.contexts.OperationContext;

import static com.google.common.collect.ImmutableSet.*;
import static com.google.common.collect.Sets.*;

@Component
public class DefaultTagsProvider {
  public ImmutableSet<String> tags(OperationContext context) {
    return copyOf(newHashSet(context.getGroupName()));
  }
}
