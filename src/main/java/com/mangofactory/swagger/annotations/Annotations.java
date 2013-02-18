package com.mangofactory.swagger.annotations;

import java.lang.reflect.ParameterizedType;

public class Annotations {

    public static Class<?> getClass(final Class<?> genericClass) {
        ParameterizedType type = (ParameterizedType) genericClass.getGenericInterfaces()[0];
        return (Class<?>) type.getActualTypeArguments()[0];
    }

    public static String getAnnotatedType(ApiModel apiModel) {
        StringBuilder sb = new StringBuilder();
        if (apiModel.collection()) {
            sb.append(String.format("%s[%s]", apiModel.listType(), apiModel.type().getSimpleName()));
        } else {
            sb.append(apiModel.type().getSimpleName());
        }
        return sb.toString();
    }
}
