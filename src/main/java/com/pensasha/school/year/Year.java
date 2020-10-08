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
import com.pensasha.school.form.Form;
import com.pensasha.school.school.School;
import com.pensasha.school.student.Student;
import com.pensasha.school.subject.Subject;

@Entity
public class Year {

	@Id
	private int year;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "Year_School", joinColumns = @JoinColumn(name = "year"), inverseJoinColumns = @JoinColumn(name = "code"))
	private Collection<School> schools;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "Year_Form", joinColumns = @JoinColumn(name = "year"), inverseJoinColumns = @JoinColumn(name = "form"))
	private Collection<Form> forms;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "Year_Subjects", joinColumns = @JoinColumn(name = "year"), inverseJoinColumns = @JoinColumn(name = "subject"))
	private Collection<Subject> subjects;

	@JsonIgnore
	@ManyToMany(mappedBy = "years", cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	private Collection<Student> students;

	public Year(int year, Collection<School> schools, Collection<Form> forms, Collection<Subject> subjects,
			Collection<Student> students) {
		super();
		this.year = year;
		this.schools = schools;
		this.forms = forms;
		this.subjects = subjects;
		this.students = students;
	}

	public Year(int year, int school_code) {
		super();
		this.year = year;
		this.schools = new ArrayList<>(List.of(new School("", school_code)));
	}

	public Year(int year) {
		super();
		this.year = year;
	}

	public Year() {
		super();
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Collection<School> getSchools() {
		return schools;
	}

	public void setSchools(Collection<School> schools) {
		this.schools = schools;
	}

	public Collection<Form> getForms() {
		return forms;
	}

	public void setForms(Collection<Form> forms) {
		this.forms = forms;
	}

	public Collection<Student> getStudents() {
		return students;
	}

	public void setStudents(Collection<Student> students) {
		this.students = students;
	}

	public Collection<Subject> getSubjects() {
		return subjects;
	}

	public void setSubjects(Collection<Subject> subjects) {
		this.subjects = subjects;
	}

}
