package com.pensasha.school.exam;

import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
	
	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "mark_names", joinColumns = @JoinColumn(name = "mark_id"), inverseJoinColumns = @JoinColumn(name = "examName_id"))
	private Collection<ExamName> examNames;

	public Mark(int id, int mark, Student student, Year year, Form form, Term term, Subject subject,
			Collection<ExamName> examNames) {
		super();
		this.id = id;
		this.mark = mark;
		this.student = student;
		this.year = year;
		this.form = form;
		this.term = term;
		this.subject = subject;
		this.examNames = examNames;
	}
	
	public Mark(Student student, Year year, Form form, Term term, Subject subject) {
		super();
		this.student = student;
		this.year = year;
		this.form = form;
		this.term = term;
		this.subject = subject;
	}

	public Mark(Student student, Year year, Form form, Term term, Subject subject, List<ExamName> examNames) {
		super();
		this.student = student;
		this.year = year;
		this.form = form;
		this.term = term;
		this.subject = subject;
		this.examNames = examNames;
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

	public Collection<ExamName> getExamNames() {
		return examNames;
	}

	public void setExamNames(Collection<ExamName> examNames) {
		this.examNames = examNames;
	}

}
