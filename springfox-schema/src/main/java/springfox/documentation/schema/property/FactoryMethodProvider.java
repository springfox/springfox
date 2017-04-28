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
package springfox.documentation.schema.property;


import com.fasterxml.classmate.MemberResolver;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedConstructor;
import com.fasterxml.classmate.members.ResolvedMethod;
import com.fasterxml.classmate.members.ResolvedParameterizedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

import static com.google.common.collect.FluentIterable.*;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Lists.*;

@Component
public class FactoryMethodProvider {
  private MemberResolver memberResolver;

  @Autowired
  public FactoryMethodProvider(TypeResolver resolver) {
    memberResolver = new MemberResolver(resolver);
  }

  public Optional<? extends ResolvedParameterizedMember> in(
      ResolvedType resolvedType,
      Predicate<ResolvedParameterizedMember> predicate) {
    return from(concat(constructors(resolvedType), delegatedFactoryMethods(resolvedType))).firstMatch(predicate);
  }

  static Predicate<ResolvedParameterizedMember> factoryMethodOf(final AnnotatedParameter parameter) {
    return new Predicate<ResolvedParameterizedMember>() {
      @Override
      public boolean apply(ResolvedParameterizedMember input) {
        return input.getRawMember().equals(parameter.getOwner().getMember());
      }
    };
  }

  public Collection<ResolvedConstructor> constructors(ResolvedType resolvedType) {
    ResolvedTypeWithMembers typeWithMembers = memberResolver.resolve(resolvedType, null, null);
    return newArrayList(typeWithMembers.getConstructors());
  }

  public Collection<ResolvedMethod> delegatedFactoryMethods(ResolvedType resolvedType) {
    ResolvedTypeWithMembers typeWithMembers = memberResolver.resolve(resolvedType, null, null);
    return newArrayList(typeWithMembers.getStaticMethods());
  }
}
