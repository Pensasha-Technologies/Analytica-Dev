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
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    
    private String depature;
    
    private String arrival;
    
    private String reason;
    
    private String type;
    
    @ManyToOne
    private Student student;

    public Discipline(int id, String depature, String arrival, String reason, String type, Student student) {
        this.id = id;
        this.depature = depature;
        this.arrival = arrival;
        this.reason = reason;
        this.type = type;
        this.student = student;
    }

    public Discipline(String depature, String arrival, String reason, String type, Student student) {
        this.depature = depature;
        this.arrival = arrival;
        this.reason = reason;
        this.type = type;
        this.student = student;
    }

    public Discipline() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDepature() {
        return this.depature;
    }

    public void setDepature(String depature) {
        this.depature = depature;
    }

    public String getArrival() {
        return this.arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Student getStudent() {
        return this.student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}