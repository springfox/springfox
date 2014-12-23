package com.mangofactory.swagger.dto.mappers;

import com.mangofactory.service.model.ApiKey;
import com.mangofactory.service.model.Authorization;
import com.mangofactory.service.model.AuthorizationCodeGrant;
import com.mangofactory.service.model.AuthorizationScope;
import com.mangofactory.service.model.AuthorizationType;
import com.mangofactory.service.model.BasicAuth;
import com.mangofactory.service.model.GrantType;
import com.mangofactory.service.model.ImplicitGrant;
import com.mangofactory.service.model.LoginEndpoint;
import com.mangofactory.service.model.OAuth;
import com.mangofactory.service.model.TokenEndpoint;
import com.mangofactory.service.model.TokenRequestEndpoint;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2014-12-22T22:29:37-0600"
)
@Component
public class AuthorizationTypesMapperImpl extends AuthorizationTypesMapper {

    @Override
    public com.mangofactory.swagger.dto.OAuth toSwagger(OAuth from)  {
        if ( from == null ) {
            return null;
        }

        com.mangofactory.swagger.dto.OAuth oAuth = new com.mangofactory.swagger.dto.OAuth();

        oAuth.setScopes( authorizationScopesToSwagger( from.getScopes() ) );
        oAuth.setGrantTypes( grantTypeListToGrantTypeList( from.getGrantTypes() ) );


        return oAuth;
    }


    @Override
    public com.mangofactory.swagger.dto.BasicAuth toSwagger(BasicAuth from)  {
        if ( from == null ) {
            return null;
        }

        com.mangofactory.swagger.dto.BasicAuth basicAuth = new com.mangofactory.swagger.dto.BasicAuth();



        return basicAuth;
    }


    @Override
    public com.mangofactory.swagger.dto.ApiKey toSwagger(ApiKey from)  {
        if ( from == null ) {
            return null;
        }

        com.mangofactory.swagger.dto.ApiKey apiKey = new com.mangofactory.swagger.dto.ApiKey();

        apiKey.setPassAs( from.getPassAs() );
        apiKey.setKeyname( from.getKeyname() );


        return apiKey;
    }


    @Override
    public com.mangofactory.swagger.dto.ImplicitGrant toSwagger(ImplicitGrant from)  {
        if ( from == null ) {
            return null;
        }

        com.mangofactory.swagger.dto.ImplicitGrant implicitGrant = new com.mangofactory.swagger.dto.ImplicitGrant();

        implicitGrant.setTokenName( from.getTokenName() );
        implicitGrant.setLoginEndpoint( toSwagger( from.getLoginEndpoint() ) );
        implicitGrant.setType( from.getType() );


        return implicitGrant;
    }


    @Override
    public com.mangofactory.swagger.dto.AuthorizationCodeGrant toSwagger(AuthorizationCodeGrant from)  {
        if ( from == null ) {
            return null;
        }

        com.mangofactory.swagger.dto.AuthorizationCodeGrant authorizationCodeGrant = new com.mangofactory.swagger.dto.AuthorizationCodeGrant();

        authorizationCodeGrant.setTokenRequestEndpoint( toSwagger( from.getTokenRequestEndpoint() ) );
        authorizationCodeGrant.setType( from.getType() );
        authorizationCodeGrant.setTokenEndpoint( toSwagger( from.getTokenEndpoint() ) );


        return authorizationCodeGrant;
    }


    @Override
    public com.mangofactory.swagger.dto.TokenEndpoint toSwagger(TokenEndpoint from)  {
        if ( from == null ) {
            return null;
        }

        com.mangofactory.swagger.dto.TokenEndpoint tokenEndpoint = new com.mangofactory.swagger.dto.TokenEndpoint();

        tokenEndpoint.setTokenName( from.getTokenName() );
        tokenEndpoint.setUrl( from.getUrl() );


        return tokenEndpoint;
    }


    @Override
    public com.mangofactory.swagger.dto.TokenRequestEndpoint toSwagger(TokenRequestEndpoint from)  {
        if ( from == null ) {
            return null;
        }

        com.mangofactory.swagger.dto.TokenRequestEndpoint tokenRequestEndpoint = new com.mangofactory.swagger.dto.TokenRequestEndpoint();

        tokenRequestEndpoint.setClientSecretName( from.getClientSecretName() );
        tokenRequestEndpoint.setClientIdName( from.getClientIdName() );
        tokenRequestEndpoint.setUrl( from.getUrl() );


        return tokenRequestEndpoint;
    }


    @Override
    public com.mangofactory.swagger.dto.AuthorizationScope toSwagger(AuthorizationScope from)  {
        if ( from == null ) {
            return null;
        }

        com.mangofactory.swagger.dto.AuthorizationScope authorizationScope = new com.mangofactory.swagger.dto.AuthorizationScope();

        authorizationScope.setScope( from.getScope() );
        authorizationScope.setDescription( from.getDescription() );


        return authorizationScope;
    }


    @Override
    public com.mangofactory.swagger.dto.Authorization toSwagger(Authorization from)  {
        if ( from == null ) {
            return null;
        }

        com.mangofactory.swagger.dto.Authorization authorization = new com.mangofactory.swagger.dto.Authorization();

        authorization.setScopes( authorizationScopesToSwagger( from.getScopes() ) );
        authorization.setType( from.getType() );


        return authorization;
    }


    @Override
    public com.mangofactory.swagger.dto.LoginEndpoint toSwagger(LoginEndpoint from)  {
        if ( from == null ) {
            return null;
        }

        com.mangofactory.swagger.dto.LoginEndpoint loginEndpoint = new com.mangofactory.swagger.dto.LoginEndpoint();

        loginEndpoint.setUrl( from.getUrl() );


        return loginEndpoint;
    }


    @Override
    public List<com.mangofactory.swagger.dto.AuthorizationScope> authorizationScopesToSwagger(List<AuthorizationScope> from)  {
        if ( from == null ) {
            return null;
        }

        List<com.mangofactory.swagger.dto.AuthorizationScope> list = new ArrayList<com.mangofactory.swagger.dto.AuthorizationScope>();

        for ( AuthorizationScope authorizationScope : from ) {
            list.add( toSwagger( authorizationScope ) );
        }

        return list;
    }


    @Override
    public List<com.mangofactory.swagger.dto.AuthorizationType> toSwagger(List<AuthorizationType> from)  {
        if ( from == null ) {
            return null;
        }

        List<com.mangofactory.swagger.dto.AuthorizationType> list = new ArrayList<com.mangofactory.swagger.dto.AuthorizationType>();

        for ( AuthorizationType authorizationType : from ) {
            list.add( concreteToSwagger( authorizationType ) );
        }

        return list;
    }



    protected List<com.mangofactory.swagger.dto.GrantType> grantTypeListToGrantTypeList(List<GrantType> list)  {
        if ( list == null ) {
            return null;
        }

        List<com.mangofactory.swagger.dto.GrantType> list_ = new ArrayList<com.mangofactory.swagger.dto.GrantType>();

        for ( GrantType grantType : list ) {
            list_.add( concreteToSwagger( grantType ) );
        }

        return list_;
    }

}
