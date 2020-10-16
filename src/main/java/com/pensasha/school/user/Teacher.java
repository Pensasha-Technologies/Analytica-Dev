package com.pensasha.school.user;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.pensasha.school.subject.Subject;

@Entity
public class Teacher extends SchoolUser {

	private String teacherNumber;
	private String tscNumber;
	private String initials;

	@OneToMany
	private List<Subject> subjects;

	public Teacher(String username, String firstname, String secondname, String thirdname, String password,
			String email, int phoneNumber, String address, String teacherNumber, String tscNumber, String initials,
			List<Subject> subjects) {
		super(username, firstname, secondname, thirdname, password, email, phoneNumber, address);
		this.teacherNumber = teacherNumber;
		this.tscNumber = tscNumber;
		this.initials = initials;
		this.subjects = subjects;
	}

	public Teacher(String username, String firstname, String secondname, String thirdname, String password,
			String email, int phoneNumber, String address, String teacherNumber, String tscNumber, String initials) {
		super(username, firstname, secondname, thirdname, password, email, phoneNumber, address);
		this.teacherNumber = teacherNumber;
		this.tscNumber = tscNumber;
		this.initials = initials;
	}

	public Teacher(String username, String firstname, String secondname, String thirdname, String password,
			String email, int phoneNumber, String address) {
		super(username, firstname, secondname, thirdname, password, email, phoneNumber, address);
	}

	public Teacher() {

	}

	public String getTeacherNumber() {
		return teacherNumber;
	}

	public void setTeacherNumber(String teacherNumber) {
		this.teacherNumber = teacherNumber;
	}

	public String getTscNumber() {
		return tscNumber;
	}

	public void setTscNumber(String tscNumber) {
		this.tscNumber = tscNumber;
	}

	public String getInitials() {
		return initials;
	}

	public void setInitials(String initials) {
		this.initials = initials;
	}

	public List<Subject> getSubjects() {
		return subjects;
	}

	public void setSubjects(List<Subject> subjects) {
		this.subjects = subjects;
	}

}
