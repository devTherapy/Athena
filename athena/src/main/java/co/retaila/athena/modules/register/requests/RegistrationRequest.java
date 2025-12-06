package co.retaila.athena.modules.register.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {

    private @NotBlank String resourceId;
    private @NotBlank String domain;
    private @NotNull String namespace;
    private List<@Valid Authority> authorities;
    private List<@Valid Role> roles;
    private List<@Valid RoleAuthoritiesMapping> roleAuthoritiesMappings;
    private List<@Valid Scope> scopes;
    private List<@Valid ScopeAuthoritiesMapping> scopeAuthoritiesMappings;

    @Getter
    @Setter
    public static class Authority {

        @NotBlank(message = "name is required")
        private String name;
        private String description;

    }

    @Getter
    @Setter
    public static class Role {

        @NotBlank(message = "name is required")
        private String name;
        private String description;

    }

    @Getter
    @Setter
    public static class RoleAuthoritiesMapping {

        @NotBlank(message = "role is required")
        private String role;
        @NotEmpty(message = "At least one authority is required")
        private List<@NotBlank String> authorities;

    }

    @Getter
    @Setter
    public static class Scope {

        @NotBlank(message = "name is required")
        private String name;
        private String description;

    }

    @Getter
    @Setter
    public static class ScopeAuthoritiesMapping {

        @NotBlank(message = "scope is required")
        private String scope;
        @NotEmpty(message = "At least one authority is required")
        private List<@NotBlank String> authorities;

    }

}
