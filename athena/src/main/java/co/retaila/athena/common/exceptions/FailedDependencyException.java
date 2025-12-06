package co.retaila.athena.common.exceptions;


import co.retaila.athena.common.constants.ErrorConstants;

public class FailedDependencyException extends ApplicationException {

    public FailedDependencyException(String errorDescription) {
        super(errorDescription, ErrorConstants.FAILED_DEPENDENCY, 424);
    }

}
