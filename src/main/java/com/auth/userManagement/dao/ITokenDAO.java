package com.auth.userManagement.dao;

import com.auth.userManagement.entity.User;
import com.auth.userManagement.entity.VerificationToken;

public interface ITokenDAO {
	public VerificationToken findByToken(String token);

	public VerificationToken findByUser(User user);

	public void save(VerificationToken token);

	void remove(VerificationToken token);

}
