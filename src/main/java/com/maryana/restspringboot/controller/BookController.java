package com.maryana.restspringboot.controller;

import com.maryana.restspringboot.dto.BookRequest;
import com.maryana.restspringboot.dto.SimpleResponse;
import com.maryana.restspringboot.entity.Book;
import com.maryana.restspringboot.exception_handler.NotFound;
import com.maryana.restspringboot.service.BookService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @ApiOperation(value="Find all books",notes = "Information about books")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of books"),
            @ApiResponse(code = 401, message = "Unauthorized")
    })
        public ResponseEntity<List<Book>> getBooks( @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @RequestParam(required = false) String title,
                                                    @RequestParam(required = false) String author){
        List<Book> books;
        Pageable pageable = PageRequest.of(page, size);
        if(title == null && author == null)
             books = bookService.getBooksPage(pageable).getContent();
        else if(title!=null && author!=null)
            books = bookService.getBooksPageContainingTitleAndAuthorContaining(pageable,title, author).getContent();
        else if(author == null)
            books = bookService.getBooksPageContainingTitle(pageable, title).getContent();
        else
            books = bookService.getBooksPageContainingAuthor(pageable, author).getContent();

        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    @ApiOperation(value="Find book by id",notes = "Information about book with specified id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Information about book with specified id"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Book with this id doesn't exist"),
    })
    public ResponseEntity<Object> getBookById(@PathVariable Long id){
        Book book = null;
        try{
            book = bookService.getBookById(id);
        }catch(NotFound notFound){
            return new ResponseEntity<>(new SimpleResponse("Book with id [ " + id + " ] wasn't found in list of books"),
                    HttpStatus.NOT_FOUND);
        }
       return  ResponseEntity.ok(book);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ApiOperation(value="Add new book",notes = "Admin can add new book to library")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Book was added. Location header contains path to new book"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Error: Forbidden. You are authorized, but you don't have role to access this page")})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Book to add.", required = true,
            content = @Content(schema=@Schema(implementation = Book.class)))
    public ResponseEntity<SimpleResponse> addBook(@RequestBody @Valid BookRequest book){

        Long id = bookService.addNewBookToList(book);

        return ResponseEntity.status(HttpStatus.CREATED).header("Location","http://localhost:8080/api/books/"+id)
                .body(new SimpleResponse("Book author [ " + book.getAuthor() + " ] , title [ " + book.getTitle() + " ] was successfully" +
                " added to library."));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ApiOperation(value="Delete book from library",notes = "Admin can delete book from library")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Book was deleted"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Error: Forbidden. You are authorized, but you don't have role to access this page"),
            @ApiResponse(code = 404, message = "Book with this id doesn't exists")})
    public ResponseEntity<SimpleResponse> deleteBook(@PathVariable Long id){

        try {
            bookService.deleteBook(id);
        }catch(NotFound notFound){
            return new ResponseEntity<>(new SimpleResponse("Book with id [ " + id + " ] wasn't found in library"), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new SimpleResponse("Book with id  [ " + id + " ] was deleted"), HttpStatus.OK);
    }


}
