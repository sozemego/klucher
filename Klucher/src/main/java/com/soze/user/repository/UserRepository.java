package com.soze.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soze.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	public User findOneByUsername(String username);

	public List<User> findAllByUsernameIn(Iterable<String> usernames);

}
