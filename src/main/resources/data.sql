-- run when app start - insert init values -> 2 roles and 1 user with role admin
-- login/password to user with role admin - marmar/marmar
INSERT INTO roles (id,name) VALUES
(1,'ROLE_ADMIN'),
(2,'ROLE_USER');
INSERT INTO users(id,name,surname,login,password) VALUES
(1,'marmar', 'marmar', 'marmar', '$2a$12$8fHklVGDDcIm/xoW6syC0uFIuMXPzHMAIIoxGXfamO5mJr1zdAqI.');
INSERT INTO user_roles (user_id, role_id) VALUES
(1,1);