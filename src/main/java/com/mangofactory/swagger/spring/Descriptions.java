package com.mangofactory.swagger.spring;

import static com.google.common.base.Strings.isNullOrEmpty;

public class Descriptions {
    public static String splitCamelCase(String s) {
        if (isNullOrEmpty(s)) {
            return "";
        }
        return s.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
        );
    }

    public static String splitCamelCase(String s, String separator) {
        if (isNullOrEmpty(s)) {
            return "";
        }
        return s.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                separator
        );
    }
}
