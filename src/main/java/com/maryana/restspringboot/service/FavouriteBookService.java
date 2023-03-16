package com.maryana.restspringboot.service;

import com.maryana.restspringboot.dto.BookFavourite;
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
public class FavouriteBookService {

    private final BookRepository bookRepository;
    private final UserService userService;


    @Autowired
    public FavouriteBookService(BookRepository bookRepository, UserService userService) {
        this.bookRepository = bookRepository;
        this.userService = userService;
    }


    public List<Book> getBooksOfUser(String loginOfUser) {
        return new ArrayList<>(userService.getUserBooks(loginOfUser));
    }

    public Book getBooksOfUserById(String login, int id) throws NotFound {
        return getBooksOfUser(login).stream().filter((el)->el.getId() == id).findFirst().orElseThrow(
                ()-> new NotFound("Book with id " + id + " wasn't found in list of your books"));
    }

    public void addBookToUserList(Long bookId, String login) throws NotFound, Conflict {

        Book book = bookRepository.findById(bookId).orElseThrow(
                    () -> new NotFound("Book with id [ + " +bookId + " ] wasn't found in library")
        );

        List<Book> bookListOfUser = getBooksOfUser(login);
        for (Book b : bookListOfUser) {
            if (b.getId().equals(bookId))
                throw new Conflict("Book " + b.getAuthor() + " " + b.getTitle()
                        + " has been already added in your list");
        }

        User user = userService.getUserByLogin(login);
        user.getFavouriteBooks().add(book);
        userService.save(user);
    }

    public void deleteBookFormList(Long id, String login) throws NotFound {

        List<Book> bookListOfUser = getBooksOfUser(login);
        Book book = bookListOfUser.stream().filter(el -> el.getId().equals(id)).findAny().orElseThrow(()->
             new NotFound("Book with id " + id + " wasn't found in list of your books"));

        User user = userService.getUserByLogin(login);
        user.getFavouriteBooks().remove(book);
        userService.save(user);
    }
}
