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
	private int mark;

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
	
	@ManyToOne
	private ExamName examName;
	
	public Mark(int id, int mark, Student student, Year year, Form form, Term term, Subject subject,
			ExamName examName) {
		super();
		this.id = id;
		this.mark = mark;
		this.student = student;
		this.year = year;
		this.form = form;
		this.term = term;
		this.subject = subject;
		this.examName = examName;
	}

	public Mark(Student student, Year year, Form form, Term term, Subject subject) {
		super();
		this.student = student;
		this.year = year;
		this.form = form;
		this.term = term;
		this.subject = subject;
	}
	
	public Mark(Student student, Form form, Term term, Subject subject, ExamName examName) {
		super();
		this.student = student;
		this.form = form;
		this.term = term;
		this.subject = subject;
		this.examName = examName;
	}

	public Mark(int id, int mark) {
		super();
		this.id = id;
		this.mark = mark;
	}

	public Mark(int mark) {
		super();
		this.mark = mark;
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

	public int getMark() {
		return mark;
	}

	public void setMark(int mark) {
		this.mark = mark;
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

	public ExamName getExamName() {
		return examName;
	}

	public void setExamName(ExamName examName) {
		this.examName = examName;
	}

}
