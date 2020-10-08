package com.pensasha.school.subject;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.pensasha.school.school.School;
import com.pensasha.school.school.SchoolService;

@RestController
public class SubjectController {

	private SchoolService schoolService;
	private SubjectService subjectService;

	public SubjectController(SchoolService schoolService, SubjectService subjectService) {
		super();
		this.schoolService = schoolService;
		this.subjectService = subjectService;
	}

	// Getting all subjects in school
	@GetMapping("/api/schools/{code}/subjects")
	public List<Subject> getSubjectsInSchool(@PathVariable int code) {
		return subjectService.getAllSubjectInSchool(code);
	}

	// Get subject in school
	@GetMapping("/api/schools/{code}/subjects/{subject}")
	public Subject getSubjectInSchool(@PathVariable int code, @PathVariable String subject) {
		return subjectService.getSubjectInSchool(subject, code);
	}

	@GetMapping("/api/schools/{code}/years/{year}/subjects/form/{form}/student/{admNo}")
	public List<Subject> getAllSubjectsByStudentAndForm(@PathVariable int code, @PathVariable int year,
			@PathVariable int form, @PathVariable String admNo) {
		return subjectService.getAllSubjectsByYearFormAndAdmNo(year, form, code + "_" + admNo);
	}

	// Adding subject to school
	@PostMapping("/api/schools/{code}/subjects")
	public Subject addSubjectToSchool(@RequestBody Subject subject, @PathVariable int code) {

		List<School> schools = schoolService.getAllSchoolsWithSubject(subject.getInitials());
		schools.add(schoolService.getSchool(code).get());

		subject.setSchools(schools);

		return subjectService.addSubject(subject);
	}

	// Delete subject from school
	@DeleteMapping("/api/schools/{code}/subjects/{subject}")
	public void deleteSubjectFromSchool(@PathVariable int code, @PathVariable String subject) {

		Subject subjectObj = subjectService.getSubjectInSchool(subject, code);

		subjectObj.getSchools().remove(schoolService.getSchool(code).get());

		subjectService.addSubject(subjectObj);
	}

	// Getting subjects taken by student
	@GetMapping("/api/schools/{code}/years/{year}/forms/{form}/terms/{term}/students/{admNo}/subjects")
	public List<Subject> getSubjectByStudent(@PathVariable int code, @PathVariable String admNo) {
		return subjectService.getSubjectDoneByStudent(admNo);
	}

	// Getting subject taken by student
	@GetMapping("/api/schools/{code}/years/{year}/forms/{form}/terms/{term}/students/{admNo}/subjects/{subject}")
	public Subject getSubjectTakenByStudent(@PathVariable String admNo, @PathVariable String subject) {
		return subjectService.getSubjectByStudent(subject, admNo);
	}

}
