package com.pensasha.school.subject;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubjectService {

	@Autowired
	private SubjectRepository subjectRepository;

	// Adding subject to school
	public Subject addSubject(Subject subject) {
		return subjectRepository.save(subject);
	}
	
	//Does subject exists
	public Boolean doesSubjectExistsInSchool(String initials, int code) {
		return subjectRepository.existsByInitialsAndSchoolsCode(initials, code);
	}

	// Updating subject
	public Subject updateSubject(Subject subject) {
		return subjectRepository.save(subject);
	}
	
	//Getting all subjects
	public List<Subject> getAllSubjects(){
		return subjectRepository.findAll();
	}
	
	//Get subject by name
	public Subject getSubjectByName(String name) {
		return subjectRepository.findByName(name);
	}

	// Get all subject in school
	public List<Subject> getAllSubjectInSchool(int code) {
		return subjectRepository.findBySchoolsCode(code);
	}

	// Get all subjects in form and admNo
	public List<Subject> getAllSubjectsByFormAndAdmNo(int form, String admNo) {
		return subjectRepository.findByFormsFormAndStudentsAdmNo(form, admNo);
	}

	// Get all subjects in year, form and with admNo
	public List<Subject> getAllSubjectsByYearFormAndAdmNo(int year, int form, String admNo) {
		return subjectRepository.findByYearsYearAndFormsFormAndStudentsAdmNo(year, form, admNo);
	}

	// Get one subject in school
	public Subject getSubjectInSchool(String initials, int code) {
		return subjectRepository.findByInitialsAndSchoolsCode(initials, code);
	}

	// Get subject by initial
	public Subject getSubject(String initials) {
		return subjectRepository.findById(initials).get();
	}

	// delete subject in school
	public void deleteSubjectInSchool(String initials) {
		subjectRepository.deleteById(initials);
	}

	// Getting all subjects done by a student
	public List<Subject> getSubjectDoneByStudent(String admNo) {
		return subjectRepository.findByStudentsAdmNo(admNo);
	}

	// Getting subject done by student
	public Subject getSubjectByStudent(String initials, String admNo) {
		return subjectRepository.findByInitialsAndStudentsAdmNo(initials, admNo);
	}

}
