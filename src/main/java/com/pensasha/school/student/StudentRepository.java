package com.pensasha.school.student;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, String> {
 
    public List<Student> findBySchoolCode(int var1);

    public Student findByAdmNoAndSchoolCode(String var1, int var2);

    public List<Student> findBySchoolCodeAndFormsFormAndYearsYear(int var1, int var2, int var3);

    public List<Student> findBySchoolCodeAndFormsFormAndYearsYearAndStreamStream(int var1, int var2, int var3, String var4);

    public Student findByAdmNoAndSchoolCodeAndFormsFormAndYearsYear(String var1, int var2, int var3, int var4);

    public List<Student> findBySubjectsInitials(String var1);

    public Boolean existsByAdmNoAndSchoolCode(String var1, int var2);

    public List<Student> findBySchoolCodeAndYearsYearAndFormsFormAndFormsTermsTermAndSubjectsInitials(int var1, int var2, int var3, int var4, String var5);

    public List<Student> findBySchoolCodeAndYearsYearAndFormsFormAndFormsTermsTerm(int var1, int var2, int var3, int var4);

    public List<Student> findBySchoolCodeAndYearsYearAndFormsFormAndFormsTermsTermAndStreamStream(int var1, int var2, int var3, int var4, String var5);

    public List<Student> findBySchoolCodeAndYearsYearAndFormsFormAndFormsTermsTermAndAndSubjectsInitialsAndStreamId(int var1, int var2, int var3, int var4, String var5, int var6);

}