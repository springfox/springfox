/*
 *
 *  Copyright 2015-2018 the original author or authors.
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
package springfox.documentation.spi.schema.contexts;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import springfox.documentation.builders.ModelBuilder;
import springfox.documentation.spi.schema.UniqueTypeNameAdjuster;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.AlternateTypeProvider;
import springfox.documentation.spi.schema.GenericTypeNamingStrategy;

import java.lang.reflect.Type;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.ImmutableSet.copyOf;

public class ModelContext {
  private final ResolvedType type;
  private final boolean returnType;
  private final String groupName;
  private final DocumentationType documentationType;

  private final Optional<ResolvedType> view;
  private final Set<ResolvedType> validationGroups;
  
  private final ModelContext parentContext;
  private final Set<ResolvedType> seenTypes = newHashSet();
  private final ModelBuilder modelBuilder;
  private final UniqueTypeNameAdjuster uniqueTypeNameAdjuster;
  private final AlternateTypeProvider alternateTypeProvider;
  private final GenericTypeNamingStrategy genericNamingStrategy;
  private final ImmutableSet<Class> ignorableTypes;

  private final boolean adjustTypeNames;

  ModelContext(
      String groupName,
      ResolvedType type,
      boolean returnType,
      Optional<ResolvedType> view,
      Set<ResolvedType> validationGroups,
      DocumentationType documentationType,
      UniqueTypeNameAdjuster uniqueTypeNameAdjuster,
      AlternateTypeProvider alternateTypeProvider,
      GenericTypeNamingStrategy genericNamingStrategy,
      ImmutableSet<Class> ignorableTypes) {
    this.groupName = groupName;
    this.documentationType = documentationType;
    this.uniqueTypeNameAdjuster = uniqueTypeNameAdjuster;
    this.alternateTypeProvider = alternateTypeProvider;
    this.genericNamingStrategy = genericNamingStrategy;
    this.ignorableTypes = ignorableTypes;
    this.parentContext = null;
    this.type = type;
    this.returnType = returnType;
    this.view = view;
    this.validationGroups = copyOf(validationGroups);
    this.modelBuilder = new ModelBuilder(String.valueOf(hashCode()));
    this.adjustTypeNames = false;
  }

  ModelContext(ModelContext parentContext, ResolvedType input) {
    this.parentContext = parentContext;
    this.type = input;
    this.groupName = parentContext.groupName;
    this.returnType = parentContext.isReturnType();
    this.view = parentContext.getView();
    this.validationGroups = parentContext.getValidationGroups();
    this.documentationType = parentContext.getDocumentationType();
    this.uniqueTypeNameAdjuster = parentContext.uniqueTypeNameAdjuster;
    this.alternateTypeProvider = parentContext.alternateTypeProvider;
    this.ignorableTypes = parentContext.ignorableTypes;
    this.genericNamingStrategy = parentContext.getGenericNamingStrategy();
    this.modelBuilder = new ModelBuilder(String.valueOf(hashCode()));
    this.adjustTypeNames = parentContext.adjustTypeNames;
  }
  
  ModelContext(ModelContext parentContext, boolean adjustTypeNames) {
    this.parentContext = parentContext;
    this.type = parentContext.type;
    this.groupName = parentContext.groupName;
    this.returnType = parentContext.isReturnType();
    this.view = parentContext.getView();
    this.validationGroups = parentContext.getValidationGroups();
    this.documentationType = parentContext.getDocumentationType();
    this.uniqueTypeNameAdjuster = parentContext.uniqueTypeNameAdjuster;
    this.alternateTypeProvider = parentContext.alternateTypeProvider;
    this.ignorableTypes = parentContext.ignorableTypes;
    this.genericNamingStrategy = parentContext.getGenericNamingStrategy();
    this.modelBuilder = new ModelBuilder(String.valueOf(hashCode()));
    this.adjustTypeNames = adjustTypeNames;
  }


  /**
   * @return type behind this context
   */
  public Type getType() {
    return type;
  }

  /**
   * @param resolver - type resolved
   * @return resolved type
   */
  public ResolvedType resolvedType(TypeResolver resolver) {
    return resolver.resolve(getType());
  }

  /**
   * @return resolved type postfix
   */
  public String typePostfix() {
    return adjustTypeNames ? uniqueTypeNameAdjuster.get(hashCode()) : "";
  }
  
  /**
   * @return is the context for a return type
   */
  public boolean isReturnType() {
    return returnType;
  }

  /**
   * @return view
   */
  public Optional<ResolvedType> getView() {
    return view;
  }
  
  /**
   * @return a set of jsr-303 validation groups
   */
  public Set<ResolvedType> getValidationGroups() {
    return validationGroups;
  }

  /**
   * @return alternate type provider that's available to this context
   */
  public AlternateTypeProvider getAlternateTypeProvider() {
    return alternateTypeProvider;
  }

  /**
   * @param resolved - type to find an alternate type for
   * @return alternate type for given resolved type
   */
  public ResolvedType alternateFor(ResolvedType resolved) {
    return alternateTypeProvider.alternateFor(resolved);
  }

  /**
   * @return group name of the docket
   */
  public String getGroupName() {
    return groupName;
  }

  /**
   * Convenience method to provide an new context for an input parameter
   *
   * @param group                 - group name of the docket
   * @param type                  - type
   * @param documentationType     - for documentation type
   * @param alternateTypeProvider - alternate type provider
   * @param genericNamingStrategy - how generic types should be named
   * @param ignorableTypes        - types that can be ignored
   * @return new context
   */
  public static ModelContext inputParam(
      String group,
      ResolvedType type,
      Optional<ResolvedType> view,
      Set<ResolvedType> validationGroups,
      DocumentationType documentationType,
      UniqueTypeNameAdjuster uniqueTypeNameAdjuster,
      AlternateTypeProvider alternateTypeProvider,
      GenericTypeNamingStrategy genericNamingStrategy,
      ImmutableSet<Class> ignorableTypes) {

    ModelContext context = new ModelContext(
        group,
        type,
        false,
        view,
        validationGroups,
        documentationType,
        uniqueTypeNameAdjuster,
        alternateTypeProvider,
        genericNamingStrategy,
        ignorableTypes);

    uniqueTypeNameAdjuster.registerType(type, context.hashCode());
    return context;
  }

  /**
   * Convenience method to provide an new context for an return parameter
   *
   *
   * @param groupName             - group name of the docket
   * @param type                  - type
   * @param documentationType     - for documentation type
   * @param alternateTypeProvider - alternate type provider
   * @param genericNamingStrategy - how generic types should be named
   * @param ignorableTypes        - types that can be ignored
   * @return new context
   */
  public static ModelContext returnValue(
      String groupName,
      ResolvedType type,
      Optional<ResolvedType> view,
      DocumentationType documentationType,
      UniqueTypeNameAdjuster uniqueTypeNameAdjuster,
      AlternateTypeProvider alternateTypeProvider,
      GenericTypeNamingStrategy genericNamingStrategy,
      ImmutableSet<Class> ignorableTypes) {

    ModelContext context = new ModelContext(
        groupName,
        type,
        true,
        view,
        Sets.<ResolvedType>newHashSet(),
        documentationType,
        uniqueTypeNameAdjuster,
        alternateTypeProvider,
        genericNamingStrategy,
        ignorableTypes);
    
    uniqueTypeNameAdjuster.registerType(type, context.hashCode());
    return context;
  }

  /**
   * Convenience method to provide an new context for an input parameter
   *
   * @param input - context for given input
   * @return new context based on parent context for a given input
   */
  public static ModelContext fromParent(ModelContext context, ResolvedType input) {
    ModelContext newContext = new ModelContext(context, input);
    context.uniqueTypeNameAdjuster.registerType(input, newContext.hashCode());

    return newContext;
  }
  
  /**
   * Convenience method to use adjusted type name
   *
   * @param input - context for given input
   * @return new context based on parent context for a given input
   */
  public static ModelContext withAdjustedTypeName(ModelContext context) {
    return new ModelContext(context, true);
  }

  /**
   * Answers the question, has the given type been processed?
   *
   * @param resolvedType - type to check
   * @return true or false
   */
  public boolean hasSeenBefore(ResolvedType resolvedType) {
    return seenTypes.contains(resolvedType)
        || seenTypes.contains(new TypeResolver().resolve(resolvedType.getErasedType()))
        || parentHasSeenBefore(resolvedType);
  }

  public DocumentationType getDocumentationType() {
    return documentationType;
  }

  /**
   * Answers the question, has the given type been processed by its parent context?
   *
   * @param resolvedType - type to check
   * @return true or false
   */
  private boolean parentHasSeenBefore(ResolvedType resolvedType) {
    if (parentContext == null) {
      return false;
    }
    return parentContext.hasSeenBefore(resolvedType);
  }

  public GenericTypeNamingStrategy getGenericNamingStrategy() {
    if (parentContext == null) {
      return genericNamingStrategy;
    }
    return parentContext.getGenericNamingStrategy();
  }

  public ModelBuilder getBuilder() {
    return modelBuilder;
  }

  public void seen(ResolvedType resolvedType) {
    seenTypes.add(resolvedType);
  }

  public void assumeEqualsTo(ModelContext other) {
    if (other.type.equals(type)) {
      uniqueTypeNameAdjuster.setEqualityFor(type, hashCode(), other.hashCode());
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ModelContext that = (ModelContext) o;

    return
        Objects.equal(groupName, that.groupName) &&
        Objects.equal(type, that.type) &&
        Objects.equal(view, that.view) &&
        Objects.equal(validationGroups, that.validationGroups) &&
        Objects.equal(documentationType, that.documentationType) &&
        Objects.equal(returnType, that.returnType) &&
        Objects.equal(namingStrategy(), that.namingStrategy());

  }

  private String namingStrategy() {
    if (genericNamingStrategy != null) {
      return genericNamingStrategy.getClass().getName();
    }
    return "";
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(
        groupName,
        type,
        view,
        validationGroups,
        documentationType,
        returnType,
        namingStrategy());
  }

  public String description() {
    return MoreObjects.toStringHelper(ModelContext.class)
        .add("groupName", this.getGroupName())
        .add("type", this.getType())
        .add("isReturnType", this.isReturnType())
        .add("view", this.getView())
        .toString();
  }

  public boolean canIgnore(ResolvedType type) {
    return ignorableTypes.contains(type.getErasedType());
  }
}
