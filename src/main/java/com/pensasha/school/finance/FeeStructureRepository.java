package com.pensasha.school.finance;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FeeStructureRepository extends JpaRepository<FeeStructure, Integer>{
	
	public List<FeeStructure> findBySchoolCode(int code);

	public List<FeeStructure> findBySchoolCodeAndYearYearAndFormFormAndTermTerm(int code, int year, int form, int term);

}
