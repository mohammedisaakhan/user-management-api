package com.auth.userManagement.service;

import java.util.Arrays;
import java.util.logging.Logger;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth.userManagement.dao.IRoleDAO;
import com.auth.userManagement.dao.IUserDAO;
import com.auth.userManagement.dao.TokenDAO;
import com.auth.userManagement.entity.User;
import com.auth.userManagement.entity.VerificationToken;

@Service
public class UserService implements IUserService {

	@Autowired
	private IUserDAO userDAO;
	@Autowired
	private IRoleDAO roleDAO;

	@Autowired
	private TokenDAO tokenDAO;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// logger
	private Logger logger = Logger.getLogger(getClass().getName());

	@Override
	@Transactional
	public User registerUser(User user) {
		String hashedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(hashedPassword);
		user.setRoles(Arrays.asList(roleDAO.findByRoleName("ROLE_USER")));
		userDAO.save(user);
		return user;
	}

	@Override
	@Transactional
	public User findByUsername(String username) {
		return userDAO.findByUsername(username);
	}

	@Override
	public void createVerificationToken(User user, String token) {

		VerificationToken newUserToken = new VerificationToken(token, user);

		tokenDAO.save(newUserToken);
	}

	@Override
	@Transactional
	public VerificationToken getVerificationToken(String verificationToken) {
		return tokenDAO.findByToken(verificationToken);
	}

	@Override
	@Transactional
	public void enableRegisteredUser(User user) {
		userDAO.save(user);
	}

	@Override
	@Transactional
	public void changePassword(User user) {

		String hashedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(hashedPassword);
		userDAO.save(user);
	}
	
	@Override
	@Transactional
	public void removeVerificationToken(User user) {
		
		VerificationToken userToken = tokenDAO.findByUser(user);

		if (userToken != null)
			tokenDAO.remove(userToken);

	}
	

}
