package com.pensasha.school.exam;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MarkService {

	@Autowired
	private MarkRepository markRepository;

	// Getting mark by student in a year, form, term, subject
	public Mark getMarkByStudentOnAsubject(String admNo, int year, int form, int term, String subject) {
		return markRepository.findByStudentAdmNoAndYearYearAndFormFormAndTermTermAndSubjectInitials(admNo, year, form,
				term, subject);
	}
	
	//Getting mark by student in a year, form, term, subject and examName
	public Mark getMarksByStudentOnSubjectByExamId(String admNo, int year, int form, int term, String subject, int id) {
		return markRepository.findByStudentAdmNoAndYearYearAndFormFormAndTermTermAndSubjectInitialsAndExamNamesId(admNo, year, form,
				term, subject, id);
	}

	// Get a list of all subject marks
	public Mark getAllSubjectMarks(String admNo, int year, int form, int term) {
		return markRepository.findByStudentAdmNoAndYearYearAndFormFormAndTermTerm(admNo, year, form, term);
	}
	
	//Get a list of all subject marks for term
	public List<Mark> getTermlySubjectMark(String admNo, int form, int term) {
		return markRepository.findByStudentAdmNoAndFormFormAndTermTerm(admNo, form, term);
	}
	
	//Getting all students by school, term, form and year
	public List<Mark> getAllStudentsMarksBySchoolYearFormAndTerm(int code, int form, int term, int year){
		return markRepository.findByStudentSchoolCodeAndYearYearAndFormFormAndTermTerm(code, year, form, term);
	}

	// Getting all marks
	public List<Mark> allMarks(String admNo) {
		return markRepository.findByStudentAdmNo(admNo);
	}

	// Getting mark by admNo
	public Boolean getMarkByAdm(String admNo) {
		return markRepository.existsByStudentAdmNo(admNo);
	}

	// Adding marks to a subject form a student in a term,form and year
	public Mark addMarksToSubject(Mark mark) {
		return markRepository.save(mark);
	}

	// Updating marks to a subject form a student in a term, form and year
	public Mark updateMark(Mark mark) {
		return markRepository.save(mark);
	}

	// Delete mark from student
	public void deleteMark(int id) {
		markRepository.deleteById(id);
	}
}
