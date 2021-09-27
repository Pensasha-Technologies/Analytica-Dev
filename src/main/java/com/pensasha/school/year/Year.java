package com.pensasha.school.year;

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
import com.pensasha.school.form.Form;
import com.pensasha.school.school.School;
import com.pensasha.school.student.Student;
import com.pensasha.school.subject.Subject;
import com.pensasha.school.user.Teacher;

@Entity
public class Year {
    @Id
    private int year;
    @JsonIgnore
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="Year_School", joinColumns={@JoinColumn(name="year")}, inverseJoinColumns={@JoinColumn(name="code")})
    private Collection<School> schools;
    @JsonIgnore
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="Year_Form", joinColumns={@JoinColumn(name="year")}, inverseJoinColumns={@JoinColumn(name="form")})
    private Collection<Form> forms;
    @JsonIgnore
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="Year_Subjects", joinColumns={@JoinColumn(name="year")}, inverseJoinColumns={@JoinColumn(name="subject")})
    private Collection<Subject> subjects;
    @JsonIgnore
    @ManyToMany(mappedBy="years", cascade={CascadeType.MERGE, CascadeType.PERSIST})
    private Collection<Student> students;
    @JsonIgnore
    @ManyToMany(mappedBy="years", cascade={CascadeType.MERGE, CascadeType.PERSIST})
    private Collection<ExamName> examNames;
    @JsonIgnore
    @ManyToMany(mappedBy="years", cascade={CascadeType.MERGE, CascadeType.PERSIST})
    private List<Teacher> teachers;

    public Year(int year, Collection<School> schools, Collection<Form> forms, Collection<Subject> subjects, Collection<Student> students, Collection<ExamName> examNames) {
        this.year = year;
        this.schools = schools;
        this.forms = forms;
        this.subjects = subjects;
        this.students = students;
        this.examNames = examNames;
    }

    public Year(int year, int school_code) {
        this.year = year;
        this.schools = new ArrayList<School>(List.of(new School("", school_code)));
    }

    public Year(int year) {
        this.year = year;
    }

    public Year() {
    }

    public int getYear() {
        return this.year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Collection<School> getSchools() {
        return this.schools;
    }

    public void setSchools(Collection<School> schools) {
        this.schools = schools;
    }

    public Collection<Form> getForms() {
        return this.forms;
    }

    public void setForms(Collection<Form> forms) {
        this.forms = forms;
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