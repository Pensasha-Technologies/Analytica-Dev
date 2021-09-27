package com.pensasha.school.school;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pensasha.school.exam.ExamName;
import com.pensasha.school.student.Student;
import com.pensasha.school.subject.Subject;
import com.pensasha.school.user.SchoolUser;
import com.pensasha.school.year.Year;

@Entity
public class School {
    private String name;
    @Id
    private int code;
    private String address;
    private int contactNumber;
    private String logo;
    private int fax;
    private String email;
    private String location;
    private String gender;
    private String scholar;
    @JsonIgnore
    @OneToMany(mappedBy="school", cascade={CascadeType.ALL})
    private Collection<SchoolUser> users;
    @JsonIgnore
    @ManyToMany(mappedBy="schools", cascade={CascadeType.MERGE, CascadeType.PERSIST})
    private Collection<Subject> subjects;
    @JsonIgnore
    @ManyToMany
    @JoinTable(name="Subject_compf1f2_school", joinColumns={@JoinColumn(name="initials")}, inverseJoinColumns={@JoinColumn(name="code")})
    private Collection<Subject> compSubjectF1F2;
    @JsonIgnore
    @ManyToMany
    @JoinTable(name="Subject_compf3f4_school", joinColumns={@JoinColumn(name="initials")}, inverseJoinColumns={@JoinColumn(name="code")})
    private Collection<Subject> compSubjectF3F4;
    @JsonIgnore
    @ManyToMany(mappedBy="schools", cascade={CascadeType.MERGE, CascadeType.PERSIST})
    private Collection<Year> years;
    @JsonIgnore
    @OneToMany(mappedBy="school", cascade={CascadeType.ALL})
    private Collection<Student> students;
    @JsonIgnore
    @ManyToMany(mappedBy="schools", cascade={CascadeType.MERGE, CascadeType.PERSIST})
    private Collection<ExamName> examNames;

    public School() {
    }

    public School(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public School(String name, int code, String address, int contactNumber, String logo, int fax, String email, String location, String gender, String scholar, Collection<SchoolUser> users, Collection<Subject> subjects, Collection<Subject> compSubjectF1F2, Collection<Subject> compSubjectF3F4, Collection<Year> years, Collection<Student> students, Collection<ExamName> examNames) {
        this.name = name;
        this.code = code;
        this.address = address;
        this.contactNumber = contactNumber;
        this.logo = logo;
        this.fax = fax;
        this.email = email;
        this.location = location;
        this.gender = gender;
        this.scholar = scholar;
        this.users = users;
        this.subjects = subjects;
        this.compSubjectF1F2 = compSubjectF1F2;
        this.compSubjectF3F4 = compSubjectF3F4;
        this.years = years;
        this.students = students;
        this.examNames = examNames;
    }

    public School(String name, int code, Collection<SchoolUser> users, Collection<Subject> subjects, Collection<Year> years, Collection<Student> students, Collection<ExamName> examNames) {
        this.name = name;
        this.code = code;
        this.users = users;
        this.subjects = subjects;
        this.years = years;
        this.students = students;
        this.examNames = examNames;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getContactNumber() {
        return this.contactNumber;
    }

    public void setContactNumber(int contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getLogo() {
        return this.logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public int getFax() {
        return this.fax;
    }

    public void setFax(int fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getScholar() {
        return this.scholar;
    }

    public void setScholar(String scholar) {
        this.scholar = scholar;
    }

    public Collection<SchoolUser> getUsers() {
        return this.users;
    }

    public void setUsers(Collection<SchoolUser> users) {
        this.users = users;
    }

    public Collection<Subject> getSubjects() {
        return this.subjects;
    }

    public void setSubjects(Collection<Subject> subjects) {
        this.subjects = subjects;
    }

    public Collection<Year> getYears() {
        return this.years;
    }

    public void setYears(Collection<Year> years) {
        this.years = years;
    }

    public Collection<Student> getStudents() {
        return this.students;
    }

    public void setStudents(Collection<Student> students) {
        this.students = students;
    }

    public Collection<Subject> getCompSubjectF1F2() {
        return this.compSubjectF1F2;
    }

    public void setCompSubjectF1F2(Collection<Subject> compSubjectF1F2) {
        this.compSubjectF1F2 = compSubjectF1F2;
    }

    public Collection<Subject> getCompSubjectF3F4() {
        return this.compSubjectF3F4;
    }

    public void setCompSubjectF3F4(Collection<Subject> compSubjectF3F4) {
        this.compSubjectF3F4 = compSubjectF3F4;
    }

    public Collection<ExamName> getExamNames() {
        return this.examNames;
    }

    public void setExamNames(Collection<ExamName> examNames) {
        this.examNames = examNames;
    }
}