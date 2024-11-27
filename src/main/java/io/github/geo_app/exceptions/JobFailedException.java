package io.github.geo_app.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class JobFailedException extends RuntimeException {
    public JobFailedException(String message) {
        super(message);
    }
}