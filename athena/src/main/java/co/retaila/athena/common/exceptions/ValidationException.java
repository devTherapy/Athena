package co.retaila.athena.common.exceptions;

import co.retaila.athena.common.constants.ErrorConstants;
import co.retaila.athena.common.error.Error;
import co.retaila.athena.common.error.ErrorResponse;
import co.retaila.athena.common.error.ValidationErrorResponse;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class ValidationException extends ApplicationException {

    private final Map<String, String> errors;

    private ValidationException() {
        super("One or more validation errors have occurred", ErrorConstants.UNPROCESSABLE_ENTITY, 422);
        this.errors = new HashMap<>();
    }

    private ValidationException(String error) {
        super("One or more validation errors have occurred", error, 422);
        this.errors = new HashMap<>();
    }

    public ValidationException(Error fieldError) {
        this();
        this.errors.put(fieldError.getField(), fieldError.getMessage());
    }

    public ValidationException(String error, Error fieldError) {
        this(error);
        this.errors.put(fieldError.getField(), fieldError.getMessage());
    }

    public ValidationException(List<Error> errors) {
        this();
        errors.forEach(error -> this.errors.put(error.getField(), error.getMessage()));
    }

    public ValidationException(String error, List<Error> fieldErrors) {
        this(error);
        fieldErrors.forEach(fieldError -> this.errors.put(fieldError.getField(), fieldError.getMessage()));
    }

    @Override
    public ErrorResponse toErrorResponse() {
        ValidationErrorResponse errorResponse = new ValidationErrorResponse();

        errorResponse.setError(getError());
        errorResponse.setErrorDescription(getErrorDescription());
        errorResponse.setErrors(getErrors());

        return errorResponse;
    }

}
