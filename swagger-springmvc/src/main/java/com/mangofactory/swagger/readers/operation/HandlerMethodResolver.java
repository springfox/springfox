package com.mangofactory.swagger.readers.operation;

import com.fasterxml.classmate.MemberResolver;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedMethod;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;

import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.*;
import static com.mangofactory.swagger.models.ResolvedTypes.*;

public class HandlerMethodResolver {

  private static final Logger log = LoggerFactory.getLogger(HandlerMethodResolver.class);
  private final TypeResolver typeResolver;

  public HandlerMethodResolver(TypeResolver typeResolver) {
    this.typeResolver = typeResolver;
  }

  public List<ResolvedMethodParameter> methodParameters(final HandlerMethod methodToResolve) {
    Class hostClass = use(methodToResolve.getBeanType())
            .or(methodToResolve.getMethod().getDeclaringClass());
    ResolvedMethod resolvedMethod = getResolvedMethod(methodToResolve.getMethod(), hostClass);
    List<ResolvedMethodParameter> parameters = newArrayList();
    MethodParameter[] methodParameters = methodToResolve.getMethodParameters();
    if (resolvedMethod != null) {
      if (methodParameters.length == resolvedMethod.getArgumentCount()) {
        for (int index = 0; index < resolvedMethod.getArgumentCount(); index++) {
          MethodParameter methodParameter = methodParameters[index];
          methodParameter.initParameterNameDiscovery(new LocalVariableTableParameterNameDiscoverer());
          parameters.add(new ResolvedMethodParameter(methodParameter, resolvedMethod.getArgumentType(index)));
        }
      } else {
        log.warn(String.format("Problem trying to resolve a method named %s", methodToResolve.getMethod().getName()));
        log.warn(String.format("Method parameter count %s does not match resolved method argument count %s",
                methodParameters.length, resolvedMethod.getArgumentCount()));
      }
    }
    return parameters;
  }

  public static Optional<Class> use(Class beanType) {
    if (Class.class.getName().equals(beanType.getName())) {
      return Optional.absent();
    }
    return Optional.fromNullable(beanType);
  }

  /**
   * Resolves the return type of the given method in the class.
   *
   * @param methodToResolve a method which is declared in the implementing class or one of its subclasses
   * @param actualClass     the actual class. Used to resolve generic types if needed.
   * @return
   */
  public ResolvedType methodReturnType(final Method methodToResolve, Class<?> actualClass) {
    ResolvedMethod resolvedMethod = getResolvedMethod(methodToResolve, actualClass);
    if (resolvedMethod != null) {
      return returnTypeOrVoid(resolvedMethod);
    }
    return asResolved(typeResolver, methodToResolve.getReturnType());
  }

  private ResolvedMethod getResolvedMethod(final Method methodToResolve, Class<?> beanType) {
    ResolvedType enclosingType = typeResolver.resolve(beanType);
    MemberResolver resolver = new MemberResolver(typeResolver);
    resolver.setIncludeLangObject(false);
    ResolvedTypeWithMembers typeWithMembers = resolver.resolve(enclosingType, null, null);
    Iterable<ResolvedMethod> filtered = filter(newArrayList(typeWithMembers.getMemberMethods()),
            methodNamesAreSame(methodToResolve));
    return resolveToMethodWithMaxResolvedTypes(filtered, methodToResolve);
  }

  private static Predicate<ResolvedMethod> methodNamesAreSame(final Method methodToResolve) {
    return new Predicate<ResolvedMethod>() {
      @Override
      public boolean apply(ResolvedMethod input) {
        return input.getRawMember().getName().equals(methodToResolve.getName());
      }
    };
  }

  private ResolvedMethod resolveToMethodWithMaxResolvedTypes(Iterable<ResolvedMethod> filtered,
                                                             Method methodToResolve) {

    if (Iterables.size(filtered) > 1) {
      Iterable<ResolvedMethod> covariantMethods = covariantMethods(filtered, methodToResolve);
      if (Iterables.size(covariantMethods) == 0) {
        return byArgumentCount().max(filtered);
      } else if (Iterables.size(covariantMethods) == 1) {
        return Iterables.getFirst(covariantMethods, null);
      } else {
        return byArgumentCount().max(covariantMethods);
      }
    } else if (Iterables.size(filtered) == 1) {
      return Iterables.getFirst(filtered, null);
    }
    return null;
  }

  private static Ordering<ResolvedMethod> byArgumentCount() {
    return Ordering.from(new Comparator<ResolvedMethod>() {
      @Override
      public int compare(ResolvedMethod first, ResolvedMethod second) {
        return Ints.compare(first.getArgumentCount(), second.getArgumentCount());
      }
    });
  }

  private Iterable<ResolvedMethod> covariantMethods(Iterable<ResolvedMethod> filtered,
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

  private boolean bothAreVoids(ResolvedType candidateMethodReturnValue, Type returnType) {
    return Void.class == candidateMethodReturnValue.getErasedType()
            && (Void.TYPE == returnType || Void.class == returnType);
  }

  private ResolvedType returnTypeOrVoid(ResolvedMethod input) {
    ResolvedType returnType = input.getReturnType();
    if (returnType == null) {
      returnType = typeResolver.resolve(Void.class);
    }
    return returnType;
  }

  private boolean contravariant(ResolvedType candidateMethodReturnValue, Type returnValueOnMethod) {
    return isSubClass(candidateMethodReturnValue, returnValueOnMethod)
            || isGenericTypeSubclass(candidateMethodReturnValue, returnValueOnMethod);
  }

  private boolean isGenericTypeSubclass(ResolvedType candidateMethodReturnValue, Type returnValueOnMethod) {
    return returnValueOnMethod instanceof ParameterizedType &&
            candidateMethodReturnValue.getErasedType()
                    .isAssignableFrom((Class<?>) ((ParameterizedType) returnValueOnMethod).getRawType());
  }

  private boolean isSubClass(ResolvedType candidateMethodReturnValue, Type returnValueOnMethod) {
    return returnValueOnMethod instanceof Class
            && candidateMethodReturnValue.getErasedType().isAssignableFrom((Class<?>) returnValueOnMethod);
  }

  private boolean covariant(ResolvedType candidateMethodArgument, Type argumentOnMethod) {
    return isSuperClass(candidateMethodArgument, argumentOnMethod)
            || isGenericTypeSuperClass(candidateMethodArgument, argumentOnMethod);
  }

  private boolean isGenericTypeSuperClass(ResolvedType candidateMethodArgument, Type argumentOnMethod) {
    return argumentOnMethod instanceof ParameterizedType &&
            ((Class<?>) ((ParameterizedType) argumentOnMethod).getRawType())
                    .isAssignableFrom(candidateMethodArgument.getErasedType());
  }

  private boolean isSuperClass(ResolvedType candidateMethodArgument, Type argumentOnMethod) {
    return argumentOnMethod instanceof Class
            && ((Class<?>) argumentOnMethod).isAssignableFrom(candidateMethodArgument.getErasedType());
  }


  private static Iterable<ResolvedMethod> methodsWithSameNumberOfParams(Iterable<ResolvedMethod> filtered,
                                                                        final Method methodToResolve) {

    return filter(filtered, new Predicate<ResolvedMethod>() {
      @Override
      public boolean apply(ResolvedMethod input) {
        return input.getArgumentCount() == methodToResolve.getParameterTypes().length;
      }
    });
  }
}
