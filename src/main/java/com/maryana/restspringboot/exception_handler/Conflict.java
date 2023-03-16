package com.maryana.restspringboot.exception_handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class Conflict extends Exception {
    public Conflict(String message){
        super(message);
    }
}
