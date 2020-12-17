package com.pensasha.school.exam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.*;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.pensasha.school.form.Form;
import com.pensasha.school.form.FormService;
import com.pensasha.school.school.School;
import com.pensasha.school.school.SchoolService;
import com.pensasha.school.stream.Stream;
import com.pensasha.school.stream.StreamService;
import com.pensasha.school.student.Student;
import com.pensasha.school.student.StudentService;
import com.pensasha.school.subject.Subject;
import com.pensasha.school.subject.SubjectService;
import com.pensasha.school.term.Term;
import com.pensasha.school.term.TermService;
import com.pensasha.school.user.User;
import com.pensasha.school.user.UserService;
import com.pensasha.school.year.Year;
import com.pensasha.school.year.YearService;

@Controller
public class MarkController {

	private StudentService studentService;
	private MarkService markService;
	private FormService formService;
	private YearService yearService;
	private TermService termService;
	private SubjectService subjectService;
	private UserService userService;
	private StreamService streamService;
	private SchoolService schoolService;
	private ExamNameService examNameService;
	private final TemplateEngine templateEngine;

	public MarkController(StudentService studentService, MarkService markService, FormService formService,
			YearService yearService, TermService termService, SubjectService subjectService, UserService userService,
			StreamService streamService, SchoolService schoolService, ExamNameService examNameService,
			TemplateEngine templateEngine) {
		super();
		this.studentService = studentService;
		this.markService = markService;
		this.formService = formService;
		this.yearService = yearService;
		this.termService = termService;
		this.subjectService = subjectService;
		this.userService = userService;
		this.streamService = streamService;
		this.schoolService = schoolService;
		this.examNameService = examNameService;
		this.templateEngine = templateEngine;
	}

	@Autowired
	ServletContext servletContext;

	@GetMapping("/schools/{code}/years/{year}/examination")
	public String examinations(@PathVariable int code, @PathVariable int year, Model model, Principal principal) {

		User activeUser = userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(code).get();
		Student student = new Student();

		List<ExamName> examNames = examNameService.getExamBySchoolYear(code, year);

		List<ExamName> form1term1 = examNameService.getExamBySchoolYearFormTerm(code, year, 1, 1);
		List<ExamName> form1term2 = examNameService.getExamBySchoolYearFormTerm(code, year, 1, 2);
		List<ExamName> form1term3 = examNameService.getExamBySchoolYearFormTerm(code, year, 1, 3);

		model.addAttribute("activeUser", activeUser);
		model.addAttribute("school", school);
		model.addAttribute("student", student);
		model.addAttribute("examNames", examNames);
		model.addAttribute("year", year);
		model.addAttribute("form1term1", form1term1);
		model.addAttribute("form1term2", form1term2);
		model.addAttribute("form1term3", form1term3);

		return "examination";
	}

	@GetMapping("/schools/{code}/years/{year}/forms/{form}/terms/{term}/exams")
	@ResponseBody
	public List<ExamName> examNames(@PathVariable int code, @PathVariable int year, @PathVariable int form,
			@PathVariable int term) {

		return examNameService.getExamBySchoolYearFormTerm(code, year, form, term);
	}

	@PostMapping("/schools/{code}/examination")
	public String allExamination(@PathVariable int code, @RequestParam int year) {

		return "redirect:/schools/" + code + "/years/" + year + "/examination";
	}

	@PostMapping("/schools/{code}/years/{year}/examination")
	public String addingExamination(@PathVariable int code, @PathVariable int year, @RequestParam String name,
			@RequestParam int form, @RequestParam int term, @RequestParam int outOf) {

		ExamName examName = new ExamName(name, outOf);

		School school = schoolService.getSchool(code).get();
		List<School> schools = new ArrayList<>();
		schools.add(school);
		examName.setSchools(schools);

		Year yearObj = yearService.getYearFromSchool(year, code).get();
		List<Year> years = new ArrayList<>();
		years.add(yearObj);
		examName.setYears(years);

		Form formObj = formService.getForm(form, year, code).get();
		List<Form> forms = new ArrayList<>();
		forms.add(formObj);
		examName.setForms(forms);

		Term termObj = termService.getTerm(term, form, year, code);
		List<Term> terms = new ArrayList<>();
		terms.add(termObj);
		examName.setTerms(terms);

		examNameService.addExam(examName);

		return "redirect:/schools/" + code + "/years/" + year + "/examination";

	}

	@GetMapping("/schools/{code}/years/{year}/forms/{form}/terms/{term}/examination/{id}")
	public String deleteExamFromSchool(@PathVariable int code, @PathVariable int id, @PathVariable int year,
			@PathVariable int form, @PathVariable int term) {

		examNameService.deleteExam(id);

		return "redirect:/schools/" + code + "/years/" + year + "/examination";
	}

	@PostMapping("/schools/{code}/stream/{stream}/marks/{exam}")
	public String addMarksToStudentSubjects(@PathVariable int code, @PathVariable String stream, @PathVariable int exam,
			HttpServletRequest request, Model model, Principal principal) {

		int form = Integer.parseInt(request.getParameter("form"));
		int year = Integer.parseInt(request.getParameter("year"));
		int term = Integer.parseInt(request.getParameter("term"));
		String subject = request.getParameter("subject");

		Subject subjectObj = subjectService.getSubject(subject);

		List<Student> students = studentService.findAllStudentDoingSubject(code, year, form, term,
				subjectObj.getInitials());

		Year yearObj = yearService.getYearFromSchool(year, code).get();
		Term termObj = termService.getTerm(term, form, year, code);
		Form formObj = formService.getFormByForm(form);

		Mark mark = new Mark();
		List<Mark> marks = new ArrayList<>();

		for (int i = 0; i < students.size(); i++) {

			if (markService.getMarksByStudentOnSubjectByExamId(students.get(i).getAdmNo(), year, form, term, subject,
					exam) != null) {

				mark = markService.getMarksByStudentOnSubjectByExamId(students.get(i).getAdmNo(), year, form, term,
						subject, exam);

			} else {
				mark = new Mark(students.get(i), yearObj, formObj, termObj, subjectObj);
			}

			mark.setMark(Integer.parseInt(request.getParameter(students.get(i).getAdmNo() + "mark")));

			marks.add(markService.addMarksToSubject(mark));
		}

		model.addAttribute("success", "Marks saved successfully");

		if (students.size() == 0) {
			model.addAttribute("fail", "No student. Cannot add marks");
		}

		return "redirect:/schools/" + code + "/years/" + year + "/forms/" + form + "/terms/" + term + "/subjects/"
				+ subject + "/streams/" + stream + "/exams/" + exam;

	}

	@PostMapping("/schools/{code}/marksSheet")
	public String getMeritList(Model model, Principal principal, @PathVariable int code, @RequestParam int year,
			@RequestParam int form, @RequestParam int term, @RequestParam String subject, @RequestParam int stream,
			@RequestParam int examType) {

		return "redirect:/schools/" + code + "/years/" + year + "/forms/" + form + "/terms/" + term + "/subjects/" + subject + "/streams/" + stream + "/exams/" + examType;
	}

	@GetMapping("/schools/{code}/years/{year}/forms/{form}/terms/{term}/subjects/{subject}/streams/{stream}/exams/{exam}")
	public String addingMarksForAllStudentSubjects(Model model, Principal principal, @PathVariable int code,
			@PathVariable int year, @PathVariable String subject, @PathVariable int form, @PathVariable int term,
			@PathVariable int stream, @PathVariable int exam) {

		User activeUser = userService.getByUsername(principal.getName()).get();
		Subject subjectObj = subjectService.getSubject(subject);
		School school = schoolService.getSchool(code).get();
		Student student = new Student();
		Term termObj = termService.getTerm(term, form, year, code);
		Year yearObj = yearService.getYearFromSchool(year, code).get();
		Form formObj = formService.getFormByForm(form);
		Stream streamObj = streamService.getStream(stream);
		ExamName examName = examNameService.getExam(exam);

		List<Student> students = studentService.findAllStudentDoingSubject(code, year, form, term, subject);
		List<Stream> streams = streamService.getStreamsInSchool(school.getCode());
		List<Year> years = yearService.getAllYearsInSchool(school.getCode());
		List<Subject> subjects = subjectService.getAllSubjectInSchool(school.getCode());

		List<Mark> marks = new ArrayList<>();

		for (int i = 0; i < students.size(); i++) {
			Mark mark = new Mark();
			if (markService.getMarksByStudentOnSubjectByExamId(students.get(i).getAdmNo(), year, form, term, subject,
					exam) != null) {
				marks.add(markService.getMarksByStudentOnSubjectByExamId(students.get(i).getAdmNo(), year, form, term,
						subject, exam));
			} else {
				mark.setStudent(students.get(i));
				mark.setSubject(subjectObj);
				mark.setTerm(termObj);
				mark.setYear(yearObj);
				mark.setForm(formObj);

				List<ExamName> examNames = new ArrayList<>();
				examNames.add(examName);

				mark.setExamNames(examNames);
				marks.add(mark);

				markService.addMarksToSubject(mark);

			}

		}

		model.addAttribute("marks", marks);
		model.addAttribute("subjects", subjects);
		model.addAttribute("streams", streams);
		model.addAttribute("years", years);
		model.addAttribute("students", students);
		model.addAttribute("subject", subjectObj);
		model.addAttribute("year", year);
		model.addAttribute("form", form);
		model.addAttribute("term", term);
		model.addAttribute("stream", streamObj);
		model.addAttribute("examName", examName);
		model.addAttribute("student", student);
		model.addAttribute("school", school);
		model.addAttribute("activeUser", activeUser);

		return "marksEntry";

	}

	@GetMapping("/schools/{code}/years/{year}/forms/{form}/terms/{term}/subjects/{subject}/streams/{stream}/exams/{exam}/pdf")
	public ResponseEntity<?> getPDF(@PathVariable int code, @PathVariable int year, @PathVariable int form,
			@PathVariable int term, @PathVariable String subject, @PathVariable int stream, @PathVariable int exam,
			HttpServletRequest request, HttpServletResponse response, Principal principal) throws IOException {

		/* Do Business Logic */

		User activeUser = userService.getByUsername(principal.getName()).get();
		Subject subjectObj = subjectService.getSubject(subject);
		School school = schoolService.getSchool(code).get();
		Student student = new Student();
		Term termObj = termService.getTerm(term, form, year, code);
		Year yearObj = yearService.getYearFromSchool(year, code).get();
		Form formObj = formService.getFormByForm(form);
		Stream streamObj = streamService.getStream(stream);
		ExamName examName = examNameService.getExam(exam);

		List<Student> students = studentService.findAllStudentDoingSubject(code, year, form, term, subject);
		List<Stream> streams = streamService.getStreamsInSchool(school.getCode());
		List<Year> years = yearService.getAllYearsInSchool(school.getCode());
		List<Subject> subjects = subjectService.getAllSubjectInSchool(school.getCode());

		List<Mark> marks = new ArrayList<>();

		for (int i = 0; i < students.size(); i++) {
			Mark mark = new Mark();
			if (markService.getMarksByStudentOnSubjectByExamId(students.get(i).getAdmNo(), year, form, term, subject,
					exam) != null) {
				marks.add(markService.getMarksByStudentOnSubjectByExamId(students.get(i).getAdmNo(), year, form, term,
						subject, exam));
			} else {
				mark.setStudent(students.get(i));
				mark.setSubject(subjectObj);
				mark.setTerm(termObj);
				mark.setYear(yearObj);
				mark.setForm(formObj);

				List<ExamName> examNames = new ArrayList<>();
				examNames.add(examName);

				mark.setExamNames(examNames);
				marks.add(mark);

				markService.addMarksToSubject(mark);

			}

		}

		/* Create HTML using Thymeleaf template Engine */

		WebContext context = new WebContext(request, response, servletContext);
		context.setVariable("marks", marks);
		context.setVariable("subjects", subjects);
		context.setVariable("streams", streams);
		context.setVariable("years", years);
		context.setVariable("students", students);
		context.setVariable("subject", subjectObj);
		context.setVariable("year", year);
		context.setVariable("form", form);
		context.setVariable("term", term);
		context.setVariable("stream", streamObj);
		context.setVariable("examName", examName);
		context.setVariable("student", student);
		context.setVariable("school", school);
		context.setVariable("activeUser", activeUser);
		String marksSheetHtml = templateEngine.process("markEntryPdf", context);

		/* Setup Source and target I/O streams */

		ByteArrayOutputStream target = new ByteArrayOutputStream();

		/* Setup converter properties. */
		ConverterProperties converterProperties = new ConverterProperties();
		converterProperties.setBaseUri("http://localhost:8080");

		/* Call convert method */

		HtmlConverter.convertToPdf(marksSheetHtml, target, converterProperties);

		/* extract output as bytes */
		byte[] bytes = target.toByteArray();

		/* Send the response as downloadable PDF */

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(bytes);

	}

}
