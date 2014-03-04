package com.mangofactory.swagger.mixins
import com.fasterxml.classmate.MemberResolver
import com.fasterxml.classmate.ResolvedTypeWithMembers
import com.fasterxml.classmate.TypeResolver
import com.fasterxml.classmate.members.ResolvedField
import com.fasterxml.classmate.members.ResolvedMethod

import static com.mangofactory.swagger.models.ResolvedTypes.*

class ModelPropertySupport {
  static ResolvedMethod accessorMethod(def typeToTest, String methodName) {
    TypeResolver resolver = new TypeResolver()
    MemberResolver memberResolver = new MemberResolver(resolver);
    memberResolver.setIncludeLangObject(false);

    ResolvedTypeWithMembers typeWithMembers = memberResolver.resolve(asResolved(resolver, typeToTest), null, null);
    typeWithMembers.memberMethods.find { it.name == methodName}
  }
  static ResolvedField field(def typeToTest, String fieldName) {
    TypeResolver resolver = new TypeResolver()
    MemberResolver memberResolver = new MemberResolver(resolver);
    memberResolver.setIncludeLangObject(false);

    ResolvedTypeWithMembers typeWithMembers = memberResolver.resolve(asResolved(resolver, typeToTest), null, null);
    typeWithMembers.memberFields.find { it.name == fieldName}
  }
}
