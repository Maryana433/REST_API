package com.maryana.restspringboot.controller;

import com.maryana.restspringboot.dto.BookRequest;
import com.maryana.restspringboot.dto.SimpleResponse;
import com.maryana.restspringboot.entity.Book;
import com.maryana.restspringboot.exception_handler.Conflict;
import com.maryana.restspringboot.exception_handler.NotFound;
import com.maryana.restspringboot.service.BookService;
import io.swagger.annotations.*;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }


    @GetMapping
    @ApiOperation(value="Find all books of USER",notes = "Information about books of user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of user's books"),
            @ApiResponse(code = 401, message = "Unauthorized")
    })
        public ResponseEntity<List<Book>> getBooks(){
        String loginOfUser =  SecurityContextHolder.getContext().getAuthentication().getName();
        List<Book> bookList = bookService.getBooks(loginOfUser);
        return ResponseEntity.ok(bookList);
    }

    @GetMapping("/{id}")
    @ApiOperation(value="Find book of USER by id",notes = "Information about book of user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Information about user's book"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Book with this id doesn't exist"),
    })
    public ResponseEntity<Object> getBookById(@PathVariable int id){
        String loginOfUser =  SecurityContextHolder.getContext().getAuthentication().getName();
        Book book = null;
        try{
            book = bookService.getBookById(id,loginOfUser);
        }catch(NotFound notFound){
            return new ResponseEntity(new SimpleResponse("Book with id [ " + id + " ] wasn't found in list of your books"), HttpStatus.NOT_FOUND);
        }
       return  ResponseEntity.ok(book);
    }

    @PostMapping
    @ApiOperation(value="Add new book",notes = "User can add new book")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Book was added. Location header contains path to new book"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 409, message = "Book with this title and author was already added")})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Book to add.", required = true,
            content = @Content(schema=@Schema(implementation = Book.class)))
    public ResponseEntity<SimpleResponse> addBook(@RequestBody @Valid BookRequest book){

        int id = 0;
        try{
            id = bookService.addNewBook(book,SecurityContextHolder.getContext().getAuthentication().getName());
        }catch (Conflict c){
            return new ResponseEntity(new SimpleResponse("Book with author [ " + book.getAuthor() + " ] , title [ " +book.getTitle() + " ] was already added "), HttpStatus.CONFLICT);
        }

        return ResponseEntity.status(HttpStatus.CREATED).header("Location","http://localhost:8080/api/books/"+id)
                .body(new SimpleResponse("Book author [ " + book.getAuthor() + " ] , title [ " + book.getTitle() + " ] was successfully" +
                " added to your library."));
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value="Delete book from list",notes = "User can delete book")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Book was deleted"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Book with this id doesn't exists")})
    public ResponseEntity<String> deleteBook(@PathVariable int id){

        try {
            bookService.deleteBook(id, SecurityContextHolder.getContext().getAuthentication().getName());
        }catch(NotFound notFound){
            return new ResponseEntity(new SimpleResponse("Book with id [ " + id + " ] wasn't found in list of your books"), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(new SimpleResponse("Book with id  [ " + id + " ] was deleted"), HttpStatus.OK);
    }


}
