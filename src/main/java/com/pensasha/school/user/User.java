package com.pensasha.school.user;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.pensasha.school.role.Role;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class User {

	@Id
	private String username;
	private String firstname;
	private String secondname;
	private String thirdname;
	private String password;
	private String email;
	private int phoneNumber;
	private String address;
	
	@ManyToOne
	@JoinColumn(name = "role_id")
	private Role role;
	
	public User(String username, String firstname, String secondname, String thirdname, String password, String email,
			int phoneNumber, String address) {
		super();
		this.username = username;
		this.firstname = firstname;
		this.secondname = secondname;
		this.thirdname = thirdname;
		this.password = password;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.address = address;
	}
	
	public User(String username, String firstname, String secondname, String thirdname, String password,
			int phoneNumber) {
		super();
		this.username = username;
		this.firstname = firstname;
		this.secondname = secondname;
		this.thirdname = thirdname;
		this.password = password;
		this.phoneNumber = phoneNumber;
	}

	public User(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	public User() {
		super();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getSecondname() {
		return secondname;
	}

	public void setSecondname(String secondname) {
		this.secondname = secondname;
	}

	public String getThirdname() {
		return thirdname;
	}

	public void setThirdname(String thirdname) {
		this.thirdname = thirdname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(int phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
}
