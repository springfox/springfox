package com.mangofactory.swagger.models;

import com.fasterxml.classmate.MemberResolver;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedMethod;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Lists.transform;

public class ResolvedTypes {
    static Pattern getter = Pattern.compile("^get([a-zA-Z].*)");
    static Pattern isGetter = Pattern.compile("^is([a-zA-Z].*)");
    static Pattern setter = Pattern.compile("^set([a-zA-Z].*)");

    static String toCamelCase(String s) {
        return s.substring(0, 1).toLowerCase() +
                s.substring(1);
    }
    public static boolean isGetter(Method method) {
        if (method.getParameterTypes().length == 0) {
            if (method.getName().matches("^get[a-zA-Z].*") &&
                    !method.getReturnType().equals(void.class))
                return true;
            if (method.getName().matches("^is[a-zA-Z].*") &&
                    method.getReturnType().equals(boolean.class))
                return true;
        }
        return false;
    }

    public static boolean isSetter(Method method) {
        return method.getReturnType().equals(void.class) &&
                method.getParameterTypes().length == 1 &&
                method.getName().matches("^set[a-zA-Z].*");
    }

    public static String propertyName(String methodName) {
        Preconditions.checkNotNull(methodName);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(methodName));
        Matcher matcher = getter.matcher(methodName);
        if (matcher.matches()) {
            return toCamelCase(matcher.group(1));
        }
        matcher = isGetter.matcher(methodName);
        if (matcher.matches()) {
            return toCamelCase(matcher.group(1));
        }
        matcher = setter.matcher(methodName);
        if (matcher.matches()) {
            return toCamelCase(matcher.group(1));
        }
        throw new IllegalArgumentException(String.format("Method (%s) is not a getter or a setter", methodName));
    }

    public static List<ResolvedProperty> gettersAndSetters(TypeResolver typeResolver, ResolvedType resolvedType) {

        MemberResolver resolver = new MemberResolver(typeResolver);
        resolver.setIncludeLangObject(false);

        ResolvedTypeWithMembers typeWithMembers = resolver.resolve(resolvedType, null, null);
        Iterable<ResolvedMethod> filteredProperties
                = filter(newArrayList(typeWithMembers.getMemberMethods()), new Predicate<ResolvedMethod>() {
            @Override
            public boolean apply(ResolvedMethod input) {
                return isGetter(input.getRawMember()) || isSetter(input.getRawMember());
            }
        });
        return transform(newArrayList(filteredProperties), new Function<ResolvedMethod,
                ResolvedProperty>() {
            @Override
            public ResolvedProperty apply(ResolvedMethod input) {
                return new ResolvedProperty(propertyName(input.getRawMember().getName()), input,
                        isGetter(input.getRawMember()));
            }
        });
    }

    public static List<ResolvedType> methodParameters(TypeResolver typeResolver, final Method methodToResolve) {

        ResolvedMethod resolvedMethod = getResolvedMethod(typeResolver, methodToResolve);
        List<ResolvedType> parameters = newArrayList();
        if (resolvedMethod != null) {
            for (int index = 0; index < resolvedMethod.getArgumentCount(); index++) {
                parameters.add(resolvedMethod.getArgumentType(index));
            }
        }
        return parameters;

    }

    public static ResolvedType methodReturnType(TypeResolver typeResolver, final Method methodToResolve) {
        ResolvedMethod resolvedMethod = getResolvedMethod(typeResolver, methodToResolve);
        if (resolvedMethod != null) {
            return resolvedMethod.getReturnType();
        }
        return asResolvedType(methodToResolve.getReturnType());

    }

    private static ResolvedMethod getResolvedMethod(TypeResolver typeResolver, final Method methodToResolve) {
        ResolvedType enclosingType = typeResolver.resolve(methodToResolve.getDeclaringClass());
        MemberResolver resolver = new MemberResolver(typeResolver);
        resolver.setIncludeLangObject(false);
        ResolvedTypeWithMembers typeWithMembers = resolver.resolve(enclosingType, null, null);
        Iterable<ResolvedMethod> filtered = filter(newArrayList(typeWithMembers.getMemberMethods()), new Predicate<ResolvedMethod>() {
            @Override
            public boolean apply(ResolvedMethod input) {
                return input.getRawMember().getName().equals(methodToResolve.getName());
            }
        });
        return resolveToMethodWithMaxResolvedTypes(filtered);
    }

    private static ResolvedMethod resolveToMethodWithMaxResolvedTypes(Iterable<ResolvedMethod> filtered) {
        if (Iterables.size(filtered) > 0) {
            return Ordering.from(new Comparator<ResolvedMethod>() {
                @Override
                public int compare(ResolvedMethod first, ResolvedMethod second) {
                    return Ints.compare(first.getArgumentCount(), second.getArgumentCount());
                }
            }).max(filtered);
        }
        return null;
    }

    public static ResolvedType asResolvedType(Class clazz) {
        return new TypeResolver().resolve(clazz);
    }

    public static ResolvedType asResolvedType(TypeResolver typeResolver, Class clazz) {
        return typeResolver.resolve(clazz);
    }

    public static String modelName(ResolvedType resolvedType) {
        StringBuilder sb = new StringBuilder();
        sb.append(resolvedType.getErasedType().getSimpleName());
        boolean first = true;
        for (ResolvedType typeParam:  resolvedType.getTypeParameters()) {
            if (first) {
                sb.append(String.format("[%s", typeParam.getErasedType().getSimpleName()));
                first = false;
            } else {
                sb.append(String.format(",%s", typeParam.getErasedType().getSimpleName()));
            }
        }
        if (!first) {
            sb.append("]");
        }
        return sb.toString();
    }
}
