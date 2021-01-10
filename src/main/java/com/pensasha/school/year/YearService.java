package com.pensasha.school.year;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class YearService {

	@Autowired
	YearRepository yearRepository;

	// Getting all years from school
	public List<Year> getAllYearsInSchool(int code) {
		List<Year> years = new ArrayList<>();
		yearRepository.findBySchoolsCode(code).forEach(years::add);

		return years;
	}

	// Get all years by forms
	public List<Year> getAllYearsByForm(int form) {
		return yearRepository.findByFormsForm(form);
	}
	
	//Getting all years with exam name
	public List<Year> getAllYearsWithExamName(String name){
		return yearRepository.findByExamNamesName(name);
	}

	// Getting one year from school
	public Optional<Year> getYearFromSchool(int year, int code) {
		return yearRepository.findByYearAndSchoolsCode(year, code);
	}
	
	//If year exists in school
	public Boolean doesYearExistInSchool(int year, int code) {
		return yearRepository.existsByYearAndSchoolsCode(year, code);
	}

	// Getting all years for student
	public List<Year> allYearsForStudent(String admNo) {
		return yearRepository.findByStudentsAdmNo(admNo);
	}

	// Getting all year for a subject
	public List<Year> allYearsForSubject(String initials) {
		return yearRepository.findBySubjectsInitials(initials);
	}

	// Saving year
	public Year addYear(Year year) {
		return yearRepository.save(year);
	}

	// Updating year
	public Year updateYear(Year year) {
		return yearRepository.save(year);
	}

	// Deleting year
	public void deleteYearById(int year) {

		Year yearObj = yearRepository.findById(year).get();

		yearObj.getSchools().forEach(school -> {
			school.getYears().remove(yearObj);
		});
		yearObj.getForms().forEach(form -> {
			form.getYears().remove(yearObj);
		});

		yearRepository.deleteById(year);
	}

}
