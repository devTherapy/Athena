package co.retaila.athena.modules.role.models;

import co.retaila.athena.common.entities.Authority;
import co.retaila.athena.common.entities.Role;
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
public class RoleAuthoritiesUpdateRequestValidationResult {

    private Role role;
    private List<Authority> authorities;

}
