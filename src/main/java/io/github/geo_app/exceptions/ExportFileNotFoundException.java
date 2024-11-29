package io.github.geo_app.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ExportFileNotFoundException extends RuntimeException {
    public ExportFileNotFoundException() {
        super("Export file not found. Job is probably not finished or failed.");
    }
}