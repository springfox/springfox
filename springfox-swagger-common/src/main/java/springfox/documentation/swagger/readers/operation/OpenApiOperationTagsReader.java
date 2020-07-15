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
package springfox.documentation.swagger.readers.operation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static springfox.documentation.service.Tags.*;

@Component
@Order(SwaggerPluginSupport.OAS_PLUGIN_ORDER)
public class OpenApiOperationTagsReader implements OperationBuilderPlugin {

  @Override
  public void apply(OperationContext context) {
    context.operationBuilder()
        .tags(Stream.concat(
            operationTags(context).stream(),
            controllerTags(context).stream())
            .collect(toSet()));
  }

  private Set<String> controllerTags(OperationContext context) {
    return tagsFromOasAnnotations(context)
        .stream()
        .map(springfox.documentation.service.Tag::getName)
        .collect(toSet());
  }

  private Set<springfox.documentation.service.Tag> tagsFromOasAnnotations(OperationContext context) {
    HashSet<springfox.documentation.service.Tag> controllerTags
        = new HashSet<>();
    List<Tags> tags =
        context.findAllAnnotations(Tags.class);
    tags.forEach(ts ->
        Arrays.stream(ts.value())
            .forEach(t -> controllerTags.add(
                new springfox.documentation.service.Tag(t.name(), t.description()))));
    List<Tag> tag = context.findAllAnnotations(Tag.class);
    tag.forEach(t -> controllerTags.add(
        new springfox.documentation.service.Tag(t.name(), t.description())));
    return controllerTags;
  }

  private Set<String> operationTags(OperationContext context) {
    Optional<Operation> oasAnnotation = context.findAnnotation(Operation.class);
    return new HashSet<>(oasAnnotation.map(tagsFromOasOperation()).orElse(new HashSet<>()));
  }

  private Function<Operation, Set<String>> tagsFromOasOperation() {
    return input -> Stream.of(input.tags())
        .filter(emptyTags())
        .collect(toCollection(TreeSet::new));
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }
}

