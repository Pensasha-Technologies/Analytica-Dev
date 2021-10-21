package com.pensasha.school.user;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pensasha.school.form.Form;
import com.pensasha.school.stream.Stream;
import com.pensasha.school.subject.Subject;
import com.pensasha.school.year.Year;

@Entity
public class Teacher extends SchoolUser {
    private String teacherNumber;
    private String tscNumber;
    private String initials;
    
  
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="Teacher_Subject", joinColumns= {@JoinColumn(name="username")}, inverseJoinColumns= {@JoinColumn(name="initials")})
    private List<Subject> subjects;
    
    @JsonIgnore
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="Teacher_Year", joinColumns={@JoinColumn(name="username")}, inverseJoinColumns={@JoinColumn(name="year")})
    private List<Year> years;
    
    @JsonIgnore
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="Teacher_Form", joinColumns={@JoinColumn(name="username")}, inverseJoinColumns={@JoinColumn(name="form")})
    private List<Form> forms;
    
    @JsonIgnore
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="Teacher_Stream", joinColumns={@JoinColumn(name="username")}, inverseJoinColumns={@JoinColumn(name="stream")})
    private List<Stream> streams;

    public Teacher(String username, String firstname, String secondname, String thirdname, String password, String email, int phoneNumber, String address, String teacherNumber, String tscNumber, String initials, List<Subject> subjects) {
        super(username, firstname, secondname, thirdname, password, email, phoneNumber, address);
        this.teacherNumber = teacherNumber;
        this.tscNumber = tscNumber;
        this.initials = initials;
        this.subjects = subjects;
    }

    public Teacher(String username, String firstname, String secondname, String thirdname, String password, String email, int phoneNumber, String address, String teacherNumber, String tscNumber, String initials) {
        super(username, firstname, secondname, thirdname, password, email, phoneNumber, address);
        this.teacherNumber = teacherNumber;
        this.tscNumber = tscNumber;
        this.initials = initials;
    }

    public Teacher(String username, String firstname, String secondname, String thirdname, String password, String email, int phoneNumber, String address) {
        super(username, firstname, secondname, thirdname, password, email, phoneNumber, address);
    }

    public Teacher() {
    }

    public String getTeacherNumber() {
        return this.teacherNumber;
    }

    public void setTeacherNumber(String teacherNumber) {
        this.teacherNumber = teacherNumber;
    }

    public String getTscNumber() {
        return this.tscNumber;
    }

    public void setTscNumber(String tscNumber) {
        this.tscNumber = tscNumber;
    }

    public String getInitials() {
        return this.initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public List<Subject> getSubjects() {
        return this.subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    public List<Year> getYears() {
        return this.years;
    }

    public void setYears(List<Year> years) {
        this.years = years;
    }

    public List<Form> getForms() {
        return this.forms;
    }

    public void setForms(List<Form> forms) {
        this.forms = forms;
    }

    public List<Stream> getStreams() {
        return this.streams;
    }

    public void setStreams(List<Stream> streams) {
        this.streams = streams;
    }
}