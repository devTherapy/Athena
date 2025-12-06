package co.retaila.athena.common.exceptions;

import co.retaila.athena.common.constants.ErrorConstants;

public class ServerErrorException extends ApplicationException {

    public ServerErrorException() {
        super(
                "An unexpected error occurred. Please try again or confirm server operation status",
                ErrorConstants.SERVER_ERROR,
                500
        );
    }

}
