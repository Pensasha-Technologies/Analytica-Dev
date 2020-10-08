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
import com.pensasha.school.user.User;
import com.pensasha.school.year.Year;

@Entity
public class School {

	private String name;

	@Id
	private int code;

	@JsonIgnore
	@OneToMany(mappedBy = "school", cascade = CascadeType.ALL)
	private Collection<User> users;

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

	public School(String name, int code, Collection<User> users, Collection<Subject> subjects, Collection<Year> years,
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

	public Collection<User> getUsers() {
		return users;
	}

	public void setUsers(Collection<User> users) {
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
