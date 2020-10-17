package com.auth.userManagement.dao;

import com.auth.userManagement.entity.Role;

public interface IRoleDAO {
	public Role findByRoleName(String role);

	public void save(Role role);
}
