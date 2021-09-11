package com.pensasha.school.exam;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExamNameService {

	@Autowired
	ExamNameRepository examNameRepository;

	// Adding exam
	public ExamName addExam(ExamName examName) {
		return examNameRepository.save(examName);
	}

	// Updating exam
	public ExamName updateExam(ExamName examName) {
		return examNameRepository.save(examName);
	}

	//Get all exam in school
	public List<ExamName> getAllExamInSchool(int id){
		return examNameRepository.findBySchoolsCode(id);
	}

	//Getting all exam with examName
	public Boolean nameExists(int id){
		return examNameRepository.existsById(id);
	}

	//Getting exam in school by id, year, form and term
	public ExamName getExamByIdSchoolCodeYearFormTerm(int id, int code, int year, int form, int term) {
		
		return examNameRepository.findByIdAndSchoolsCodeAndYearsYearAndFormsFormAndTermsTerm(id, code, year, form, term);
	}
	
	//Getting all exam in school by year, form and term
	public List<ExamName> getExamBySchoolYearFormTerm(int code, int year, int form, int term){
		return examNameRepository.findBySchoolsCodeAndYearsYearAndFormsFormAndTermsTerm(code, year, form, term);
	}
	
	// Getting all exam in school by year, form
	public List<ExamName> getExamBySchoolYearForm(int code, int year, int form) {
		return examNameRepository.findBySchoolsCodeAndYearsYearAndFormsForm(code, year, form);
	}
	
	// Getting all exam in school 

	//Getting all exam in school by year
	public List<ExamName> getExamBySchoolYear(int code, int year){
		return examNameRepository.findBySchoolsCodeAndYearsYear(code, year);
	}
	
	// Getting one exam
	public ExamName getExam(int id) {
		return examNameRepository.findById(id).get();
	}

	// Deleting exam
	public void deleteExam(int id) {
		examNameRepository.deleteById(id);
	}
}
