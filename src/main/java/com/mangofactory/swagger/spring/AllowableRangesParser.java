package com.mangofactory.swagger.spring;

import com.wordnik.swagger.core.DocumentationAllowableRangeValues;

public class AllowableRangesParser {

    private static final String POSITIVE_INFINITY_STRING = "Infinity";
    private static final String NEGATIVE_INFINITY_STRING = "-Infinity";

    public static DocumentationAllowableRangeValues buildAllowableRangeValues(String[] ranges, String inputStr) {
        float min, max;
        if (ranges.length < 2) {
            throw new RuntimeException("Allowable values format " + inputStr + "is incorrect");
        }
        if (ranges[0].equalsIgnoreCase(POSITIVE_INFINITY_STRING)) {
            min = Float.POSITIVE_INFINITY;
        } else if (ranges[0].equalsIgnoreCase(NEGATIVE_INFINITY_STRING)) {
            min = Float.NEGATIVE_INFINITY;
        } else {
            min = Float.parseFloat(ranges[0]);
        }
        if (ranges[1].equalsIgnoreCase(POSITIVE_INFINITY_STRING)) {
            max = Float.POSITIVE_INFINITY;
        } else if (ranges[1].equalsIgnoreCase(NEGATIVE_INFINITY_STRING)) {
            max = Float.NEGATIVE_INFINITY;
        } else {
            max = Float.parseFloat(ranges[1]);
        }
        return new DocumentationAllowableRangeValues(min, max);
    }
}
