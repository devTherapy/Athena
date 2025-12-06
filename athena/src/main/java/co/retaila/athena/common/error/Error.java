package co.retaila.athena.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Error {

    private final String field;
    private final String message;
    private Object attemptedValue;

    public static Error create(String field, String message) {
        return new Error(field, message);
    }

    public static Error create(String field, String message, Object attemptedValue) {
        return new Error(field, message, attemptedValue);
    }

}
