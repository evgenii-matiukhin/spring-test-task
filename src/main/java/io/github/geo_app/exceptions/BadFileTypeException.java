package io.github.geo_app.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class BadFileTypeException extends RuntimeException {
    public BadFileTypeException() {
        super("Invalid file type. Please upload an Excel file");
    }
}