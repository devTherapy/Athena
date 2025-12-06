package co.retaila.athena.modules.user.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevokeUserAuthoritiesRequest {

    @NotEmpty(message = "At least one authority is required")
    private List<@NotBlank String> authorities;

}
