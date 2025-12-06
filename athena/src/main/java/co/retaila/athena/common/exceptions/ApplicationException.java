package co.retaila.athena.common.exceptions;

import co.retaila.athena.common.error.ErrorResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public abstract class ApplicationException extends RuntimeException {

    private String error;
    private String errorDescription;
    private int statusCode;

    public ApplicationException(String error, int statusCode) {
        this.error = error;
        this.statusCode = statusCode;
    }

    public ApplicationException(String error, int statusCode, Throwable innerException) {
        super(innerException);
        this.error = error;
        this.statusCode = statusCode;
    }

    public ApplicationException(String errorDescription, String error, int statusCode) {
        super(errorDescription);
        this.errorDescription = errorDescription;
        this.error = error;
        this.statusCode = statusCode;
    }

    public ApplicationException(String errorDescription, String error, int statusCode, Throwable innerException) {
        super(errorDescription, innerException);
        this.errorDescription = errorDescription;
        this.error = error;
        this.statusCode = statusCode;
    }

    public ErrorResponse toErrorResponse() {
        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setError(getError());
        errorResponse.setErrorDescription(getMessage());

        return errorResponse;
    }

}
