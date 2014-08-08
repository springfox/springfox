package com.mangofactory.swagger.models.property.bean;

import com.fasterxml.classmate.MemberResolver;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedMethod;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.google.common.collect.Lists.*;
import static com.mangofactory.swagger.models.property.bean.Accessors.*;

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
      public boolean apply(ResolvedMethod input) {
        return isGetter(input.getRawMember()) || isSetter(input.getRawMember());
      }
    };
  }

  public com.google.common.collect.ImmutableList<ResolvedMethod> in(ResolvedType resolvedType) {
    MemberResolver resolver = new MemberResolver(typeResolver);
    resolver.setIncludeLangObject(false);
    if (resolvedType.getErasedType() == Object.class) {
      return ImmutableList.of();
    }
    ResolvedTypeWithMembers typeWithMembers = resolver.resolve(resolvedType, null, null);
    return FluentIterable
            .from(newArrayList(typeWithMembers.getMemberMethods()))
            .filter(onlyGettersAndSetters()).toList();
  }
}
