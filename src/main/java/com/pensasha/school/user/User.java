package com.pensasha.school.user;

import com.pensasha.school.role.Role;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class User {
    @Id
    private String username;
    @NotNull
    @Size(min=3, max=24)
    private @NotNull @Size(min=3, max=24) String firstname;
    private String secondname;
    private String thirdname;
    private String password;
    private String email;
    private int phoneNumber;
    private String address;
    @ManyToOne
    @JoinColumn(name="role_id")
    private Role role;

    public User(String username, String firstname, String secondname, String thirdname, String password, String email, int phoneNumber, String address) {
        this.username = username;
        this.firstname = firstname;
        this.secondname = secondname;
        this.thirdname = thirdname;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public User(String username, String firstname, String secondname, String thirdname, String password, int phoneNumber) {
        this.username = username;
        this.firstname = firstname;
        this.secondname = secondname;
        this.thirdname = thirdname;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User() {
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return this.role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSecondname() {
        return this.secondname;
    }

    public void setSecondname(String secondname) {
        this.secondname = secondname;
    }

    public String getThirdname() {
        return this.thirdname;
    }

    public void setThirdname(String thirdname) {
        this.thirdname = thirdname;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}