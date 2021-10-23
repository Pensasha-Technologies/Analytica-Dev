package com.pensasha.school.finance;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class VoteHeadAllocations {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private String voteHead;
	
	private int amount;
	
	private int code;

	public VoteHeadAllocations(Long id, String voteHead, int amount, int code) {
		super();
		this.id = id;
		this.voteHead = voteHead;
		this.amount = amount;
		this.code = code;
	}

	public VoteHeadAllocations(String voteHead, int amount, int code) {
		super();
		this.voteHead = voteHead;
		this.amount = amount;
		this.code = code;
	}

	public VoteHeadAllocations() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getVoteHead() {
		return voteHead;
	}

	public void setVoteHead(String voteHead) {
		this.voteHead = voteHead;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
	
}
