package com.pensasha.school.form;

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
import com.pensasha.school.student.Student;
import com.pensasha.school.subject.Subject;
import com.pensasha.school.term.Term;
import com.pensasha.school.year.Year;

@Entity
public class Form {

	@Id
	private int form;

	@JsonIgnore
	@ManyToMany(mappedBy = "forms", cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	private Collection<Year> years;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "Form_Subject", joinColumns = @JoinColumn(name = "form"), inverseJoinColumns = @JoinColumn(name = "subject"))
	private Collection<Subject> subjects;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "Form_Term", joinColumns = @JoinColumn(name = "form"), inverseJoinColumns = @JoinColumn(name = "term"))
	private Collection<Term> terms;

	@JsonIgnore
	@ManyToMany(mappedBy = "forms", cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	private Collection<Student> students;

	public Form(int form, Collection<Term> terms) {
		super();
		this.form = form;
		this.terms = terms;
	}

	public Form(int form, Collection<Year> years, Collection<Subject> subjects, Collection<Term> terms,
			Collection<Student> students) {
		super();
		this.form = form;
		this.years = years;
		this.subjects = subjects;
		this.terms = terms;
		this.students = students;
	}

	public Form(int form, int year) {
		super();
		this.form = form;
		this.years = new ArrayList<>(List.of(new Year(year)));
	}

	public Form(int form) {
		super();
		this.form = form;
	}

	public Form() {
		super();
	}

	public int getForm() {
		return form;
	}

	public void setForm(int form) {
		this.form = form;
	}

	public Collection<Year> getYears() {
		return years;
	}

	public void setYears(Collection<Year> years) {
		this.years = years;
	}

	public Collection<Term> getTerms() {
		return terms;
	}

	public void setTerms(Collection<Term> terms) {
		this.terms = terms;
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
