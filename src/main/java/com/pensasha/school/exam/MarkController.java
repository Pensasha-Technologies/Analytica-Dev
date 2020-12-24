package com.pensasha.school.exam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
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

import net.sf.jasperreports.engine.xml.JRExpressionFactory.ComparatorExpressionFactory;

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

		return "redirect:/schools/" + code + "/years/" + year + "/forms/" + form + "/terms/" + term + "/subjects/"
				+ subject + "/streams/" + stream + "/exams/" + examType;
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
		converterProperties.setBaseUri("http://analytica-env.eba-iigws4mq.us-east-2.elasticbeanstalk.com/");

		/* Call convert method */

		HtmlConverter.convertToPdf(marksSheetHtml, target, converterProperties);

		/* extract output as bytes */
		byte[] bytes = target.toByteArray();

		/* Send the response as downloadable PDF */

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(bytes);

	}

	@PostMapping("/schools/{code}/meritList")
	public String studentsMeritList(@PathVariable int code, @RequestParam int year, @RequestParam int form,
			@RequestParam int term, Model model, Principal principal) {

		return "redirect:/schools/" + code + "/years/" + year + "/forms/" + form + "/terms/" + term + "/meritList";

	}

	@GetMapping("/schools/{code}/years/{year}/forms/{form}/terms/{term}/meritList")
	public String getMeritList(@PathVariable int code, @PathVariable int year, @PathVariable int form,
			@PathVariable int term, Model model, Principal principal) {

		User activeUser = userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(code).get();
		Student student = new Student();
		List<Student> students = studentService.getAllStudentsInSchoolByYearFormTerm(code, year, form, term);
		List<Subject> subjects = subjectService.getAllSubjectInSchool(code);
		List<Mark> allMarks = markService.getAllStudentsMarksBySchoolYearFormAndTerm(code, form, term, year);

		List<Student> studentsWithoutMarks = new ArrayList<>();
		List<Student> studentsWithMarks = new ArrayList<>();

		for (int i = 0; i < students.size(); i++) {
			if (!markService.getMarkByAdm(students.get(i).getAdmNo())) {
				studentsWithoutMarks.add(students.get(i));
			} else {
				studentsWithMarks.add(students.get(i));
			}

		}

		MeritList meritList = new MeritList();
		List<MeritList> meritLists = new ArrayList<>();

		int mathsCount = 0, engCount = 0, kisCount = 0, bioCount = 0, chemCount = 0, phyCount = 0, histCount = 0,
				creCount = 0, geoCount = 0, ireCount = 0, hreCount = 0, hsciCount = 0, andCount = 0, agricCount = 0,
				compCount = 0, aviCount = 0, elecCount = 0, pwrCount = 0, woodCount = 0, metalCount = 0, bcCount = 0,
				frenCount = 0, germCount = 0, arabCount = 0, mscCount = 0, bsCount = 0, dndCount = 0;

		for (int i = 0; i < studentsWithMarks.size(); i++) {

			int count = 0;

			for (int j = 0; j < subjects.size(); j++) {

				meritList.setFirstname(students.get(i).getFirstname());
				meritList.setSecondname(students.get(i).getThirdname());
				meritList.setAdmNo(students.get(i).getAdmNo());
				meritList.setKcpe(students.get(i).getKcpeMarks());
				meritList.setStream(students.get(i).getStream().getStream());

				List<Mark> marks = new ArrayList<>();

				int sum = 0;

				switch (subjects.get(j).getInitials()) {
				case "Maths":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						mathsCount++;
					}
					meritList.setMaths(sum);
					break;
				case "Eng":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						engCount++;
					}
					meritList.setEng(sum);

					break;
				case "Kis":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						kisCount++;
					}
					meritList.setKis(sum);

					break;
				case "Bio":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						bioCount++;
					}
					meritList.setBio(sum);

					break;
				case "Chem":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						chemCount++;
					}
					meritList.setChem(sum);

					break;
				case "Phy":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						phyCount++;
					}
					meritList.setPhy(sum);

					break;
				case "Hist":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						histCount++;
					}
					meritList.setHist(sum);

					break;
				case "C.R.E":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						creCount++;
					}
					meritList.setCre(sum);

					break;
				case "Geo":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						geoCount++;
					}
					meritList.setGeo(sum);

					break;
				case "I.R.E":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						ireCount++;
					}
					meritList.setIre(sum);

					break;
				case "H.R.E":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						hreCount++;
					}
					meritList.setHre(sum);

					break;
				case "Hsci":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						hsciCount++;
					}
					meritList.setHsci(sum);

					break;
				case "AnD":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						andCount++;
					}
					meritList.setAnd(sum);

					break;
				case "Agric":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						agricCount++;
					}
					meritList.setAgric(sum);

					break;
				case "Comp":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						compCount++;
					}
					meritList.setComp(sum);

					break;
				case "Avi":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						aviCount++;
					}
					meritList.setAvi(sum);

					break;
				case "Elec":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						elecCount++;
					}
					meritList.setElec(sum);

					break;
				case "Pwr":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						pwrCount++;
					}
					meritList.setPwr(sum);

					break;
				case "Wood":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						woodCount++;
					}
					meritList.setWood(sum);

					break;
				case "Metal":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						metalCount++;
					}
					meritList.setMetal(sum);

					break;
				case "Bc":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						bcCount++;
					}
					meritList.setBc(sum);

					break;
				case "Fren":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						frenCount++;
					}
					meritList.setFren(sum);

					break;
				case "Germ":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						germCount++;
					}
					meritList.setGerm(sum);

					break;
				case "Arab":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						arabCount++;
					}
					meritList.setArab(sum);

					break;
				case "Msc":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						mscCount++;
					}
					meritList.setMsc(sum);

					break;
				case "Bs":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						bsCount++;
					}
					meritList.setBs(sum);

					break;
				case "DnD":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						dndCount++;
					}
					meritList.setDnd(sum);

					break;
				}

			}

			meritList.setTotal(meritList.getMaths() + meritList.getEng() + meritList.getKis() + meritList.getBio()
					+ meritList.getChem() + meritList.getPhy() + meritList.getHist() + meritList.getCre()
					+ meritList.getGeo() + meritList.getIre() + meritList.getHre() + meritList.getHsci()
					+ meritList.getAnd() + meritList.getAgric() + meritList.getComp() + meritList.getAvi()
					+ meritList.getElec() + meritList.getPwr() + meritList.getWood() + meritList.getMetal()
					+ meritList.getBc() + meritList.getFren() + meritList.getGerm() + meritList.getArab()
					+ meritList.getMsc() + meritList.getBs() + meritList.getDnd());

			meritList.setAverage(meritList.getTotal() / count);
			meritList.setDeviation(meritList.getAverage() - (students.get(i).getKcpeMarks())/5);
			meritLists.add(meritList);

		}
		
		Collections.sort(meritLists, new SortByTotal());

		for (int i = 0; i < studentsWithoutMarks.size(); i++) {

			meritList = new MeritList();
			meritList.setFirstname(studentsWithoutMarks.get(i).getFirstname());
			meritList.setSecondname(studentsWithoutMarks.get(i).getSecondname());
			meritList.setAdmNo(studentsWithoutMarks.get(i).getAdmNo());
			meritList.setKcpe(studentsWithoutMarks.get(i).getKcpeMarks());
			meritList.setStream(studentsWithoutMarks.get(i).getStream().getStream());
			meritList.setTotal(0);

			meritLists.add(meritList);

		}

		model.addAttribute("activeUser", activeUser);
		model.addAttribute("school", school);
		model.addAttribute("student", student);
		model.addAttribute("year", year);
		model.addAttribute("form", form);
		model.addAttribute("term", term);
		model.addAttribute("marks", allMarks);
		model.addAttribute("subjects", subjects);
		model.addAttribute("students", students);
		model.addAttribute("studentsWithoutMarks", studentsWithoutMarks);
		model.addAttribute("meritLists", meritLists);

		model.addAttribute("MathsCount", mathsCount);
		model.addAttribute("EngCount", engCount);
		model.addAttribute("KisCount", kisCount);
		model.addAttribute("BioCount", bioCount);
		model.addAttribute("ChemCount", chemCount);
		model.addAttribute("PhyCount", phyCount);
		model.addAttribute("HistCount", histCount);
		model.addAttribute("creCount", creCount);
		model.addAttribute("GeoCount", geoCount);
		model.addAttribute("ireCount", ireCount);
		model.addAttribute("hreCount", hreCount);
		model.addAttribute("HsciCount", hsciCount);
		model.addAttribute("AndCount", andCount);
		model.addAttribute("AgricCount", agricCount);
		model.addAttribute("CompCount", compCount);
		model.addAttribute("AviCount", aviCount);
		model.addAttribute("ElectCount", elecCount);
		model.addAttribute("PwrCount", pwrCount);
		model.addAttribute("WoodCount", woodCount);
		model.addAttribute("MetalCount", metalCount);
		model.addAttribute("BcCount", bcCount);
		model.addAttribute("FrenCount", frenCount);
		model.addAttribute("GermCount", germCount);
		model.addAttribute("ArabCount", arabCount);
		model.addAttribute("MscCount", mscCount);
		model.addAttribute("BsCount", bsCount);
		model.addAttribute("DndCount", dndCount);

		Collections.sort(meritLists, new SortByDeviation().reversed());
		model.addAttribute("mostImproved", meritLists.get(0));
		
		return "meritList";

	}

	@GetMapping("/schools/{code}/years/{year}/forms/{form}/terms/{term}/meritList/pdf")
	public ResponseEntity<?> getMeritListPDF(@PathVariable int code, @PathVariable int year, @PathVariable int form,
			@PathVariable int term, HttpServletRequest request, HttpServletResponse response, Principal principal)
			throws IOException {

		/* Do Business Logic */

		User activeUser = userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(code).get();
		Student student = new Student();
		List<Student> students = studentService.getAllStudentsInSchoolByYearFormTerm(code, year, form, term);
		List<Subject> subjects = subjectService.getAllSubjectInSchool(code);
		List<Mark> allMarks = markService.getAllStudentsMarksBySchoolYearFormAndTerm(code, form, term, year);

		List<Student> studentsWithoutMarks = new ArrayList<>();
		List<Student> studentsWithMarks = new ArrayList<>();

		for (int i = 0; i < students.size(); i++) {
			if (!markService.getMarkByAdm(students.get(i).getAdmNo())) {
				studentsWithoutMarks.add(students.get(i));
			} else {
				studentsWithMarks.add(students.get(i));
			}

		}

		MeritList meritList = new MeritList();
		List<MeritList> meritLists = new ArrayList<>();

		for (int i = 0; i < studentsWithMarks.size(); i++) {

			int count = 0;

			for (int j = 0; j < subjects.size(); j++) {

				meritList.setFirstname(students.get(i).getFirstname());
				meritList.setSecondname(students.get(i).getSecondname());
				meritList.setAdmNo(students.get(i).getAdmNo());
				meritList.setKcpe(students.get(i).getKcpeMarks());
				meritList.setStream(students.get(i).getStream().getStream());

				List<Mark> marks = new ArrayList<>();

				int sum = 0;

				switch (subjects.get(j).getInitials()) {
				case "Maths":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					count++;
					meritList.setMaths(sum);
					break;
				case "Eng":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					count++;
					meritList.setEng(sum);

					break;
				case "Kis":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					count++;
					meritList.setKis(sum);

					break;
				case "Bio":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					count++;
					meritList.setBio(sum);

					break;
				case "Chem":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					count++;
					meritList.setChem(sum);

					break;
				case "Phy":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					count++;
					meritList.setPhy(sum);

					break;
				case "Hist":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					count++;
					meritList.setHist(sum);

					break;
				case "C.R.E":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					count++;
					meritList.setCre(sum);

					break;
				case "Geo":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					count++;
					meritList.setGeo(sum);

					break;
				case "I.R.E":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					count++;
					meritList.setIre(sum);

					break;
				case "H.R.E":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					count++;
					meritList.setHre(sum);

					break;
				case "Hsci":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					count++;
					meritList.setHsci(sum);

					break;
				case "AnD":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					count++;
					meritList.setAnd(sum);

					break;
				case "Agric":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					count++;
					meritList.setAgric(sum);

					break;
				case "Comp":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					count++;
					meritList.setComp(sum);

					break;
				case "Avi":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					count++;
					meritList.setAvi(sum);

					break;
				case "Elec":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					count++;
					meritList.setElec(sum);

					break;
				case "Pwr":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					count++;
					meritList.setPwr(sum);

					break;
				case "Wood":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					count++;
					meritList.setWood(sum);

					break;
				case "Metal":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					count++;
					meritList.setMetal(sum);

					break;
				case "Bc":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					count++;
					meritList.setBc(sum);

					break;
				case "Fren":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					count++;
					meritList.setFren(sum);

					break;
				case "Germ":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					count++;
					meritList.setGerm(sum);

					break;
				case "Arab":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					count++;
					meritList.setArab(sum);

					break;
				case "Msc":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					count++;
					meritList.setMsc(sum);

					break;
				case "Bs":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					count++;
					meritList.setBs(sum);

					break;
				case "DnD":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					count++;
					meritList.setDnd(sum);

					break;
				}

			}

			meritList.setTotal(meritList.getMaths() + meritList.getEng() + meritList.getKis() + meritList.getBio()
					+ meritList.getChem() + meritList.getPhy() + meritList.getHist() + meritList.getCre()
					+ meritList.getGeo() + meritList.getIre() + meritList.getHre() + meritList.getHsci()
					+ meritList.getAnd() + meritList.getAgric() + meritList.getComp() + meritList.getAvi()
					+ meritList.getElec() + meritList.getPwr() + meritList.getWood() + meritList.getMetal()
					+ meritList.getBc() + meritList.getFren() + meritList.getGerm() + meritList.getArab()
					+ meritList.getMsc() + meritList.getBs() + meritList.getDnd());

			meritList.setAverage(meritList.getTotal() / count);

			meritLists.add(meritList);

		}

		Collections.sort(meritLists, new SortByTotal());

		for (int i = 0; i < studentsWithoutMarks.size(); i++) {

			meritList = new MeritList();
			meritList.setFirstname(studentsWithoutMarks.get(i).getFirstname());
			meritList.setSecondname(studentsWithoutMarks.get(i).getSecondname());
			meritList.setAdmNo(studentsWithoutMarks.get(i).getAdmNo());
			meritList.setKcpe(studentsWithoutMarks.get(i).getKcpeMarks());
			meritList.setStream(studentsWithoutMarks.get(i).getStream().getStream());
			meritList.setTotal(0);

			meritLists.add(meritList);

		}

		/* Create HTML using Thymeleaf template Engine */

		WebContext context = new WebContext(request, response, servletContext);
		context.setVariable("activeUser", activeUser);
		context.setVariable("school", school);
		context.setVariable("student", student);
		context.setVariable("year", year);
		context.setVariable("form", form);
		context.setVariable("term", term);
		context.setVariable("marks", allMarks);
		context.setVariable("subjects", subjects);
		context.setVariable("students", students);
		context.setVariable("studentsWithoutMarks", studentsWithoutMarks);
		context.setVariable("meritLists", meritLists);
		String meritListHtml = templateEngine.process("meritListPdf", context);

		/* Setup Source and target I/O streams */

		ByteArrayOutputStream target = new ByteArrayOutputStream();
		PdfWriter writer = new PdfWriter(target);
		PdfDocument pdfDocument = new PdfDocument(writer);
		pdfDocument.setDefaultPageSize(PageSize.A4.rotate());

		/* Setup converter properties. */
		ConverterProperties converterProperties = new ConverterProperties();
		// converterProperties.setBaseUri("http://analytica-env.eba-iigws4mq.us-east-2.elasticbeanstalk.com/");
		converterProperties.setBaseUri("http://localhost:8080/");

		/* Call convert method */

		HtmlConverter.convertToPdf(meritListHtml, pdfDocument, converterProperties);

		/* extract output as bytes */
		byte[] bytes = target.toByteArray();

		/* Send the response as downloadable PDF */

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(bytes);

	}

}

class SortByTotal implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getTotal() - b.getTotal();
	}
}

class SortByDeviation implements Comparator<MeritList>{
	public int compare(MeritList a, MeritList b) {
		return a.getDeviation() - b.getDeviation();
	}
}