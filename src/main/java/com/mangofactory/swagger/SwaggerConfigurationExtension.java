package com.mangofactory.swagger;

import com.mangofactory.swagger.filters.Filter;
import com.wordnik.swagger.core.Documentation;
import com.wordnik.swagger.core.DocumentationEndPoint;
import com.wordnik.swagger.core.DocumentationError;
import com.wordnik.swagger.core.DocumentationOperation;
import com.wordnik.swagger.core.DocumentationParameter;
import lombok.Data;

import java.util.List;

import static com.google.common.collect.Lists.*;

@Data
public class SwaggerConfigurationExtension {
    private List<Filter<Documentation>> documentationFilters = newArrayList();
    private List<Filter<DocumentationEndPoint>> endpointFilters = newArrayList();
    private List<Filter<DocumentationOperation>> operationFilters = newArrayList();
    private List<Filter<DocumentationParameter>> parameterFilters = newArrayList();
    private List<Filter<List<DocumentationError>>> errorFilters = newArrayList();
    private List<Class<?>> ignorableParameterTypes = newArrayList();

}
