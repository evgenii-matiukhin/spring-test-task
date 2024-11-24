package io.github.geo_app.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class GeoClassNotFoundException extends RuntimeException {
    public GeoClassNotFoundException(Long id) {
        super("Geological class was "+id+" not found");
    }
}