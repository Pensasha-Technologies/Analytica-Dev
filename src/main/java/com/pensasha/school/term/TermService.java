package com.pensasha.school.term;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TermService {

	@Autowired
	TermRepository termRepository;

	// getting all terms in school by form, year and school
	public List<Term> getAllTerms(int form, int year, int code) {
		return termRepository.findByFormsFormAndFormsYearsYearAndFormsYearsSchoolsCode(form, year, code);
	}

	// get one by in school by form, year and school
	public Term getTerm(int term, int form, int year, int code) {
		return termRepository.findByTermAndFormsFormAndFormsYearsYearAndFormsYearsSchoolsCode(term, form, year, code);
	}

	// Get one term in school
	public Term getOneTerm(int term, int form, int code) {
		return termRepository.findByTermAndFormsFormAndFormsYearsSchoolsCode(term, form, code);
	}

	// Saving a term
	public Term addTerm(Term term) {
		return termRepository.save(term);
	}
}
