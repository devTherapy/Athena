package co.retaila.athena.modules.auth.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum GrantType {

    PASSWORD("password"),
    CLIENT_CREDENTIALS("client_credentials"),
    AUTHORIZATION_CODE("authorization_code"),
    DEVICE_CODE("device_code");

    private final String value;

    GrantType(String value) {
        this.value = value;
    }

    public static List<String> getGrantTypes() {
        return Arrays.stream(GrantType.values())
                .map(GrantType::getValue)
                .collect(Collectors.toList());
    }

}
