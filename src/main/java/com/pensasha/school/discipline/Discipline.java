package com.pensasha.school.discipline;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.pensasha.school.student.Student;

@Entity
public class Discipline {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String depature;
	private String arrival;
	private String reason;
	private String type;

	@ManyToOne
	private Student student;

	public Discipline(int id, String depature, String arrival, String reason, String type,
			Student student) {
		super();
		this.id = id;
		this.depature = depature;
		this.arrival = arrival;
		this.reason = reason;
		this.type = type;
		this.student = student;
	}

	public Discipline(String depature, String arrival, String reason, String type, Student student) {
		super();
		this.depature = depature;
		this.arrival = arrival;
		this.reason = reason;
		this.type = type;
		this.student = student;
	}

	public Discipline() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDepature() {
		return depature;
	}

	public void setDepature(String depature) {
		this.depature = depature;
	}

	public String getArrival() {
		return arrival;
	}

	public void setArrival(String arrival) {
		this.arrival = arrival;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

}
