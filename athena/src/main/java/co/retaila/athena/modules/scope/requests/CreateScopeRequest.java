package co.retaila.athena.modules.scope.requests;

import co.retaila.athena.modules.register.requests.RegistrationRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateScopeRequest {

    @NotBlank(message = "name is required")
    private String name;
    private String description;
    @NotBlank(message = "domain is required")
    private String domain;
    @Builder.Default
    private List<@NotBlank String> authorities = new ArrayList<>();

    public static CreateScopeRequest from(RegistrationRequest.Scope scope, String domain) {
        return CreateScopeRequest.builder()
                .name(scope.getName())
                .description(scope.getDescription())
                .domain(domain)
                .build();
    }

}
