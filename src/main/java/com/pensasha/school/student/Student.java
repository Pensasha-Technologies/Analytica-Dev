package com.pensasha.school.student;

import java.sql.Date;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.*;

import com.pensasha.school.form.Form;
import com.pensasha.school.school.School;
import com.pensasha.school.stream.Stream;
import com.pensasha.school.subject.Subject;
import com.pensasha.school.year.Year;

@Entity
public class Student {

	@NotBlank(message = "First Name should not be blank")
	@Size(min = 2, max = 24, message = "First Name should have a minimum character of 2 and maximum 24")
	private String firstname;

	@Size(max = 24, message = "Second Name should have a maximum of 24 characters")
	private String secondname;

	@NotBlank(message = "Third Name should not be blank")
	@Size(min = 2, max = 24, message = "Third Name should have a minimum character of 2 and maximum 24")
	private String thirdname;

	@Id
	@NotBlank(message = "First Name should not be blank")
	@Size(min = 1, max = 24)
	private String admNo;

	@Size(max = 8)
	private String upiNo;

	@Size(max = 12)
	private String hudumaNo;

	@Size(max = 13)
	private String birthNo;

	@NotNull
	private Date dob;

	private String f_firstname;

	private String f_secondname;

	private String f_thirdname;

	private int f_phoneNumber;

	private String f_email;

	private String m_firstname;

	private String m_secondname;

	private String m_thirdname;

	private int m_phoneNumber;

	private String m_email;

	private String g_firstname;

	private String g_secondname;

	private String g_thirdname;

	private int g_phoneNumber;

	private String g_email;

	private String gender;

	@Size(max = 30)
	private String sponsor;

	@NotNull
	@Min(value = 1)
	@Max(value = 500)
	private int kcpeMarks;

	private int scholar;

	@NotNull
	@Min(value = 1000, message="Year entered has an invalid format")
	@Max(value = 9999, message="Year entered has an invalid format")
	private int yearAdmitted;

	private int currentForm;

	@ManyToMany
	@JoinTable(name = "Student_Form", joinColumns = @JoinColumn(name = "admNo"), inverseJoinColumns = @JoinColumn(name = "form"))
	private Collection<Form> forms;

	@ManyToOne
	@JoinColumn(name = "stream_id")
	private Stream stream;

	@ManyToMany
	@JoinTable(name = "Student_Year", joinColumns = @JoinColumn(name = "admNo"), inverseJoinColumns = @JoinColumn(name = "year"))
	private Collection<Year> years;

	@ManyToMany
	@JoinTable(name = "Student_Subject", joinColumns = @JoinColumn(name = "admNo"), inverseJoinColumns = @JoinColumn(name = "initials"))
	private Collection<Subject> subjects;

	@ManyToOne
	@JoinColumn(name = "school_code")
	private School school;

	public Student(String firstname, String secondname, String thirdname, String admNo, String upiNo, String hudumaNo,
			String birthNo, Date dob, String f_firstname, String f_secondname, String f_thirdname, int f_phoneNumber,
			String f_email, String m_firstname, String m_secondname, String m_thirdname, int m_phoneNumber,
			String m_email, String g_firstname, String g_secondname, String g_thirdname, int g_phoneNumber,
			String g_email, String gender, String sponsor, int kcpeMarks, int scholar, int yearAdmitted,
			int currentForm, Collection<Form> forms, Stream stream, Collection<Year> years,
			Collection<Subject> subjects, School school) {
		super();
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

	public Student(String firstname, String secondname, String thirdname, String admNo, String upiNo, String hudumaNo,
			String birthNo, Date dob, String f_firstname, String f_secondname, String f_thirdname, int f_phoneNumber,
			String f_email, String m_firstname, String m_secondname, String m_thirdname, int m_phoneNumber,
			String m_email, String g_firstname, String g_secondname, String g_thirdname, int g_phoneNumber,
			String g_email, String gender, String sponsor, int kcpeMarks, int scholar, int yearAdmitted,
			int currentForm) {
		super();
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
		super();
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getSecondname() {
		return secondname;
	}

	public void setSecondname(String secondname) {
		this.secondname = secondname;
	}

	public String getThirdname() {
		return thirdname;
	}

	public void setThirdname(String thirdname) {
		this.thirdname = thirdname;
	}

	public String getAdmNo() {
		return admNo;
	}

	public void setAdmNo(String admNo) {
		this.admNo = admNo;
	}

	public String getUpiNo() {
		return upiNo;
	}

	public void setUpiNo(String upiNo) {
		this.upiNo = upiNo;
	}

	public String getHudumaNo() {
		return hudumaNo;
	}

	public void setHudumaNo(String hudumaNo) {
		this.hudumaNo = hudumaNo;
	}

	public String getBirthNo() {
		return birthNo;
	}

	public void setBirthNo(String birthNo) {
		this.birthNo = birthNo;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public String getF_firstname() {
		return f_firstname;
	}

	public void setF_firstname(String f_firstname) {
		this.f_firstname = f_firstname;
	}

	public String getF_secondname() {
		return f_secondname;
	}

	public void setF_secondname(String f_secondname) {
		this.f_secondname = f_secondname;
	}

	public String getF_thirdname() {
		return f_thirdname;
	}

	public void setF_thirdname(String f_thirdname) {
		this.f_thirdname = f_thirdname;
	}

	public int getF_phoneNumber() {
		return f_phoneNumber;
	}

	public void setF_phoneNumber(int f_phoneNumber) {
		this.f_phoneNumber = f_phoneNumber;
	}

	public String getF_email() {
		return f_email;
	}

	public void setF_email(String f_email) {
		this.f_email = f_email;
	}

	public String getM_firstname() {
		return m_firstname;
	}

	public void setM_firstname(String m_firstname) {
		this.m_firstname = m_firstname;
	}

	public String getM_secondname() {
		return m_secondname;
	}

	public void setM_secondname(String m_secondname) {
		this.m_secondname = m_secondname;
	}

	public String getM_thirdname() {
		return m_thirdname;
	}

	public void setM_thirdname(String m_thirdname) {
		this.m_thirdname = m_thirdname;
	}

	public int getM_phoneNumber() {
		return m_phoneNumber;
	}

	public void setM_phoneNumber(int m_phoneNumber) {
		this.m_phoneNumber = m_phoneNumber;
	}

	public String getM_email() {
		return m_email;
	}

	public void setM_email(String m_email) {
		this.m_email = m_email;
	}

	public String getG_firstname() {
		return g_firstname;
	}

	public void setG_firstname(String g_firstname) {
		this.g_firstname = g_firstname;
	}

	public String getG_secondname() {
		return g_secondname;
	}

	public void setG_secondname(String g_secondname) {
		this.g_secondname = g_secondname;
	}

	public String getG_thirdname() {
		return g_thirdname;
	}

	public void setG_thirdname(String g_thirdname) {
		this.g_thirdname = g_thirdname;
	}

	public int getG_phoneNumber() {
		return g_phoneNumber;
	}

	public void setG_phoneNumber(int g_phoneNumber) {
		this.g_phoneNumber = g_phoneNumber;
	}

	public String getG_email() {
		return g_email;
	}

	public void setG_email(String g_email) {
		this.g_email = g_email;
	}

	public Collection<Form> getForms() {
		return forms;
	}

	public void setForms(Collection<Form> forms) {
		this.forms = forms;
	}

	public Stream getStream() {
		return stream;
	}

	public void setStream(Stream stream) {
		this.stream = stream;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getSponsor() {
		return sponsor;
	}

	public void setSponsor(String sponsor) {
		this.sponsor = sponsor;
	}

	public int getKcpeMarks() {
		return kcpeMarks;
	}

	public void setKcpeMarks(int kcpeMarks) {
		this.kcpeMarks = kcpeMarks;
	}

	public int getScholar() {
		return scholar;
	}

	public void setScholar(int scholar) {
		this.scholar = scholar;
	}

	public int getYearAdmitted() {
		return yearAdmitted;
	}

	public void setYearAdmitted(int yearAdmitted) {
		this.yearAdmitted = yearAdmitted;
	}

	public int getCurrentForm() {
		return currentForm;
	}

	public void setCurrentForm(int currentForm) {
		this.currentForm = currentForm;
	}

	public Collection<Year> getYears() {
		return years;
	}

	public void setYears(Collection<Year> years) {
		this.years = years;
	}

	public Collection<Subject> getSubjects() {
		return subjects;
	}

	public void setSubjects(Collection<Subject> subjects) {
		this.subjects = subjects;
	}

	public School getSchool() {
		return school;
	}

	public void setSchool(School school) {
		this.school = school;
	}

}