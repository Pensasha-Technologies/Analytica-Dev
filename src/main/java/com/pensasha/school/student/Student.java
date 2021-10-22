package com.pensasha.school.student;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pensasha.school.form.Form;
import com.pensasha.school.school.School;
import com.pensasha.school.stream.Stream;
import com.pensasha.school.subject.Subject;
import com.pensasha.school.year.Year;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Date;
import java.util.Collection;

@Entity
public class Student {
    @JsonIgnore
    private String photo;
    @JsonIgnore
    @NotBlank(message="First Name should not be blank")
    @Size(min=2, max=24, message="First Name should have a minimum character of 2 and maximum 24")
    private @NotBlank(message="First Name should not be blank") @Size(min=2, max=24, message="First Name should have a minimum character of 2 and maximum 24") String firstname;
    @JsonIgnore
    @Size(max=24, message="Second Name should have a maximum of 24 characters")
    private @Size(max=24, message="Second Name should have a maximum of 24 characters") String secondname;
    @JsonIgnore
    @NotBlank(message="Third Name should not be blank")
    @Size(min=2, max=24, message="Third Name should have a minimum character of 2 and maximum 24")
    private @NotBlank(message="Third Name should not be blank") @Size(min=2, max=24, message="Third Name should have a minimum character of 2 and maximum 24") String thirdname;
    @Id
    @NotBlank(message="First Name should not be blank")
    @Size(min=1, max=24)
    private @NotBlank(message="First Name should not be blank") @Size(min=1, max=24) String admNo;
    @JsonIgnore
    @Size(max=8)
    private @Size(max=8) String upiNo;
    @JsonIgnore
    @Size(max=12)
    private @Size(max=12) String hudumaNo;
    @JsonIgnore
    @Size(max=13)
    private @Size(max=13) String birthNo;
    @JsonIgnore
    @NotNull
    private Date dob;
    @JsonIgnore
    private String f_firstname;
    @JsonIgnore
    private String f_secondname;
    @JsonIgnore
    private String f_thirdname;
    @JsonIgnore
    private int f_phoneNumber;
    @JsonIgnore
    private String f_email;
    @JsonIgnore
    private String m_firstname;
    @JsonIgnore
    private String m_secondname;
    @JsonIgnore
    private String m_thirdname;
    @JsonIgnore
    private int m_phoneNumber;
    @JsonIgnore
    private String m_email;
    @JsonIgnore
    private String g_firstname;
    @JsonIgnore
    private String g_secondname;
    @JsonIgnore
    private String g_thirdname;
    @JsonIgnore
    private int g_phoneNumber;
    @JsonIgnore
    private String g_email;
    @JsonIgnore
    private String gender;
    @JsonIgnore
    @Size(max=30)
    private @Size(max=30) String sponsor;
    @JsonIgnore
    @NotNull
    @Min(value=1L)
    @Max(value=500L)
    private @NotNull @Min(value=1L) @Max(value=500L) int kcpeMarks;
    @JsonIgnore
    private String scholar;
    @JsonIgnore
    @NotNull
    @Min(value=1000L, message="Year entered has an invalid format")
    @Max(value=9999L, message="Year entered has an invalid format")
    private @NotNull @Min(value=1000L, message="Year entered has an invalid format") @Max(value=9999L, message="Year entered has an invalid format") int yearAdmitted;
    @JsonIgnore
    private int currentForm;
    @JsonIgnore
    @ManyToMany
    @JoinTable(name="Student_Form", joinColumns={@JoinColumn(name="admNo")}, inverseJoinColumns={@JoinColumn(name="form")})
    private Collection<Form> forms;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="stream_id")
    private Stream stream;
    @JsonIgnore
    @ManyToMany
    @JoinTable(name="Student_Year", joinColumns={@JoinColumn(name="admNo")}, inverseJoinColumns={@JoinColumn(name="year")})
    private Collection<Year> years;
    @JsonIgnore
    @ManyToMany
    @JoinTable(name="Student_Subject", joinColumns={@JoinColumn(name="admNo")}, inverseJoinColumns={@JoinColumn(name="initials")})
    private Collection<Subject> subjects;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="school_code")
    private School school;

    public Student(String firstname, String secondname, String thirdname, String admNo, String upiNo, String hudumaNo, String birthNo, Date dob, String f_firstname, String f_secondname, String f_thirdname, int f_phoneNumber, String f_email, String m_firstname, String m_secondname, String m_thirdname, int m_phoneNumber, String m_email, String g_firstname, String g_secondname, String g_thirdname, int g_phoneNumber, String g_email, String gender, String sponsor, int kcpeMarks, String scholar, int yearAdmitted, int currentForm, Collection<Form> forms, Stream stream, Collection<Year> years, Collection<Subject> subjects, School school) {
        this.firstname = firstname;
        this.secondname = secondname;
        this.thirdname = thirdname;
        this.admNo = admNo;
        this.upiNo = upiNo;
        this.hudumaNo = hudumaNo;
        this.birthNo = birthNo;
        this.dob = dob;
        this.f_firstname = f_firstname;
        this.f_secondname = f_secondname;
        this.f_thirdname = f_thirdname;
        this.f_phoneNumber = f_phoneNumber;
        this.f_email = f_email;
        this.m_firstname = m_firstname;
        this.m_secondname = m_secondname;
        this.m_thirdname = m_thirdname;
        this.m_phoneNumber = m_phoneNumber;
        this.m_email = m_email;
        this.g_firstname = g_firstname;
        this.g_secondname = g_secondname;
        this.g_thirdname = g_thirdname;
        this.g_phoneNumber = g_phoneNumber;
        this.g_email = g_email;
        this.gender = gender;
        this.sponsor = sponsor;
        this.kcpeMarks = kcpeMarks;
        this.scholar = scholar;
        this.yearAdmitted = yearAdmitted;
        this.currentForm = currentForm;
        this.forms = forms;
        this.stream = stream;
        this.years = years;
        this.subjects = subjects;
        this.school = school;
    }

    public Student(String firstname, String secondname, String thirdname, String admNo, String upiNo, String hudumaNo, String birthNo, Date dob, String f_firstname, String f_secondname, String f_thirdname, int f_phoneNumber, String f_email, String m_firstname, String m_secondname, String m_thirdname, int m_phoneNumber, String m_email, String g_firstname, String g_secondname, String g_thirdname, int g_phoneNumber, String g_email, String gender, String sponsor, int kcpeMarks, String scholar, int yearAdmitted, int currentForm) {
        this.firstname = firstname;
        this.secondname = secondname;
        this.thirdname = thirdname;
        this.admNo = admNo;
        this.upiNo = upiNo;
        this.hudumaNo = hudumaNo;
        this.birthNo = birthNo;
        this.dob = dob;
        this.f_firstname = f_firstname;
        this.f_secondname = f_secondname;
        this.f_thirdname = f_thirdname;
        this.f_phoneNumber = f_phoneNumber;
        this.f_email = f_email;
        this.m_firstname = m_firstname;
        this.m_secondname = m_secondname;
        this.m_thirdname = m_thirdname;
        this.m_phoneNumber = m_phoneNumber;
        this.m_email = m_email;
        this.g_firstname = g_firstname;
        this.g_secondname = g_secondname;
        this.g_thirdname = g_thirdname;
        this.g_phoneNumber = g_phoneNumber;
        this.g_email = g_email;
        this.gender = gender;
        this.sponsor = sponsor;
        this.kcpeMarks = kcpeMarks;
        this.scholar = scholar;
        this.yearAdmitted = yearAdmitted;
        this.currentForm = currentForm;
    }

    public Student() {
    }

    public String getFirstname() {
        return this.firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSecondname() {
        return this.secondname;
    }

    public void setSecondname(String secondname) {
        this.secondname = secondname;
    }

    public String getThirdname() {
        return this.thirdname;
    }

    public void setThirdname(String thirdname) {
        this.thirdname = thirdname;
    }

    public String getAdmNo() {
        return this.admNo;
    }

    public void setAdmNo(String admNo) {
        this.admNo = admNo;
    }

    public String getUpiNo() {
        return this.upiNo;
    }

    public void setUpiNo(String upiNo) {
        this.upiNo = upiNo;
    }

    public String getHudumaNo() {
        return this.hudumaNo;
    }

    public void setHudumaNo(String hudumaNo) {
        this.hudumaNo = hudumaNo;
    }

    public String getBirthNo() {
        return this.birthNo;
    }

    public void setBirthNo(String birthNo) {
        this.birthNo = birthNo;
    }

    public Date getDob() {
        return this.dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getF_firstname() {
        return this.f_firstname;
    }

    public void setF_firstname(String f_firstname) {
        this.f_firstname = f_firstname;
    }

    public String getF_secondname() {
        return this.f_secondname;
    }

    public void setF_secondname(String f_secondname) {
        this.f_secondname = f_secondname;
    }

    public String getF_thirdname() {
        return this.f_thirdname;
    }

    public void setF_thirdname(String f_thirdname) {
        this.f_thirdname = f_thirdname;
    }

    public int getF_phoneNumber() {
        return this.f_phoneNumber;
    }

    public void setF_phoneNumber(int f_phoneNumber) {
        this.f_phoneNumber = f_phoneNumber;
    }

    public String getF_email() {
        return this.f_email;
    }

    public void setF_email(String f_email) {
        this.f_email = f_email;
    }

    public String getM_firstname() {
        return this.m_firstname;
    }

    public void setM_firstname(String m_firstname) {
        this.m_firstname = m_firstname;
    }

    public String getM_secondname() {
        return this.m_secondname;
    }

    public void setM_secondname(String m_secondname) {
        this.m_secondname = m_secondname;
    }

    public String getM_thirdname() {
        return this.m_thirdname;
    }

    public void setM_thirdname(String m_thirdname) {
        this.m_thirdname = m_thirdname;
    }

    public int getM_phoneNumber() {
        return this.m_phoneNumber;
    }

    public void setM_phoneNumber(int m_phoneNumber) {
        this.m_phoneNumber = m_phoneNumber;
    }

    public String getM_email() {
        return this.m_email;
    }

    public void setM_email(String m_email) {
        this.m_email = m_email;
    }

    public String getG_firstname() {
        return this.g_firstname;
    }

    public void setG_firstname(String g_firstname) {
        this.g_firstname = g_firstname;
    }

    public String getG_secondname() {
        return this.g_secondname;
    }

    public void setG_secondname(String g_secondname) {
        this.g_secondname = g_secondname;
    }

    public String getG_thirdname() {
        return this.g_thirdname;
    }

    public void setG_thirdname(String g_thirdname) {
        this.g_thirdname = g_thirdname;
    }

    public int getG_phoneNumber() {
        return this.g_phoneNumber;
    }

    public void setG_phoneNumber(int g_phoneNumber) {
        this.g_phoneNumber = g_phoneNumber;
    }

    public String getG_email() {
        return this.g_email;
    }

    public void setG_email(String g_email) {
        this.g_email = g_email;
    }

    public Collection<Form> getForms() {
        return this.forms;
    }

    public void setForms(Collection<Form> forms) {
        this.forms = forms;
    }

    public Stream getStream() {
        return this.stream;
    }

    public void setStream(Stream stream) {
        this.stream = stream;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSponsor() {
        return this.sponsor;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }

    public int getKcpeMarks() {
        return this.kcpeMarks;
    }

    public void setKcpeMarks(int kcpeMarks) {
        this.kcpeMarks = kcpeMarks;
    }

    public String getScholar() {
        return this.scholar;
    }

    public void setScholar(String scholar) {
        this.scholar = scholar;
    }

    public int getYearAdmitted() {
        return this.yearAdmitted;
    }

    public void setYearAdmitted(int yearAdmitted) {
        this.yearAdmitted = yearAdmitted;
    }

    public int getCurrentForm() {
        return this.currentForm;
    }

    public void setCurrentForm(int currentForm) {
        this.currentForm = currentForm;
    }

    public Collection<Year> getYears() {
        return this.years;
    }

    public void setYears(Collection<Year> years) {
        this.years = years;
    }

    public Collection<Subject> getSubjects() {
        return this.subjects;
    }

    public void setSubjects(Collection<Subject> subjects) {
        this.subjects = subjects;
    }

    public School getSchool() {
        return this.school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public String getPhoto() {
        return this.photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

}