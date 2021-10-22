package com.pensasha.school.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pensasha.school.form.Form;
import com.pensasha.school.stream.Stream;
import com.pensasha.school.subject.Subject;
import com.pensasha.school.year.Year;

import javax.persistence.*;

@Entity
public class TeacherYearFormStream {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="username")
	private Teacher teacher;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="year")
	private Year year;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="form")
	private Form form;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="stream_id")
	private Stream stream;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="initials")
	private Subject subject;

	public TeacherYearFormStream(Long id, Teacher teacher, Year year, Form form, Stream stream, Subject subject) {
		super();
		this.id = id;
		this.teacher = teacher;
		this.year = year;
		this.form = form;
		this.stream = stream;
		this.subject = subject;
	}
	
	public TeacherYearFormStream(Teacher teacher, Year year, Form form, Stream stream, Subject subject) {
		super();
		this.teacher = teacher;
		this.year = year;
		this.form = form;
		this.stream = stream;
		this.subject = subject;
	}

	public TeacherYearFormStream() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Teacher getTeacher() {
		return teacher;
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
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

	public Stream getStream() {
		return stream;
	}

	public void setStream(Stream stream) {
		this.stream = stream;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	
}
