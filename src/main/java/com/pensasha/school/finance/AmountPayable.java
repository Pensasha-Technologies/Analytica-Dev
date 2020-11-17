package com.pensasha.school.finance;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import com.pensasha.school.term.Term;

@Entity
public class AmountPayable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	private int amount;
	
	@OneToOne
	private Term term;

	public AmountPayable(int amount, Term term) {
		super();
		this.amount = amount;
		this.term = term;
	}

	public AmountPayable(int amount) {
		super();
		this.amount = amount;
	}

	public AmountPayable() {
		super();
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public Term getTerm() {
		return term;
	}

	public void setTerm(Term term) {
		this.term = term;
	}

}
