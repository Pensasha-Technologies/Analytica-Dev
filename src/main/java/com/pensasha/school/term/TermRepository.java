package com.pensasha.school.term;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TermRepository extends JpaRepository<Term, Integer> {

	public List<Term> findByFormsFormAndFormsYearsYearAndFormsYearsSchoolsCode(int form, int year, int code);

	public Term findByTermAndFormsFormAndFormsYearsYearAndFormsYearsSchoolsCode(int term, int form, int year, int code);

	public Term findByTermAndFormsFormAndFormsYearsSchoolsCode(int term, int form, int code);
}
