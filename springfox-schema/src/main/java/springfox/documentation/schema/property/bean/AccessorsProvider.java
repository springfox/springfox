/*
 *
 *  Copyright 2015-2019 the original author or authors.
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

package springfox.documentation.schema.property.bean;

import com.fasterxml.classmate.MemberResolver;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static springfox.documentation.schema.property.bean.Accessors.*;

@Component
public class AccessorsProvider {

  private TypeResolver typeResolver;

  @Autowired
  public AccessorsProvider(TypeResolver typeResolver) {
    this.typeResolver = typeResolver;
  }

  private Predicate<ResolvedMethod> onlyGettersAndSetters() {
    return input -> maybeAGetter(input.getRawMember()) || maybeASetter(input.getRawMember());
  }

  public List<ResolvedMethod> in(ResolvedType resolvedType) {
    MemberResolver resolver = new MemberResolver(typeResolver);
    resolver.setIncludeLangObject(false);
    if (resolvedType.getErasedType() == Object.class) {
      return Collections.emptyList();
    }
    ResolvedTypeWithMembers typeWithMembers = resolver.resolve(resolvedType, null, null);
    return
        Stream.of(typeWithMembers.getMemberMethods())
        .filter(onlyGettersAndSetters()).collect(collectingAndThen(toList(), Collections::unmodifiableList));
  }
}
