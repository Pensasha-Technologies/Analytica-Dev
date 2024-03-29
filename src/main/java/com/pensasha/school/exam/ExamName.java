package com.pensasha.school.exam;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pensasha.school.form.Form;
import com.pensasha.school.school.School;
import com.pensasha.school.subject.Subject;
import com.pensasha.school.term.Term;
import com.pensasha.school.year.Year;

import javax.persistence.*;
import java.util.Collection;

@Entity
public class ExamName {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    private String name;
    private int outOf;
    @JsonIgnore
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="examName_school", joinColumns={@JoinColumn(name="examName_id")}, inverseJoinColumns={@JoinColumn(name="code")})
    private Collection<School> schools;
    @JsonIgnore
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="examName_year", joinColumns={@JoinColumn(name="examName_id")}, inverseJoinColumns={@JoinColumn(name="year")})
    private Collection<Year> years;
    @JsonIgnore
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="examName_form", joinColumns={@JoinColumn(name="examName_id")}, inverseJoinColumns={@JoinColumn(name="form")})
    private Collection<Form> forms;
    @JsonIgnore
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="examName_term", joinColumns={@JoinColumn(name="examName_id")}, inverseJoinColumns={@JoinColumn(name="term")})
    private Collection<Term> terms;
    @JsonIgnore
    @OneToOne
    private Subject subject;

    public ExamName(int id, String name, int outOf, Collection<School> schools, Collection<Year> years, Collection<Form> forms, Collection<Term> terms, Subject subject) {
        this.id = id;
        this.name = name;
        this.outOf = outOf;
        this.schools = schools;
        this.years = years;
        this.forms = forms;
        this.terms = terms;
        this.subject = subject;
    }

    public ExamName(String name, int outOf) {
        this.name = name;
        this.outOf = outOf;
    }

    public ExamName() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOutOf() {
        return this.outOf;
    }

    public void setOutOf(int outOf) {
        this.outOf = outOf;
    }

    public Collection<School> getSchools() {
        return this.schools;
    }

    public void setSchools(Collection<School> schools) {
        this.schools = schools;
    }

    public Collection<Year> getYears() {
        return this.years;
    }

    public void setYears(Collection<Year> years) {
        this.years = years;
    }

    public Collection<Form> getForms() {
        return this.forms;
    }

    public void setForms(Collection<Form> forms) {
        this.forms = forms;
    }

    public Collection<Term> getTerms() {
        return this.terms;
    }

    public void setTerms(Collection<Term> terms) {
        this.terms = terms;
    }

    public Subject getSubject() {
        return this.subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }
}