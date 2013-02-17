package com.mangofactory.swagger.spring.filters;

import com.mangofactory.swagger.filters.Filter;
import com.mangofactory.swagger.filters.FilterContext;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.wordnik.swagger.core.Documentation;

public class ApplicationFilter implements Filter<Documentation> {
    @Override
    public void apply(FilterContext<Documentation> context) {
        Documentation doc = context.subject();
        SwaggerConfiguration config = context.get("swagger");
        doc.setSwaggerVersion(config.getSwaggerVersion());
        doc.setBasePath(config.getBasePath());
        doc.setApiVersion(config.getApiVersion());
    }

}
