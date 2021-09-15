package com.pensasha.school.term;

import com.pensasha.school.term.Term;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TermRepository extends JpaRepository<Term, Integer> {
    
    public List<Term> findByFormsFormAndFormsYearsYearAndFormsYearsSchoolsCode(int var1, int var2, int var3);

    public Term findByTermAndFormsFormAndFormsYearsYearAndFormsYearsSchoolsCode(int var1, int var2, int var3, int var4);

    public Term findByTermAndFormsFormAndFormsYearsSchoolsCode(int var1, int var2, int var3);

    public List<Term> findByExamNamesName(String var1);

}