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

package springfox.documentation.spring.web.readers.operation;

import com.fasterxml.classmate.MemberResolver;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedMethod;



import com.google.common.base.Predicate;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import com.google.common.primitives.Ints;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.web.method.HandlerMethod;
import springfox.documentation.service.ResolvedMethodParameter;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import static com.google.common.base.Strings.*;
import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.*;

public class HandlerMethodResolver {

  private static final String SPRING4_DISCOVERER = "org.springframework.core.DefaultParameterNameDiscoverer";
  private final ParameterNameDiscoverer parameterNameDiscover = parameterNameDiscoverer();
  private final TypeResolver typeResolver;
  private Map<Class, List<ResolvedMethod>> methodsResolvedForHostClasses = new HashMap<Class, List<ResolvedMethod>>();

  public HandlerMethodResolver(TypeResolver typeResolver) {
    this.typeResolver = typeResolver;
  }

  public ResolvedType methodReturnType(HandlerMethod handlerMethod) {
    return resolvedMethod(handlerMethod).map(toReturnType(typeResolver)).orElse(typeResolver.resolve(Void.TYPE));
  }

  public static Optional<Class> useType(Class beanType) {
    if (Proxy.class.isAssignableFrom(beanType)) {
      return Optional.empty();
    }
    if (Class.class.getName().equals(beanType.getName())) {
      return Optional.empty();
    }
    return Optional.ofNullable(beanType);
  }

  public List<ResolvedMethodParameter> methodParameters(final HandlerMethod methodToResolve) {
    return resolvedMethod(methodToResolve)
        .map(toParameters(methodToResolve))
        .orElse(Lists.<ResolvedMethodParameter>newArrayList());
  }

  boolean contravariant(ResolvedType candidateMethodReturnValue, Type returnValueOnMethod) {
    return isSubClass(candidateMethodReturnValue, returnValueOnMethod)
        || isGenericTypeSubclass(candidateMethodReturnValue, returnValueOnMethod);
  }


  static Comparator<ResolvedMethod> byArgumentCount() {
    return new Comparator<ResolvedMethod>() {
      @Override
      public int compare(ResolvedMethod first, ResolvedMethod second) {
        return Ints.compare(first.getArgumentCount(), second.getArgumentCount());
      }
    };
  }

  boolean bothAreVoids(ResolvedType candidateMethodReturnValue, Type returnType) {
    return (Void.class == candidateMethodReturnValue.getErasedType()
                || Void.TYPE == candidateMethodReturnValue.getErasedType())
        && (Void.TYPE == returnType
                || Void.class == returnType);
  }

  boolean isGenericTypeSubclass(ResolvedType candidateMethodReturnValue, Type returnValueOnMethod) {
    return returnValueOnMethod instanceof ParameterizedType &&
        candidateMethodReturnValue.getErasedType()
            .isAssignableFrom((Class<?>) ((ParameterizedType) returnValueOnMethod).getRawType());
  }

  boolean isSubClass(ResolvedType candidateMethodReturnValue, Type returnValueOnMethod) {
    return returnValueOnMethod instanceof Class
        && candidateMethodReturnValue.getErasedType().isAssignableFrom((Class<?>) returnValueOnMethod);
  }

  boolean covariant(ResolvedType candidateMethodArgument, Type argumentOnMethod) {
    return isSuperClass(candidateMethodArgument, argumentOnMethod)
        || isGenericTypeSuperClass(candidateMethodArgument, argumentOnMethod);
  }

  boolean isGenericTypeSuperClass(ResolvedType candidateMethodArgument, Type argumentOnMethod) {
    return argumentOnMethod instanceof ParameterizedType &&
        ((Class<?>) ((ParameterizedType) argumentOnMethod).getRawType())
            .isAssignableFrom(candidateMethodArgument.getErasedType());
  }

  boolean isSuperClass(ResolvedType candidateMethodArgument, Type argumentOnMethod) {
    return argumentOnMethod instanceof Class
        && ((Class<?>) argumentOnMethod).isAssignableFrom(candidateMethodArgument.getErasedType());
  }

  private Optional<ResolvedMethod> resolvedMethod(HandlerMethod handlerMethod) {
    if (handlerMethod == null) {
      return Optional.empty();
    }
    Class hostClass = useType(handlerMethod.getBeanType())
        .orElse(handlerMethod.getMethod().getDeclaringClass());
    Iterable<ResolvedMethod> filtered = filter(getMemberMethods(hostClass),
        methodNamesAreSame(handlerMethod.getMethod()));
    return resolveToMethodWithMaxResolvedTypes(filtered, handlerMethod.getMethod());
  }

  private List<ResolvedMethod> getMemberMethods(
          Class hostClass) {
    if(!methodsResolvedForHostClasses.containsKey(hostClass)) {
      ResolvedType beanType = typeResolver.resolve(hostClass);
      MemberResolver resolver = new MemberResolver(typeResolver);
      resolver.setIncludeLangObject(false);
      ResolvedTypeWithMembers typeWithMembers = resolver.resolve(beanType, null, null);
      methodsResolvedForHostClasses.put(hostClass, newArrayList(typeWithMembers.getMemberMethods()));
    }
    return methodsResolvedForHostClasses.get(hostClass);
  }

  private static Function<ResolvedMethod, ResolvedType> toReturnType(final TypeResolver resolver) {
    return new Function<ResolvedMethod, ResolvedType>() {
      @Override
      public ResolvedType apply(ResolvedMethod input) {
        return Optional.ofNullable(input.getReturnType()).orElse(resolver.resolve(Void.TYPE));
      }
    };
  }

  private Function<ResolvedMethod, List<ResolvedMethodParameter>> toParameters(final HandlerMethod methodToResolve) {
    return new Function<ResolvedMethod, List<ResolvedMethodParameter>>() {
      @Override
      public List<ResolvedMethodParameter> apply(ResolvedMethod input) {
        List<ResolvedMethodParameter> parameters = newArrayList();
        MethodParameter[] methodParameters = methodToResolve.getMethodParameters();
        for (int i = 0; i < input.getArgumentCount(); i++) {
          parameters.add(new ResolvedMethodParameter(
              discoveredName(methodParameters[i]).orElse(String.format("param%s", i)),
              methodParameters[i],
              input.getArgumentType(i)));
        }
        return parameters;
      }
    };
  }

  private static Iterable<ResolvedMethod> methodsWithSameNumberOfParams(
      Iterable<ResolvedMethod> filtered,
      final Method methodToResolve) {

    return filter(filtered, new Predicate<ResolvedMethod>() {
      @Override
      public boolean apply(ResolvedMethod input) {
        return input.getArgumentCount() == methodToResolve.getParameterTypes().length;
      }
    });
  }

  private static Predicate<ResolvedMethod> methodNamesAreSame(final Method methodToResolve) {
    return new Predicate<ResolvedMethod>() {
      @Override
      public boolean apply(ResolvedMethod input) {
        return input.getRawMember().getName().equals(methodToResolve.getName());
      }
    };
  }

  private Optional<ResolvedMethod> resolveToMethodWithMaxResolvedTypes(
      Iterable<ResolvedMethod> filtered,
      Method methodToResolve) {
    if (Iterables.size(filtered) > 1) {
      Iterable<ResolvedMethod> covariantMethods = covariantMethods(filtered, methodToResolve);
      if (Iterables.size(covariantMethods) == 0) {
        return StreamSupport.stream(filtered.spliterator(), false)
            .filter(sameMethod(methodToResolve)).findFirst();
      } else if (Iterables.size(covariantMethods) == 1) {
        return StreamSupport.stream(covariantMethods.spliterator(), false).findFirst();
      } else {
        return StreamSupport.stream(covariantMethods.spliterator(), false).max(byArgumentCount());
      }
    }
    return StreamSupport.stream(filtered.spliterator(), false).findFirst();
  }

  private Predicate<ResolvedMethod> sameMethod(final Method methodToResolve) {
    return new Predicate<ResolvedMethod>() {
      @Override
      public boolean apply(ResolvedMethod input) {
        return methodToResolve.equals(input.getRawMember());
      }
    };
  }

  private Iterable<ResolvedMethod> covariantMethods(
      Iterable<ResolvedMethod> filtered,
      final Method methodToResolve) {

    return filter(methodsWithSameNumberOfParams(filtered, methodToResolve), onlyCovariantMethods(methodToResolve));
  }

  private Predicate<ResolvedMethod> onlyCovariantMethods(final Method methodToResolve) {
    return new Predicate<ResolvedMethod>() {
      @Override
      public boolean apply(ResolvedMethod input) {
        for (int index = 0; index < input.getArgumentCount(); index++) {
          if (!covariant(input.getArgumentType(index), methodToResolve.getGenericParameterTypes()[index])) {
            return false;
          }
        }
        ResolvedType candidateMethodReturnValue = returnTypeOrVoid(input);
        return bothAreVoids(candidateMethodReturnValue, methodToResolve.getGenericReturnType())
            || contravariant(candidateMethodReturnValue, methodToResolve.getGenericReturnType());
      }
    };
  }

  private ResolvedType returnTypeOrVoid(ResolvedMethod input) {
    ResolvedType returnType = input.getReturnType();
    if (returnType == null) {
      returnType = typeResolver.resolve(Void.class);
    }
    return returnType;
  }


  private Optional<String> discoveredName(MethodParameter methodParameter) {
    String[] discoveredNames = parameterNameDiscover.getParameterNames(methodParameter.getMethod());
    int discoveredNameCount = Optional.ofNullable(discoveredNames).orElse(new String[0]).length;
    return methodParameter.getParameterIndex() < discoveredNameCount
           ? Optional.ofNullable(emptyToNull(discoveredNames[methodParameter.getParameterIndex()]))
           : Optional.ofNullable(methodParameter.getParameterName());
  }


  private ParameterNameDiscoverer parameterNameDiscoverer() {
    ParameterNameDiscoverer discoverer;
    try {
      discoverer = (ParameterNameDiscoverer) Class.forName(SPRING4_DISCOVERER).newInstance();
    } catch (Exception e) {
      discoverer = new LocalVariableTableParameterNameDiscoverer();
    }
    return discoverer;
  }
}
