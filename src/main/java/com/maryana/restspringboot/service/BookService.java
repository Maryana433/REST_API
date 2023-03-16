package com.maryana.restspringboot.service;

import com.maryana.restspringboot.dto.BookRequest;
import com.maryana.restspringboot.entity.Book;
import com.maryana.restspringboot.exception_handler.NotFound;
import com.maryana.restspringboot.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getBooks() {
        return bookRepository.findByIsDeletedFalse();
    }

    public Page<Book> getBooksPage(Pageable pageable) {
        return bookRepository.findByIsDeletedFalse(pageable);
    }

    public Page<Book> getBooksPageContainingTitle(Pageable pageable, String title) {
        return bookRepository.findByIsDeletedFalseAndTitleContaining(title, pageable);
    }

    public Page<Book> getBooksPageContainingAuthor(Pageable pageable, String author) {
        return bookRepository.findByIsDeletedFalseAndAuthorContaining(author, pageable);
    }

    public Page<Book> getBooksPageContainingTitleAndAuthorContaining(Pageable pageable, String title, String author) {
        return bookRepository.findByIsDeletedFalseAndTitleContainingAndAuthorContaining(title,author, pageable);
    }


    public Book getBookById(Long id) throws NotFound {
        return bookRepository.findById(id).orElseThrow(
                ()-> new NotFound("Book with id " + id + " wasn't found in list of books"));
    }

    public Long addNewBookToList(BookRequest bookRequest) {
        Book book = new Book(bookRequest);
        return bookRepository.save(book).getId();
    }


    public void deleteBook(Long id) throws NotFound {
        Book book = this.getBookById(id);
        book.setDeleted(true);
        bookRepository.save(book);
    }

}
