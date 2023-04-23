# REST_API
Books Rest API

API umożliwia rejestrację/logowanie. Korzysta z JWT. 
Ma testowego użytkownika z rolą ADMIN - login/hasło - marmar/marmar.
Swagger UI umożliwia dodanie headera z tokenem do każdego zapytania. Token można otrzymać po rejestracji
użytkownika przy logowaniu. Trzeba w Authorize dodać "Bearer otrzymany_token".

Api umożliwia zarejestrowanemu użytkownikowi pobranie listy książek (z możliwością filtrowania)/książki po id, dodanie książki
do swojej listy/usunięcie książki z listy.
Użytkownik z rolą ADMIN dodatkowo ma dostęp do listy użytkowników, może zmienić dane użytkownika, usunąc użytkownika
i dodatkowo może dodawać książki do wspólnej listy.

GCP : https://book-rest-api-2.lm.r.appspot.com
Dokumentacja Swagger UI: https://book-rest-api-2.lm.r.appspot.com/swagger-ui/

