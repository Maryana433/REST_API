package com.maryana.restspringboot.repository;

import com.maryana.restspringboot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Modifying
    @Transactional
    @Query("delete from User u where u.id=:idUser")
    void deleteUser(@Param("idUser") int id);


    Optional<User> findByLogin(String login);

    Boolean existsByLogin(String login);

}
