package org.skypro.star.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class NoValidValueException extends RuntimeException {
    private String message;

    public NoValidValueException(String message) {
        super(" - this value '" + message + "' does not valid");
    }
}
