package com.maryana.restspringboot.service;

import com.maryana.restspringboot.entity.Book;
import com.maryana.restspringboot.exception_handler.NotFound;
import com.maryana.restspringboot.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class BookServiceTest {


    private BookRepository bookRepository;
    private BookService bookService;

    @BeforeEach
    void init(){
        bookRepository = mock(BookRepository.class);
        bookService = new BookService(bookRepository);
    }


    @Test
    void testDeleteBookFromList() throws NotFound {

        Long id = 1L;
        Book book = new Book();
        book.setId(id);
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        bookService.deleteBook(id);
        assertTrue(book.isDeleted());
        verify(bookRepository).save(book);
    }
}
