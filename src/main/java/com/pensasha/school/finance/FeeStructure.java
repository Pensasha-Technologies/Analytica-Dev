package com.pensasha.school.finance;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pensasha.school.form.Form;
import com.pensasha.school.school.School;
import com.pensasha.school.term.Term;
import com.pensasha.school.year.Year;

@Entity
public class FeeStructure {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String name;
	private int cost;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "code")
	private School school;
	
	@ManyToOne
	@JoinColumn(name = "year")
	private Year year;
	
	@ManyToOne
	@JoinColumn(name = "form")
	private Form form;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "term")
	private Term term;

	public FeeStructure(int id, String name, int cost, School school, Year year, Form form, Term term) {
		super();
		this.id = id;
		this.name = name;
		this.cost = cost;
		this.school = school;
		this.year = year;
		this.form = form;
		this.term = term;
	}
	
	public FeeStructure(String name, int cost, School school, Year year, Form form, Term term) {
		super();
		this.name = name;
		this.cost = cost;
		this.school = school;
		this.year = year;
		this.form = form;
		this.term = term;
	}

	public FeeStructure(String name, int cost) {
		super();
		this.name = name;
		this.cost = cost;
	}

	public FeeStructure() {
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

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public School getSchool() {
		return school;
	}

	public void setSchool(School school) {
		this.school = school;
	}

	public Form getForm() {
		return form;
	}

	public void setForm(Form formObj) {
		this.form = formObj;
	}

	public Year getYear() {
		return year;
	}

	public void setYear(Year year) {
		this.year = year;
	}

	public Term getTerm() {
		return term;
	}

	public void setTerm(Term term) {
		this.term = term;
	}

}
