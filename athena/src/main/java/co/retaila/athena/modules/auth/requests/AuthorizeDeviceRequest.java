package co.retaila.athena.modules.auth.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizeDeviceRequest {

    @NotBlank(message = "userCode is required")
    private String userCode;
    @NotNull(message = "authorized is required")
    private Boolean authorized;

}
