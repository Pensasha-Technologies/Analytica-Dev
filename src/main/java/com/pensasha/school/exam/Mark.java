package com.pensasha.school.exam;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.pensasha.school.form.Form;
import com.pensasha.school.student.Student;
import com.pensasha.school.subject.Subject;
import com.pensasha.school.term.Term;
import com.pensasha.school.year.Year;

@Entity
public class Mark {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private int cat1;
	private int cat2;
	private int mainExam;

	@ManyToOne
	@JoinColumn(name = "Stud_AdmNo")
	private Student student;

	@ManyToOne
	@JoinColumn(name = "year")
	private Year year;

	@ManyToOne
	@JoinColumn(name = "form")
	private Form form;

	@ManyToOne
	@JoinColumn(name = "term")
	private Term term;

	@ManyToOne
	@JoinColumn(name = "subject")
	private Subject subject;

	public Mark(int id, int cat1, int cat2, int mainExam, Student student, Year year, Form form, Term term,
			Subject subject) {
		super();
		this.id = id;
		this.cat1 = cat1;
		this.cat2 = cat2;
		this.mainExam = mainExam;
		this.student = student;
		this.year = year;
		this.form = form;
		this.term = term;
		this.subject = subject;
	}

	public Mark(Student student, Year year, Form form, Term term) {
		super();
		this.student = student;
		this.year = year;
		this.form = form;
		this.term = term;
	}

	public Mark(int id, int cat1, int cat2, int mainExam) {
		super();
		this.id = id;
		this.cat1 = cat1;
		this.cat2 = cat2;
		this.mainExam = mainExam;
	}

	public Mark(int cat1, int cat2, int mainExam) {
		super();
		this.cat1 = cat1;
		this.cat2 = cat2;
		this.mainExam = mainExam;
	}

	public Mark() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCat1() {
		return cat1;
	}

	public void setCat1(int cat1) {
		this.cat1 = cat1;
	}

	public int getCat2() {
		return cat2;
	}

	public void setCat2(int cat2) {
		this.cat2 = cat2;
	}

	public int getMainExam() {
		return mainExam;
	}

	public void setMainExam(int mainExam) {
		this.mainExam = mainExam;
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

	public Term getTerm() {
		return term;
	}

	public void setTerm(Term term) {
		this.term = term;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

}
