package com.mangofactory.swagger.models.property.bean;

import static com.mangofactory.swagger.models.property.BeanPropertyDefinitions.name;
import static com.mangofactory.swagger.models.property.bean.Accessors.isGetter;
import static com.mangofactory.swagger.models.property.bean.Accessors.propertyName;
import static com.mangofactory.swagger.models.property.bean.BeanModelProperty.accessorMemberIs;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.google.common.base.Optional;
import com.mangofactory.swagger.models.BeanPropertyNamingStrategy;
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider;
import com.mangofactory.swagger.models.property.ModelProperty;
import com.mangofactory.swagger.models.property.provider.AbstractModelPropertyProvider;

@Component
public class BeanModelPropertyProvider extends AbstractModelPropertyProvider<ResolvedMethod> {

  private final TypeResolver typeResolver;

  @Autowired
  public BeanModelPropertyProvider(AccessorsProvider accessors, TypeResolver typeResolver,
      AlternateTypeProvider alternateTypeProvider, BeanPropertyNamingStrategy namingStrategy) {

    super(accessors, alternateTypeProvider, namingStrategy);
    this.typeResolver = typeResolver;
  }

  @Override
  protected String getPropertyDefinitionKey(ResolvedMethod resolvedMethod) {
    return propertyName(resolvedMethod.getName());
  }

  @Override
  protected boolean isApplicable(ResolvedMethod resolvedMethod, AnnotatedMember annotatedMember) {
    return accessorMemberIs(resolvedMethod, methodName(annotatedMember));
  }

  @Override
  protected void addModelProperty(List<ModelProperty> candidates, ResolvedMethod resolvedMethod,
      Optional<BeanPropertyDefinition> jacksonProperty, boolean forSerialization) {
    candidates.add(beanModelProperty(resolvedMethod, jacksonProperty, forSerialization));
  }
  
  @Override
  protected ResolvedType getUnwrappedTypeForDeserialization(ResolvedMethod resolvedMethod) {
    return resolvedMethod.getArgumentType(0);
  }

  private String methodName(AnnotatedMember member) {
    if (member == null || member.getMember() == null) {
      return "";
    }
    return member.getMember().getName();
  }

  private BeanModelProperty beanModelProperty(ResolvedMethod childProperty,
      Optional<BeanPropertyDefinition> jacksonProperty, boolean forSerialization) {

    BeanPropertyDefinition beanPropertyDefinition = jacksonProperty.get();
    String propertyName = name(beanPropertyDefinition, forSerialization, namingStrategy);
    return new BeanModelProperty(propertyName, beanPropertyDefinition, childProperty,
        isGetter(childProperty.getRawMember()), typeResolver, alternateTypeProvider);
  }
}
