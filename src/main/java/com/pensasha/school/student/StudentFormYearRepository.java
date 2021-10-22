package com.pensasha.school.student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentFormYearRepository extends JpaRepository<StudentFormYear, Long>  {

	void deleteByStudentAdmNo(String admNo);

	List<StudentFormYear> findByStudentAdmNo(String admNo);

	List<StudentFormYear> findByStudentSchoolCodeAndYearYearAndFormFormAndStudentStreamStream(int code, int year,
			int form, String stream);

	List<StudentFormYear> findByStudentSchoolCodeAndYearYearAndFormFormAndFormTermsTermAndStudentSubjectsInitialsAndStudentStreamId(
			int code, int year, int form, int term, String initials, int stream);

	List<StudentFormYear> findByStudentSchoolCode(int code);

	List<StudentFormYear> findByStudentSchoolCodeAndYearYearAndFormFormAndFormTermsTerm(int code, int year, int form,
			int term);

	List<StudentFormYear> findByStudentSchoolCodeAndYearYearAndFormFormAndFormTermsTermAndStudentStreamStream(int code,
			int year, int form, int term, String stream);

}
