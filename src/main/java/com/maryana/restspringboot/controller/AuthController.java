package com.maryana.restspringboot.controller;

import com.maryana.restspringboot.dto.SignupRequest;
import com.maryana.restspringboot.dto.SimpleResponse;
import com.maryana.restspringboot.entity.ERole;
import com.maryana.restspringboot.entity.Role;
import com.maryana.restspringboot.entity.User;
import com.maryana.restspringboot.repository.RoleRepository;
import com.maryana.restspringboot.repository.UserRepository;
import com.maryana.restspringboot.security.JwtUtils;
import com.maryana.restspringboot.security.UserDetailsImpl;
import com.maryana.restspringboot.dto.JwtResponse;
import com.maryana.restspringboot.dto.LoginRequest;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/signup")
    @ApiOperation(value="Create an account",notes = "User can create an account")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User successfully registered"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 409, message = "Login is already taken")})
    public ResponseEntity<?> registerUser(@RequestBody @Valid SignupRequest signUpRequest) {
        if (userRepository.existsByLogin(signUpRequest.getLogin())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new SimpleResponse("Error: Login ["+ signUpRequest.getLogin() +"] is already taken."));
        }

        // Create new user's account
        User user = new User(signUpRequest.getLogin(),
                encoder.encode(signUpRequest.getPassword()),signUpRequest.getFirstName(),signUpRequest.getLastName());

        // Set role to ROLE_USER
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        roles.add(userRole);
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new SimpleResponse("User [ login - " + signUpRequest.getLogin() + " ] registered successfully"));
    }



    @PostMapping("/signin")
    @ApiOperation(value="Login",notes = "User can authenticate and get JWT Token")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User successfully authenticated"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Login doesn't exist")})
    public ResponseEntity<?> authenticateUser(@RequestBody @Valid LoginRequest loginRequest) {

        if (!userRepository.existsByLogin(loginRequest.getLogin())) {
            return new ResponseEntity<>(
                    new SimpleResponse("Error: User with login [" + loginRequest.getLogin() + "] doesn't exist"), HttpStatus.UNAUTHORIZED);
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getLogin(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                roles));
    }
}
