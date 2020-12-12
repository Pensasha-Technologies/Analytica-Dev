package com.pensasha.school.term;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pensasha.school.exam.ExamName;
import com.pensasha.school.exam.ExamNameService;
import com.pensasha.school.exam.Mark;
import com.pensasha.school.exam.MarkService;
import com.pensasha.school.form.Form;
import com.pensasha.school.form.FormService;
import com.pensasha.school.school.School;
import com.pensasha.school.school.SchoolService;
import com.pensasha.school.student.Student;
import com.pensasha.school.student.StudentService;
import com.pensasha.school.subject.Subject;
import com.pensasha.school.subject.SubjectService;
import com.pensasha.school.user.User;
import com.pensasha.school.user.UserService;
import com.pensasha.school.year.Year;
import com.pensasha.school.year.YearService;

@Controller
public class TermController {

	private SubjectService subjectService;
	private SchoolService schoolService;
	private StudentService studentService;
	private YearService yearService;
	private FormService formService;
	private UserService userService;
	private MarkService markService;
	private ExamNameService examNameService;

	public TermController(SubjectService subjectService, SchoolService schoolService, StudentService studentService,
			YearService yearService, FormService formService, UserService userService, MarkService markService,
			ExamNameService examNameService) {
		super();
		this.subjectService = subjectService;
		this.schoolService = schoolService;
		this.studentService = studentService;
		this.yearService = yearService;
		this.formService = formService;
		this.userService = userService;
		this.markService = markService;
		this.examNameService = examNameService;
	}

	@PostMapping("/schools/{code}/student/{admNo}/termlyReport")
	public String getTermlyReport(@PathVariable int code, @PathVariable String admNo, @RequestParam int form, @RequestParam int year,
			@RequestParam int term, Model model, Principal principal) {

		School school = schoolService.getSchool(code).get();
		Student student = studentService.getStudentInSchool(admNo, code);
		List<Subject> subjects = subjectService.getSubjectDoneByStudent(admNo);
		List<Year> years = yearService.allYearsForStudent(admNo);
		List<Form> forms = formService.studentForms(admNo);
		User activeUser = userService.getByUsername(principal.getName()).get();
		List<ExamName> examNames = examNameService.getExamBySchoolYearFormTerm(code, year, form, term);
		
		List<Mark> marks = markService.getTermlySubjectMark(admNo, form, term);
		
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("marks", marks);
		model.addAttribute("forms", forms);
		model.addAttribute("years", years);
		model.addAttribute("subjects", subjects);
		model.addAttribute("student", student);
		model.addAttribute("school", school);
		model.addAttribute("examNames", examNames);

		return "termlyReport";
	}
}
