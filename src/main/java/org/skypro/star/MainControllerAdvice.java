package org.skypro.star;

import org.skypro.star.repository.NoSuchObjectException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;


@ControllerAdvice(basePackages = "org.skypro.star.controller")
public class MainControllerAdvice extends RuntimeException {

    @ExceptionHandler(NoSuchObjectException.class)
    public ResponseStatus handleNoSuchSomeObjectException(NoSuchObjectException e) {
        return e.getClass().getAnnotation(ResponseStatus.class);
    }
}