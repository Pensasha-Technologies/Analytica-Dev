package com.pensasha.school.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolUserRepository extends JpaRepository<SchoolUser, String>{

	public List<SchoolUser> findBySchoolCode(int code);
}
