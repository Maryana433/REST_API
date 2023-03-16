package com.maryana.restspringboot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import static springfox.documentation.builders.PathSelectors.regex;

//http://localhost:8080/swagger-ui/
@Configuration
public class SwaggerConfig {

    public static final String AUTHORIZATION_HEADER = "Authorization";

    private ApiKey apiKey(){
        return new ApiKey("JWT", AUTHORIZATION_HEADER, "header");
    }

    private ApiInfo apiInfo(){
        return new ApiInfo("Book API",
                "REST API.\n" +
                        "AuthController - User can log in and get JWT token or create an account.\n" +
                        "BookController - Every registered user have access to list of books. User can get all books/get book by id. User with role ADMIN can delete book or add new book.\n" +
                        "FavouriteBookController - Every registered user has his own list of favourite books from list of all books. User can get all books/get book by id, delete book or add new book.\n" +
                        "UserController - Users with role ADMIN have access to list of all users. They can get list of users/ user by id, delete user or update user info.",
                "1.0.0",
                "",
                new Contact("Maryana","","marmar72@st.amu.edu.pl"),
                "","", Collections.EMPTY_LIST);
    }

    @Bean
    public Docket api(){
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo())
                .securityContexts(Arrays.asList(securityContext()))
                .securitySchemes(Arrays.asList(apiKey()))
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.maryana.restspringboot.controller"))
                .paths(paths())
                .build();
    }

    private SecurityContext securityContext(){
        return SecurityContext.builder().securityReferences(defaultAuth()).build();
    }

    private List<SecurityReference> defaultAuth(){
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
    }


    private Predicate<String> paths() {
        return regex("/api/auth.*").or(regex("/api/books.*")).or(regex("/api/users.*"));
    }

}