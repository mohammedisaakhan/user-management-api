package com.auth.userManagement.repository;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;

import com.auth.userManagement.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	@QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
	public User findByUsername(String name);

	@QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
	public User findByUsernameAndPassword(String username, String Password);
}
