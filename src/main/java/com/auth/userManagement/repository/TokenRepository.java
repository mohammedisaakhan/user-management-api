package com.auth.userManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auth.userManagement.entity.VerificationToken;

public interface TokenRepository extends JpaRepository<VerificationToken, Long> {

	public VerificationToken findByToken(String token);

	public VerificationToken findByUserId(int userId);

}
