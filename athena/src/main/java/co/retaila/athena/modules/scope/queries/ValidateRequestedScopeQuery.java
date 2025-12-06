package co.retaila.athena.modules.scope.queries;

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
public class ValidateRequestedScopeQuery {

    @NotBlank
    private String requestedScope;
    @NotBlank
    private String domain;

}
