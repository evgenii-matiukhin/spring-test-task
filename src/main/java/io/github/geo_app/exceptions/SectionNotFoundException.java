package io.github.geo_app.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class SectionNotFoundException extends RuntimeException {
    public SectionNotFoundException(Long id) {
        super("Section was "+id+" not found");
    }
}