package com.mangofactory.swagger.scanners;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import java.util.List;

public class ResourceGroups {
    public static List<String> groupUris(Iterable<ResourceGroup> resourceGroups) {
        return FluentIterable.from(resourceGroups).transform(groupUri()).toList();
    }

    public static Function<ResourceGroup, String> groupUri() {
        return new Function<ResourceGroup, String>() {
            @Override
            public String apply(ResourceGroup input) {
                return input.getGroupName();
            }
        };
    }
}
