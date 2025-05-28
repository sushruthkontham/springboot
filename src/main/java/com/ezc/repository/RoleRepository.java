package com.ezc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ezc.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
  
}
