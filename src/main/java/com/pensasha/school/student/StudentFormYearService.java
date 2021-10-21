package com.pensasha.school.student;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentFormYearService {

	@Autowired
	StudentFormYearRepository recordRepository;
	
	// Saving student form and year data
	public StudentFormYear addStudentFormYearRecord(StudentFormYear record) {
		return recordRepository.save(record);
	}
	
	// Getting all with a partular year, form and stream
	public List<StudentFormYear> getAllStudentFormYearbyFormYearandStream(int code, int year, int form, String stream){
		return recordRepository.findByStudentSchoolCodeAndYearYearAndFormFormAndStudentStreamStream(code, year, form, stream);
	}
	
	// Getting all student in a particular year, form, term and stream
	public List<StudentFormYear> getAllStudentFormYearByFormTermAndStream(int code, int year, int form, int term, String stream){
		return recordRepository.findByStudentSchoolCodeAndYearYearAndFormFormAndFormTermsTermAndStudentStreamStream(code, year, form, term, stream);
	}
	
	// Getting all students doing subject in stream
	public List<StudentFormYear> findAllStudentDoingSubjectInStream(int code, int year, int form, int term, String initials, int stream) {
        return recordRepository.findByStudentSchoolCodeAndYearYearAndFormFormAndFormTermsTermAndStudentSubjectsInitialsAndStudentStreamId(code, year, form, term, initials, stream);
    }
	
	// Getting all students in code, form, year and term
	public List<StudentFormYear> getAllStudentsInSchoolByYearFormTerm(int code, int year, int form, int term) {
		return recordRepository.findByStudentSchoolCodeAndYearYearAndFormFormAndFormTermsTerm(code, year, form, term);
	}
	
	// Getting all students in form, year and term
	public List<StudentFormYear> getAllStudentFormYearByStudentAdmNo(String admNo){
		return recordRepository.findByStudentAdmNo(admNo);
	}
	
	// Deleting student details from table
	public void deleteStudentFormYearDetails(String admNo) {
		recordRepository.deleteByStudentAdmNo(admNo);
	}
	
	//Delete by id
	public void deleteStudentById(Long id) {
		recordRepository.deleteById(id);
	}
	
	// Getting all studentFormYear in School
	public List<StudentFormYear> getAllStudentFormYearInSchool(int code){
		return recordRepository.findByStudentSchoolCode(code);
	}
	
}
