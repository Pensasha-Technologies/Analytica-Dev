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

	//Getting all exam with examName
	public Boolean nameExists(String name){
		return examNameRepository.existsById(name);
	}
	
	// Getting all exam in school by year, form
	public List<ExamName> getExamBySchoolYearForm(int code, int year, int form) {
		return examNameRepository.findBySchoolsCodeAndYearsYearAndFormsForm(code, year, form);
	}

	//Getting all exam in school by year
	public List<ExamName> getExamBySchoolYear(int code, int year){
		return examNameRepository.findBySchoolsCodeAndYearsYear(code, year);
	}
	
	// Getting one exam
	public ExamName getExam(String name) {
		return examNameRepository.findById(name).get();
	}

	// Deleting exam
	public void deleteExam(String name) {
		examNameRepository.deleteById(name);
	}
}
