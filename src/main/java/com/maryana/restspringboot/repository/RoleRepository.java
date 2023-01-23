package com.maryana.restspringboot.repository;


import com.maryana.restspringboot.entity.ERole;
import com.maryana.restspringboot.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(ERole name);




}