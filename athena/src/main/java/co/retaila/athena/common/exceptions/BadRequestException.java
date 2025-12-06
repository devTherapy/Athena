package co.retaila.athena.common.exceptions;


import co.retaila.athena.common.constants.ErrorConstants;

public class BadRequestException extends ApplicationException {

    public BadRequestException(String errorDescription) {
        super(errorDescription, ErrorConstants.BAD_REQUEST, 400);
    }

    public BadRequestException(String errorDescription, String error) {
        super(errorDescription, error, 400);
    }

}
