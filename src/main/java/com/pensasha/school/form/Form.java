package com.pensasha.school.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pensasha.school.exam.ExamName;
import com.pensasha.school.student.Student;
import com.pensasha.school.subject.Subject;
import com.pensasha.school.term.Term;
import com.pensasha.school.user.Teacher;
import com.pensasha.school.year.Year;

@Entity
public class Form {
    @Id
    private int form;
    @JsonIgnore
    @ManyToMany(mappedBy="forms", cascade={CascadeType.MERGE, CascadeType.PERSIST})
    private Collection<Year> years;
    @JsonIgnore
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="Form_Subject", joinColumns={@JoinColumn(name="form")}, inverseJoinColumns={@JoinColumn(name="subject")})
    private Collection<Subject> subjects;
    @JsonIgnore
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="Form_Term", joinColumns={@JoinColumn(name="form")}, inverseJoinColumns={@JoinColumn(name="term")})
    private Collection<Term> terms;
    @JsonIgnore
    @ManyToMany(mappedBy="forms", cascade={CascadeType.MERGE, CascadeType.PERSIST})
    private Collection<Student> students;
    @JsonIgnore
    @ManyToMany(mappedBy="forms", cascade={CascadeType.MERGE, CascadeType.PERSIST})
    private Collection<ExamName> examNames;
    @JsonIgnore
    @ManyToMany(mappedBy="forms", cascade={CascadeType.MERGE, CascadeType.PERSIST})
    private List<Teacher> teachers;

    public Form(int form, Collection<Term> terms) {
        this.form = form;
        this.terms = terms;
    }

    public Form(int form, Collection<Year> years, Collection<Subject> subjects, Collection<Term> terms, Collection<Student> students, Collection<ExamName> examNames) {
        this.form = form;
        this.years = years;
        this.subjects = subjects;
        this.terms = terms;
        this.students = students;
        this.examNames = examNames;
    }

    public Form(int form, int year) {
        this.form = form;
        this.years = new ArrayList<Year>(List.of(new Year(year)));
    }

    public Form(int form) {
        this.form = form;
    }

    public Form() {
    }

    public int getForm() {
        return this.form;
    }

    public void setForm(int form) {
        this.form = form;
    }

    public Collection<Year> getYears() {
        return this.years;
    }

    public void setYears(Collection<Year> years) {
        this.years = years;
    }

    public Collection<Term> getTerms() {
        return this.terms;
    }

    public void setTerms(Collection<Term> terms) {
        this.terms = terms;
    }

    public Collection<Student> getStudents() {
        return this.students;
    }

    public void setStudents(Collection<Student> students) {
        this.students = students;
    }

    public Collection<Subject> getSubjects() {
        return this.subjects;
    }

    public void setSubjects(Collection<Subject> subjects) {
        this.subjects = subjects;
    }

    public Collection<ExamName> getExamNames() {
        return this.examNames;
    }

    public void setExamNames(Collection<ExamName> examNames) {
        this.examNames = examNames;
    }

    public List<Teacher> getTeachers() {
        return this.teachers;
    }

    public void setTeachers(List<Teacher> teachers) {
        this.teachers = teachers;
    }
}