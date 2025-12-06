package co.retaila.athena.modules.scope.requests;

import co.retaila.athena.modules.register.requests.RegistrationRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateScopeAuthoritiesRequest {

    @NotEmpty(message = "At least one authority is required")
    private List<@NotBlank String> authorities;

    public static UpdateScopeAuthoritiesRequest from(List<String> authorities, String namespace) {
        return UpdateScopeAuthoritiesRequest.builder()
                .authorities(
                        authorities.stream()
                                .map(authority -> namespace + authority)
                                .collect(Collectors.toList())
                )
                .build();
    }

}
