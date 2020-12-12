package com.pensasha.school.exam;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamNameRepository extends JpaRepository<ExamName, Integer>{

	List<ExamName> findBySchoolsCodeAndYearsYearAndFormsForm(int code, int year, int form);

	List<ExamName> findBySchoolsCodeAndYearsYear(int code, int year);

	List<ExamName> findBySchoolsCodeAndYearsYearAndFormsFormAndTermsTerm(int code, int year, int form, int term);

	ExamName findByIdAndSchoolsCodeAndYearsYearAndFormsFormAndTermsTerm(int id, int code, int year, int form,
			int term);

	ExamName findByName(String name);

}
