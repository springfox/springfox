package com.mangofactory.swagger.models;

import com.fasterxml.classmate.MemberResolver;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedMethod;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.Lists.newArrayList;

@Component
public class AccessorsProvider {
    private static Pattern getter = Pattern.compile("^get([a-zA-Z_0-9].*)");
    private static Pattern isGetter = Pattern.compile("^is([a-zA-Z_0_9].*)");
    private static Pattern setter = Pattern.compile("^set([a-zA-Z_0-9].*)");

    private TypeResolver typeResolver;
    @Autowired
    public AccessorsProvider(TypeResolver typeResolver) {
        this.typeResolver = typeResolver;
    }

    public static boolean isGetter(Method method) {
        if (method.getParameterTypes().length == 0) {
            if (getter.matcher(method.getName()).find() &&
                    !method.getReturnType().equals(void.class)) {
                return true;
            }
            if (isGetter.matcher(method.getName()).find() &&
                    method.getReturnType().equals(boolean.class)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSetter(Method method) {
        return method.getParameterTypes().length == 1 &&
                setter.matcher(method.getName()).find();
    }

    private Function<ResolvedMethod, BeanModelProperty> toBeanModelProperty() {
        return new Function<ResolvedMethod, BeanModelProperty>() {
            @Override
            public BeanModelProperty apply(ResolvedMethod input) {
                return new BeanModelProperty(propertyName(input.getRawMember().getName()), input,
                        isGetter(input.getRawMember()));
            }
        };
    }

    private String toCamelCase(String s) {
        return s.substring(0, 1).toLowerCase() +
                s.substring(1);
    }

    @VisibleForTesting
    String propertyName(String methodName) {
        Preconditions.checkNotNull(methodName);
        Matcher matcher = getter.matcher(methodName);
        if (matcher.find()) {
            return toCamelCase(matcher.group(1));
        }
        matcher = isGetter.matcher(methodName);
        if (matcher.find()) {
            return toCamelCase(matcher.group(1));
        }
        matcher = setter.matcher(methodName);
        if (matcher.find()) {
            return toCamelCase(matcher.group(1));
        }
        return "";
    }

    private Predicate<ResolvedMethod> onlyGettersAndSetters() {
        return new Predicate<ResolvedMethod>() {
            @Override
            public boolean apply(ResolvedMethod input) {
                return isGetter(input.getRawMember()) || isSetter(input.getRawMember());
            }
        };
    }

    public List<BeanModelProperty> in(ResolvedType resolvedType) {
        MemberResolver resolver = new MemberResolver(typeResolver);
        resolver.setIncludeLangObject(false);
        if (resolvedType.getErasedType() == Object.class) {
            return newArrayList();
        }
        ResolvedTypeWithMembers typeWithMembers = resolver.resolve(resolvedType, null, null);
        return FluentIterable
                .from(newArrayList(typeWithMembers.getMemberMethods()))
                .filter(onlyGettersAndSetters())
                .transform(toBeanModelProperty()).toList();
    }
}
