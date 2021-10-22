package com.pensasha.school.exam;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamNameRepository extends JpaRepository<ExamName, Integer> {
    
    List<ExamName> findBySchoolsCodeAndYearsYearAndFormsForm(int var1, int var2, int var3);

    List<ExamName> findBySchoolsCodeAndYearsYear(int var1, int var2);

    List<ExamName> findBySchoolsCodeAndYearsYearAndFormsFormAndTermsTerm(int var1, int var2, int var3, int var4);

    ExamName findByIdAndSchoolsCodeAndYearsYearAndFormsFormAndTermsTerm(int var1, int var2, int var3, int var4, int var5);

    ExamName findByName(String var1);

    List<ExamName> findBySchoolsCode(int var1);

    List<ExamName> findBySchoolsCodeAndYearsYearAndFormsFormAndTermsTermAndName(int var1, int var2, int var3, int var4, String var5);

    ExamName findBySchoolsCodeAndYearsYearAndFormsFormAndTermsTermAndSubjectInitials(int var1, int var2, int var3, int var4, String var5);

}