package co.retaila.athena.common.exceptions;


import co.retaila.athena.common.constants.ErrorConstants;

public class InvalidOperationException extends ApplicationException {

    public InvalidOperationException(String errorDescription) {
        super(errorDescription, ErrorConstants.INVALID_OPERATION, 400);
    }

}
