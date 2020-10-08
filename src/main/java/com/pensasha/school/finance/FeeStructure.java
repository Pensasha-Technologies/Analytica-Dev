package com.pensasha.school.finance;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.pensasha.school.form.Form;
import com.pensasha.school.school.School;

@Entity
public class FeeStructure {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String name;
	private int cost;

	@ManyToOne
	@JoinColumn(name = "code")
	private School school;
	
	@ManyToOne
	@JoinColumn(name = "form")
	private Form form;

	public FeeStructure(int id, String name, int cost, School school, Form form) {
		super();
		this.id = id;
		this.name = name;
		this.cost = cost;
		this.school = school;
		this.form = form;
	}

	public FeeStructure(int id, String name, int cost) {
		super();
		this.id = id;
		this.name = name;
		this.cost = cost;
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

}
