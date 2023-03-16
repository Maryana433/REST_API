package com.maryana.restspringboot.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.Set;

@Builder
@Getter
@Setter
public class UserRequest {

    @NotBlank(message = "name cannot be empty")
    private String name;

    @NotBlank(message = "surname cannot be empty")
    private String surname;

    @NotEmpty(message = "roles cannot be empty [ available roles: ROLE_USER, ROLE_ADMIN ]")
    @Size(min=1)
    private Set<String> roles;

}
