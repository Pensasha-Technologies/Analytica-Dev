package com.pensasha.school.exam;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MarkRepository extends JpaRepository<Mark, Integer> {

	public List<Mark> findByStudentAdmNoAndYearYearAndFormFormAndTermTermAndSubjectInitials(String admNo, int year, int form,int term, String subject);
	
	public Mark findByStudentAdmNoAndYearYearAndFormFormAndTermTerm(String admNo, int year, int form,
			int term);

	public Boolean existsByStudentAdmNo(String admNo);
	
	public List<Mark> findByStudentAdmNo(String admNo);

	public List<Mark> findByYearYearAndFormFormAndTermTermAndSubjectInitials(int year, int form, int term,
			String subject);

	public List<Mark> findByStudentAdmNoAndFormFormAndTermTerm(String admNo, int form, int term);

	public List<Mark> findByStudentSchoolCodeAndYearYearAndFormFormAndTermTerm(int code, int year, int form, int term);

	public Mark findByStudentAdmNoAndYearYearAndFormFormAndTermTermAndSubjectInitialsAndExamNamesId(String admNo,
			int year, int form, int term, String subject, int id);

	
}
