package com.pensasha.school.subject;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pensasha.school.form.Form;
import com.pensasha.school.school.School;
import com.pensasha.school.student.Student;
import com.pensasha.school.year.Year;

@Entity
public class Subject {

	@Id
	private String initials;
	private String name;

	@JsonIgnore
	@ManyToMany
	@JoinTable(name = "Subject_school", joinColumns = @JoinColumn(name = "initials"), inverseJoinColumns = @JoinColumn(name = "code"))
	private Collection<School> schools;

	@JsonIgnore
	@ManyToMany(mappedBy = "subjects", cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	private Collection<Student> students;

	@ManyToMany(mappedBy = "subjects", cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	private Collection<Form> forms;

	@ManyToMany(mappedBy = "subjects", cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	private Collection<Year> years;
	
	@ManyToMany(mappedBy = "compSubjectF1F2", cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	private Collection<School> compF1F2schools;
	
	@JsonIgnore
	@ManyToMany(mappedBy = "compSubjectF3F4", cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	private Collection<School> compF3F4schools;
	
	public Subject(String initials, String name, Collection<School> schools, Collection<Student> students,
			Collection<Form> forms, Collection<Year> years, Collection<School> compF1F2schools,
			Collection<School> compF3F4schools) {
		super();
		this.initials = initials;
		this.name = name;
		this.schools = schools;
		this.students = students;
		this.forms = forms;
		this.years = years;
		this.compF1F2schools = compF1F2schools;
		this.compF3F4schools = compF3F4schools;
	}

	public Subject(String initials, String name) {
		super();
		this.initials = initials;
		this.name = name;
	}

	public Subject() {
		super();
	}

	public String getInitials() {
		return initials;
	}

	public void setInitials(String initials) {
		this.initials = initials;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<School> getSchools() {
		return schools;
	}

	public void setSchools(Collection<School> schools) {
		this.schools = schools;
	}

	public Collection<Student> getStudents() {
		return students;
	}

	public void setStudents(Collection<Student> students) {
		this.students = students;
	}

	public Collection<Form> getForms() {
		return forms;
	}

	public void setForms(Collection<Form> forms) {
		this.forms = forms;
	}

	public Collection<Year> getYears() {
		return years;
	}

	public void setYears(Collection<Year> years) {
		this.years = years;
	}

	public Collection<School> getCompF1F2schools() {
		return compF1F2schools;
	}

	public void setCompF1F2schools(Collection<School> compF1F2schools) {
		this.compF1F2schools = compF1F2schools;
	}

	public Collection<School> getCompF3F4schools() {
		return compF3F4schools;
	}

	public void setCompF3F4schools(Collection<School> compF3F4schools) {
		this.compF3F4schools = compF3F4schools;
	}

}
