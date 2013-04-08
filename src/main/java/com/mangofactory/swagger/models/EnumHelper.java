package com.mangofactory.swagger.models;

import com.google.common.base.Function;

import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.*;

class EnumHelper {
    static boolean isEnum(Class<?> subject) {
        return subject.isEnum();
    }

    static List<String> getEnumValues(Class<?> subject) {
        return transform(Arrays.asList(subject.getEnumConstants()), new Function<Object, String>() {
            @Override
            public String apply(Object input) {
                return input.toString();
            }
        });
    }
}
