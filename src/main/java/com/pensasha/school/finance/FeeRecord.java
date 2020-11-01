package com.pensasha.school.finance;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pensasha.school.form.Form;
import com.pensasha.school.student.Student;
import com.pensasha.school.term.Term;

@Entity
public class FeeRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String receiptNo;
	private int amount;
	private String datePaid;

	@JsonIgnore
	@ManyToOne
	private Student student;

	@ManyToOne
	private Form form;

	@ManyToOne
	private Term term;

	public FeeRecord(int id, String receiptNo, int amount, String datePaid, Student student, Form form, Term term) {
		super();
		this.id = id;
		this.receiptNo = receiptNo;
		this.amount = amount;
		this.datePaid = datePaid;
		this.student = student;
		this.form = form;
		this.term = term;
	}

	public FeeRecord(String receiptNo, int amount, String datePaid, Student student, Form form, Term term) {
		super();
		this.receiptNo = receiptNo;
		this.amount = amount;
		this.datePaid = datePaid;
		this.student = student;
		this.form = form;
		this.term = term;
	}

	public FeeRecord() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
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

	public String getDatePaid() {
		return datePaid;
	}

	public void setDatePaid(String datePaid) {
		this.datePaid = datePaid;
	}

}
