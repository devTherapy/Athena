package co.retaila.athena.modules.auth.services.impl;

import co.retaila.athena.common.constants.SecurityConstants;
import co.retaila.athena.common.entities.AuthCode;
import co.retaila.athena.common.entities.Authority;
import co.retaila.athena.common.entities.Client;
import co.retaila.athena.common.entities.DeviceCode;
import co.retaila.athena.common.entities.ResourceServer;
import co.retaila.athena.common.entities.Role;
import co.retaila.athena.common.entities.Scope;
import co.retaila.athena.common.entities.User;
import co.retaila.athena.common.properties.AppProperties;
import co.retaila.athena.common.utils.AuthUtils;
import co.retaila.athena.modules.auth.enums.GrantType;
import co.retaila.athena.modules.auth.responses.LoginResponse;
import co.retaila.athena.modules.auth.services.AuthTokenService;
import co.retaila.athena.modules.auth.services.JwtAuthTokenService;
import co.retaila.athena.modules.authority.services.AuthorityService;
import co.retaila.athena.modules.resourceserver.services.ResourceServerService;
import co.retaila.athena.modules.revocation.services.RevocationService;
import co.retaila.athena.modules.role.services.RoleService;
import co.retaila.athena.modules.scope.services.ScopeService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthTokenServiceImpl implements AuthTokenService {

    private final AuthorityService authorityService;
    private final RevocationService revocationService;
    private final ResourceServerService resourceServerService;
    private final RoleService roleService;
    private final ScopeService scopeService;
    private final JwtAuthTokenService jwtAuthTokenService;
    private final AppProperties appProperties;

    @Override
    public LoginResponse generateAuthToken(Client client, Integer expires) {
        int expiryInSeconds = expires != null ? expires : client.getTokenValidityInSeconds();

        PrivateKey privateKey = AuthUtils.getPrivateKey(appProperties.getPrivateKey());

        List<String> resourceIds = getResourceServers(client);
        List<String> authorities = getAuthorities(client);

        String accessToken = jwtAuthTokenService.buildAccessTokenJwt(client, expiryInSeconds, privateKey, resourceIds, authorities);
        return buildLoginResponse(expiryInSeconds, accessToken);
    }

    private List<String> getAuthorities(Client client) {
        return authorityService.getClientAuthorities(client).stream()
                .map(Authority::getName)
                .collect(Collectors.toList());
    }

    @Override
    public LoginResponse generateAuthToken(Client client, User user, Integer expires) {
        int expiryInSeconds = expires != null ? expires : user.getDomain().getTokenValidityInSeconds();

        PrivateKey privateKey = AuthUtils.getPrivateKey(appProperties.getPrivateKey());

        List<String> resourceIds = getResourceServers(client);
        List<Role> roles = roleService.getUserRoles(user);
        List<String> revocations = getRevocations(user);
        List<String> authorities = getAuthorities(roles, revocations);

        String accessToken = jwtAuthTokenService.buildAccessTokenJwt(
                client, user, GrantType.PASSWORD, expiryInSeconds, privateKey, resourceIds, roles, authorities
        );
        return buildLoginResponse(expiryInSeconds, accessToken);
    }

    @Override
    public LoginResponse generateAuthToken(Client client, AuthCode authCode, Integer expires) {
        int expiryInSeconds = expires != null ? expires : authCode.getRequestedDomain().getTokenValidityInSeconds();

        PrivateKey privateKey = AuthUtils.getPrivateKey(appProperties.getPrivateKey());

        List<String> resourceIds = getResourceServers(client);
        List<Role> roles = roleService.getUserRoles(authCode.getUser());
        List<Scope> scopes = scopeService.validateRegularScopes(authCode.getRequestedScope(), authCode.getRequestedDomain());
        List<String> revocations = getRevocations(authCode.getUser());
        List<String> authorities = getAuthorities(roles, revocations, scopes);

        String accessToken = jwtAuthTokenService.buildAccessTokenJwt(
                client, authCode.getUser(), GrantType.AUTHORIZATION_CODE,
                expiryInSeconds, privateKey, resourceIds, roles, authorities, scopes
        );
        String idToken = getIdToken(client, authCode.getUser(), authCode.getRequestedScope(), expiryInSeconds, privateKey);

        return buildLoginResponse(expiryInSeconds, accessToken, idToken);
    }

    @Override
    public LoginResponse generateAuthToken(Client client, User user, DeviceCode deviceCode, Integer expires) {
        int expiryInSeconds = expires != null ? expires : deviceCode.getRequestedDomain().getTokenValidityInSeconds();

        PrivateKey privateKey = AuthUtils.getPrivateKey(appProperties.getPrivateKey());

        List<String> resourceIds = getResourceServers(client);
        List<Role> roles = roleService.getUserRoles(user);

        String accessToken = getAccessToken(
                client, user, deviceCode, expiryInSeconds, privateKey, resourceIds, roles
        );
        String idToken = getIdToken(client, user, deviceCode, expiryInSeconds, privateKey);

        return buildLoginResponse(expiryInSeconds, accessToken, idToken);
    }

    private List<String> getResourceServers(Client client) {
        return resourceServerService.getClientResourceServers(client).stream()
                .map(ResourceServer::getResourceId)
                .collect(Collectors.toList());
    }

    private String getAccessToken(
            Client client, User user, DeviceCode deviceCode, int expiryInSeconds,
            PrivateKey privateKey, List<String> resourceIds, List<Role> roles
    ) {
        List<String> revocations = getRevocations(user);

        if (deviceCode.scopeNotRequested()) {
            List<String> authorities = getAuthorities(roles, revocations);

            var effectiveAuthorities = CollectionUtils.subtract(authorities, revocations);

            return jwtAuthTokenService.buildAccessTokenJwt(
                    client, user, GrantType.DEVICE_CODE, expiryInSeconds, privateKey, resourceIds, roles, effectiveAuthorities
            );
        }
        else {
            List<Scope> scopes = scopeService.validateRegularScopes(deviceCode.getRequestedScope(), deviceCode.getRequestedDomain());
            List<String> authorities = getAuthorities(roles, revocations, scopes);

            var effectiveAuthorities = CollectionUtils.subtract(authorities, revocations);

            return jwtAuthTokenService.buildAccessTokenJwt(
                    client, user, GrantType.DEVICE_CODE, expiryInSeconds, privateKey, resourceIds, roles, effectiveAuthorities, scopes
            );
        }
    }

    private List<String> getAuthorities(List<Role> roles, List<String> revocations, List<Scope> scopes) {
        var authorities = getAuthorities(roles, revocations);
        var delegations = getDelegations(scopes);

        return authorities.stream()
                .filter(delegations::contains)
                .collect(Collectors.toList());
    }

    private List<String> getAuthorities(List<Role> roles, List<String> revocations) {
        var authorities = roles.stream()
                .map(authorityService::getRoleAuthorities)
                .flatMap(List::stream)
                .map(Authority::getName)
                .collect(Collectors.toList());

        return authorities.stream()
                .filter(authority -> !revocations.contains(authority))
                .collect(Collectors.toList());
    }

    private List<String> getDelegations(List<Scope> scopes) {
        return scopes.stream()
                .map(authorityService::getScopeAuthorities)
                .flatMap(List::stream)
                .map(Authority::getName)
                .collect(Collectors.toList());
    }

    private List<String> getRevocations(User user) {
        return revocationService.getUserRevocations(user).stream()
                .map(Authority::getName)
                .collect(Collectors.toList());
    }

    private String getIdToken(Client client, User user, DeviceCode deviceCode, int expiryInSeconds, PrivateKey privateKey) {
        if (deviceCode.scopeNotRequested())
            return null;

        return getIdToken(client, user, deviceCode.getRequestedScope(), expiryInSeconds, privateKey);
    }

    private String getIdToken(Client client, User user, String requestedScope, int expiryInSeconds, PrivateKey privateKey) {
        List<String> oidcScopes = scopeService.validateOidcScopes(requestedScope).stream()
                .map(Scope::getName)
                .collect(Collectors.toList());

        if (oidcScopes.isEmpty())
            return null;

        return jwtAuthTokenService.buildIdTokenJwt(client, user, expiryInSeconds, privateKey, oidcScopes);
    }

    private LoginResponse buildLoginResponse(int expiryInSeconds, String accessToken) {
        return buildLoginResponse(expiryInSeconds, accessToken, null);
    }

    private LoginResponse buildLoginResponse(int expiryInSeconds, String accessToken, String idToken) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .idToken(idToken)
                .tokenType(SecurityConstants.AUTH_TOKEN_TYPE)
                .expiresIn(expiryInSeconds)
                .build();
    }

    @Override
    public void invalidateUserTokens(User user) {
        // TODO: Implement user token invalidation.
    }

}
