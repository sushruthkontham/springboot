package com.ezc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ezc.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  public Optional<User> findByToken(String token);
  public Optional<User> findByEmail(String email);
}
