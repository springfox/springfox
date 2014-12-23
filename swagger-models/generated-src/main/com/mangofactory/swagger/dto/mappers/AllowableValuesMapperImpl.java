package com.mangofactory.swagger.dto.mappers;

import com.mangofactory.service.model.AllowableListValues;
import com.mangofactory.service.model.AllowableRangeValues;
import java.util.ArrayList;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2014-12-22T22:29:37-0600"
)
@Component
public class AllowableValuesMapperImpl extends AllowableValuesMapper {

    @Override
    public com.mangofactory.swagger.dto.AllowableListValues toSwagger(AllowableListValues from)  {
        if ( from == null ) {
            return null;
        }

        com.mangofactory.swagger.dto.AllowableListValues allowableListValues = new com.mangofactory.swagger.dto.AllowableListValues();

        if ( from.getValues() != null ) {
            allowableListValues.setValues( new ArrayList<String>( from.getValues() ) );
        }
        allowableListValues.setValueType( from.getValueType() );


        return allowableListValues;
    }


    @Override
    public com.mangofactory.swagger.dto.AllowableRangeValues toSwagger(AllowableRangeValues from)  {
        if ( from == null ) {
            return null;
        }

        com.mangofactory.swagger.dto.AllowableRangeValues allowableRangeValues = new com.mangofactory.swagger.dto.AllowableRangeValues();

        allowableRangeValues.setMin( from.getMin() );
        allowableRangeValues.setMax( from.getMax() );


        return allowableRangeValues;
    }

}
