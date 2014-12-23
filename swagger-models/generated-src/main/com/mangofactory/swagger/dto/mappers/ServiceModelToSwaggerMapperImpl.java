package com.mangofactory.swagger.dto.mappers;

import com.mangofactory.service.model.ApiDescription;
import com.mangofactory.service.model.ApiInfo;
import com.mangofactory.service.model.ApiListing;
import com.mangofactory.service.model.ApiListingReference;
import com.mangofactory.service.model.Authorization;
import com.mangofactory.service.model.AuthorizationScope;
import com.mangofactory.service.model.Model;
import com.mangofactory.service.model.ModelProperty;
import com.mangofactory.service.model.Operation;
import com.mangofactory.service.model.Parameter;
import com.mangofactory.service.model.ResourceListing;
import com.mangofactory.service.model.ResponseMessage;
import com.mangofactory.swagger.dto.ModelDto;
import com.mangofactory.swagger.dto.ModelPropertyDto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2014-12-22T22:29:37-0600"
)
@Component
public class ServiceModelToSwaggerMapperImpl implements ServiceModelToSwaggerMapper {

    @Autowired
    private DataTypeMapper dataTypeMapper;

    @Autowired
    private AuthorizationTypesMapper authorizationTypesMapper;

    @Autowired
    private AllowableValuesMapper allowableValuesMapper;

    @Override
    public com.mangofactory.swagger.dto.ApiDescription toSwagger(ApiDescription from)  {
        if ( from == null ) {
            return null;
        }

        com.mangofactory.swagger.dto.ApiDescription apiDescription = new com.mangofactory.swagger.dto.ApiDescription();

        apiDescription.setHidden( from.isHidden() );
        apiDescription.setDescription( from.getDescription() );
        apiDescription.setPath( from.getPath() );
        apiDescription.setOperations( operationListToOperationList( from.getOperations() ) );


        return apiDescription;
    }


    @Override
    public com.mangofactory.swagger.dto.ApiInfo toSwagger(ApiInfo from)  {
        if ( from == null ) {
            return null;
        }

        com.mangofactory.swagger.dto.ApiInfo apiInfo = new com.mangofactory.swagger.dto.ApiInfo();

        apiInfo.setTermsOfServiceUrl( from.getTermsOfServiceUrl() );
        apiInfo.setTitle( from.getTitle() );
        apiInfo.setDescription( from.getDescription() );
        apiInfo.setLicense( from.getLicense() );
        apiInfo.setContact( from.getContact() );
        apiInfo.setLicenseUrl( from.getLicenseUrl() );


        return apiInfo;
    }


    @Override
    public com.mangofactory.swagger.dto.ApiListing toSwagger(ApiListing from)  {
        if ( from == null ) {
            return null;
        }

        com.mangofactory.swagger.dto.ApiListing apiListing = new com.mangofactory.swagger.dto.ApiListing();

        apiListing.setPosition( from.getPosition() );
        apiListing.setModels( stringModelMapToStringModelDtoMap( from.getModels() ) );
        apiListing.setBasePath( from.getBasePath() );
        apiListing.setApiVersion( from.getApiVersion() );
        apiListing.setResourcePath( from.getResourcePath() );
        apiListing.setSwaggerVersion( from.getSwaggerVersion() );
        apiListing.setAuthorizations( authorizationListToAuthorizationList( from.getAuthorizations() ) );
        if ( from.getProtocol() != null ) {
            apiListing.setProtocol( new ArrayList<String>( from.getProtocol() ) );
        }
        apiListing.setApis( apiDescriptionListToApiDescriptionList( from.getApis() ) );
        apiListing.setDescription( from.getDescription() );
        if ( from.getProduces() != null ) {
            apiListing.setProduces( new ArrayList<String>( from.getProduces() ) );
        }
        if ( from.getConsumes() != null ) {
            apiListing.setConsumes( new ArrayList<String>( from.getConsumes() ) );
        }


        return apiListing;
    }


    @Override
    public com.mangofactory.swagger.dto.ApiListingReference toSwagger(ApiListingReference from)  {
        if ( from == null ) {
            return null;
        }

        com.mangofactory.swagger.dto.ApiListingReference apiListingReference = new com.mangofactory.swagger.dto.ApiListingReference();

        apiListingReference.setPosition( from.getPosition() );
        apiListingReference.setDescription( from.getDescription() );
        apiListingReference.setPath( from.getPath() );


        return apiListingReference;
    }


    @Override
    public ModelDto toSwagger(Model from)  {
        if ( from == null ) {
            return null;
        }

        ModelDto modelDto = new ModelDto();

        modelDto.setId( from.getId() );
        modelDto.setBaseModel( from.getBaseModel() );
        modelDto.setDescription( from.getDescription() );
        modelDto.setQualifiedType( from.getQualifiedType() );
        modelDto.setName( from.getName() );
        modelDto.setDiscriminator( from.getDiscriminator() );
        modelDto.setProperties( stringModelPropertyMapToStringModelPropertyDtoMap( from.getProperties() ) );
        if ( from.getSubTypes() != null ) {
            modelDto.setSubTypes( new ArrayList<String>( from.getSubTypes() ) );
        }


        return modelDto;
    }


    @Override
    public ModelPropertyDto toSwagger(ModelProperty from)  {
        if ( from == null ) {
            return null;
        }

        ModelPropertyDto modelPropertyDto = new ModelPropertyDto();

        modelPropertyDto.setPosition( from.getPosition() );
        modelPropertyDto.setItems( dataTypeMapper.toSwagger( from.getItems() ) );
        modelPropertyDto.setDescription( from.getDescription() );
        modelPropertyDto.setQualifiedType( from.getQualifiedType() );
        modelPropertyDto.setAllowableValues( allowableValuesMapper.concreteToSwagger( from.getAllowableValues() ) );
        modelPropertyDto.setRequired( from.isRequired() );
        modelPropertyDto.setType( dataTypeMapper.concreteToSwagger( from.getType() ) );


        return modelPropertyDto;
    }


    @Override
    public com.mangofactory.swagger.dto.Operation toSwagger(Operation from)  {
        if ( from == null ) {
            return null;
        }

        com.mangofactory.swagger.dto.Operation operation = new com.mangofactory.swagger.dto.Operation();

        operation.setSummary( from.getSummary() );
        operation.setPosition( from.getPosition() );
        operation.setDeprecated( from.getDeprecated() );
        if ( from.getProtocol() != null ) {
            operation.setProtocol( new ArrayList<String>( from.getProtocol() ) );
        }
        operation.setAuthorizations( stringListMapToStringListMap( from.getAuthorizations() ) );
        operation.setNickname( from.getNickname() );
        if ( from.getConsumes() != null ) {
            operation.setConsumes( new ArrayList<String>( from.getConsumes() ) );
        }
        operation.setDataType( dataTypeMapper.concreteToSwagger( from.getDataType() ) );
        operation.setResponseClass( from.getResponseClass() );
        operation.setResponseMessages( responseMessageSetToResponseMessageSet( from.getResponseMessages() ) );
        if ( from.getProduces() != null ) {
            operation.setProduces( new ArrayList<String>( from.getProduces() ) );
        }
        operation.setMethod( from.getMethod() );
        operation.setParameters( parameterListToParameterList( from.getParameters() ) );
        operation.setNotes( from.getNotes() );


        return operation;
    }


    @Override
    public com.mangofactory.swagger.dto.Parameter toSwagger(Parameter from)  {
        if ( from == null ) {
            return null;
        }

        com.mangofactory.swagger.dto.Parameter parameter = new com.mangofactory.swagger.dto.Parameter();

        parameter.setDescription( from.getDescription() );
        parameter.setParamAccess( from.getParamAccess() );
        parameter.setName( from.getName() );
        parameter.setAllowMultiple( from.isAllowMultiple() );
        parameter.setAllowableValues( allowableValuesMapper.concreteToSwagger( from.getAllowableValues() ) );
        parameter.setParameterType( dataTypeMapper.concreteToSwagger( from.getParameterType() ) );
        parameter.setRequired( from.isRequired() );
        parameter.setDefaultValue( from.getDefaultValue() );
        parameter.setParamType( from.getParamType() );


        return parameter;
    }


    @Override
    public com.mangofactory.swagger.dto.ResourceListing toSwagger(ResourceListing from)  {
        if ( from == null ) {
            return null;
        }

        com.mangofactory.swagger.dto.ResourceListing resourceListing = new com.mangofactory.swagger.dto.ResourceListing();

        resourceListing.setApiVersion( from.getApiVersion() );
        resourceListing.setSwaggerVersion( from.getSwaggerVersion() );
        resourceListing.setAuthorizations( authorizationTypesMapper.toSwagger( from.getAuthorizations() ) );
        resourceListing.setApis( apiListingReferenceListToApiListingReferenceList( from.getApis() ) );
        resourceListing.setInfo( toSwagger( from.getInfo() ) );


        return resourceListing;
    }


    @Override
    public com.mangofactory.swagger.dto.ResponseMessage toSwagger(ResponseMessage from)  {
        if ( from == null ) {
            return null;
        }

        com.mangofactory.swagger.dto.ResponseMessage responseMessage = new com.mangofactory.swagger.dto.ResponseMessage();

        responseMessage.setMessage( from.getMessage() );
        responseMessage.setResponseModel( from.getResponseModel() );
        responseMessage.setCode( from.getCode() );


        return responseMessage;
    }



    protected List<com.mangofactory.swagger.dto.Operation> operationListToOperationList(List<Operation> list)  {
        if ( list == null ) {
            return null;
        }

        List<com.mangofactory.swagger.dto.Operation> list_ = new ArrayList<com.mangofactory.swagger.dto.Operation>();

        for ( Operation operation : list ) {
            list_.add( toSwagger( operation ) );
        }

        return list_;
    }



    protected Map<String, ModelDto> stringModelMapToStringModelDtoMap(Map<String, Model> map)  {
        if ( map == null ) {
            return null;
        }

        Map<String, ModelDto> map_ = new HashMap<String, ModelDto>();

        for ( java.util.Map.Entry<String, Model> entry : map.entrySet() ) {
            String key = entry.getKey();
            ModelDto value = toSwagger( entry.getValue() );
            map_.put( key, value );
        }

        return map_;
    }



    protected List<com.mangofactory.swagger.dto.Authorization> authorizationListToAuthorizationList(List<Authorization> list)  {
        if ( list == null ) {
            return null;
        }

        List<com.mangofactory.swagger.dto.Authorization> list_ = new ArrayList<com.mangofactory.swagger.dto.Authorization>();

        for ( Authorization authorization : list ) {
            list_.add( authorizationTypesMapper.toSwagger( authorization ) );
        }

        return list_;
    }



    protected List<com.mangofactory.swagger.dto.ApiDescription> apiDescriptionListToApiDescriptionList(List<ApiDescription> list)  {
        if ( list == null ) {
            return null;
        }

        List<com.mangofactory.swagger.dto.ApiDescription> list_ = new ArrayList<com.mangofactory.swagger.dto.ApiDescription>();

        for ( ApiDescription apiDescription : list ) {
            list_.add( toSwagger( apiDescription ) );
        }

        return list_;
    }



    protected Map<String, ModelPropertyDto> stringModelPropertyMapToStringModelPropertyDtoMap(Map<String, ModelProperty> map)  {
        if ( map == null ) {
            return null;
        }

        Map<String, ModelPropertyDto> map_ = new HashMap<String, ModelPropertyDto>();

        for ( java.util.Map.Entry<String, ModelProperty> entry : map.entrySet() ) {
            String key = entry.getKey();
            ModelPropertyDto value = toSwagger( entry.getValue() );
            map_.put( key, value );
        }

        return map_;
    }



    protected Map<String, List<com.mangofactory.swagger.dto.AuthorizationScope>> stringListMapToStringListMap(Map<String, List<AuthorizationScope>> map)  {
        if ( map == null ) {
            return null;
        }

        Map<String, List<com.mangofactory.swagger.dto.AuthorizationScope>> map_ = new HashMap<String, List<com.mangofactory.swagger.dto.AuthorizationScope>>();

        for ( java.util.Map.Entry<String, List<AuthorizationScope>> entry : map.entrySet() ) {
            String key = entry.getKey();
            List value = authorizationTypesMapper.authorizationScopesToSwagger( entry.getValue() );
            map_.put( key, value );
        }

        return map_;
    }



    protected Set<com.mangofactory.swagger.dto.ResponseMessage> responseMessageSetToResponseMessageSet(Set<ResponseMessage> set)  {
        if ( set == null ) {
            return null;
        }

        Set<com.mangofactory.swagger.dto.ResponseMessage> set_ = new HashSet<com.mangofactory.swagger.dto.ResponseMessage>();

        for ( ResponseMessage responseMessage : set ) {
            set_.add( toSwagger( responseMessage ) );
        }

        return set_;
    }



    protected List<com.mangofactory.swagger.dto.Parameter> parameterListToParameterList(List<Parameter> list)  {
        if ( list == null ) {
            return null;
        }

        List<com.mangofactory.swagger.dto.Parameter> list_ = new ArrayList<com.mangofactory.swagger.dto.Parameter>();

        for ( Parameter parameter : list ) {
            list_.add( toSwagger( parameter ) );
        }

        return list_;
    }



    protected List<com.mangofactory.swagger.dto.ApiListingReference> apiListingReferenceListToApiListingReferenceList(List<ApiListingReference> list)  {
        if ( list == null ) {
            return null;
        }

        List<com.mangofactory.swagger.dto.ApiListingReference> list_ = new ArrayList<com.mangofactory.swagger.dto.ApiListingReference>();

        for ( ApiListingReference apiListingReference : list ) {
            list_.add( toSwagger( apiListingReference ) );
        }

        return list_;
    }

}
