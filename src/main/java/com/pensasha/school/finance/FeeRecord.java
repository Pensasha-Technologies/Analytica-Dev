package com.pensasha.school.finance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pensasha.school.form.Form;
import com.pensasha.school.student.Student;

import javax.persistence.*;

@Entity
public class FeeRecord {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    private String receiptNo;
    private int amount;
    private String datePaid;
    @JsonIgnore
    @ManyToOne
    private Student student;
    @ManyToOne
    private Form form;

    public FeeRecord(int id, String receiptNo, int amount, String datePaid, Student student, Form form) {
        this.id = id;
        this.receiptNo = receiptNo;
        this.amount = amount;
        this.datePaid = datePaid;
        this.student = student;
        this.form = form;
    }

    public FeeRecord(String receiptNo, int amount, String datePaid, Student student, Form form) {
        this.receiptNo = receiptNo;
        this.amount = amount;
        this.datePaid = datePaid;
        this.student = student;
        this.form = form;
    }

    public FeeRecord() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReceiptNo() {
        return this.receiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Student getStudent() {
        return this.student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getDatePaid() {
        return this.datePaid;
    }

    public void setDatePaid(String datePaid) {
        this.datePaid = datePaid;
    }

    public Form getForm() {
        return this.form;
    }

    public void setForm(Form form) {
        this.form = form;
    }
}