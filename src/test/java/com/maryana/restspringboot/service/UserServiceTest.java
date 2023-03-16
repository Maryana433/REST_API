package com.maryana.restspringboot.service;

import com.maryana.restspringboot.dto.UserRequest;
import com.maryana.restspringboot.entity.Book;
import com.maryana.restspringboot.entity.ERole;
import com.maryana.restspringboot.entity.Role;
import com.maryana.restspringboot.entity.User;
import com.maryana.restspringboot.exception_handler.Conflict;
import com.maryana.restspringboot.exception_handler.NotFound;
import com.maryana.restspringboot.repository.BookRepository;
import com.maryana.restspringboot.repository.RoleRepository;
import com.maryana.restspringboot.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BookRepository bookRepository;

    @BeforeEach
    void init(){
        userRepository = mock(UserRepository.class);
        roleRepository = mock(RoleRepository.class);
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(new Role(ERole.ROLE_USER)));
        when(roleRepository.findByName(ERole.ROLE_ADMIN)).thenReturn(Optional.of(new Role(ERole.ROLE_ADMIN)));
        bookRepository = mock(BookRepository.class);
        userService = new UserService(userRepository, roleRepository, bookRepository);
    }

    @Test
    void testSaveUser(){
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        User user = new User();
        userService.save(user);
        verify(userRepository).save(captor.capture());
        assertSame(user, captor.getValue());
    }


    @Test()
    void testUpdateUserRoleNotExists()  {

        String ROLE_NOT_EXISTS = "NOT_EXISTS_ROLE";
        String login = "login";

        User userFromDb = new User();
        userFromDb.setName("New name");
        userFromDb.setSurname("New SurName");
        userFromDb.setLogin(login);
        userFromDb.setRoles(Set.of(new Role(ERole.ROLE_ADMIN)));

        when(userRepository.findById(1L)).thenReturn(Optional.of((userFromDb)));
        when(userRepository.findByLogin(login)).thenReturn(Optional.of((userFromDb)));

        UserRequest userRequest = UserRequest.builder()
                .name("NAME").surname("SURNAME").roles(Set.of(ROLE_NOT_EXISTS)).build();

        NotFound ex = assertThrows(NotFound.class, () -> userService.updateUser(userRequest, 1L, login));
        assertEquals("Role - " + ROLE_NOT_EXISTS + " doesn't exists", ex.getMessage());
    }

    @Test()
    void testUpdateUser() throws NotFound, Conflict {

        String ADMIN_ROLE = ERole.ROLE_ADMIN.name();

        String old_name = "NAME";
        String new_name = "NEW NAME";
        String old_surname = "SURNAME";
        String new_surname = "NEW SURNAME";
        String login = "login";

        User userFromDb = new User();
        userFromDb.setName(old_name);
        userFromDb.setSurname(old_surname);
        userFromDb.setRoles(Set.of(new Role(ERole.ROLE_USER)));
        userFromDb.setLogin(login);
        userFromDb.setId(1L);

        when(userRepository.findByLogin(login)).thenReturn(Optional.of(userFromDb));
        when(userRepository.findById(1L)).thenReturn(Optional.of(userFromDb));
        when(userRepository.save(userFromDb)).thenReturn(userFromDb);

        UserRequest userRequest = UserRequest.builder()
                .name(new_name).surname(new_surname).roles(Set.of(ADMIN_ROLE)).build();

        userService.updateUser(userRequest, 1L, "login");

        assertEquals(new_name, userFromDb.getName());
        assertEquals(new_surname, userFromDb.getSurname());
        assertTrue(userFromDb.getRoles().stream().anyMatch(el -> el.getName().name().equals(ADMIN_ROLE)));

        verify(userRepository).save(userFromDb);
    }

    @Test
    void userCannotDeleteHisAccount() {

        Long userId = 1L;
        String login = "login";

        User user = new User();
        user.setId(userId);
        user.setLogin(login);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));

        // user try to delete his account
        assertThrows(Conflict.class, () -> userService.deleteUser(userId, login));
    }


    @Test
    void userCanDeleteAccount() throws NotFound, Conflict {

        long userId = 1L;
        long userToDeleteId = 2L;
        String login = "login";

        User user = new User();
        user.setId(userId);
        user.setLogin(login);


        User userToDelete = new User();
        userToDelete.setId(userToDeleteId);
        userToDelete.setLogin(login + ".");

        Book book = new Book();
        userToDelete.setFavouriteBooks(Set.of(book));

        when(userRepository.findById(userToDeleteId)).thenReturn(Optional.of(userToDelete));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));

        userService.deleteUser(2L, login);

        verify(bookRepository).delete(book);
        verify(userRepository).deleteUser(userToDeleteId);

    }




}
