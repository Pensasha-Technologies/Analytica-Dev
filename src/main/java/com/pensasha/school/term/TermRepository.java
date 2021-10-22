package com.pensasha.school.term;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TermRepository extends JpaRepository<Term, Integer> {
    
    List<Term> findByFormsFormAndFormsYearsYearAndFormsYearsSchoolsCode(int var1, int var2, int var3);

    Term findByTermAndFormsFormAndFormsYearsYearAndFormsYearsSchoolsCode(int var1, int var2, int var3, int var4);

    Term findByTermAndFormsFormAndFormsYearsSchoolsCode(int var1, int var2, int var3);

    List<Term> findByExamNamesName(String var1);

}