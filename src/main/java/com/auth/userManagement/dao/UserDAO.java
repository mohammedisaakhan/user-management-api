package com.auth.userManagement.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.auth.userManagement.entity.User;
import com.auth.userManagement.repository.UserRepository;

@Repository
public class UserDAO implements IUserDAO {

	@Autowired
	private UserRepository userRepository;

	@Override
	public User findByUsername(String userName) {

		return userRepository.findByUsername(userName);
	}

	@Override
	public void save(User user) {

		if (user == null)
			return;
		userRepository.save(user);
	}
	
	@Override
	public void delete(Long id) {

		if (id == 0)
			return;
		userRepository.deleteById(id);
	}

	@Override
	public List<User> findAll() {
		return userRepository.findAll();
	}

}
