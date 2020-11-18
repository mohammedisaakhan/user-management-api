package com.auth.userManagement.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.auth.userManagement.entity.User;
import com.auth.userManagement.entity.VerificationToken;

@Service
public interface IUserService {
	
	public User registerUser(User user);

	public User findByUsername(String username);

	public void createVerificationToken(User user, String token);

	public VerificationToken getVerificationToken(String verificationToken);

	public void enableRegisteredUser(User user);
	
	public void deActivateRegisteredUser(User user);
	
	public void changePassword(User user);

	void removeVerificationToken(User user);
	
	public List<User> getUsers();

	public User updateUser(User user);

	void deleteUser(Long id);


}
