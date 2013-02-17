package com.mangofactory.swagger.filters;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class FilterContext<T> {
    private T subject;
    private Map<String, Object> lookup = newHashMap();

    public FilterContext(T subject) {
        this.subject = subject;
    }

    public T subject() {
        return subject;
    }

    public void put(String key, Object value) {
        lookup.put(key, value);
    }

    public <U> U get(String key) {
        return (U) lookup.get(key);
    }

}
