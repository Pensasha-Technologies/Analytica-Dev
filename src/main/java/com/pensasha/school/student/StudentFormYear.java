package com.pensasha.school.student;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pensasha.school.form.Form;
import com.pensasha.school.year.Year;

import javax.persistence.*;

@Entity
public class StudentFormYear {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="adm_no")
	private Student student;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="year")
	private Year year;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="form")
	private Form form;

	public StudentFormYear(Student student, Year year, Form form) {
		super();
		this.student = student;
		this.year = year;
		this.form = form;
	}

	public StudentFormYear() {
		super();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public Year getYear() {
		return year;
	}

	public void setYear(Year year) {
		this.year = year;
	}

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}
		
	
}
