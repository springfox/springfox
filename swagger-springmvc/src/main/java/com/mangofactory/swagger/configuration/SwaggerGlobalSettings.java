package com.mangofactory.swagger.configuration;

import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider;
import com.mangofactory.swagger.models.dto.ResponseMessage;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.*;

public class SwaggerGlobalSettings {
  /**
   * Set of classes to exclude from spring controller request mapping methods
   * e.g HttpServletRequest, BindingResult
   */
  private Set<Class> ignorableParameterTypes;

  /**
   * Map of spring RequestMethod's to a list of http status codes and accompanying messages
   *
   * @see com.mangofactory.swagger.readers.operation.DefaultResponseMessageReader
   */
  private Map<RequestMethod, List<ResponseMessage>> globalResponseMessages = newHashMap();
  private TypeResolver typeResolver = new TypeResolver();
  private AlternateTypeProvider alternateTypeProvider;

  public Set<Class> getIgnorableParameterTypes() {
    return ignorableParameterTypes;
  }

  public void setIgnorableParameterTypes(Set<Class> ignorableParameterTypes) {
    this.ignorableParameterTypes = ignorableParameterTypes;
  }

  public void setAlternateTypeProvider(AlternateTypeProvider alternateTypeProvider) {
    this.alternateTypeProvider = alternateTypeProvider;
  }

  public Map<RequestMethod, List<ResponseMessage>> getGlobalResponseMessages() {
    return globalResponseMessages;
  }

  public void setGlobalResponseMessages(Map<RequestMethod, List<ResponseMessage>> globalResponseMessages) {
    this.globalResponseMessages = globalResponseMessages;
  }

  public TypeResolver getTypeResolver() {
    return typeResolver;
  }

  public AlternateTypeProvider getAlternateTypeProvider() {
    return alternateTypeProvider;
  }
}
