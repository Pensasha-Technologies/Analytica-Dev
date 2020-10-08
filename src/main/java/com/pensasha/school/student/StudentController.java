package com.pensasha.school.student;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.pensasha.school.form.Form;
import com.pensasha.school.school.SchoolService;
import com.pensasha.school.subject.Subject;
import com.pensasha.school.year.Year;

@RestController
public class StudentController {

	private StudentService studentService;
	private SchoolService schoolService;

	public StudentController(StudentService studentService, SchoolService schoolService) {
		super();
		this.studentService = studentService;
		this.schoolService = schoolService;
	}

	// Getting students in a school
	@GetMapping("/api/schools/{code}/students")
	public List<Student> getStudentInSchool(@PathVariable int code) {
		return studentService.getAllStudentsInSchool(code);
	}

	// Get student in a school
	@GetMapping("/api/schools/{code}/students/{admNo}")
	public Student getStudentInSchool(@PathVariable int code, @PathVariable String admNo) {
		return studentService.getStudentInSchool(admNo, code);
	}

	// Get students by form and year
	@GetMapping("/api/schools/{code}/years/{year}/forms/{form}/terms/{term}/students")
	public List<Student> getStudent(@PathVariable int code, @PathVariable int year, @PathVariable int form) {
		return studentService.getStudentsByFormAndYear(code, form, year);
	}

	// Get student by form and year
	@GetMapping("/api/schools/{code}/years/{year}/forms/{form}/terms/{term}/students/{admNo}")
	public Student getStudentByFormAndYear(@PathVariable String admNo, @PathVariable int code, @PathVariable int year,
			@PathVariable int form) {
		return studentService.getStudentByFormAndYear(admNo, code, form, year);
	}

	// Cheching if student is present
	@GetMapping("/api/schools/{code}/student/{admNo}")
	public Boolean isStudentPresent(@PathVariable int code, @PathVariable String admNo) {

		String endAdmin = code + "_" + admNo;
		return studentService.ifStudentExists(endAdmin);

	}

	// Adding a student with school code, year and form
	@PostMapping("/api/schools/{code}/years/{year}/forms/{form}/terms/{term}/students")
	public Student addStudent(@PathVariable int code, @PathVariable int year, @PathVariable int form,
			@PathVariable int term, @RequestBody Student student) {

		student.setSchool(schoolService.getSchool(code).get());

		List<Year> years = new ArrayList<>();
		years.add(new Year(year));
		student.setYears(years);

		List<Form> forms = new ArrayList<>();
		forms.add(new Form(form));
		student.setForms(forms);

		return studentService.addStudent(student);

	}

	// Updating student details
	@PutMapping("/api/schools/{code}/years/{year}/forms/{form}/terms/{term}/students/{admNo}")
	public Student updateStudentDetails(@RequestBody Student student) {
		return studentService.addStudent(student);
	}

	// Deleting a student
	@DeleteMapping("/api/schools/{code}/years/{year}/forms/{form}/terms/{term}/students/{admNo}")
	public void deleteStudentByAdmNo(String admNo) {
		studentService.deleteStudent(admNo);
	}

	// Assigning subject to student
	@PostMapping("/api/schools/{code}/years/{year}/forms/{form}/terms/{term}/students/{admNo}/subjects")
	public Student assignSubjectToStudent(@PathVariable Subject subject, @PathVariable String admNo,
			@PathVariable int code, @PathVariable int year, @PathVariable int form) {
		Student student = studentService.getStudentByFormAndYear(admNo, code, form, year);

		List<Subject> subjects = new ArrayList<>();
		subjects.add(subject);

		student.setSubjects(subjects);

		return studentService.addStudent(student);
	}
}
