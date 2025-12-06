package co.retaila.athena.modules.user.models;

import co.retaila.athena.common.entities.Role;
import co.retaila.athena.common.entities.User;
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
public class UserRolesUpdateRequestValidationResult {

    private User user;
    private List<Role> roles;

}
