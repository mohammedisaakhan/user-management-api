package com.auth.userManagement.dao;

import com.auth.userManagement.entity.User;

public interface IUserDAO {
	
	public User findByUsername(String username);

	public void save(User user);
	
}
