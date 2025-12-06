package co.retaila.athena.modules.authority.requests;

import co.retaila.athena.modules.register.requests.RegistrationRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAuthorityRequest {

    @NotBlank(message = "name is required")
    private String name;
    private String description;
    @NotBlank(message = "domain is required")
    private String domain;

    public static CreateAuthorityRequest from(RegistrationRequest.Authority authority, RegistrationRequest request) {
        return CreateAuthorityRequest.builder()
                .name(request.getNamespace() + authority.getName())
                .description(authority.getDescription())
                .domain(request.getDomain())
                .build();
    }

}