package com.maryana.restspringboot.dto;

import javax.validation.constraints.*;
import java.util.Set;

public class UserRequest {

    @NotBlank(message = "name cannot be empty")
    private String name;

    @NotBlank(message = "surname cannot be empty")
    private String surname;

    @NotEmpty(message = "roles cannot be empty [ available roles: ROLE_USER, ROLE_ADMIN ]")
    @Size(min=1)
    private Set<String> roles;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }


    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
