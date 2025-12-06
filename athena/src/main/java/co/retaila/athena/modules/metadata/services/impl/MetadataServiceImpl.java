package co.retaila.athena.modules.metadata.services.impl;

import co.retaila.athena.common.properties.AppProperties;
import co.retaila.athena.common.services.CurrentRequestService;
import co.retaila.athena.modules.auth.enums.AuthResponseType;
import co.retaila.athena.modules.auth.enums.GrantType;
import co.retaila.athena.modules.metadata.models.Metadata;
import co.retaila.athena.modules.metadata.services.MetadataService;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MetadataServiceImpl implements MetadataService {

    private final CurrentRequestService currentRequestService;
    private final AppProperties appProperties;

    @Override
    public Metadata fetchMetadata() {
        String baseUrl = currentRequestService.getBaseUrl();

        return Metadata.builder()
                .issuer(baseUrl)
                .authorizationEndpoint(appProperties.getAuthorizationUri())
                .tokenEndpoint(String.format("%s/api/auth/token", baseUrl))
                .responseTypesSupported(AuthResponseType.getAuthResponseTypes())
                .grantTypesSupported(GrantType.getGrantTypes())
                .publicKey(appProperties.getPublicKey())
                .signingAlgorithm(SignatureAlgorithm.RS256.getValue())
                .build();
    }

}
