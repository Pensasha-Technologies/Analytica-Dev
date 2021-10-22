package com.pensasha.school.term;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TermService {
    @Autowired
    TermRepository termRepository;

    public List<Term> getAllTerms(int form, int year, int code) {
        return this.termRepository.findByFormsFormAndFormsYearsYearAndFormsYearsSchoolsCode(form, year, code);
    }

    public List<Term> getAllTermByExamName(String name) {
        return this.termRepository.findByExamNamesName(name);
    }

    public Term getTerm(int term, int form, int year, int code) {
        return this.termRepository.findByTermAndFormsFormAndFormsYearsYearAndFormsYearsSchoolsCode(term, form, year, code);
    }

    public Term getOneTerm(int term, int form, int code) {
        return this.termRepository.findByTermAndFormsFormAndFormsYearsSchoolsCode(term, form, code);
    }

    public Term addTerm(Term term) {
        return this.termRepository.save(term);
    }
}