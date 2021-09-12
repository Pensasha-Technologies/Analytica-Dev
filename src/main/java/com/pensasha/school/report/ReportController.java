package com.pensasha.school.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.pensasha.school.exam.ExamName;
import com.pensasha.school.exam.ExamNameService;
import com.pensasha.school.exam.Mark;
import com.pensasha.school.exam.MarkService;
import com.pensasha.school.exam.MeritList;
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
import com.pensasha.school.timetable.Timetable;
import com.pensasha.school.timetable.TimetableService;
import com.pensasha.school.user.SchoolUser;
import com.pensasha.school.user.Teacher;
import com.pensasha.school.user.User;
import com.pensasha.school.user.UserService;
import com.pensasha.school.year.Year;
import com.pensasha.school.year.YearService;

@Controller
public class ReportController {

	private UserService userService;
	private SubjectService subjectService;
	private TermService termService;
	private YearService yearService;
	private FormService formService;
	private SchoolService schoolService;
	private StreamService streamService;
	private ExamNameService examNameService;
	private StudentService studentService;
	private MarkService markService;
	private TimetableService timetableService;
	private final TemplateEngine templateEngine;

	@Autowired
	ServletContext servletContext;

	public ReportController(UserService userService, SubjectService subjectService, TermService termService,
			YearService yearService, FormService formService, SchoolService schoolService, StreamService streamService,
			ExamNameService examNameService, StudentService studentService, MarkService markService,
			TemplateEngine templateEngine, ServletContext servletContext) {
		super();
		this.userService = userService;
		this.subjectService = subjectService;
		this.termService = termService;
		this.yearService = yearService;
		this.formService = formService;
		this.schoolService = schoolService;
		this.streamService = streamService;
		this.examNameService = examNameService;
		this.studentService = studentService;
		this.markService = markService;
		this.templateEngine = templateEngine;
		this.servletContext = servletContext;
	}

	// private final String baseUrl = "http://analytica-env.eba-iigws4mq.us-east-2.elasticbeanstalk.com/";
	private final String baseUrl = "http://localhost:8080/";

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

				mark.setExamName(examName);
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
		converterProperties.setBaseUri(baseUrl);

		/* Call convert method */

		HtmlConverter.convertToPdf(marksSheetHtml, target, converterProperties);

		/* extract output as bytes */
		byte[] bytes = target.toByteArray();

		/* Send the response as downloadable PDF */

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(bytes);

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
			meritList.setDeviation(meritList.getAverage() - (students.get(i).getKcpeMarks()) / 5);
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

		for (int i = 0; i < meritLists.size(); i++) {
			meritLists.get(i).setRank(i + 1);
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
		context.setVariable("MathsCount", mathsCount);
		context.setVariable("EngCount", engCount);
		context.setVariable("KisCount", kisCount);
		context.setVariable("BioCount", bioCount);
		context.setVariable("ChemCount", chemCount);
		context.setVariable("PhyCount", phyCount);
		context.setVariable("HistCount", histCount);
		context.setVariable("creCount", creCount);
		context.setVariable("GeoCount", geoCount);
		context.setVariable("ireCount", ireCount);
		context.setVariable("hreCount", hreCount);
		context.setVariable("HsciCount", hsciCount);
		context.setVariable("AndCount", andCount);
		context.setVariable("AgricCount", agricCount);
		context.setVariable("CompCount", compCount);
		context.setVariable("AviCount", aviCount);
		context.setVariable("ElectCount", elecCount);
		context.setVariable("PwrCount", pwrCount);
		context.setVariable("WoodCount", woodCount);
		context.setVariable("MetalCount", metalCount);
		context.setVariable("BcCount", bcCount);
		context.setVariable("FrenCount", frenCount);
		context.setVariable("GermCount", germCount);
		context.setVariable("ArabCount", arabCount);
		context.setVariable("MscCount", mscCount);
		context.setVariable("BsCount", bsCount);
		context.setVariable("DndCount", dndCount);

		Collections.sort(meritLists, new SortByDeviation().reversed());
		if (meritLists.size() > 0) {
			context.setVariable("mostImproved", meritLists.get(0));
		}

		Collections.sort(meritLists, new SortByMaths());
		if (meritLists.size() > 0) {
			context.setVariable("MathsGiant", meritLists.get(0));
		}

		Collections.sort(meritLists, new SortByEng());
		if (meritLists.size() > 0) {
			context.setVariable("EngGiant", meritLists.get(0));
		}

		Collections.sort(meritLists, new SortByKis());
		if (meritLists.size() > 0) {
			context.setVariable("KisGiant", meritLists.get(0));
		}

		Collections.sort(meritLists, new SortByBio());
		if (meritLists.size() > 0) {
			context.setVariable("BioGiant", meritLists.get(0));
		}

		Collections.sort(meritLists, new SortByChem());
		if (meritLists.size() > 0) {
			context.setVariable("ChemGiant", meritLists.get(0));
		}

		Collections.sort(meritLists, new SortByPhy());
		if (meritLists.size() > 0) {
			context.setVariable("PhyGiant", meritLists.get(0));
		}

		Collections.sort(meritLists, new SortByHist());
		if (meritLists.size() > 0) {
			context.setVariable("HistGiant", meritLists.get(0));
		}

		Collections.sort(meritLists, new SortByCre());
		if (meritLists.size() > 0) {
			context.setVariable("creGiant", meritLists.get(0));
		}

		Collections.sort(meritLists, new SortByGeo());
		if (meritLists.size() > 0) {
			context.setVariable("GeoGiant", meritLists.get(0));
		}

		Collections.sort(meritLists, new SortByIre());
		if (meritLists.size() > 0) {
			context.setVariable("ireGiant", meritLists.get(0));
		}

		Collections.sort(meritLists, new SortByHre());
		if (meritLists.size() > 0) {
			context.setVariable("hreGiant", meritLists.get(0));
		}

		Collections.sort(meritLists, new SortByHsci());
		if (meritLists.size() > 0) {
			context.setVariable("HsciGiant", meritLists.get(0));
		}

		Collections.sort(meritLists, new SortByAnd());
		if (meritLists.size() > 0) {
			context.setVariable("AndGiant", meritLists.get(0));
		}

		Collections.sort(meritLists, new SortByAgric());
		if (meritLists.size() > 0) {
			context.setVariable("AgricGiant", meritLists.get(0));
		}

		Collections.sort(meritLists, new SortByComp());
		if (meritLists.size() > 0) {
			context.setVariable("CompGiant", meritLists.get(0));
		}

		Collections.sort(meritLists, new SortByAvi());
		if (meritLists.size() > 0) {
			context.setVariable("AviGiant", meritLists.get(0));
		}

		Collections.sort(meritLists, new SortByElec());
		if (meritLists.size() > 0) {
			context.setVariable("ElectGiant", meritLists.get(0));
		}

		Collections.sort(meritLists, new SortByPwr());
		if (meritLists.size() > 0) {
			context.setVariable("PwrGiant", meritLists.get(0));
		}

		Collections.sort(meritLists, new SortByWood());
		if (meritLists.size() > 0) {
			context.setVariable("WoodGiant", meritLists.get(0));
		}

		Collections.sort(meritLists, new SortByMetal());
		if (meritLists.size() > 0) {
			context.setVariable("MetalGiant", meritLists.get(0));
		}

		Collections.sort(meritLists, new SortByBc());
		if (meritLists.size() > 0) {
			context.setVariable("BcGiant", meritLists.get(0));
		}

		Collections.sort(meritLists, new SortByFren());
		if (meritLists.size() > 0) {
			context.setVariable("FrenGiant", meritLists.get(0));
		}

		Collections.sort(meritLists, new SortByGerm());
		if (meritLists.size() > 0) {
			context.setVariable("GermGiant", meritLists.get(0));
		}

		Collections.sort(meritLists, new SortByArab());
		if (meritLists.size() > 0) {
			context.setVariable("ArabGiant", meritLists.get(0));
		}

		Collections.sort(meritLists, new SortByMsc());
		if (meritLists.size() > 0) {
			context.setVariable("MscGiant", meritLists.get(0));
		}

		Collections.sort(meritLists, new SortByBs());
		if (meritLists.size() > 0) {
			context.setVariable("BsGiant", meritLists.get(0));
		}

		Collections.sort(meritLists, new SortByDnd());
		if (meritLists.size() > 0) {
		}

		String meritListHtml = templateEngine.process("meritListPdf", context);

		/* Setup Source and target I/O streams */

		ByteArrayOutputStream target = new ByteArrayOutputStream();
		PdfWriter writer = new PdfWriter(target);
		PdfDocument pdfDocument = new PdfDocument(writer);
		pdfDocument.setDefaultPageSize(PageSize.A4.rotate());

		/* Setup converter properties. */
		ConverterProperties converterProperties = new ConverterProperties();
		// converterProperties.setBaseUri("http://analytica-env.eba-iigws4mq.us-east-2.elasticbeanstalk.com/");
		converterProperties.setBaseUri(baseUrl);

		/* Call convert method */

		HtmlConverter.convertToPdf(meritListHtml, pdfDocument, converterProperties);

		/* extract output as bytes */
		byte[] bytes = target.toByteArray();

		/* Send the response as downloadable PDF */

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(bytes);

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

		List<Teacher> teachers = userService.getAllTeachersByAcademicYearAndSchoolFormStream(code, form,
				student.getStream().getId(), year);

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
		context.setVariable("teachers", teachers);
		String termlyReportHtml = templateEngine.process("termlyReportPdf", context);

		/* Setup Source and target I/O streams */
		ByteArrayOutputStream target = new ByteArrayOutputStream();

		/* Setup converter properties. */
		ConverterProperties converterProperties = new ConverterProperties();
		converterProperties.setBaseUri(baseUrl);

		/* Call convert method */
		HtmlConverter.convertToPdf(termlyReportHtml, target, converterProperties);

		/* extract output as bytes */
		byte[] bytes = target.toByteArray();

		/* Send the response as downloadable PDF */
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(bytes);

	}

	@GetMapping("/schools/{code}/years/{year}/forms/{form}/terms/{term}/streams/{stream}/students/{admNo}/studentsReport/pdf")
	public ResponseEntity<?> getStudentReportPDF(@PathVariable int code, @PathVariable int form, @PathVariable int year,
			@PathVariable int term, @PathVariable String stream, @PathVariable String admNo, HttpServletRequest request,
			HttpServletResponse response, Principal principal) throws IOException {

		/* Do Business Logic */
		User activeUser = userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(code).get();
		Student student = new Student();
		List<Student> allStudents = studentService.getAllStudentsInSchoolByYearFormTerm(code, year, form, term);
		List<Student> students = studentService.getAllStudentinSchoolYearFormTermStream(code, year, form, term,
				stream);
		List<ExamName> examNames = examNameService.getExamBySchoolYearFormTerm(code, year, form, term);
		List<Subject> subjects = subjectService.getAllSubjectInSchool(code);
		int cnt = 0;

		getMeritList get = new getMeritList();

		/* Create HTML using Thymeleaf template Engine */
		WebContext context = new WebContext(request, response, servletContext);

		for (int i = 0; i < students.size(); i++) {
			context.setVariable("subjects" + students.get(i).getAdmNo(),
					subjectService.getSubjectDoneByStudent(students.get(i).getAdmNo()));
			List<Mark> marks = markService.getTermlySubjectMark(students.get(i).getAdmNo(), form, term);

			for (int j = 0; j < subjects.size(); j++) {

				int sum = 0;

				for (int k = 0; k < marks.size(); k++) {
					if (marks.get(k).getSubject().equals(subjects.get(j))) {
						sum = sum + marks.get(k).getMark();
					}

				}

				if (sum > 0) {
					cnt++;
				}
			}

			context.setVariable("marks" + students.get(i).getAdmNo(), marks);

			List<Teacher> teachers = userService.getAllTeachersByAcademicYearAndSchoolFormStream(code, form,
					students.get(i).getStream().getId(), year);

			context.setVariable("teachers" + students.get(i).getAdmNo(), teachers);

		}

		context.setVariable("activeUser", activeUser);
		context.setVariable("school", school);
		context.setVariable("student", student);
		context.setVariable("students", students);
		context.setVariable("year", year);
		context.setVariable("form", form);
		context.setVariable("term", term);
		context.setVariable("examNames", examNames);

		int count = 0;

		List<MeritList> allStudentsMeritList = get.getList(allStudents, subjects, markService, year, form, term);
		Collections.sort(allStudentsMeritList, new SortByTotal().reversed());

		for(int i=0;i<allStudentsMeritList.size();i++){
			count++;
			if(count>1){
				if(allStudentsMeritList.get(i).getTotal() == allStudentsMeritList.get(i-1).getTotal()){
					count--;
				}
			}
			allStudentsMeritList.get(i).setRank(count);
		}

		context.setVariable("meritLists", allStudentsMeritList);

		int counter = 0;

		List<MeritList> streamStudentsMeritList = get.getList(students, subjects, markService, year, form, term);
		Collections.sort(streamStudentsMeritList, new SortByTotal().reversed());

		for(int i=0;i<streamStudentsMeritList.size();i++){
			counter++;
			if(counter>1){
				if(streamStudentsMeritList.get(i).getTotal() == streamStudentsMeritList.get(i-1).getTotal()){
					counter--;
				}
			}
			streamStudentsMeritList.get(i).setRank(counter);
		}

		context.setVariable("studentMeritList", streamStudentsMeritList);

		context.setVariable("count", cnt);
		String studentReportHtml = templateEngine.process("studentsReportPdf", context);

		/* Setup Source and target I/O streams */
		ByteArrayOutputStream target = new ByteArrayOutputStream();

		/* Setup converter properties. */
		ConverterProperties converterProperties = new ConverterProperties();
		converterProperties.setBaseUri(baseUrl);

		/* Call convert method */
		HtmlConverter.convertToPdf(studentReportHtml, target, converterProperties);

		/* extract output as bytes */
		byte[] bytes = target.toByteArray();

		/* Send the response as downloadable PDF */
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(bytes);

	}

	@GetMapping("/schools/{code}/years/{year}/forms/{form}/terms/{term}/streams/{stream}/timetable/pdf")
	public ResponseEntity<?> getMeritListPDF(@PathVariable int code, @PathVariable int year, @PathVariable int form,
			@PathVariable int term, @PathVariable int stream, HttpServletRequest request, HttpServletResponse response,
			Principal principal) throws IOException {

		/* Do Business Logic */

		School school = schoolService.getSchool(code).get();
		Year yearObj = yearService.getYearFromSchool(year, code).get();
		Form formObj = formService.getFormByForm(form);
		Term termObj = termService.getTerm(term, form, year, code);
		Stream streamObj = streamService.getStream(stream);

		Student student = new Student();
		User activeUser = userService.getByUsername(principal.getName()).get();
		List<Subject> subjects = subjectService.getAllSubjectInSchool(code);

		Random rand = new Random();

		List<String> days = new ArrayList<>();
		days.add("Mon");
		days.add("Tue");
		days.add("Wed");
		days.add("Thu");
		days.add("Fri");

		Timetable timetable = new Timetable();
		List<Timetable> timetables = timetableService.getTimetableBySchoolYearFormStream(code, year, form, term,
				stream);

		String breaks[] = { "B", "R", "E", "A", "K" };
		String lunch[] = { "L", "U", "N", "C", "H" };

		if (timetables.isEmpty()) {
			for (int i = 0; i < days.size(); i++) {

				timetable = new Timetable(days.get(i), subjects.get(rand.nextInt(subjects.size())).getInitials(),
						subjects.get(rand.nextInt(subjects.size())).getInitials(),
						subjects.get(rand.nextInt(subjects.size())).getInitials(),
						subjects.get(rand.nextInt(subjects.size())).getInitials(),
						subjects.get(rand.nextInt(subjects.size())).getInitials(),
						subjects.get(rand.nextInt(subjects.size())).getInitials(),
						subjects.get(rand.nextInt(subjects.size())).getInitials(),
						subjects.get(rand.nextInt(subjects.size())).getInitials(),
						subjects.get(rand.nextInt(subjects.size())).getInitials(),
						subjects.get(rand.nextInt(subjects.size())).getInitials(),
						subjects.get(rand.nextInt(subjects.size())).getInitials(),
						subjects.get(rand.nextInt(subjects.size())).getInitials(),
						subjects.get(rand.nextInt(subjects.size())).getInitials(), school, yearObj, formObj, termObj,
						streamObj);

				timetables.add(timetable);

			}

		}

		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 10; j++) {
				if (j == 2) {
					timetables.get(i).setTime3(breaks[i]);
				} else if (j == 4) {
					timetables.get(i).setTime6(breaks[i]);
				} else if (j == 6) {
					timetables.get(i).setTime9(lunch[i]);
				}

			}
		}

		if (formService.getFormByForm(form) != null) {
			for (int i = 0; i < timetables.size(); i++) {

				timetableService.saveTimetableItem(timetables.get(i));
			}
		}

		List<Timetable> finalTimetables = timetableService.getTimetableBySchoolYearFormStream(code, year, form, term,
				stream);

		if (finalTimetables == null) {

			finalTimetables = new ArrayList<>();
		}

		List<Stream> streams = streamService.getStreamsInSchool(school.getCode());
		List<Year> years = yearService.getAllYearsInSchool(school.getCode());

		/* Create HTML using Thymeleaf template Engine */

		WebContext context = new WebContext(request, response, servletContext);
		context.setVariable("year", year);
		context.setVariable("form", form);
		context.setVariable("term", term);
		context.setVariable("stream", streamObj);
		context.setVariable("years", years);
		context.setVariable("streams", streams);
		context.setVariable("timetables", finalTimetables);
		context.setVariable("activeUser", activeUser);
		context.setVariable("student", student);
		context.setVariable("school", school);
		String timetableHtml = templateEngine.process("timetablePdf", context);

		/* Setup Source and target I/O streams */

		ByteArrayOutputStream target = new ByteArrayOutputStream();
		PdfWriter writer = new PdfWriter(target);
		PdfDocument pdfDocument = new PdfDocument(writer);
		pdfDocument.setDefaultPageSize(PageSize.A4.rotate());

		/* Setup converter properties. */
		ConverterProperties converterProperties = new ConverterProperties();
		// converterProperties.setBaseUri("http://analytica-env.eba-iigws4mq.us-east-2.elasticbeanstalk.com/");
		converterProperties.setBaseUri(baseUrl);

		/* Call convert method */
		HtmlConverter.convertToPdf(timetableHtml, pdfDocument, converterProperties);

		/* extract output as bytes */
		byte[] bytes = target.toByteArray();

		/* Send the response as downloadable PDF */

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(bytes);

	}

	@GetMapping("/schools/{code}/years/{year}/forms/{form}/streams/{stream}/classList/pdf")
	public ResponseEntity<?> getPDF(@PathVariable int code, @PathVariable int year, @PathVariable int form,
			@PathVariable String stream, HttpServletRequest request, HttpServletResponse response, Principal principal)
			throws IOException {

		/* Do Business Logic */

		User activeUser = userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(code).get();
		Student student = new Student();
		User user = new User();
		List<SchoolUser> schoolUsers = userService.getUsersBySchoolCode(school.getCode());

		List<Year> years = yearService.getAllYearsInSchool(school.getCode());
		List<Stream> streams = streamService.getStreamsInSchool(school.getCode());

		List<Student> students = studentService.getAllStudentsInSchoolByYearFormandStream(code, year, form, stream);
		List<Subject> subjects = subjectService.getAllSubjectInSchool(code);

		/* Create HTML using Thymeleaf template Engine */

		WebContext context = new WebContext(request, response, servletContext);
		context.setVariable("subjects", subjects);
		context.setVariable("form", form);
		context.setVariable("stream", stream);
		context.setVariable("year", year);
		context.setVariable("students", students);
		context.setVariable("streams", streams);
		context.setVariable("years", years);
		context.setVariable("schoolUsers", schoolUsers);
		context.setVariable("user", user);
		context.setVariable("activeUser", activeUser);
		context.setVariable("student", student);
		context.setVariable("school", school);
		String classListHtml = templateEngine.process("classListPdf", context);

		/* Setup Source and target I/O streams */

		ByteArrayOutputStream target = new ByteArrayOutputStream();

		/* Setup converter properties. */
		ConverterProperties converterProperties = new ConverterProperties();
		converterProperties.setBaseUri(baseUrl);

		/* Call convert method */
		HtmlConverter.convertToPdf(classListHtml, target, converterProperties);

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

class SortByMaths implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getMaths() - b.getMaths();
	}
}

class SortByEng implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getEng() - b.getEng();
	}
}

class SortByKis implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getKis() - b.getKis();
	}
}

class SortByBio implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getBio() - b.getBio();
	}
}

class SortByChem implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getChem() - b.getChem();
	}
}

class SortByPhy implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getPhy() - b.getPhy();
	}
}

class SortByHist implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getHist() - b.getHist();
	}
}

class SortByCre implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getCre() - b.getCre();
	}
}

class SortByGeo implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getGeo() - b.getGeo();
	}
}

class SortByIre implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getIre() - b.getIre();
	}
}

class SortByHre implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getHre() - b.getHre();
	}
}

class SortByHsci implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getHsci() - b.getHsci();
	}
}

class SortByAnd implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getAnd() - b.getAnd();
	}
}

class SortByAgric implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getAgric() - b.getAgric();
	}
}

class SortByComp implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getComp() - b.getComp();
	}
}

class SortByAvi implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getAvi() - b.getAvi();
	}
}

class SortByElec implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getElec() - b.getElec();
	}
}

class SortByPwr implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getPwr() - b.getPwr();
	}
}

class SortByWood implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getWood() - b.getWood();
	}
}

class SortByMetal implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getMetal() - b.getMetal();
	}
}

class SortByBc implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getBc() - b.getBc();
	}
}

class SortByFren implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getFren() - b.getFren();
	}
}

class SortByGerm implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getGerm() - b.getGerm();
	}
}

class SortByArab implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getArab() - b.getArab();
	}
}

class SortByMsc implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getMsc() - b.getMsc();
	}
}

class SortByBs implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getBs() - b.getBs();
	}
}

class SortByDnd implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getDnd() - b.getDnd();
	}
}

class SortByDeviation implements Comparator<MeritList> {
	public int compare(MeritList a, MeritList b) {
		return a.getDeviation() - b.getDeviation();
	}
}

class getMeritList {

	public List<MeritList> getList(List<Student> students, List<Subject> subjects, MarkService markService, int year,
			int form, int term) {

		List<Student> studentsWithoutMarks = new ArrayList<>();
		List<Student> studentsWithMarks = new ArrayList<>();

		for (int i = 0; i < students.size(); i++) {
			if (!markService.getMarkByAdm(students.get(i).getAdmNo())) {
				studentsWithoutMarks.add(students.get(i));
			} else {
				studentsWithMarks.add(students.get(i));
			}

		}

		List<MeritList> meritLists = new ArrayList<>();

		int mathsCount = 0, engCount = 0, kisCount = 0, bioCount = 0, chemCount = 0, phyCount = 0, histCount = 0,
				creCount = 0, geoCount = 0, ireCount = 0, hreCount = 0, hsciCount = 0, andCount = 0, agricCount = 0,
				compCount = 0, aviCount = 0, elecCount = 0, pwrCount = 0, woodCount = 0, metalCount = 0, bcCount = 0,
				frenCount = 0, germCount = 0, arabCount = 0, mscCount = 0, bsCount = 0, dndCount = 0;

		for (int i = 0; i < studentsWithMarks.size(); i++) {

			int count = 0;
			MeritList meritList = new MeritList();

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

			if(count>0){
				meritList.setAverage(meritList.getTotal() / count);
			}else{
				meritList.setAverage(0);
			}

			meritList.setDeviation(meritList.getAverage() - (students.get(i).getKcpeMarks()) / 5);
			meritLists.add(meritList);

		}

		Collections.sort(meritLists, new SortByTotal());

		for (int i = 0; i < studentsWithoutMarks.size(); i++) {

			MeritList meritList = new MeritList();

			meritList = new MeritList();
			meritList.setFirstname(studentsWithoutMarks.get(i).getFirstname());
			meritList.setSecondname(studentsWithoutMarks.get(i).getSecondname());
			meritList.setAdmNo(studentsWithoutMarks.get(i).getAdmNo());
			meritList.setKcpe(studentsWithoutMarks.get(i).getKcpeMarks());
			meritList.setStream(studentsWithoutMarks.get(i).getStream().getStream());
			meritList.setTotal(0);

			meritLists.add(meritList);

		}

		for (int i = 0; i < meritLists.size(); i++) {
			meritLists.get(i).setRank(i + 1);
		}

		return meritLists;
	}
}
