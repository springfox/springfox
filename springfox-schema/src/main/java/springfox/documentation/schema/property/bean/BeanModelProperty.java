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

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedMember;
import com.fasterxml.classmate.members.ResolvedMethod;
import springfox.documentation.schema.property.BaseModelProperty;
import springfox.documentation.spi.schema.AlternateTypeProvider;


public class BeanModelProperty extends BaseModelProperty {

  private final ResolvedMethod method;
  private final boolean isGetter;
  private TypeResolver typeResolver;


  public BeanModelProperty(String propertyName, ResolvedMethod method,
                           boolean isGetter, TypeResolver typeResolver,
                           AlternateTypeProvider alternateTypeProvider) {

    super(propertyName, alternateTypeProvider);

    this.method = method;
    this.isGetter = isGetter;
    this.typeResolver = typeResolver;
  }

  public static boolean accessorMemberIs(ResolvedMember method, String methodName) {
    return method.getRawMember().getName().equals(methodName);
  }

  @Override
  protected ResolvedType realType() {
    if (isGetter) {
      if (method.getReturnType().getErasedType().getTypeParameters().length > 0) {
        return method.getReturnType();
      } else {
        return typeResolver.resolve(method.getReturnType().getErasedType());
      }
    } else {
      if (method.getArgumentType(0).getErasedType().getTypeParameters().length > 0) {
        return method.getArgumentType(0);
      } else {
        return typeResolver.resolve(method.getArgumentType(0).getErasedType());
      }
    }
  }
}
