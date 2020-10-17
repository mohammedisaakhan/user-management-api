package com.auth.userManagement.service;

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

	public void changePassword(User user);

	void removeVerificationToken(User user);
}
