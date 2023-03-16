package com.maryana.restspringboot.dto;

import javax.validation.constraints.NotBlank;

public class BookRequest {

    @NotBlank(message = "title of book cannot be empty")
    private String title;

    @NotBlank(message = "author cannot be empty")
    private String author;


    public BookRequest(String title, String author) {
        this.title = title;
        this.author = author;
    }

    public BookRequest() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "BookRequest{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}
