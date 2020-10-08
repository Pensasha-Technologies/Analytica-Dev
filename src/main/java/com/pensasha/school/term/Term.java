package com.pensasha.school.term;

import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pensasha.school.form.Form;

@Entity
public class Term {

	@Id
	private int term;

	@JsonIgnore
	@ManyToMany(mappedBy = "terms", cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	private Collection<Form> forms;

	public Term(int term, Collection<Form> forms) {
		super();
		this.term = term;
		this.forms = forms;
	}

	public Term(int term) {
		super();
		this.term = term;
	}

	public Term() {
		super();
	}

	public int getTerm() {
		return term;
	}

	public void setTerm(int term) {
		this.term = term;
	}

	public Collection<Form> getForms() {
		return forms;
	}

	public void setForms(Collection<Form> forms) {
		this.forms = forms;
	}

}
