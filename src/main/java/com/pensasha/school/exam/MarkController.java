package com.pensasha.school.exam;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.pensasha.school.form.Form;
import com.pensasha.school.form.FormService;
import com.pensasha.school.student.Student;
import com.pensasha.school.student.StudentService;
import com.pensasha.school.subject.Subject;
import com.pensasha.school.subject.SubjectService;
import com.pensasha.school.term.Term;
import com.pensasha.school.term.TermService;
import com.pensasha.school.year.Year;
import com.pensasha.school.year.YearService;

@RestController
public class MarkController {

	private StudentService studentService;
	private MarkService markService;
	private FormService formService;
	private YearService yearService;
	private TermService termService;
	private SubjectService subjectService;

	public MarkController(StudentService studentService, MarkService markService, FormService formService,
			YearService yearService, TermService termService, SubjectService subjectService) {
		super();
		this.studentService = studentService;
		this.markService = markService;
		this.formService = formService;
		this.yearService = yearService;
		this.termService = termService;
		this.subjectService = subjectService;
	}
	
	//Get all marks
	@GetMapping("/api/schools/{code}/student/{admNo}/yearly")
	public List<Mark> getAllMarks(@PathVariable String admNo){
		return markService.allMarks(admNo);
	}

	// Get All student marks
	@GetMapping("/api/schools/{code}/years/{year}/forms/{form}/terms/{term}/students/{admNo}/marks")
	public Mark getAllStudentMarks(@PathVariable int code, @PathVariable int year, @PathVariable int form, @PathVariable int term, @PathVariable String admNo){
		return markService.getAllSubjectMarks(admNo, year, form, term);
	}

	// Adding marks to subject
	@PostMapping("/api/schools/{code}/years/{year}/forms/{form}/terms/{term}/students/{admNo}/subjects/{subject}/mark")
	public Mark addMark(@PathVariable int code, @PathVariable int year, @PathVariable int form, @PathVariable int term,
			@PathVariable String admNo, @PathVariable String subject, @RequestBody Mark mark) {

		Student student = studentService.getStudentInSchool(admNo, code);
		Year yearObj = yearService.getYearFromSchool(year, code).get();
		Form formObj = formService.getForm(form, year, code).get();
		Term termObj = termService.getTerm(term, form, year, code);
		Subject subjectObj = subjectService.getSubject(subject);

		Mark markObj = new Mark(mark.getId(), mark.getCat1(), mark.getCat2(), mark.getMainExam(), student, yearObj,
				formObj, termObj, subjectObj);

		return markService.addMarksToSubject(markObj);
	}
}
