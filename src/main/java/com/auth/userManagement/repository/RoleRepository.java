package com.auth.userManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auth.userManagement.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

	public Role findByRolename(String name);

}
