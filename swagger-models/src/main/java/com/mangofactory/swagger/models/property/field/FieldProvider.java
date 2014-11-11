package com.mangofactory.swagger.models.property.field;

import com.fasterxml.classmate.MemberResolver;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedField;
import com.mangofactory.swagger.models.property.ResolvedMemberProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.google.common.collect.Lists.*;

@Component
public class FieldProvider implements ResolvedMemberProvider<ResolvedField> {
  private final TypeResolver typeResolver;

  @Autowired
  public FieldProvider(TypeResolver typeResolver) {
    this.typeResolver = typeResolver;
  }

  @Override
  public Iterable<ResolvedField> in(ResolvedType resolvedType) {
    MemberResolver memberResolver = new MemberResolver(typeResolver);
    if (resolvedType.getErasedType() == Object.class) {
      return newArrayList();
    }
    ResolvedTypeWithMembers resolvedMemberWithMembers = memberResolver.resolve(resolvedType, null, null);
    return newArrayList(resolvedMemberWithMembers.getMemberFields());
  }
}
