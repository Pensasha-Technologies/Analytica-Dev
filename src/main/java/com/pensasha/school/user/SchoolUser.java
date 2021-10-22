package com.pensasha.school.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pensasha.school.school.School;

import javax.persistence.*;

@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@Entity
public class SchoolUser extends User {
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="school_code")
    private School school;

    public SchoolUser(String username, String firstname, String secondname, String thirdname, String password, String email, int phoneNumber, String address) {
        super(username, firstname, secondname, thirdname, password, email, phoneNumber, address);
    }

    public SchoolUser() {
    }

    public School getSchool() {
        return this.school;
    }

    public void setSchool(School school) {
        this.school = school;
    }
}