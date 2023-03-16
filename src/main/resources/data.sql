-- run when app start - insert init values -> 2 roles and 1 user with role admin and 1 user with role user
-- login/password to user with role admin - marmar/marmar
-- login/password to user with role user - user/user
INSERT INTO roles (id,name) VALUES
(1,'ROLE_ADMIN'),
(2,'ROLE_USER');

INSERT INTO users(id,name,surname,login,password) VALUES
(1,'marmar', 'marmar', 'marmar', '$2a$12$8fHklVGDDcIm/xoW6syC0uFIuMXPzHMAIIoxGXfamO5mJr1zdAqI.');

INSERT INTO users(id,name,surname,login,password) VALUES
(2,'user', 'user', 'user', '$2a$10$hYPuXH3cTQazhaiwez4TtOYUUdufOoW/QOD2W6eRSQ9EtdjTGqSEm');

INSERT INTO user_roles (user_id, role_id) VALUES
(1,1);

INSERT INTO user_roles (user_id, role_id) VALUES
(2,2);

INSERT INTO book (title, author, is_deleted) VALUES
('Wprowadzenie do teorii obliczeń', 'Michał Sipser', false),
('Systemy operacyjne', 'Tanenbaum', false ),
('The Master and Margarita', 'Mikhail Bulgakov', false);



