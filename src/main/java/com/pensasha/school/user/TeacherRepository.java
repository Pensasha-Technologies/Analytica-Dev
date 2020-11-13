package com.pensasha.school.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, String>{

	public Teacher findByUsername(String username);
}
