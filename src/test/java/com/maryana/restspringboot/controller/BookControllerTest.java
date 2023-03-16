package com.maryana.restspringboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maryana.restspringboot.entity.Book;
import com.maryana.restspringboot.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

;


@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

    }


    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void shouldReturnStatusOkAuthorizedUserGetBooks() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/api/books"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void shouldReturnNotFoundThanNoBookWithThisId() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/api/books/-1"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void shouldReturnForbiddenThanUserWithoutRoleAdminTryToAddBook() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/api/books").contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(new Book(1L,"Title", "Author"))))
                        .andDo(print())
                        .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    public void shouldEnableAdminToAddNewBookAndReturnLocationHeader() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/api/books").contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(new Book("Title", "Author"))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void shouldReturnForbiddenThanUserWithoutRoleAdminTryToDeleteBook() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.delete("/api/books/-1"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }


    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



}
