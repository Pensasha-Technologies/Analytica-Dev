package com.pensasha.school.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, String>{

	public Teacher findByUsername(String username);

	public List<Teacher> findBySubjectsInitials(String initials);

	public List<Teacher> findBySchoolCodeAndYearsYear(int code, int year);

	public List<Teacher> findBySchoolCodeAndYearsYearAndFormsFormAndStreamsId(int code, int year, int form, int stream);

	public List<Teacher> findBySchoolCode(int code);

}
