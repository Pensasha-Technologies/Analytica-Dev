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
	
	public List<FeeStructure> allFeeItemInSchoolYearFormTerm(int code, int year, int form, int term) {

		return feeStructureRepository.findBySchoolCodeAndYearYearAndFormFormAndTermTerm(code, year, form, term);
	}

	public FeeStructure addItem(FeeStructure feeStructure) {

		return feeStructureRepository.save(feeStructure);
	}

	public void deleteFeeStructureItem(int id) {

		feeStructureRepository.deleteById(id);
	}
}
