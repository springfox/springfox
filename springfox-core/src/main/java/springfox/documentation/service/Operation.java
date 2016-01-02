/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.service;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import org.springframework.http.HttpMethod;
import springfox.documentation.schema.ModelReference;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;

public class Operation {
  private final HttpMethod method;
  private final String summary;
  private final String notes;
  private final ModelReference responseModel;
  private final String uniqueId;
  private final int position;
  private final Set<String> tags;
  private final Set<String> produces;
  private final Set<String> consumes;
  private final Set<String> protocol;
  private final boolean isHidden;
  private final Map<String, List<AuthorizationScope>> securityReferences;
  private final List<Parameter> parameters;
  private final Set<ResponseMessage> responseMessages;
  private final String deprecated;
  private final List<VendorExtension> vendorExtensions;

  public Operation(
      HttpMethod method,
      String summary,
      String notes,
      ModelReference responseModel,
      String uniqueId,
      int position,
      Set<String> tags,
      Set<String> produces,
      Set<String> consumes,
      Set<String> protocol,
      List<SecurityReference> securityReferences,
      List<Parameter> parameters,
      Set<ResponseMessage> responseMessages,
      String deprecated,
      boolean isHidden,
      Collection<VendorExtension> vendorExtensions) {

    this.method = method;
    this.summary = summary;
    this.notes = notes;
    this.responseModel = responseModel;
    this.uniqueId = uniqueId;
    this.position = position;
    this.tags = tags;
    this.produces = produces;
    this.consumes = consumes;
    this.protocol = protocol;
    this.isHidden = isHidden;
    this.securityReferences = toAuthorizationsMap(securityReferences);
    this.parameters = parameters;
    this.responseMessages = responseMessages;
    this.deprecated = deprecated;
    this.vendorExtensions = newArrayList(vendorExtensions);
  }

  public boolean isHidden() {
    return isHidden;
  }

  public ModelReference getResponseModel() {
    return responseModel;
  }

  public Set<String> getTags() {
    return tags;
  }

  private Map<String, List<AuthorizationScope>> toAuthorizationsMap(List<SecurityReference> securityReferences) {
    return Maps.transformEntries(Maps.uniqueIndex(securityReferences, byType()), toScopes());
  }

  private EntryTransformer<? super String, ? super SecurityReference, List<AuthorizationScope>> toScopes() {
    return new EntryTransformer<String, SecurityReference, List<AuthorizationScope>>() {
      @Override
      public List<AuthorizationScope> transformEntry(String key, SecurityReference value) {
        return newArrayList(value.getScopes());
      }
    };
  }

  private Function<? super SecurityReference, String> byType() {
    return new Function<SecurityReference, String>() {
      @Override
      public String apply(SecurityReference input) {
        return input.getReference();
      }
    };
  }

  public HttpMethod getMethod() {
    return method;
  }

  public String getSummary() {
    return summary;
  }

  public String getNotes() {
    return notes;
  }

  public String getUniqueId() {
    return uniqueId;
  }

  public int getPosition() {
    return position;
  }

  public Set<String> getProduces() {
    return produces;
  }

  public Set<String> getConsumes() {
    return consumes;
  }

  public Set<String> getProtocol() {
    return protocol;
  }

  public Map<String, List<AuthorizationScope>> getSecurityReferences() {
    return securityReferences;
  }

  public List<Parameter> getParameters() {
    return parameters;
  }

  public Set<ResponseMessage> getResponseMessages() {
    return responseMessages;
  }

  public String getDeprecated() {
    return deprecated;
  }


  public List<VendorExtension> getVendorExtensions() {
    return vendorExtensions;
  }

}
