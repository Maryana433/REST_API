package com.maryana.restspringboot.service;

import com.maryana.restspringboot.entity.Book;
import com.maryana.restspringboot.entity.User;
import com.maryana.restspringboot.exception_handler.Conflict;
import com.maryana.restspringboot.exception_handler.NotFound;
import com.maryana.restspringboot.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


public class FavouriteBookServiceTest {

    private BookRepository bookRepository;
    private UserService userService;
    private FavouriteBookService favouriteBookService;

    @BeforeEach
    void init(){
        bookRepository = mock(BookRepository.class);
        userService = mock(UserService.class);
        favouriteBookService = new FavouriteBookService(bookRepository, userService);
    }


    @Test
    void testAddBookToUserListNotFound() {
        Long bookId = 1L;
        String login = "USER";
        NotFound n = assertThrows(NotFound.class, () -> favouriteBookService.addBookToUserList(bookId, login));
        assertEquals(n.getMessage(), "Book with id [ + " +bookId + " ] wasn't found in library");

        verify(bookRepository).findById(bookId);
        verifyNoMoreInteractions(bookRepository);
    }


    @Test
    void testAddBookToUserListConflict(){

        Long bookId = 1L;
        String login = "USER";
        Book bookAlreadyAdded = new Book(bookId, "Book title", "Book author");

        when(userService.getUserBooks(login)).thenReturn(Set.of(bookAlreadyAdded));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookAlreadyAdded));

        assertThrows(Conflict.class, () -> favouriteBookService.addBookToUserList(bookId, login));

        verify(bookRepository).findById(bookId);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    void testAddBookToUserList() throws Conflict, NotFound {
        Long bookId = 1L;
        String login = "USER";
        Book bookToAdd = new Book(bookId, "Book title", "Book author");

        Set<Book> usersBooks = new HashSet<>();
        Book bookInUserList = new Book(bookId+1, "Another title", "Another author");
        usersBooks.add(bookInUserList);
        User user = new User(login, "pass", "first", "last");
        user.setFavouriteBooks(usersBooks);

        when(userService.getUserByLogin(login)).thenReturn(user);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookToAdd));


        favouriteBookService.addBookToUserList(bookId, login);


        verify(bookRepository).findById(bookId);
        verifyNoMoreInteractions(bookRepository);

        verify(userService).save(user);

        Set<Book> books = Set.of(bookToAdd, bookInUserList);
        assertEquals(books,user.getFavouriteBooks());
    }



    @Test
    void deleteBookFromUsersListNotFound(){
        Long bookId = 1L;
        String login = "USER";

        Set<Book> usersBooks = new HashSet<>();
        Book bookInUserList = new Book(bookId + 1, "Book title", "Book author");
        usersBooks.add(bookInUserList);
        User user = new User(login, "pass", "first", "last");
        user.setFavouriteBooks(usersBooks);

        assertThrows(NotFound.class, () -> favouriteBookService.deleteBookFormList(bookId, login));
        verifyNoInteractions(bookRepository);
    }


    @Test
    void deleteBookFromUsersList() throws NotFound {
        Long bookId = 1L;
        String login = "USER";

        Set<Book> usersBooks = new HashSet<>();
        Book bookInUserList = new Book(bookId, "Book title", "Book author");
        usersBooks.add(bookInUserList);
        User user = new User(login, "pass", "first", "last");
        user.setFavouriteBooks(usersBooks);
        when(userService.getUserBooks(login)).thenReturn(usersBooks);
        when(userService.getUserByLogin(login)).thenReturn(user);

        favouriteBookService.deleteBookFormList(bookId, login);

        assertEquals(Collections.emptySet(), user.getFavouriteBooks());
        verify(userService).save(user);
        verifyNoInteractions(bookRepository);
    }

}
