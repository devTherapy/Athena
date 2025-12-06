package co.retaila.athena.modules.client.requests;

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
public class UpdateClientResourceServersRequest {

    @NotEmpty(message = "At least one resourceId is required")
    private List<@NotBlank String> resourceIds;

}
