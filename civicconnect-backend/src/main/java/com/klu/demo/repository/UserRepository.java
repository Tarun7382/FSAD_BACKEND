package com.klu.demo.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.klu.demo.model.User;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameAndRole(String username, String role);

        Optional<User> findByEmail(String email);

    boolean existsByUsernameAndRole(String username, String role);
}
