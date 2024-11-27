package io.github.geo_app.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class AlreadyPendingImportJobException extends RuntimeException {
    public AlreadyPendingImportJobException() {
        super("Other import job is already in progress");
    }
}