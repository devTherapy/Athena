package co.retaila.athena.modules.client.requests;

import co.retaila.athena.common.enums.ClientApplicationType;
import co.retaila.athena.common.enums.ClientType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateClientRequest {

    @NotBlank(message = "name is required")
    private String name;
    @NotBlank(message = "identifier is required")
    private String identifier;
    @NotBlank(message = "secret is required")
    private String secret;
    @NotBlank(message = "adminEmail is required")
    private String adminEmail;
    @NotBlank(message = "redirectUri is required")
    private String redirectUri;
    @NotNull(message = "clientType is required")
    private ClientType clientType;
    @NotNull(message = "clientApplicationType is required")
    private ClientApplicationType clientApplicationType;
    @Builder.Default
    private List<@NotBlank String> authorities = new ArrayList<>();
    @NotEmpty(message = "At least one domain is required")
    private List<@NotBlank String> domains;
    @Builder.Default
    private List<@NotBlank String> resourceIds = new ArrayList<>();
    @Min(60)
    private Integer tokenValidityInSeconds;

}
