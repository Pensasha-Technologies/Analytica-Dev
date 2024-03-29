package com.pensasha.school.finance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pensasha.school.form.Form;

import java.util.List;

@Service
public class FeeStructureService {
    @Autowired
    private FeeStructureRepository feeStructureRepository;

    public List<FeeStructure> allFeeItemInSchool(int code) {
        return this.feeStructureRepository.findBySchoolCode(code);
    }

    public List<FeeStructure> allFeeItemInSchoolYear(int code, int year) {
        return this.feeStructureRepository.findBySchoolCodeAndYearYear(code, year);
    }

    public List<FeeStructure> allFeeItemInSchoolYearFormScholarTerm(int code, int year, int form, String scholar,int term) {
        return this.feeStructureRepository.findBySchoolCodeAndYearYearAndFormFormAndScholarAndTermTerm(code, year, form, scholar, term);
    }
    
    public List<FeeStructure> allFeeItemInSchoolYearFormScholar(int code, int year, Form form, String scholar){
    	return this.feeStructureRepository.findBySchoolCodeAndYearYearAndFormFormAndScholar(code, year, form, scholar);
    }

    public FeeStructure addItem(FeeStructure feeStructure) {
        return this.feeStructureRepository.save(feeStructure);
    }

    public void deleteFeeStructureItem(int id) {
        this.feeStructureRepository.deleteById(id);
    }
}