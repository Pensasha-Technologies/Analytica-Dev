package com.pensasha.school.student;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, String> {

	public List<Student> findBySchoolCode(int code);

	public Student findByAdmNoAndSchoolCode(String admNo, int code);

	public List<Student> findBySchoolCodeAndFormsFormAndYearsYear(int code, int form, int year);

	public List<Student> findBySchoolCodeAndFormsFormAndYearsYearAndStreamStream(int code, int form, int year, String stream);
	
	public Student findByAdmNoAndSchoolCodeAndFormsFormAndYearsYear(String admNo, int code, int form, int year);

	public List<Student> findBySubjectsInitials(String initials);
	
	public Boolean existsByAdmNoAndSchoolCode(String admNo, int code);
	
	public List<Student> findBySchoolCodeAndYearsYearAndFormsFormAndFormsTermsTermAndSubjectsInitials(int code, int year, int form, int term, String initials);

	public List<Student> findBySchoolCodeAndYearsYearAndFormsFormAndFormsTermsTerm(int code, int year, int form, int term);

}
