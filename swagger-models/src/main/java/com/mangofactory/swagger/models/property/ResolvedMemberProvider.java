package com.mangofactory.swagger.models.property;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.members.ResolvedMember;

public interface ResolvedMemberProvider<T extends ResolvedMember> {

  Iterable<T> in(ResolvedType resolvedType);
}
