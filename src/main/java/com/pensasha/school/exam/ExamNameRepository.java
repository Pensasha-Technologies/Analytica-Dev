package com.pensasha.school.exam;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamNameRepository extends JpaRepository<ExamName, Integer> {
    
    public List<ExamName> findBySchoolsCodeAndYearsYearAndFormsForm(int var1, int var2, int var3);

    public List<ExamName> findBySchoolsCodeAndYearsYear(int var1, int var2);

    public List<ExamName> findBySchoolsCodeAndYearsYearAndFormsFormAndTermsTerm(int var1, int var2, int var3, int var4);

    public ExamName findByIdAndSchoolsCodeAndYearsYearAndFormsFormAndTermsTerm(int var1, int var2, int var3, int var4, int var5);

    public ExamName findByName(String var1);

    public List<ExamName> findBySchoolsCode(int var1);

    public List<ExamName> findBySchoolsCodeAndYearsYearAndFormsFormAndTermsTermAndName(int var1, int var2, int var3, int var4, String var5);

    public ExamName findBySchoolsCodeAndYearsYearAndFormsFormAndTermsTermAndSubjectInitials(int var1, int var2, int var3, int var4, String var5);

}