package com.mangofactory.swagger.readers.operation.parameter;

import com.mangofactory.swagger.readers.operation.RequestMappingReader;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.model.Parameter;

import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Base implementation of Swagger parameters readers
 * @author fgaule
 * @since 18/07/2014
 */
public abstract class SwaggerParameterReader implements RequestMappingReader {

  @Override
  public final void execute(RequestMappingContext context) {
    List<Parameter> parameters = (List<Parameter>) context.get("parameters");
    if (parameters == null) {
      parameters = newArrayList();
    }
    parameters.addAll(this.readParameters(context));
    context.put("parameters", parameters);
  }

  abstract protected Collection<? extends Parameter> readParameters(RequestMappingContext context);
}
