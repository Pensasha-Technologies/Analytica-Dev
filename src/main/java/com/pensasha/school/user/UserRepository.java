package com.pensasha.school.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

	public List<User> findByRoleName(String role);

	public List<User> findBySchoolCode(int code);
	
}
