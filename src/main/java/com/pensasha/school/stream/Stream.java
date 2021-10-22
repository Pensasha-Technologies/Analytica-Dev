package com.pensasha.school.stream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pensasha.school.school.School;
import com.pensasha.school.user.Teacher;

import javax.persistence.*;
import java.util.List;

@Entity
public class Stream {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    private String stream;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="school_code")
    private School school;
    @JsonIgnore
    @ManyToMany(mappedBy="streams", cascade={CascadeType.MERGE, CascadeType.PERSIST})
    private List<Teacher> teachers;
    
    public Stream(int id, String stream, School school) {
        this.id = id;
        this.stream = stream;
        this.school = school;
    }

    public Stream(int id, String stream) {
        this.id = id;
        this.stream = stream;
    }

    public Stream(String stream) {
        this.stream = stream;
    }

    public Stream() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStream() {
        return this.stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public School getSchool() {
        return this.school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public List<Teacher> getTeachers() {
        return this.teachers;
    }

    public void setTeachers(List<Teacher> teachers) {
        this.teachers = teachers;
    }
    
}