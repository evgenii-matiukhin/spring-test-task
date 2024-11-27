package io.github.geo_app.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class JobRecordNotFoundException extends RuntimeException {
    public JobRecordNotFoundException(UUID id) {
        super("Job with id "+id+" was not found");
    }
}