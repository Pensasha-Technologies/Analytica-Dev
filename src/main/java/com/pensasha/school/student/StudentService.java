package com.pensasha.school.student;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

	@Autowired
	private StudentRepository studentRepository;

	// Adding a student
	public Student addStudent(Student student) {
		return studentRepository.save(student);
	}

	// Getting all students in school
	public List<Student> getAllStudentsInSchool(int code) {
		return studentRepository.findBySchoolCode(code);
	}
	
	//Getting all student in school by year, form and stream
	public List<Student> getAllStudentsInSchoolByYearFormandStream(int code, int year, int form, String stream){
		return studentRepository.findBySchoolCodeAndFormsFormAndYearsYearAndStreamStream(code, form, year, stream);
	}
	
	//Getting all students in school year form term and stream
	public List<Student> getAllStudentinSchoolYearFormTermStream(int code, int year, int form, int term, String stream){
		return studentRepository.findBySchoolCodeAndYearsYearAndFormsFormAndFormsTermsTermAndStreamStream(code, year, form, term, stream);
	}
	
	//Getting all student in school by year, form and term
	public List<Student> getAllStudentsInSchoolByYearFormTerm(int code, int year, int form, int term){
		return studentRepository.findBySchoolCodeAndYearsYearAndFormsFormAndFormsTermsTerm(code, year, form, term);
	}
	
	// If student exists
	public Boolean ifStudentExists(String admNo) {
		return studentRepository.existsById(admNo);
	}

	// If student exists in school
	public Boolean ifStudentExistsInSchool(String admNo, int code) {
		return studentRepository.existsByAdmNoAndSchoolCode(admNo, code);
	}

	// Get a student in school
	public Student getStudentInSchool(String admNo, int code) {
		return studentRepository.findByAdmNoAndSchoolCode(admNo, code);
	}

	// Get all students in school by year and form
	public List<Student> getStudentsByFormAndYear(int code, int form, int year) {
		return studentRepository.findBySchoolCodeAndFormsFormAndYearsYear(code, form, year);
	}

	// Getting all students doing a certain subject
	public List<Student> getAllStudentsDoing(String initials) {
		return studentRepository.findBySubjectsInitials(initials);
	}

	// Get student in school by year and form
	public Student getStudentByFormAndYear(String admNo, int code, int form, int year) {
		return studentRepository.findByAdmNoAndSchoolCodeAndFormsFormAndYearsYear(admNo, code, form, year);
	}

	// updating student details
	public Student updateStudentDetails(String admNo, Student student) {
		return studentRepository.save(student);
	}

	// Deleting student
	public void deleteStudent(String admNo) {
		studentRepository.deleteById(admNo);
	}

	public List<Student> findAllStudentDoingSubject(int code, int year, int form, int term, String initials) {
		return studentRepository.findBySchoolCodeAndYearsYearAndFormsFormAndFormsTermsTermAndSubjectsInitials(code,
				year, form, term, initials);
	}
}
