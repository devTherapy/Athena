package co.retaila.athena.modules.role.models;

import co.retaila.athena.common.entities.Authority;
import co.retaila.athena.common.entities.Domain;
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
public class CreateRoleRequestValidationResult {

    private Domain domain;
    private List<Authority> authorities;

}
