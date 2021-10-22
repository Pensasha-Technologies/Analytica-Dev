package com.pensasha.school.exam;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarkRepository extends JpaRepository<Mark, Integer> {
    
    List<Mark> findByStudentAdmNoAndYearYearAndFormFormAndTermTermAndSubjectInitials(String var1, int var2, int var3, int var4, String var5);

    Mark findByStudentAdmNoAndYearYearAndFormFormAndTermTerm(String var1, int var2, int var3, int var4);

    Boolean existsByStudentAdmNo(String var1);

    List<Mark> findByStudentAdmNo(String var1);

    List<Mark> findByYearYearAndFormFormAndTermTermAndSubjectInitials(int var1, int var2, int var3, String var4);

    List<Mark> findByStudentAdmNoAndFormFormAndTermTerm(String var1, int var2, int var3);

    List<Mark> findByStudentSchoolCodeAndYearYearAndFormFormAndTermTerm(int var1, int var2, int var3, int var4);

    Mark findByStudentAdmNoAndYearYearAndFormFormAndTermTermAndSubjectInitialsAndExamNameId(String var1, int var2, int var3, int var4, String var5, int var6);

    List<Mark> findByStudentSchoolCodeAndYearYearAndFormFormAndTermTermAndExamNameName(int var1, int var2, int var3, int var4, String var5);

}