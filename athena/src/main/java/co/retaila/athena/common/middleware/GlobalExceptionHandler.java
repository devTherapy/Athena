package co.retaila.athena.common.middleware;

import co.retaila.athena.common.error.Error;
import co.retaila.athena.common.error.ErrorResponse;
import co.retaila.athena.common.exceptions.ApplicationException;
import co.retaila.athena.common.exceptions.BadRequestException;
import co.retaila.athena.common.exceptions.ForbiddenException;
import co.retaila.athena.common.exceptions.MethodNotSupportedException;
import co.retaila.athena.common.exceptions.NotFoundException;
import co.retaila.athena.common.exceptions.ServerErrorException;
import co.retaila.athena.common.exceptions.ValidationException;
import co.retaila.lib.security.athena.error.AthenaSecurityErrorResponse;
import co.retaila.lib.security.athena.exceptions.AthenaSecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;
import java.util.stream.Collectors;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<?> handleException(Exception ex) {
        log.error(ex.getMessage(), ex);

        if (ex instanceof ApplicationException)
            return handleApplicationException((ApplicationException) ex);
        else if (ex instanceof NoHandlerFoundException)
            return handleNoHandlerFoundException();
        else if (ex instanceof HttpRequestMethodNotSupportedException)
            return handleHttpRequestMethodNotSupportedException();
        else if (ex instanceof AccessDeniedException)
            return handleAccessDeniedException();
        else if (ex instanceof BindException)
            return handleBindException((BindException) ex);
        else if (ex instanceof HttpMessageNotReadableException)
            return handleHttpMessageNotReadableException((HttpMessageNotReadableException) ex);
        else if (ex instanceof MethodArgumentTypeMismatchException)
            return handleMethodArgumentTypeMismatchException((MethodArgumentTypeMismatchException) ex);
        else if (ex instanceof AthenaSecurityException)
            return handleAthenaSecurityException((AthenaSecurityException) ex);
        else
            return handleUnknownException();
    }

    private ResponseEntity<ErrorResponse> handleApplicationException(ApplicationException ex) {
        return new ResponseEntity<>(ex.toErrorResponse(), HttpStatus.valueOf(ex.getStatusCode()));
    }

    private ResponseEntity<ErrorResponse> handleNoHandlerFoundException() {
        return handleApplicationException(new NotFoundException());
    }

    private ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException() {
        return handleApplicationException(new MethodNotSupportedException());
    }

    private ResponseEntity<ErrorResponse> handleAccessDeniedException() {
        return handleApplicationException(new ForbiddenException());
    }

    private ResponseEntity<ErrorResponse> handleBindException(BindException ex) {
        List<Error> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> Error.create(error.getField(), error.getDefaultMessage(), error.getRejectedValue()))
                .collect(Collectors.toList());

        return handleApplicationException(new ValidationException(errors));
    }

    private ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return handleApplicationException(new BadRequestException(ex.getMessage()));
    }

    private ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        Error error = Error.create(
                ex.getName(), String.format("Invalid value sent for '%s' field: %s", ex.getName(), ex.getValue())
        );
        return handleApplicationException(new ValidationException(error));
    }

    private ResponseEntity<AthenaSecurityErrorResponse> handleAthenaSecurityException(AthenaSecurityException ex) {
        return new ResponseEntity<>(ex.toErrorResponse(), HttpStatus.valueOf(ex.getStatusCode()));
    }

    private ResponseEntity<ErrorResponse> handleUnknownException() {
        return handleApplicationException(new ServerErrorException());
    }

}
