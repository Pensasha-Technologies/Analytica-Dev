package com.pensasha.school.exam;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String name;
	private int outOf;
	
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

	public ExamName(int id, String name, int outOf, Collection<Mark> marks, Collection<School> schools,
			Collection<Year> years, Collection<Form> forms, Collection<Term> terms) {
		super();
		this.id = id;
		this.name = name;
		this.outOf = outOf;
		this.marks = marks;
		this.schools = schools;
		this.years = years;
		this.forms = forms;
		this.terms = terms;
	}

	public ExamName(String name, int outOf) {
		super();
		this.name = name;
		this.outOf = outOf;
	}

	public ExamName() {
		super();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOutOf() {
		return outOf;
	}

	public void setOutOf(int outOf) {
		this.outOf = outOf;
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