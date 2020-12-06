package com.pensasha.school.exam;

import java.util.Collection;

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
import com.pensasha.school.term.Term;
import com.pensasha.school.year.Year;

@Entity
public class ExamName {

	@Id
	private String name;
	
	@JsonIgnore
	@ManyToMany(mappedBy = "examNames", cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	private Collection<Mark> marks;
	
	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "examName_school", joinColumns = @JoinColumn(name = "examName_id"), inverseJoinColumns = @JoinColumn(name = "code"))
	private Collection<School> schools;
	
	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "examName_year", joinColumns = @JoinColumn(name = "examName_id"), inverseJoinColumns = @JoinColumn(name = "year"))
	private Collection<Year> years;
	
	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "examName_form", joinColumns = @JoinColumn(name = "examName_id"), inverseJoinColumns = @JoinColumn(name = "form"))
	private Collection<Form> forms;
	
	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "examName_term", joinColumns = @JoinColumn(name = "examName_id"), inverseJoinColumns = @JoinColumn(name = "term"))
	private Collection<Term> terms;


	public ExamName(String name, Collection<Mark> marks, Collection<School> schools, Collection<Year> years,
			Collection<Form> forms, Collection<Term> terms) {
		super();
		this.name = name;
		this.marks = marks;
		this.schools = schools;
		this.years = years;
		this.forms = forms;
		this.terms = terms;
	}

	public ExamName(String name) {
		super();
		this.name = name;
	}

	public ExamName() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<Mark> getMarks() {
		return marks;
	}

	public void setMarks(Collection<Mark> marks) {
		this.marks = marks;
	}

	public Collection<School> getSchools() {
		return schools;
	}

	public void setSchools(Collection<School> schools) {
		this.schools = schools;
	}

	public Collection<Year> getYears() {
		return years;
	}

	public void setYears(Collection<Year> years) {
		this.years = years;
	}

	public Collection<Form> getForms() {
		return forms;
	}

	public void setForms(Collection<Form> forms) {
		this.forms = forms;
	}

	public Collection<Term> getTerms() {
		return terms;
	}

	public void setTerms(Collection<Term> terms) {
		this.terms = terms;
	}

	
}
