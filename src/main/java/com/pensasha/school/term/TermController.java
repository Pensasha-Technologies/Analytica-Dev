package com.pensasha.school.term;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.pensasha.school.exam.ExamName;
import com.pensasha.school.exam.ExamNameService;
import com.pensasha.school.exam.Mark;
import com.pensasha.school.exam.MarkService;
import com.pensasha.school.form.Form;
import com.pensasha.school.form.FormService;
import com.pensasha.school.school.School;
import com.pensasha.school.school.SchoolService;
import com.pensasha.school.stream.Stream;
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
	private final TemplateEngine templateEngine;

	public TermController(SubjectService subjectService, SchoolService schoolService, StudentService studentService,
			YearService yearService, FormService formService, UserService userService, MarkService markService,
			ExamNameService examNameService, TemplateEngine templateEngine) {
		super();
		this.subjectService = subjectService;
		this.schoolService = schoolService;
		this.studentService = studentService;
		this.yearService = yearService;
		this.formService = formService;
		this.userService = userService;
		this.markService = markService;
		this.examNameService = examNameService;
		this.templateEngine = templateEngine;
	}

	@Autowired
	ServletContext servletContext;
	
	@PostMapping("/schools/{code}/student/{admNo}/termlyReport")
	public String getTermlyReport(@PathVariable int code, @PathVariable String admNo, @RequestParam int form,
			@RequestParam int year, @RequestParam int term, Model model, Principal principal) {

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
		model.addAttribute("year", year);
		model.addAttribute("form", form);
		model.addAttribute("term", term);
		
		return "termlyReport";
	}

	@GetMapping("/schools/{code}/years/{year}/forms/{form}/terms/{term}/students/{admNo}/termlyReport/pdf")
	public ResponseEntity<?> getPDF(@PathVariable int code, @PathVariable int form, @PathVariable int year,
			@PathVariable int term, @PathVariable String admNo, HttpServletRequest request,
			HttpServletResponse response, Principal principal) throws IOException {

		/* Do Business Logic */

		School school = schoolService.getSchool(code).get();
		Student student = studentService.getStudentInSchool(admNo, code);
		List<Subject> subjects = subjectService.getSubjectDoneByStudent(admNo);
		List<Year> years = yearService.allYearsForStudent(admNo);
		List<Form> forms = formService.studentForms(admNo);
		User activeUser = userService.getByUsername(principal.getName()).get();
		List<ExamName> examNames = examNameService.getExamBySchoolYearFormTerm(code, year, form, term);

		List<Mark> marks = markService.getTermlySubjectMark(admNo, form, term);

		/* Create HTML using Thymeleaf template Engine */

		WebContext context = new WebContext(request, response, servletContext);
		context.setVariable("activeUser", activeUser);
		context.setVariable("marks", marks);
		context.setVariable("forms", forms);
		context.setVariable("years", years);
		context.setVariable("subjects", subjects);
		context.setVariable("student", student);
		context.setVariable("school", school);
		context.setVariable("examNames", examNames);
		context.setVariable("year", year);
		context.setVariable("form", form);
		context.setVariable("term", term);
		String termlyReportHtml = templateEngine.process("termlyReportPdf", context);

		/* Setup Source and target I/O streams */
		ByteArrayOutputStream target = new ByteArrayOutputStream();

		/* Setup converter properties. */
		ConverterProperties converterProperties = new ConverterProperties();
		converterProperties.setBaseUri("http://localhost:8080");

		/* Call convert method */
		HtmlConverter.convertToPdf(termlyReportHtml, target, converterProperties);

		/* extract output as bytes */
		byte[] bytes = target.toByteArray();

		/* Send the response as downloadable PDF */
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(bytes);

	}

}
