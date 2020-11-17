package com.pensasha.school.finance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AmountPayableService {

	@Autowired
	private AmountPayableRepository amountPayableRepository;
	
	public AmountPayable addAmountPayable(AmountPayable amountPayable) {
		
		return amountPayableRepository.save(amountPayable);
	}
	
	public AmountPayable editAmountPayable(AmountPayable amountPayable) {
		
		return amountPayableRepository.save(amountPayable);
	}
	
	public AmountPayable getAmountPayableBySchoolIdFormTerm(int code, int form, int term) {
		 
		return amountPayableRepository.findByTermFormsStudentsSchoolCodeAndTermFormsFormAndTermTerm(code, form, term);
	}
	
	public void deleteAmountPayable(int id) {
		amountPayableRepository.deleteById(id);
	}
}
