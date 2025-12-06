package co.retaila.athena.modules.user.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    @NotBlank(message = "username is required")
    private String username;
    @Email
    @NotBlank(message = "email is required")
    private String email;
    private String phoneNumber;
    @NotBlank(message = "password is required")
    private String password;
    @NotBlank(message = "firstName is required")
    private String firstName;
    private String middleName;
    @NotBlank(message = "lastName is required")
    private String lastName;
    @NotBlank(message = "domain is required")
    private String domain;
    private List<@NotBlank String> roles;

}
