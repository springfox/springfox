package com.mangofactory.swagger.models.property;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mangofactory.swagger.models.ModelContext;
import com.mangofactory.swagger.models.ResolvedTypes;
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider;
import com.wordnik.swagger.annotations.ApiModelProperty;
import com.wordnik.swagger.model.AllowableListValues;
import com.wordnik.swagger.model.AllowableValues;
import scala.Option;
import scala.collection.JavaConversions;

import java.util.Collection;

import static com.mangofactory.swagger.models.ResolvedTypes.*;

/**
 * @author fgaule
 * @since 07/08/2014
 */
public abstract class BaseModelProperty implements ModelProperty {

  private static final String EMPTY_STRING = "";

  private final Optional<ApiModelProperty> apiModelProperty;
  private final String name;
  private final AlternateTypeProvider alternateTypeProvider;

  public BaseModelProperty(String name, AlternateTypeProvider alternateTypeProvider,
                           Optional<ApiModelProperty> apiModelProperty) {
    this.name = name;
    this.apiModelProperty = apiModelProperty;
    this.alternateTypeProvider = alternateTypeProvider;
  }

  protected abstract ResolvedType realType();

  @Override
  public ResolvedType getType() {
    return alternateTypeProvider.alternateFor(realType());
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String qualifiedTypeName() {
    if (getType().getTypeParameters().size() > 0) {
      return getType().toString();
    }
    return simpleQualifiedTypeName(getType());
  }

  @Override
  public String typeName(ModelContext modelContext) {
    return ResolvedTypes.typeName(getType());
  }

  @Override
  public AllowableValues allowableValues() {
    Optional<AllowableListValues> allowableValuesOptional = apiModelProperty.transform(new Function<ApiModelProperty,
            AllowableListValues>() {
      @Override
      public AllowableListValues apply(ApiModelProperty annotation) {
        Collection<String> split = Lists.newArrayList(annotation.allowableValues().split(","));
        Collection<String> allowableValues = EMPTY_STRING.equals(Iterables.getFirst(split, EMPTY_STRING)) ?
                Lists.<String>newArrayList() : split;
        return new AllowableListValues(JavaConversions.collectionAsScalaIterable(allowableValues).toList(), "LIST");
      }
    });

    if (allowableValuesOptional.isPresent() && allowableValuesOptional.get().values().size() > 0) {
      return allowableValuesOptional.get();
    }
    return ResolvedTypes.allowableValues(getType());
  }

  @Override
  public boolean isRequired() {
    return apiModelProperty.transform(new Function<ApiModelProperty, Boolean>() {
      @Override
      public Boolean apply(ApiModelProperty annotation) {
        return annotation.required();
      }
    }).or(false);
  }

  @Override
  public Option<String> propertyDescription() {

    Optional<String> description = getApiModelProperty().transform(new Function<ApiModelProperty, String>() {
      @Override
      public String apply(ApiModelProperty annotation) {
        String description = EMPTY_STRING;
        if (!Strings.isNullOrEmpty(annotation.value())) {
          description = annotation.value();
        } else if (!Strings.isNullOrEmpty(annotation.notes())) {
          description = annotation.notes();
        }
        return description;
      }
    });

    return Option.apply(description.isPresent() && !Strings.isNullOrEmpty(description.get()) ? description
            .get() : description.orNull());
  }

  protected Optional<ApiModelProperty> getApiModelProperty() {
    return apiModelProperty;
  }


}
