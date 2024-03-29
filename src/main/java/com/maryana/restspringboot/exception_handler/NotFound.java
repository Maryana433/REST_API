package com.maryana.restspringboot.exception_handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class NotFound extends Exception {
    public NotFound(String message){
        super(message);
    }
}
