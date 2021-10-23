package com.pensasha.school.finance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VoteHeadAllocationService {

	@Autowired
	private VoteHeadAllocationRepository voteHeadAllocationRepository;
	
	public VoteHeadAllocations saveVoteHeadAllocation(VoteHeadAllocations allocation) {
		return voteHeadAllocationRepository.save(allocation);
	}
	
}
