package com.pensasha.school.finance;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeeStructureService {

	@Autowired
	private FeeStructureRepository feeStructureRepository;

	public List<FeeStructure> allFeeItemInSchool(int code){
		
		return feeStructureRepository.findBySchoolCode(code);
	}
	
	public List<FeeStructure> allFeeItemInSchoolAndForm(int code, int form) {

		return feeStructureRepository.findBySchoolCodeAndFormForm(code, form);
	}

	public FeeStructure addItem(FeeStructure feeStructure) {

		return feeStructureRepository.save(feeStructure);
	}

	public void deleteFeeStructureItem(int id) {

		feeStructureRepository.deleteById(id);
	}
}
