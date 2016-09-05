package com.soze.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soze.user.model.User;

public interface UserRepository extends JpaRepository<User, String> {

}
