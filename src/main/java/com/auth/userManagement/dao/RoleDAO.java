package com.auth.userManagement.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.auth.userManagement.entity.Role;
import com.auth.userManagement.repository.RoleRepository;

@Repository
public class RoleDAO implements IRoleDAO {

	@Autowired
	private RoleRepository roleRepository;

	@Override
	public Role findByRoleName(String roleName) {

		return roleRepository.findByRolename(roleName);
	}

	@Override
	public void save(Role role) {

		if (role == null)
			return;
		roleRepository.save(role);
	}

}
