package com.pensasha.school.student;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, String> {
 
	// finding all students in a school
    List<Student> findBySchoolCodeOrderByAdmNo(int var1);

    // Find a student in school
    Student findByAdmNoAndSchoolCode(String var1, int var2);

    // Find students in school by year and form
    List<Student> findBySchoolCodeAndYearsFormsFormAndYearsYearOrderByAdmNo(int var1, int var2, int var3);

    // Find a student in school by year and form
    Student findByAdmNoAndSchoolCodeAndYearsFormsFormAndYearsYear(String var1, int var2, int var3, int var4);

    // Finding students doing a students
    List<Student> findBySubjectsInitialsOrderByAdmNo(String var1);

    // Checking is a student is in a school
    Boolean existsByAdmNoAndSchoolCode(String var1, int var2);

    // Finding students in school by year, form and term doing a subject
    List<Student> findBySchoolCodeAndYearsYearAndYearsFormsFormAndFormsTermsTermAndSubjectsInitialsOrderByAdmNo(int var1, int var2, int var3, int var4, String var5);

    // Finding students in a school by year, form and term
    List<Student> findBySchoolCodeAndYearsYearAndYearsFormsFormAndYearsFormsTermsTermOrderByAdmNo(int var1, int var2, int var3, int var4);

    // Finding students in a school by year, form, term and stream
    List<Student> findBySchoolCodeAndYearsYearAndYearsFormsFormAndFormsTermsTermAndStreamStreamOrderByAdmNo(int var1, int var2, int var3, int var4, String var5);

    // Finding a student in a school by a year, form, term in a stream doing a subject
    List<Student> findBySchoolCodeAndYearsYearAndYearsFormsFormAndFormsTermsTermAndAndSubjectsInitialsAndStreamIdOrderByAdmNo(int var1, int var2, int var3, int var4, String var5, int var6);

    // Finding a student in a school by form, year and stream
    List<Student> findBySchoolCodeAndFormsYearsYearAndFormsFormAndStreamStreamOrderByAdmNo(int code, int year, int form, String stream);

}