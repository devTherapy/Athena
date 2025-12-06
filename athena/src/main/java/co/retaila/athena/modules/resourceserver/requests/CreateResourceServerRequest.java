package co.retaila.athena.modules.resourceserver.requests;

import co.retaila.athena.modules.register.requests.RegistrationRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateResourceServerRequest {

    @NotBlank(message = "resourceId is required")
    private String resourceId;

    public static CreateResourceServerRequest from(RegistrationRequest request) {
        return CreateResourceServerRequest.builder()
                .resourceId(request.getResourceId())
                .build();
    }

}
