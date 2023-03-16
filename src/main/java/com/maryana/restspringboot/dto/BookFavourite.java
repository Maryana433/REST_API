package com.maryana.restspringboot.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class BookFavourite {

    @NotNull(message = "id cannot be null")
    @Min(message = "min id is 1",value = 1)
    private Long id;


    public BookFavourite(Long id) {
        this.id = id;
    }

    public BookFavourite() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
