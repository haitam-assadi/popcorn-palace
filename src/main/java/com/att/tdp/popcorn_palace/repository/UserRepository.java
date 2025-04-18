package com.att.tdp.popcorn_palace.repository;

import com.att.tdp.popcorn_palace.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByUsername(String username);
}
