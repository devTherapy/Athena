package co.retaila.athena.common.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum OidcScope {

    OPENID("openid", "Gives the application access to your retaila username."),
    PROFILE("profile", "Gives the application access to your basic profile information such as your first name, middle name, last name and phone number."),
    EMAIL("email", "Gives the application access to your email address, and email_verified status.");

    private final String value;
    private final String description;

    OidcScope(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public static List<String> getOidcScopes() {
        return Arrays.stream(OidcScope.values())
                .map(OidcScope::getValue)
                .collect(Collectors.toList());
    }

}
