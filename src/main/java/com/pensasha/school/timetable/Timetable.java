package com.pensasha.school.timetable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.pensasha.school.form.Form;
import com.pensasha.school.school.School;
import com.pensasha.school.stream.Stream;
import com.pensasha.school.term.Term;
import com.pensasha.school.year.Year;

@Entity
public class Timetable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String time;
	private String monday;
	private String tuesday;
	private String wednesday;
	private String thursday;
	private String friday;
	
	@ManyToOne
	private School school;
	
	@ManyToOne 
	private Year year;
	
	@ManyToOne
	private Form form;
	
	@ManyToOne
	private Term term;
	
	@ManyToOne
	private Stream stream;

	public Timetable(int id, String time, String monday, String tuesday, String wednesday, String thursday,
			String friday, School school, Year year, Form form, Term term, Stream stream) {
		super();
		this.id = id;
		this.time = time;
		this.monday = monday;
		this.tuesday = tuesday;
		this.wednesday = wednesday;
		this.thursday = thursday;
		this.friday = friday;
		this.school = school;
		this.year = year;
		this.form = form;
		this.term = term;
		this.stream = stream;
	}

	public Timetable(String time, String monday, String tuesday, String wednesday, String thursday, String friday) {
		super();
		this.time = time;
		this.monday = monday;
		this.tuesday = tuesday;
		this.wednesday = wednesday;
		this.thursday = thursday;
		this.friday = friday;
	}

	public Timetable() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getMonday() {
		return monday;
	}

	public void setMonday(String monday) {
		this.monday = monday;
	}

	public String getTuesday() {
		return tuesday;
	}

	public void setTuesday(String tuesday) {
		this.tuesday = tuesday;
	}

	public String getWednesday() {
		return wednesday;
	}

	public void setWednesday(String wednesday) {
		this.wednesday = wednesday;
	}

	public String getThursday() {
		return thursday;
	}

	public void setThursday(String thursday) {
		this.thursday = thursday;
	}

	public String getFriday() {
		return friday;
	}

	public void setFriday(String friday) {
		this.friday = friday;
	}

	public School getSchool() {
		return school;
	}

	public void setSchool(School school) {
		this.school = school;
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

	public Stream getStream() {
		return stream;
	}

	public void setStream(Stream stream) {
		this.stream = stream;
	}

}
