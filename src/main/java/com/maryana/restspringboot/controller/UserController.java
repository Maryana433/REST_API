package com.maryana.restspringboot.controller;

import com.maryana.restspringboot.dto.SimpleResponse;
import com.maryana.restspringboot.dto.UserRequest;
import com.maryana.restspringboot.dto.UserResponse;
import com.maryana.restspringboot.exception_handler.Conflict;
import com.maryana.restspringboot.exception_handler.NotFound;
import com.maryana.restspringboot.service.UserService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    @ApiOperation(value="Find all users",notes = "Information about users")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Users were successfully found"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden. Role ADMIN is required")})
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        List<UserResponse> userResponseList = userService.findAllUsers();
        return new ResponseEntity<>(userResponseList, HttpStatus.OK);

    }

    @GetMapping("/{id}")
    @ApiOperation(value="Find user by id",notes = "Information about user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User was found"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden. Role ADMIN is required"),
            @ApiResponse(code = 404, message = "User with this id doesn't exist"),})
    public HttpEntity<? extends Object> getUserById(@ApiParam(value = "unique id of user",example = "1")
                                                @PathVariable Long id)  {

        UserResponse user = null;
        try {
            user = userService.findUserById(id);
        }catch (NotFound e){
            return new ResponseEntity<>(new SimpleResponse("User with id [ "+id+" ] not found ."), HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(user);
    }


    @PutMapping("/{id}")
    @ApiOperation(value="Update user info", notes = "roles available - ROLE_USER and ROLE_ADMIN")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User info was successfully changed"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden. Role ADMIN is required"),
            @ApiResponse(code = 404, message = "User with this id doesn't exist"),})
    public ResponseEntity<? extends Object> updateUser(@PathVariable  Long id , @Valid @RequestBody UserRequest user){

        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        UserResponse updatedUser = null;
        try {
            updatedUser = userService.updateUser(user, id, login);
        }catch (NotFound e){
            return new ResponseEntity<>(new SimpleResponse("User with id [ "+id+" ] not found or role not found. Available roles - ROLE_USER, ROLE_ADMIN"), HttpStatus.NOT_FOUND);
        }catch (Conflict c){
            return new ResponseEntity<>(new SimpleResponse("You cannot delete ROLE_ADMIN from your role list"),
                    HttpStatus.CONFLICT);
        }
        return ResponseEntity.ok(updatedUser);
    }


    @DeleteMapping("/{id}")
    @ApiOperation(value="Delete user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User was deleted"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden. Role ADMIN is required"),
            @ApiResponse(code = 404, message = "User with this id doesn't exist"),
            @ApiResponse(code = 409, message = "You cannot delete your account")})
    public ResponseEntity<? extends Object> deleteUser(@PathVariable Long id){

        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            userService.deleteUser(id, login);
        }catch (NotFound e){
            return new ResponseEntity<>(new SimpleResponse("User with id [ "+id+" ] not found ."), HttpStatus.NOT_FOUND);
        }catch (Conflict c){
            return new ResponseEntity<>(new SimpleResponse("You cannot delete your account"),
                    HttpStatus.CONFLICT);
        }

        return ResponseEntity.ok("User with id = "+id+" was deleted");
    }


}
