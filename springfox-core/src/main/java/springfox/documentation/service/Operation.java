package springfox.documentation.service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import springfox.documentation.schema.ModelRef;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.*;

public class Operation {
  private final String method;
  private final String summary;
  private final String notes;
  private final ModelRef responseModel;
  private final String nickname;
  private final int position;
  private final Set<String> tags;
  private final Set<String> produces;
  private final Set<String> consumes;
  private final Set<String> protocol;
  private final boolean isHidden;
  private final Map<String, List<AuthorizationScope>> authorizations;
  private final List<Parameter> parameters;
  private final Set<ResponseMessage> responseMessages;
  private final String deprecated;

  public Operation(String method, String summary, String notes, ModelRef responseModel,
                   String nickname, int position,
                   Set<String> tags, Set<String> produces, Set<String> consumes, Set<String> protocol,
                   List<Authorization> authorizations, List<Parameter> parameters,
                   Set<ResponseMessage> responseMessages, String deprecated, boolean isHidden) {
    this.method = method;
    this.summary = summary;
    this.notes = notes;
    this.responseModel = responseModel;
    this.nickname = nickname;
    this.position = position;
    this.tags = tags;
    this.produces = produces;
    this.consumes = consumes;
    this.protocol = protocol;
    this.isHidden = isHidden;
    this.authorizations = toAuthorizationsMap(authorizations);
    this.parameters = parameters;
    this.responseMessages = responseMessages;
    this.deprecated = deprecated;
  }

  public boolean isHidden() {
    return isHidden;
  }

  public ModelRef getResponseModel() {
    return responseModel;
  }

  public Set<String> getTags() {
    return tags;
  }

  private Map<String, List<AuthorizationScope>> toAuthorizationsMap(List<Authorization> authorizations) {
    return Maps.transformEntries(Maps.uniqueIndex(authorizations, byType()), toScopes());
  }

  private EntryTransformer<? super String, ? super Authorization, List<AuthorizationScope>> toScopes() {
    return new EntryTransformer<String, Authorization, List<AuthorizationScope>>() {
      @Override
      public List<AuthorizationScope> transformEntry(String key, Authorization value) {
        return Lists.newArrayList(value.getScopes());
      }
    };
  }

  private Function<? super Authorization, String> byType() {
    return new Function<Authorization, String>() {
      @Override
      public String apply(Authorization input) {
        return input.getType();
      }
    };
  }

  public String getMethod() {
    return method;
  }

  public String getSummary() {
    return summary;
  }

  public String getNotes() {
    return notes;
  }

  public String getNickname() {
    return nickname;
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

  public Map<String, List<AuthorizationScope>> getAuthorizations() {
    return authorizations;
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
}
