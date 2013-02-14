package com.mangofactory.swagger.filters;

public class Filters {
    public static class Fn {
        private Fn() {
            throw new UnsupportedOperationException();
        }

        public static <T> void applyFilters(Iterable<Filter<T>> filters, FilterContext<T> context) {
            for (Filter<T> filter : filters) {
                filter.apply(context);
            }
        }
    }
}
