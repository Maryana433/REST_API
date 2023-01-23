package com.maryana.restspringboot.dto;

import java.util.List;
import java.util.Set;

public class JwtResponse {

    private String token;

    private int id;

    private String username;

    private List<String> roles;


    public JwtResponse(String jwt, int  id, String username, List<String> roles) {
        this.token = jwt;
        this.id = id;
        this.username = username;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
