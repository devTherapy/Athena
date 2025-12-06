package co.retaila.athena.modules.auth.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum AuthResponseType {

    CODE("code");

    private final String value;

    AuthResponseType(String value) {
        this.value = value;
    }

    public static List<String> getAuthResponseTypes() {
        return Arrays.stream(AuthResponseType.values())
                .map(AuthResponseType::getValue)
                .collect(Collectors.toList());
    }

}
