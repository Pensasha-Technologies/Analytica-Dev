package com.pensasha.school.user;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pensasha.school.role.Role;
import com.pensasha.school.school.School;

@Entity
public class User {

	@Id
	private String username;
	private String name;
	private String password;

	
	@ManyToOne
	@JoinColumn(name = "role_id")
	private Role role;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "school_code")
	private School school;

	public User() {
		super();
	}

	public User(String username, String name, String password) {
		super();
		this.username = username;
		this.name = name;
		this.password = password;
	}

	public User(String username, String name, String password, Role role) {
		super();
		this.username = username;
		this.name = name;
		this.password = password;
		this.role = role;
	}

	public User(String username, String name, String role, int code) {
		super();
		this.username = username;
		this.name = name;
		this.role = new Role(role);
		this.school = new School("", code);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public School getSchool() {
		return school;
	}

	public void setSchool(School school) {
		this.school = school;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
