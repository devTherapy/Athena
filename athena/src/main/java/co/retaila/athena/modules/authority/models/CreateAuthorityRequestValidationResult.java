package co.retaila.athena.modules.authority.models;

import co.retaila.athena.common.entities.Domain;
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
public class CreateAuthorityRequestValidationResult {

    private Domain domain;

}
