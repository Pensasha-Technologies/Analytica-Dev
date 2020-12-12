package com.pensasha.school.form;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FormService {

	@Autowired
	FormRepository formRepository;

	// Getting all forms
	public List<Form> getAllForms(int year, int code) {
		return formRepository.findByYearsYearAndYearsSchoolsCode(year, code);
	}

	// Getting all form for student
	public List<Form> studentForms(String admNo) {
		return formRepository.findByStudentsAdmNo(admNo);
	}

	// Getting all form by subject
	public List<Form> getAllFormsBySubject(String initials) {
		return formRepository.findBySubjectsInitials(initials);
	}
	
	//Getting all form by exam name
	public List<Form> getAllFormsByExamName(String name){
		return formRepository.findByExamNamesName(name);
	}

	// Getting a form
	public Optional<Form> getForm(int form, int year, int code) {
		return formRepository.findByFormAndYearsYearAndYearsSchoolsCode(form, year, code);
	}

	//If form exists
	public Boolean ifFormExists(int form, int year, int code) {
		return formRepository.existsByFormAndYearsYearAndYearsSchoolsCode(form, year, code);
	}
	
	// Get form by student
	public Form getStudentForm(int form, String admNo) {
		return formRepository.findByFormAndStudentsAdmNo(form, admNo);
	}
	
	//Getting form by form
	public Form getFormByForm(int form) {
		return formRepository.findByForm(form);
	}

	// Checking if a form has student
	public Boolean hasStudent(String admNo, int form) {
		return formRepository.existsByFormAndStudentsAdmNo(form, admNo);
	}

	// Adding a form
	public Form addForm(Form form) {
		return formRepository.save(form);
	}

	// Deleting a form
	public void deleteForm(int form) {

		formRepository.deleteById(form);
	}

}
