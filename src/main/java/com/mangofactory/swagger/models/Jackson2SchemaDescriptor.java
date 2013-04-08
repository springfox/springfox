package com.mangofactory.swagger.models;

import com.fasterxml.classmate.MemberResolver;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedField;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static com.mangofactory.swagger.models.ResolvedTypes.*;

@Component
public class Jackson2SchemaDescriptor implements SchemaDescriptor {
    private final ObjectMapper objectMapper;

    @Autowired
    public Jackson2SchemaDescriptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public List<ResolvedField> serializableFields(TypeResolver typeResolver, ResolvedType resolvedType) {
        List<ResolvedField> serializationCandidates = newArrayList();
        MemberResolver memberResolver = new MemberResolver(typeResolver);
        ResolvedTypeWithMembers resolvedMemberWithMembers = memberResolver.resolve(resolvedType, null, null);
        SerializationConfig serializationConfig = objectMapper.getSerializationConfig();
        BeanDescription beanDescription = serializationConfig.introspect(TypeFactory.defaultInstance()
                .constructType(resolvedType.getErasedType()));
        Map<String, BeanPropertyDefinition> propertyLookup = uniqueIndex(beanDescription.findProperties(),
                beanPropertyByName());
        for (ResolvedField childField: resolvedMemberWithMembers.getMemberFields()){
            if (propertyLookup.containsKey(childField.getName())) {
                BeanPropertyDefinition propertyDefinition = propertyLookup.get(childField.getName());
                AnnotatedMember member = propertyDefinition.getPrimaryMember();
                if (member != null
                        && member.getMember() != null
                        && Field.class.isAssignableFrom(member.getMember().getClass())) {
                    serializationCandidates.add(childField);
                }
            }
        }
        return serializationCandidates;
    }

    @Override
    public List<ResolvedField> deserializableFields(TypeResolver typeResolver, ResolvedType resolvedType) {
        List<ResolvedField> serializationCandidates = newArrayList();
        MemberResolver memberResolver = new MemberResolver(typeResolver);
        ResolvedTypeWithMembers resolvedMemberWithMembers = memberResolver.resolve(resolvedType, null, null);
        DeserializationConfig serializationConfig = objectMapper.getDeserializationConfig();
        BeanDescription beanDescription = serializationConfig.introspect(TypeFactory.defaultInstance()
                .constructType(resolvedType.getErasedType()));
        Map<String, BeanPropertyDefinition> propertyLookup = uniqueIndex(beanDescription.findProperties(),
                beanPropertyByName());
        for (ResolvedField childField: resolvedMemberWithMembers.getMemberFields()){
            if (propertyLookup.containsKey(childField.getName())) {
                BeanPropertyDefinition propertyDefinition = propertyLookup.get(childField.getName());
                AnnotatedMember member = propertyDefinition.getPrimaryMember();
                if (member.getMember() != null && Field.class.isAssignableFrom(member.getMember().getClass())) {
                    serializationCandidates.add(childField);
                }
            }
        }
        return serializationCandidates;
    }

    @Override
    public List<ResolvedProperty> serializableProperties(TypeResolver typeResolver, ResolvedType resolvedType) {
        List<ResolvedProperty> serializationCandidates = newArrayList();
        SerializationConfig serializationConfig = objectMapper.getSerializationConfig();
        BeanDescription beanDescription = serializationConfig.introspect(TypeFactory.defaultInstance()
                .constructType(resolvedType.getErasedType()));
        Map<String, BeanPropertyDefinition> propertyLookup = uniqueIndex(beanDescription.findProperties(),
                beanPropertyByName());
        for (ResolvedProperty childProperty: gettersAndSetters(typeResolver, resolvedType)) {

            if (propertyLookup.containsKey(childProperty.getName())) {
                BeanPropertyDefinition propertyDefinition = propertyLookup.get(childProperty.getName());
                AnnotatedMember member = propertyDefinition.getPrimaryMember();
                if (member != null && member.getMember() != null
                        && Method.class.isAssignableFrom(member.getMember().getClass())
                        && Objects.equal(member.getMember().getName(), childProperty.getMethodName())) {
                    serializationCandidates.add(childProperty);
                }
            }
        }
        return serializationCandidates;
    }

    @Override
    public List<ResolvedProperty> deserializableProperties(TypeResolver typeResolver, ResolvedType resolvedType) {
        List<ResolvedProperty> serializationCandidates = newArrayList();
        DeserializationConfig serializationConfig = objectMapper.getDeserializationConfig();
        BeanDescription beanDescription = serializationConfig.introspect(TypeFactory.defaultInstance()
                .constructType(resolvedType.getErasedType()));
        Map<String, BeanPropertyDefinition> propertyLookup = uniqueIndex(beanDescription.findProperties(),
                beanPropertyByName());
        for (ResolvedProperty childProperty: gettersAndSetters(typeResolver, resolvedType)) {

            if (propertyLookup.containsKey(childProperty.getName())) {
                BeanPropertyDefinition propertyDefinition = propertyLookup.get(childProperty.getName());
                AnnotatedMember member = propertyDefinition.getPrimaryMember();
                if (member != null && member.getMember() != null
                        && Method.class.isAssignableFrom(member.getMember().getClass())
                        && Objects.equal(member.getMember().getName(), childProperty.getMethodName())) {
                    serializationCandidates.add(childProperty);
                }
            }
        }
        return serializationCandidates;
    }

    private Function<BeanPropertyDefinition, String> beanPropertyByName() {
        return new Function<BeanPropertyDefinition, String>() {
            @Override
            public String apply(BeanPropertyDefinition input) {
                return input.getName();
            }
        };
    }
}
