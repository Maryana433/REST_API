-- run when app start - create tables
CREATE TABLE if not exists users (
                                     id long NOT NULL AUTO_INCREMENT ,
                                     name varchar(30),
                                     surname varchar(30),
                                     data_of_birth Date,
                                     login varchar(30),
                                     PRIMARY KEY (id)
);


CREATE TABLE if not exists roles(
                    id int NOT NULL PRIMARY KEY AUTO_INCREMENT ,
                    name varchar(40)
    );


CREATE TABLE if not exists user_roles(
    user_id long REFERENCES users(id),
    role_id int REFERENCES roles(id),
    PRIMARY KEY (user_id,role_id)
);


CREATE TABLE if not exists book(
    id long NOT NULL PRIMARY KEY AUTO_INCREMENT,
    title varchar(50),
    author varchar(50),
    is_deleted bool
);

CREATE TABLE if not exists user_books(
    user_id long REFERENCES users(id),
    book_id long REFERENCES book(id),
    PRIMARY KEY (user_id, book_id)
)

