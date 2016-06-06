package com.soze.user.repository;

import org.springframework.data.repository.CrudRepository;

import com.soze.user.model.User;

public interface UserRepository extends CrudRepository<User, String> {

}
