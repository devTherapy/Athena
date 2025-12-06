package co.retaila.athena.common.exceptions;

import co.retaila.athena.common.error.ErrorResponse;
import lombok.Getter;

@Getter
public class AuthException extends ApplicationException {

    public AuthException(String error) {
        super(error, 400);
    }

    public AuthException(String error, String errorDescription) {
        super(errorDescription, error, 400);
    }

    @Override
    public ErrorResponse toErrorResponse() {
        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setError(getError());
        errorResponse.setErrorDescription(getErrorDescription());

        return errorResponse;
    }

}
