package com.auth.userManagement.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.auth.userManagement.entity.User;
import com.auth.userManagement.entity.VerificationToken;
import com.auth.userManagement.repository.TokenRepository;

@Repository
public class TokenDAO implements ITokenDAO {

	@Autowired
	private TokenRepository tokenRepository;

	@Override
	public VerificationToken findByToken(String token) {
		return tokenRepository.findByToken(token);
	}

	@Override
	public VerificationToken findByUser(User user) {
		return tokenRepository.findByUserId(user.getId());
	}

	@Override
	public void remove(VerificationToken token) {
		tokenRepository.delete(token);
	}

	@Override
	public void save(VerificationToken token) {
		if (token == null)
			return;
		tokenRepository.save(token);

	}

}
