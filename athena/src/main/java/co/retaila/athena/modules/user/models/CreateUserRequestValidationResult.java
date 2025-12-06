package co.retaila.athena.modules.user.models;

import co.retaila.athena.common.entities.Domain;
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
public class CreateUserRequestValidationResult {

    private Domain domain;
    private List<Role> roles;

}
