package com.pensasha.school.student;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, String> {
 
	// finding all students in a school
    public List<Student> findBySchoolCode(int var1);

    // Find a student in school
    public Student findByAdmNoAndSchoolCode(String var1, int var2);

    // Find students in school by year and form
    public List<Student> findBySchoolCodeAndYearsFormsFormAndYearsYear(int var1, int var2, int var3);

    // Find a student in school by year and form
    public Student findByAdmNoAndSchoolCodeAndYearsFormsFormAndYearsYear(String var1, int var2, int var3, int var4);

    // Finding students doing a students
    public List<Student> findBySubjectsInitials(String var1);

    // Checking is a student is in a school
    public Boolean existsByAdmNoAndSchoolCode(String var1, int var2);

    // Finding students in school by year, form and term doing a subject
    public List<Student> findBySchoolCodeAndYearsYearAndYearsFormsFormAndFormsTermsTermAndSubjectsInitials(int var1, int var2, int var3, int var4, String var5);

    // Finding students in a school by year, form and term
    public List<Student> findBySchoolCodeAndYearsYearAndYearsFormsFormAndYearsFormsTermsTerm(int var1, int var2, int var3, int var4);

    // Finding students in a school by year, form, term and stream
    public List<Student> findBySchoolCodeAndYearsYearAndYearsFormsFormAndFormsTermsTermAndStreamStream(int var1, int var2, int var3, int var4, String var5);

    // Finding a student in a school by a year, form, term in a stream doing a subject
    public List<Student> findBySchoolCodeAndYearsYearAndYearsFormsFormAndFormsTermsTermAndAndSubjectsInitialsAndStreamId(int var1, int var2, int var3, int var4, String var5, int var6);

    // Finding a student in a school by form, year and stream
	public List<Student> findBySchoolCodeAndFormsYearsYearAndFormsFormAndStreamStream(int code, int year, int form, String stream);

}