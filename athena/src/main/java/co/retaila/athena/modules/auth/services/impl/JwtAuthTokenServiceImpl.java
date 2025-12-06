package co.retaila.athena.modules.auth.services.impl;

import co.retaila.athena.common.entities.Client;
import co.retaila.athena.common.entities.Role;
import co.retaila.athena.common.entities.Scope;
import co.retaila.athena.common.entities.User;
import co.retaila.athena.common.enums.OidcScope;
import co.retaila.athena.common.enums.TimeFactor;
import co.retaila.athena.common.services.CurrentRequestService;
import co.retaila.athena.common.utils.CommonUtils;
import co.retaila.athena.modules.auth.enums.GrantType;
import co.retaila.athena.modules.auth.services.JwtAuthTokenService;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtAuthTokenServiceImpl implements JwtAuthTokenService {

    private final CurrentRequestService currentRequestService;

    @Override
    public String buildAccessTokenJwt(
            Client client, int expiryInSeconds, PrivateKey privateKey, List<String> resourceIds, List<String> authorities
    ) {
        return Jwts.builder()
                .setAudience(String.join(",", resourceIds))
                .setIssuer(currentRequestService.getBaseUrl())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + CommonUtils.convertToMillis(expiryInSeconds, TimeFactor.SECOND)))
                .claim("client_id", client.getIdentifier())
                .claim("grant_type", GrantType.CLIENT_CREDENTIALS.getValue())
                .claim("authorities", authorities)
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    @Override
    public String buildAccessTokenJwt(
            Client client, User user, GrantType grantType, int expiryInSeconds, PrivateKey privateKey,
            List<String> resourceIds, List<Role> roles, Collection<String> authorities
    ) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setAudience(String.join(",", resourceIds))
                .setIssuer(currentRequestService.getBaseUrl())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + CommonUtils.convertToMillis(expiryInSeconds, TimeFactor.SECOND)))
                .claim("client_id", client.getIdentifier())
                .claim("grant_type", grantType.getValue())
                .claim("roles", roles.stream().map(Role::getName).collect(Collectors.toList()))
                .claim("authorities", authorities)
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    @Override
    public String buildAccessTokenJwt(
            Client client, User user, GrantType grantType, int expiryInSeconds, PrivateKey privateKey,
            List<String> resourceIds, List<Role> roles, Collection<String> authorities, List<Scope> scopes
    ) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setAudience(String.join(",", resourceIds))
                .setIssuer(currentRequestService.getBaseUrl())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + CommonUtils.convertToMillis(expiryInSeconds, TimeFactor.SECOND)))
                .claim("client_id", client.getIdentifier())
                .claim("grant_type", grantType.getValue())
                .claim("scopes", scopes.stream().map(Scope::getName).collect(Collectors.toList()))
                .claim("roles", roles.stream().map(Role::getName).collect(Collectors.toList()))
                .claim("authorities", authorities)
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    @Override
    public String buildIdTokenJwt(
            Client client, User user, int expiryInSeconds, PrivateKey privateKey, List<String> oidcScopes
    ) {
        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(user.getUsername())
                .setAudience(client.getName())
                .setIssuer(currentRequestService.getBaseUrl())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + CommonUtils.convertToMillis(expiryInSeconds, TimeFactor.SECOND)));

        if (oidcScopes.contains(OidcScope.PROFILE.getValue())) {
            jwtBuilder.claim("first_name", user.getFirstName());
            jwtBuilder.claim("middle_name", user.getMiddleName());
            jwtBuilder.claim("last_name", user.getLastName());
            jwtBuilder.claim("phone_number", user.getPhoneNumber());
        }

        if (oidcScopes.contains(OidcScope.EMAIL.getValue())) {
            jwtBuilder.claim("email", user.getEmail());
            jwtBuilder.claim("email_verified", user.isEmailVerified());
        }

        return jwtBuilder
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

}
