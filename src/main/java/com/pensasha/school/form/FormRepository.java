package com.pensasha.school.form;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FormRepository extends JpaRepository<Form, Integer> {

	public List<Form> findByYearsYearAndYearsSchoolsCode(int year, int code);

	public Optional<Form> findByFormAndYearsYearAndYearsSchoolsCode(int form, int year, int code);

	public List<Form> findByStudentsAdmNo(String admNo);

	public List<Form> findBySubjectsInitials(String initials);

	public Boolean existsByFormAndStudentsAdmNo(int form, String admNo);

	public Form findByFormAndStudentsAdmNo(int form, String admNo);
	
	public Form findByForm(int form);

}
