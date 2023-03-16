package com.maryana.restspringboot.controller;

import com.maryana.restspringboot.dto.BookFavourite;
import com.maryana.restspringboot.dto.SimpleResponse;
import com.maryana.restspringboot.entity.Book;
import com.maryana.restspringboot.exception_handler.Conflict;
import com.maryana.restspringboot.exception_handler.NotFound;
import com.maryana.restspringboot.service.FavouriteBookService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/books/favourites")
public class FavouriteBookController {

    private final FavouriteBookService favouriteBookService;

    @Autowired
    public FavouriteBookController(FavouriteBookService favouriteBookService) {
        this.favouriteBookService = favouriteBookService;
    }


    @GetMapping
    @ApiOperation(value="Find all favourite books",notes = "Information about favourite books")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of favourite books of registered user"),
            @ApiResponse(code = 401, message = "Unauthorized")
    })
    public ResponseEntity<List<Book>> getFavouriteBooks(){
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Book> books = favouriteBookService.getBooksOfUser(login);
        return ResponseEntity.ok(books);
    }


    @GetMapping("/{id}")
    @ApiOperation(value="Find favourite book by id",notes = "Information about favourite book by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Information about book with specified id"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Book with this id doesn't exist")
    })
    public ResponseEntity getFavouriteBookById(@PathVariable int id){
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        Book book = null;
        try {
            book = favouriteBookService.getBooksOfUserById(login, id);

        } catch (NotFound n){
            return new ResponseEntity(new SimpleResponse("Book with id [ " + id + " ] wasn't found in list of your favourite books"),
                    HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(book);
    }


    @PostMapping()
    @ApiOperation(value="Add new book to your favorites",notes = "User can add new book to his list of favorites")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Book was added to your list"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Book with this id doesn't exist in library"),
            @ApiResponse(code = 409, message = "Book with this id was already added")}
    )
    public ResponseEntity<SimpleResponse> addBook(@RequestBody @Valid BookFavourite bookRequest){
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            favouriteBookService.addBookToUserList(bookRequest.getId(), login);
        } catch (NotFound notFound) {
            return new ResponseEntity<>(new SimpleResponse("Book with id [ " + bookRequest.getId() + " ] wasn't found in list of books"),
                    HttpStatus.NOT_FOUND);
        }catch (Conflict c){
            return new ResponseEntity<>(new SimpleResponse("Book with id [ " + bookRequest.getId() + " ] was already added to your list"),
                    HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(new SimpleResponse("Book with id [ " + bookRequest.getId() + " ]  added to your list"), HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    @ApiOperation(value="Add new book to your favorites",notes = "User can add new book to his list of favorites")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Book was deleted"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Book with this id doesn't exist"),
            @ApiResponse(code = 409, message = "Book with this id was already added")}
    )
    public ResponseEntity<SimpleResponse> deleteBook(@PathVariable Long id){
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            favouriteBookService.deleteBookFormList(id, login);
        } catch (NotFound notFound) {
            return new ResponseEntity<>(new SimpleResponse("Book with id [ " + id + " ] wasn't found in list of your books"),
                    HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new SimpleResponse("Book with id [ " + id + " ] deleted from your list"), HttpStatus.OK);
    }






}
