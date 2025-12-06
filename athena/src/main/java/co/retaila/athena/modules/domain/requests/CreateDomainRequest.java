package co.retaila.athena.modules.domain.requests;

import co.retaila.athena.modules.register.requests.RegistrationRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDomainRequest {

    @NotBlank(message = "name is required")
    private String name;
    @Min(60)
    private Integer tokenValidityInSeconds;
    @Min(30)
    @Max(600)
    private Integer authCodeValidityInSeconds;
    @Min(600)
    @Max(1800)
    private Integer deviceCodeValidityInSeconds;

}
