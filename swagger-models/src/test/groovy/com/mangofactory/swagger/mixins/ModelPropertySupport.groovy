package com.mangofactory.swagger.mixins

import com.fasterxml.classmate.MemberResolver
import com.fasterxml.classmate.ResolvedTypeWithMembers
import com.fasterxml.classmate.TypeResolver
import com.fasterxml.classmate.members.ResolvedField
import com.fasterxml.classmate.members.ResolvedMethod
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition
import com.fasterxml.jackson.databind.type.TypeFactory

import static com.mangofactory.swagger.models.property.bean.Accessors.propertyName
import static com.mangofactory.swagger.models.ResolvedTypes.asResolved

class ModelPropertySupport {
  static final ObjectMapper mapper = new ObjectMapper();

  static ResolvedMethod accessorMethod(def typeToTest, String methodName) {
    TypeResolver resolver = new TypeResolver()
    MemberResolver memberResolver = new MemberResolver(resolver);
    memberResolver.setIncludeLangObject(false);

    ResolvedTypeWithMembers typeWithMembers = memberResolver.resolve(asResolved(resolver, typeToTest), null, null);
    typeWithMembers.memberMethods.find { it.name == methodName}
  }
  static ResolvedField field(def typeToTest, String fieldName) {
    TypeResolver resolver = new TypeResolver()
    MemberResolver memberResolver = new MemberResolver(resolver);
    memberResolver.setIncludeLangObject(false);

    ResolvedTypeWithMembers typeWithMembers = memberResolver.resolve(asResolved(resolver, typeToTest), null, null);
    typeWithMembers.memberFields.find { it.name == fieldName}
  }

  static BeanPropertyDefinition beanPropertyDefinition(def typeToTest, def methodName) {
    JavaType type = TypeFactory.defaultInstance().constructType(typeToTest)
    BeanDescription beanDescription = mapper.getDeserializationConfig().introspectForBuilder(type)
    Map<String, BeanPropertyDefinition> propertyDefinitionsByInternalName =
            beanDescription.findProperties()
                    .collectEntries {[ it.getInternalName(), it ]}
    return propertyDefinitionsByInternalName[propertyName(methodName)]
  }

  static BeanPropertyDefinition beanPropertyDefinitionByField(def typeToTest, def fieldName) {
    JavaType type = TypeFactory.defaultInstance().constructType(typeToTest)
    BeanDescription beanDescription = mapper.getDeserializationConfig().introspectForBuilder(type)
    Map<String, BeanPropertyDefinition> propertyDefinitionsByInternalName =
            beanDescription.findProperties()
                    .collectEntries {[ it.getInternalName(), it ]}
    return propertyDefinitionsByInternalName[fieldName]
  }
}
