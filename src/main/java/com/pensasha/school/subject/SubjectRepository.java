package com.pensasha.school.subject;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, String> {

	public List<Subject> findBySchoolsCode(int code);

	public Subject findByInitialsAndSchoolsCode(String initials, int code);

	public List<Subject> findByStudentsAdmNo(String admNo);

	public Subject findByInitialsAndStudentsAdmNo(String initials, String admNo);

	public List<Subject> findByFormsFormAndStudentsAdmNo(int form, String admNo);

	public List<Subject> findByYearsYearAndFormsFormAndStudentsAdmNo(int year, int form, String admNo);
	
	public Boolean existsByInitialsAndSchoolsCode(String initials, int code);
	
	public Subject findByName(String name);
	

}
