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

package springfox.documentation.schema.property.bean;

import static springfox.documentation.schema.property.bean.Accessors.maybeAGetter;
import static springfox.documentation.schema.property.bean.Accessors.maybeASetter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.classmate.MemberResolver;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedMethod;

@Component
public class AccessorsProvider {

  private TypeResolver typeResolver;

  @Autowired
  public AccessorsProvider(TypeResolver typeResolver) {
    this.typeResolver = typeResolver;
  }

  private Predicate<ResolvedMethod> onlyGettersAndSetters() {
    return new Predicate<ResolvedMethod>() {
      @Override
      public boolean test(ResolvedMethod input) {
        return maybeAGetter(input.getRawMember()) || maybeASetter(input.getRawMember());
      }
    };
  }

  public List<ResolvedMethod> in(ResolvedType resolvedType) {
    MemberResolver resolver = new MemberResolver(typeResolver);
    resolver.setIncludeLangObject(false);
    if (resolvedType.getErasedType() == Object.class) {
      return new ArrayList<>();
    }
    ResolvedTypeWithMembers typeWithMembers = resolver.resolve(resolvedType, null, null);
    return Arrays.asList(typeWithMembers.getMemberMethods()).stream().filter(onlyGettersAndSetters()).collect(Collectors.toList());
  }
}
