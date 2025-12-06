
package co.retaila.athena.modules.metadata.models;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Metadata {

    private String authorizationEndpoint;
    private List<String> grantTypesSupported;
    private String issuer;
    private String publicKey;
    private String signingAlgorithm;
    private List<String> responseTypesSupported;
    private String revocationEndpoint;
    private List<String> scopesSupported;
    private String tokenEndpoint;
    private List<String> tokenEndpointAuthMethodsSupported;

}
