package com.pensasha.school.stream;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pensasha.school.school.School;

@Entity
public class Stream {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String stream;

	@ManyToOne
	@JsonIgnore
	@JoinColumn(name = "school_code")
	private School school;

	public Stream(int id, String stream, School school) {
		super();
		this.id = id;
		this.stream = stream;
		this.school = school;
	}

	public Stream(int id, String stream) {
		super();
		this.id = id;
		this.stream = stream;
	}

	public Stream(String stream) {
		super();
		this.stream = stream;
	}

	public Stream() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStream() {
		return stream;
	}

	public void setStream(String stream) {
		this.stream = stream;
	}

	public School getSchool() {
		return school;
	}

	public void setSchool(School school) {
		this.school = school;
	}

}
