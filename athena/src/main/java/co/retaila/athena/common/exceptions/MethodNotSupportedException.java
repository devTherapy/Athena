package co.retaila.athena.common.exceptions;

import co.retaila.athena.common.constants.ErrorConstants;

public class MethodNotSupportedException extends ApplicationException {

    public MethodNotSupportedException() {
        super(ErrorConstants.METHOD_NOT_ALLOWED, 405);
    }

}
