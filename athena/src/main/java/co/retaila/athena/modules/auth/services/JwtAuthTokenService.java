package co.retaila.athena.modules.auth.services;

import co.retaila.athena.common.entities.Client;
import co.retaila.athena.common.entities.Role;
import co.retaila.athena.common.entities.Scope;
import co.retaila.athena.common.entities.User;
import co.retaila.athena.modules.auth.enums.GrantType;

import java.security.PrivateKey;
import java.util.Collection;
import java.util.List;

public interface JwtAuthTokenService {

    String buildAccessTokenJwt(
            Client client, int expiryInSeconds, PrivateKey privateKey, List<String> resourceIds, List<String> authorities
    );
    String buildAccessTokenJwt(
            Client client, User user, GrantType grantType, int expiryInSeconds, PrivateKey privateKey,
            List<String> resourceIds, List<Role> roles, Collection<String> authorities
    );
    String buildAccessTokenJwt(
            Client client, User user, GrantType grantType, int expiryInSeconds, PrivateKey privateKey,
            List<String> resourceIds, List<Role> roles, Collection<String> authorities, List<Scope> scopes
    );
    String buildIdTokenJwt(
            Client client, User user, int expiryInSeconds, PrivateKey privateKey, List<String> oidcScopes
    );

}
