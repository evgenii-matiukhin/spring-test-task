package io.github.geo_app.handler;

import io.github.geo_app.exceptions.AlreadyPendingImportJobException;
import io.github.geo_app.exceptions.BadFileTypeException;
import io.github.geo_app.exceptions.GeoClassNotFoundException;
import io.github.geo_app.exceptions.JobRecordNotFoundException;
import io.github.geo_app.exceptions.SectionNotFoundException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Not found exceptions
    @ExceptionHandler({
            SectionNotFoundException.class,
            GeoClassNotFoundException.class,
            JobRecordNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    RestErrorResponse handleNotFoundException(
            RuntimeException ex) {
        return new RestErrorResponse( 
            HttpStatus.NOT_FOUND.value(), 
            ex.getMessage(), 
            LocalDateTime.now());
    }

    // Not found exceptions
    @ExceptionHandler({
            AlreadyPendingImportJobException.class,
            DataIntegrityViolationException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    RestErrorResponse handleConflictException(
            RuntimeException ex) {
        return new RestErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                LocalDateTime.now());
    }

    // Validation exceptions
    @ExceptionHandler({
            ValidationException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class,
            BadFileTypeException.class,
            MultipartException.class,
            HttpMediaTypeNotSupportedException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    RestErrorResponse handleValidationException(RuntimeException ex) {
        return new RestErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now());
    }

    // Handle any other exception too.
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    RestErrorResponse handleException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return new RestErrorResponse( 
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ex.getMessage(), 
            LocalDateTime.now());
    }
}