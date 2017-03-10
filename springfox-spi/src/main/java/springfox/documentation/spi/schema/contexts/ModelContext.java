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
import com.google.common.collect.ImmutableSet;
import springfox.documentation.builders.ModelBuilder;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.AlternateTypeProvider;
import springfox.documentation.spi.schema.GenericTypeNamingStrategy;

import java.lang.reflect.Type;
import java.util.Set;

import static com.google.common.collect.Sets.*;

public class ModelContext {
  private final Type type;
  private final boolean returnType;
  private final String groupName;
  private final DocumentationType documentationType;

  private final ModelContext parentContext;
  private final Set<ResolvedType> seenTypes = newHashSet();
  private final ModelBuilder modelBuilder;
  private final AlternateTypeProvider alternateTypeProvider;
  private final GenericTypeNamingStrategy genericNamingStrategy;
  private final ImmutableSet<Class> ignorableTypes;

  ModelContext(
      String groupName,
      Type type,
      boolean returnType,
      DocumentationType documentationType,
      AlternateTypeProvider alternateTypeProvider,
      GenericTypeNamingStrategy genericNamingStrategy,
      ImmutableSet<Class> ignorableTypes) {
    this.groupName = groupName;
    this.documentationType = documentationType;
    this.alternateTypeProvider = alternateTypeProvider;
    this.genericNamingStrategy = genericNamingStrategy;
    this.ignorableTypes = ignorableTypes;
    this.parentContext = null;
    this.type = type;
    this.returnType = returnType;
    this.modelBuilder = new ModelBuilder();
  }

  ModelContext(ModelContext parentContext, ResolvedType input) {
    this.parentContext = parentContext;
    this.type = input;
    this.groupName = parentContext.groupName;
    this.returnType = parentContext.isReturnType();
    this.documentationType = parentContext.getDocumentationType();
    this.modelBuilder = new ModelBuilder();
    this.alternateTypeProvider = parentContext.alternateTypeProvider;
    this.ignorableTypes = parentContext.ignorableTypes;
    this.genericNamingStrategy = parentContext.getGenericNamingStrategy();
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
   * @return is the context for a return type
   */
  public boolean isReturnType() {
    return returnType;
  }

  /**
   * @return alternate type provider thats available to this context
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
   * @param documentationType     - for documenation type
   * @param alternateTypeProvider - alternate type provider
   * @param genericNamingStrategy - how generic types should be named
   * @param ignorableTypes        - types that can be ignored
   * @return new context
   */
  public static ModelContext inputParam(
      String group,
      Type type,
      DocumentationType documentationType,
      AlternateTypeProvider alternateTypeProvider,
      GenericTypeNamingStrategy genericNamingStrategy,
      ImmutableSet<Class> ignorableTypes) {

    return new ModelContext(
        group,
        type,
        false,
        documentationType,
        alternateTypeProvider,
        genericNamingStrategy,
        ignorableTypes);
  }

  /**
   * Convenience method to provide an new context for an return parameter
   *
   *
   * @param groupName             - group name of the docket
   * @param type                  - type
   * @param documentationType     - for documenation type
   * @param alternateTypeProvider - alternate type provider
   * @param genericNamingStrategy - how generic types should be named
   * @param ignorableTypes        - types that can be ignored
   * @return new context
   */
  public static ModelContext returnValue(
      String groupName,
      Type type,
      DocumentationType documentationType,
      AlternateTypeProvider alternateTypeProvider,
      GenericTypeNamingStrategy genericNamingStrategy,
      ImmutableSet<Class> ignorableTypes) {

    return new ModelContext(
        groupName,
        type,
        true,
        documentationType,
        alternateTypeProvider,
        genericNamingStrategy,
        ignorableTypes);
  }

  /**
   * Convenience method to provide an new context for an input parameter
   *
   * @param input - context for given input
   * @return new context based on parent context for a given input
   */
  public static ModelContext fromParent(ModelContext context, ResolvedType input) {
    return new ModelContext(context, input);
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
        documentationType,
        returnType,
        namingStrategy());
  }

  public String description() {
    return MoreObjects.toStringHelper(ModelContext.class)
        .add("groupName", this.getGroupName())
        .add("type", this.getType())
        .add("isReturnType", this.isReturnType())
        .toString();
  }

  public boolean canIgnore(ResolvedType type) {
    return ignorableTypes.contains(type.getErasedType());
  }
}
