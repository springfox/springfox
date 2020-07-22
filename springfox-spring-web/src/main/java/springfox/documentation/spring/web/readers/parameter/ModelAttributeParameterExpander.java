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

package springfox.documentation.spring.web.readers.parameter;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.members.ResolvedField;
import com.fasterxml.classmate.members.ResolvedMember;
import com.fasterxml.classmate.members.ResolvedMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.common.Compatibility;
import springfox.documentation.schema.Maps;
import springfox.documentation.schema.ScalarTypes;
import springfox.documentation.schema.property.bean.AccessorsProvider;
import springfox.documentation.schema.property.field.FieldProvider;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.spi.schema.AlternateTypeProvider;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.service.contexts.ParameterExpansionContext;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Collections.*;
import static java.util.Optional.*;
import static java.util.function.Function.*;
import static java.util.stream.Collectors.*;
import static org.springframework.util.StringUtils.*;
import static springfox.documentation.schema.Collections.*;
import static springfox.documentation.schema.ResolvedTypes.*;
import static springfox.documentation.spring.web.readers.parameter.ParameterTypeDeterminer.*;

@Component
@SuppressWarnings("deprecation")
public class ModelAttributeParameterExpander {
  private static final Logger LOG = LoggerFactory.getLogger(ModelAttributeParameterExpander.class);
  private final FieldProvider fields;
  private final AccessorsProvider accessors;
  private final EnumTypeDeterminer enumTypeDeterminer;

  @Autowired
  private DocumentationPluginsManager pluginsManager;

  @Autowired
  public ModelAttributeParameterExpander(
      FieldProvider fields,
      AccessorsProvider accessors,
      EnumTypeDeterminer enumTypeDeterminer) {
    this.fields = fields;
    this.accessors = accessors;
    this.enumTypeDeterminer = enumTypeDeterminer;
  }

  public List<Compatibility<springfox.documentation.service.Parameter, RequestParameter>> expand(
      ExpansionContext context) {

    List<Compatibility<springfox.documentation.service.Parameter, RequestParameter>> parameters = new ArrayList<>();
    Set<PropertyDescriptor> propertyDescriptors = propertyDescriptors(context.getParamType().getErasedType());
    Map<Method, PropertyDescriptor> propertyLookupByGetter
        = propertyDescriptorsByMethod(context.getParamType().getErasedType(), propertyDescriptors);
    Iterable<ResolvedMethod> getters = accessors.in(context.getParamType()).stream()
        .filter(onlyValidGetters(propertyLookupByGetter.keySet())).collect(toList());

    Map<String, ResolvedField> fieldsByName =
        StreamSupport.stream(this.fields.in(context.getParamType()).spliterator(), false)
            .collect(toMap((ResolvedMember::getName), identity()));


    LOG.debug("Expanding parameter type: {}", context.getParamType());
    AlternateTypeProvider alternateTypeProvider = context.getAlternateTypeProvider();
    List<ModelAttributeField> attributes =
        allModelAttributes(
            propertyLookupByGetter,
            getters,
            fieldsByName,
            alternateTypeProvider,
            context.ignorableTypes());

    attributes.stream()
        .filter(simpleType().negate())
        .filter(recursiveType(context).negate())
        .forEach(each -> {
          LOG.debug("Attempting to expand expandable property: {}", each.getName());
          parameters.addAll(
              expand(
                  context.childContext(
                      nestedParentName(context.getParentName(), each),
                      each.getFieldType(),
                      context.getOperationContext())));
        });

    Stream<ModelAttributeField> collectionTypes = attributes.stream()
        .filter(isCollection().and(recursiveCollectionItemType(context.getParamType()).negate()));
    collectionTypes.forEachOrdered(each -> {
      LOG.debug("Attempting to expand collection/array field: {}", each.getName());

      ResolvedType itemType = collectionElementType(each.getFieldType());
      if (itemType == null) {
        return;
      }
      if (ScalarTypes.builtInScalarType(itemType).isPresent()
          || enumTypeDeterminer.isEnum(itemType.getErasedType())) {
        parameters.add(simpleFields(context.getParentName(), context, each));
      } else {
        ExpansionContext childContext = context.childContext(
            nestedParentName(context.getParentName(), each),
            itemType,
            context.getOperationContext());
        if (!context.hasSeenType(itemType)) {
          parameters.addAll(expand(childContext));
        }
      }
    });

    Stream<ModelAttributeField> simpleFields = attributes.stream().filter(simpleType());
    simpleFields.forEach(each -> parameters.add(simpleFields(context.getParentName(), context, each)));
    return parameters.stream()
        .filter(hiddenParameter().negate())
        .filter(voidParameters().negate())
        .collect(toList());
  }

  private Predicate<Compatibility<springfox.documentation.service.Parameter, RequestParameter>> hiddenParameter() {
    return c -> c.getLegacy()
        .map(Parameter::isHidden)
        .orElse(false);
  }

  @SuppressWarnings({"rawtypes", "java:S3740"})
  private List<ModelAttributeField> allModelAttributes(
      Map<Method, PropertyDescriptor> propertyLookupByGetter,
      Iterable<ResolvedMethod> getters,
      Map<String, ResolvedField> fieldsByName,
      AlternateTypeProvider alternateTypeProvider,
      Collection<Class> ignorables) {

    Stream<ModelAttributeField> modelAttributesFromGetters =
        StreamSupport.stream(getters.spliterator(), false)
            .filter(method -> !ignored(alternateTypeProvider, method, ignorables))
            .map(toModelAttributeField(fieldsByName, propertyLookupByGetter, alternateTypeProvider));

    Stream<ModelAttributeField> modelAttributesFromFields =
        fieldsByName.values().stream()
            .filter(ResolvedMember::isPublic)
            .filter(ResolvedMember::isPublic)
            .map(toModelAttributeField(alternateTypeProvider));

    return Stream.concat(
        modelAttributesFromFields,
        modelAttributesFromGetters)
        .collect(toList());
  }

  @SuppressWarnings({"java:S2175", "java:S3740", "rawtypes", "SuspiciousMethodCalls"})
  private boolean ignored(
      AlternateTypeProvider alternateTypeProvider,
      ResolvedMethod method,
      Collection<Class> ignorables) {
    boolean annotatedIgnorable = ignorables.stream()
        .filter(Annotation.class::isAssignableFrom)
        .anyMatch(annotation -> method.getAnnotations().asList().contains(annotation));
    return annotatedIgnorable
        || ignorables.contains(fieldType(alternateTypeProvider, method).getErasedType());
  }

  private Function<ResolvedField, ModelAttributeField> toModelAttributeField(
      final AlternateTypeProvider alternateTypeProvider) {

    return input -> new ModelAttributeField(
        alternateTypeProvider.alternateFor(input.getType()),
        input.getName(),
        input,
        input);
  }

  private Predicate<Compatibility<springfox.documentation.service.Parameter, RequestParameter>> voidParameters() {
    return input -> isVoid(input.getLegacy()
        .flatMap(Parameter::getType)
        .orElse(null));
  }

  private Predicate<ModelAttributeField> recursiveCollectionItemType(final ResolvedType paramType) {
    return input -> Objects.equals(collectionElementType(input.getFieldType()), paramType);
  }

  private Compatibility<springfox.documentation.service.Parameter, RequestParameter> simpleFields(
      String parentName,
      ExpansionContext context,
      ModelAttributeField each) {
    LOG.debug("Attempting to expand field: {}", each);
    String dataTypeName =
        ofNullable(springfox.documentation.schema.Types.typeNameFor(each.getFieldType().getErasedType()))
            .orElse(each.getFieldType().getErasedType().getSimpleName());
    LOG.debug("Building parameter for field: {}, with type: {}", each, each.getFieldType());
    ParameterExpansionContext parameterExpansionContext = new ParameterExpansionContext(
        dataTypeName,
        parentName,
        determineScalarParameterType(
            context.getOperationContext().consumes(),
            context.getOperationContext().httpMethod()),
        new ModelAttributeParameterMetadataAccessor(
            each.annotatedElements(),
            each.getFieldType(),
            each.getName()),
        context.getDocumentationType(),
        new springfox.documentation.builders.ParameterBuilder(),
        new RequestParameterBuilder());
    return pluginsManager.expandParameter(parameterExpansionContext);
  }

  private Predicate<ModelAttributeField> recursiveType(final ExpansionContext context) {
    return input -> context.hasSeenType(input.getFieldType());
  }

  private Predicate<ModelAttributeField> simpleType() {
    return isCollection().negate().and(isMap().negate())
        .and(
            belongsToJavaPackage()
                .or(isBaseType())
                .or(isEnum()));
  }

  private Predicate<ModelAttributeField> isCollection() {
    return input -> isContainerType(input.getFieldType());
  }

  private Predicate<ModelAttributeField> isMap() {
    return input -> Maps.isMapType(input.getFieldType());
  }

  private Predicate<ModelAttributeField> isEnum() {
    return input -> enumTypeDeterminer.isEnum(input.getFieldType().getErasedType());
  }

  private Predicate<ModelAttributeField> belongsToJavaPackage() {
    return input -> ClassUtils.getPackageName(input.getFieldType().getErasedType()).startsWith("java.lang");
  }

  private Predicate<ModelAttributeField> isBaseType() {
    return input -> ScalarTypes.builtInScalarType(input.getFieldType()).isPresent()
        || input.getFieldType().isPrimitive();
  }

  private Function<ResolvedMethod, ModelAttributeField> toModelAttributeField(
      Map<String, ResolvedField> fieldsByName,
      Map<Method, PropertyDescriptor> propertyLookupByGetter,
      AlternateTypeProvider alternateTypeProvider) {
    return input -> {
      String name = propertyLookupByGetter.get(input.getRawMember()).getName();
      return new ModelAttributeField(
          fieldType(alternateTypeProvider, input),
          name,
          input,
          fieldsByName.get(name));
    };
  }

  private Predicate<ResolvedMethod> onlyValidGetters(final Set<Method> methods) {
    return input -> methods.contains(input.getRawMember());
  }

  private String nestedParentName(
      String parentName,
      ModelAttributeField attribute) {
    String name = attribute.getName();
    ResolvedType fieldType = attribute.getFieldType();
    if (isContainerType(fieldType) &&
        !ScalarTypes.builtInScalarType(collectionElementType(fieldType)).isPresent()) {
      name += "[0]";
    }

    if (isEmpty(parentName)) {
      return name;
    }
    return String.format("%s.%s", parentName, name);
  }

  private ResolvedType fieldType(
      AlternateTypeProvider alternateTypeProvider,
      ResolvedMethod method) {
    return alternateTypeProvider.alternateFor(method.getType());
  }

  private Set<PropertyDescriptor> propertyDescriptors(final Class<?> clazz) {
    try {
      return new HashSet<>(Arrays.asList(getBeanInfo(clazz).getPropertyDescriptors()));
    } catch (IntrospectionException e) {
      LOG.warn(String.format("Failed to get bean properties on (%s)", clazz), e);
    }
    return emptySet();
  }

  private Map<Method, PropertyDescriptor> propertyDescriptorsByMethod(
      final Class<?> clazz,
      Set<PropertyDescriptor> propertyDescriptors) {
    return propertyDescriptors.stream()
        .filter(input -> input.getReadMethod() != null
            && !clazz.isAssignableFrom(Collection.class)
            && !"isEmpty".equals(input.getReadMethod().getName()))
        .collect(toMap(PropertyDescriptor::getReadMethod, identity()));

  }

  BeanInfo getBeanInfo(Class<?> clazz) throws IntrospectionException {
    return Introspector.getBeanInfo(clazz);
  }

  public DocumentationPluginsManager getPluginsManager() {
    return pluginsManager;
  }

  void setPluginsManager(DocumentationPluginsManager pluginsManager) {
    this.pluginsManager = pluginsManager;
  }
}
