package com.pensasha.school.school;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
	@OneToMany(mappedBy = "school", cascade = CascadeType.ALL)
	private Collection<SchoolUser> users;

	@JsonIgnore
	@ManyToMany(mappedBy = "schools", cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	private Collection<Subject> subjects;

	@JsonIgnore
	@ManyToMany(mappedBy = "schools", cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	private Collection<Year> years;

	@JsonIgnore
	@OneToMany(mappedBy = "school", cascade = CascadeType.ALL)
	private Collection<Student> students;

	public School() {
		super();
	}

	public School(String name, int code) {
		super();
		this.name = name;
		this.code = code;
	}

	public School(String name, int code, String address, int contactNumber, String logo, int fax, String email,
			String location, String gender, String scholar, Collection<SchoolUser> users, Collection<Subject> subjects,
			Collection<Year> years, Collection<Student> students) {
		super();
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
		this.years = years;
		this.students = students;
	}

	public School(String name, int code, Collection<SchoolUser> users, Collection<Subject> subjects, Collection<Year> years,
			Collection<Student> students) {
		super();
		this.name = name;
		this.code = code;
		this.users = users;
		this.subjects = subjects;
		this.years = years;
		this.students = students;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public int getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(int contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public int getFax() {
		return fax;
	}

	public void setFax(int fax) {
		this.fax = fax;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getScholar() {
		return scholar;
	}

	public void setScholar(String scholar) {
		this.scholar = scholar;
	}

	public Collection<SchoolUser> getUsers() {
		return users;
	}

	public void setUsers(Collection<SchoolUser> users) {
		this.users = users;
	}

	public Collection<Subject> getSubjects() {
		return subjects;
	}

	public void setSubjects(Collection<Subject> subjects) {
		this.subjects = subjects;
	}

	public Collection<Year> getYears() {
		return years;
	}

	public void setYears(Collection<Year> years) {
		this.years = years;
	}

	public Collection<Student> getStudents() {
		return students;
	}

	public void setStudents(Collection<Student> students) {
		this.students = students;
	}

}
