package co.retaila.athena.modules.auth.models;

import co.retaila.athena.common.entities.Client;
import co.retaila.athena.common.entities.Domain;
import co.retaila.athena.common.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeAuthorizeRequestValidationResult {

    private User user;
    private Client client;
    private Domain domain;

}
