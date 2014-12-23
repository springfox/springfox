package com.mangofactory.swagger.dto.mappers;

import com.mangofactory.service.model.ContainerDataType;
import com.mangofactory.service.model.ModelRef;
import com.mangofactory.service.model.PrimitiveDataType;
import com.mangofactory.service.model.PrimitiveFormatDataType;
import com.mangofactory.service.model.ReferenceDataType;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2014-12-22T22:29:37-0600"
)
@Component
public class DataTypeMapperImpl extends DataTypeMapper {

    @Override
    public com.mangofactory.swagger.dto.PrimitiveDataType toSwagger(PrimitiveDataType from)  {
        if ( from == null ) {
            return null;
        }

        com.mangofactory.swagger.dto.PrimitiveDataType primitiveDataType = new com.mangofactory.swagger.dto.PrimitiveDataType();

        primitiveDataType.setType( from.getType() );


        return primitiveDataType;
    }


    @Override
    public com.mangofactory.swagger.dto.PrimitiveFormatDataType toSwagger(PrimitiveFormatDataType from)  {
        if ( from == null ) {
            return null;
        }

        com.mangofactory.swagger.dto.PrimitiveFormatDataType primitiveFormatDataType = new com.mangofactory.swagger.dto.PrimitiveFormatDataType();

        primitiveFormatDataType.setFormat( from.getFormat() );
        primitiveFormatDataType.setType( from.getType() );


        return primitiveFormatDataType;
    }


    @Override
    public com.mangofactory.swagger.dto.ReferenceDataType toSwagger(ReferenceDataType from)  {
        if ( from == null ) {
            return null;
        }

        com.mangofactory.swagger.dto.ReferenceDataType referenceDataType = new com.mangofactory.swagger.dto.ReferenceDataType();

        referenceDataType.setReference( from.getReference() );


        return referenceDataType;
    }


    @Override
    public com.mangofactory.swagger.dto.ModelRef toSwagger(ModelRef from)  {
        if ( from == null ) {
            return null;
        }

        com.mangofactory.swagger.dto.ModelRef modelRef = new com.mangofactory.swagger.dto.ModelRef();

        modelRef.setType( concreteToSwagger( from.getType() ) );


        return modelRef;
    }


    @Override
    public com.mangofactory.swagger.dto.ContainerDataType toSwagger(ContainerDataType from)  {
        if ( from == null ) {
            return null;
        }

        com.mangofactory.swagger.dto.ContainerDataType containerDataType = new com.mangofactory.swagger.dto.ContainerDataType();

        containerDataType.setItems( concreteToSwagger( from.getItems() ) );
        containerDataType.setUniqueItems( from.isUniqueItems() );
        containerDataType.setType( from.getType() );


        return containerDataType;
    }

}
