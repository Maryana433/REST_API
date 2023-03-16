package com.maryana.restspringboot.service;

import com.maryana.restspringboot.dto.UserRequest;
import com.maryana.restspringboot.dto.UserResponse;
import com.maryana.restspringboot.entity.Book;
import com.maryana.restspringboot.entity.ERole;
import com.maryana.restspringboot.entity.Role;
import com.maryana.restspringboot.entity.User;
import com.maryana.restspringboot.exception_handler.Conflict;
import com.maryana.restspringboot.exception_handler.NotFound;
import com.maryana.restspringboot.repository.BookRepository;
import com.maryana.restspringboot.repository.RoleRepository;
import com.maryana.restspringboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BookRepository bookRepository;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bookRepository = bookRepository;
    }

    public List<UserResponse> findAllUsers(){
        return userRepository.findAll().stream().map(UserResponse::new).
                collect(Collectors.toList());
    }

    public UserResponse findUserById(Long id) throws NotFound {
        return new UserResponse(userRepository.findById(id).orElseThrow(()->
                new NotFound("User with id "+id+" not found . ")));
    }

    private User saveUser(User user){
        return userRepository.save(user);
    }

    public UserResponse updateUser(UserRequest user, Long id, String login) throws NotFound, Conflict {

        // get User from DB
        User userDB = userRepository.findById(id).orElseThrow(()->
                new NotFound("User with id : "+id+" not found . "));

        Long userId = userRepository.findByLogin(login).get().getId();

        if(id.equals(userId) && !user.getRoles().contains("ROLE_ADMIN")){
            throw new Conflict("You cannot delete ROLE_ADMIN from your role list");
        }

        // update
        userDB.setName(user.getName());
        userDB.setSurname(user.getSurname());

        // set role
        Role roleBD = null;
        Set<String> rolesFromRequest = user.getRoles();
        userDB.setRoles(new HashSet<>());
        for(String role:rolesFromRequest) {
                if (ERole.ROLE_USER.name().equals(role)) {
                    roleBD = roleRepository.findByName(ERole.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                } else if (ERole.ROLE_ADMIN.name().equals(role)) {
                    roleBD = roleRepository.findByName(ERole.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                }else{
                    throw new NotFound("Role - " + role + " doesn't exists");
                }
                userDB.addRole(roleBD);
        }

        //save
        User updatedUser = this.saveUser(userDB);

        return new UserResponse(updatedUser);
    }

    public void deleteUser(Long id, String userLogin) throws NotFound, Conflict {

        Long userId = userRepository.findByLogin(userLogin).get().getId();

        if(userId.equals(id)){
            throw new Conflict("You cannot delete your account");
        }

        if(userRepository.findById(id).isEmpty()){
            throw new NotFound("User with id : "+id+" not found . ");
        }


        // first delete all resources of user
        for(Book book: userRepository.findById(id).get().getFavouriteBooks()){
            bookRepository.delete(book);
        }

        // then delete user
        userRepository.deleteUser(id);
    }

    public User getUserByLogin(String login) throws NotFound {
        return userRepository.findByLogin(login).orElseThrow( ()->
                new NotFound("User with login [ "+login+" ] not found . "));
    }

    public Set<Book> getUserBooks(String loginOfUser) {
        Set<Book> books = null;
        try{
            books = getUserByLogin(loginOfUser).getFavouriteBooks();
        }catch (NotFound n){
            throw new RuntimeException(n);
        }
        return books;
    }

    public void save(User user) {
        userRepository.save(user);
    }
}
