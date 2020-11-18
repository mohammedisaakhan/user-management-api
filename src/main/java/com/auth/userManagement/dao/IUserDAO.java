package com.auth.userManagement.dao;

import java.util.List;

import com.auth.userManagement.entity.User;

public interface IUserDAO {
	
	public User findByUsername(String username);

	public void save(User user);
	
	public List<User> findAll();
	
	public void delete(Long id);
	
}
