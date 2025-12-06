package co.retaila.athena.modules.client.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClientRequest {

    @NotBlank(message = "adminEmail is required")
    private String adminEmail;
    @NotBlank(message = "redirectUri is required")
    private String redirectUri;
    @NotNull
    @Min(60)
    private Integer tokenValidityInSeconds;

}
