package com.maryana.restspringboot.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="users")
public class User {


    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "login")
    private String login;

    @Column(name = "password")
    private String password;

    @ManyToMany(fetch = FetchType.LAZY,cascade = CascadeType.PERSIST)
    @JoinTable(	name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY,cascade = CascadeType.PERSIST)
    @JoinTable(	name = "user_books",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id"))
    private Set<Book> favouriteBooks = new HashSet<>();

    public void addRole(Role role){
        if (role != null)
            roles.add(role);
    }

    public User(){
    }

    public User(String login, String password,String firstName,String lastName){
        this.login = login;
        this.password = password;
        this.name = firstName;
        this.surname = lastName;
    }

    public User(Long id,String name, String surname, String login,String password) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.login = login;
        this.password=password;
    }

    @Override
    public String toString() {
        return "Test{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", login='" + login + '\'' +
                '}';
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Set<Book> getFavouriteBooks() {
        return favouriteBooks;
    }

    public void setFavouriteBooks(Set<Book> favouriteBooks) {
        this.favouriteBooks = favouriteBooks;
    }

    public void addBook(Book book){
        this.favouriteBooks.add(book);
    }
}
