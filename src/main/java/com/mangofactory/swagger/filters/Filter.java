package com.mangofactory.swagger.filters;

public interface Filter<T> {
    void apply(FilterContext<T> context);
}
