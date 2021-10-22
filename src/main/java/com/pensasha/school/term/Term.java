package com.pensasha.school.term;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pensasha.school.exam.ExamName;
import com.pensasha.school.form.Form;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.Collection;

@Entity
public class Term {
    @Id
    private int term;
    @JsonIgnore
    @ManyToMany(mappedBy="terms", cascade={CascadeType.MERGE, CascadeType.PERSIST})
    private Collection<Form> forms;
    @JsonIgnore
    @ManyToMany(mappedBy="terms", cascade={CascadeType.MERGE, CascadeType.PERSIST})
    private Collection<ExamName> examNames;

    public Term(int term, Collection<Form> forms, Collection<ExamName> examNames) {
        this.term = term;
        this.forms = forms;
        this.examNames = examNames;
    }

    public Term(int term) {
        this.term = term;
    }

    public Term() {
    }

    public int getTerm() {
        return this.term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public Collection<Form> getForms() {
        return this.forms;
    }

    public void setForms(Collection<Form> forms) {
        this.forms = forms;
    }

    public Collection<ExamName> getExamNames() {
        return this.examNames;
    }

    public void setExamNames(Collection<ExamName> examNames) {
        this.examNames = examNames;
    }
}