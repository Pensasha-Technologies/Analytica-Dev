package com.pensasha.school.form;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FormController {

	@Autowired
	private FormService formService;

	// Getting all forms
	@GetMapping("/api/schools/{code}/years/{year}/forms")
	public List<Form> getAllFormInYearAndSchool(@PathVariable int code, @PathVariable int year) {
		return formService.getAllForms(year, code);
	}

	// Getting a form
	@GetMapping("/api/schools/{code}/years/{year}/forms/{form}")
	public Form getForm(@PathVariable int form, @PathVariable int code, @PathVariable int year) {
		return formService.getForm(form, year, code).get();
	}

	// Get form for student
	@GetMapping("/api/schools/{code}/students/{admNo}/forms/{form}")
	public Form getStudentForm(@PathVariable int code, @PathVariable String admNo, @PathVariable int form) {
		return formService.getStudentForm(form, code + "_" + admNo);
	}

	// Getting if a student belongs to a form
	@GetMapping("/api/students/{admNo}/forms/{form}")
	public Boolean ifStudentHasForm(@PathVariable int form, @PathVariable String admNo) {
		return formService.hasStudent(admNo, form);
	}
}
