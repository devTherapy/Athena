package co.retaila.athena.modules.scope.models;

import co.retaila.athena.common.entities.Authority;
import co.retaila.athena.common.entities.Scope;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScopeAuthoritiesUpdateRequestValidationResult {

    private Scope scope;
    private List<Authority> authorities;

}
