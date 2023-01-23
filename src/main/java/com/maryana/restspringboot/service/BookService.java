package com.maryana.restspringboot.service;

import com.maryana.restspringboot.dto.BookRequest;
import com.maryana.restspringboot.entity.Book;
import com.maryana.restspringboot.entity.User;
import com.maryana.restspringboot.exception_handler.Conflict;
import com.maryana.restspringboot.exception_handler.NotFound;
import com.maryana.restspringboot.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {

    private final BookRepository repository;
    private final UserService userService;

    @Autowired
    public BookService(BookRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }


    public List<Book> getBooks(String loginOfUser) {
        return new ArrayList<>(userService.getUserBooks(loginOfUser));
    }

    public Book getBookById(int id,String login) {
        return getBooks(login).stream().filter((el)->el.getId() == id).findFirst().orElseThrow(
                ()-> new NotFound("Book with id " + id + " wasn't found in list of your books"));
    }

    public int addNewBook(BookRequest book, String login) {
        List<Book> bookList = getBooks(login);
        System.out.println(bookList);
        for(Book book1:bookList){
            if(book1.getTitle().equals(book.getTitle()) && book1.getAuthor().equals(book.getAuthor()))
                throw new Conflict("Book " + book.getAuthor() + " " +book.getTitle()
                        + " has been already added in your list");
        }
        Book book1 = new Book();
        book1.setAuthor(book.getAuthor());
        book1.setTitle(book.getTitle());
        book1.setYearOfPublication(book.getYearOfPublication());
        User user = userService.getUserByLogin(login);
        user.addBook(book1);

        repository.save(book1);
        return book1.getId();
    }

    public void deleteBook(int id, String login) {
        this.getBookById(id,login);
        repository.remove(id);
    }
}
