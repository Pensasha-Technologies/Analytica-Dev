package com.pensasha.school.interfaceController;

import java.io.IOException;
import java.io.OutputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.pensasha.school.discipline.Discipline;
import com.pensasha.school.discipline.DisciplineService;
import com.pensasha.school.exam.ExamName;
import com.pensasha.school.exam.ExamNameService;
import com.pensasha.school.exam.Mark;
import com.pensasha.school.exam.MarkService;
import com.pensasha.school.exam.MeritList;
import com.pensasha.school.form.Form;
import com.pensasha.school.form.FormService;
import com.pensasha.school.report.ReportService;
import com.pensasha.school.role.Role;
import com.pensasha.school.role.RoleService;
import com.pensasha.school.school.School;
import com.pensasha.school.school.SchoolService;
import com.pensasha.school.sms.Gateway;
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
import com.pensasha.school.user.User;
import com.pensasha.school.user.UserService;
import com.pensasha.school.year.Year;
import com.pensasha.school.year.YearService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

@Controller
public class MainController {

	@Autowired
	ReportService reportService;

	private SchoolService schoolService;
	private StudentService studentService;
	private TermService termService;
	private SubjectService subjectService;
	private FormService formService;
	private YearService yearService;
	private MarkService markService;
	private UserService userService;
	private RoleService roleService;
	private StreamService streamService;
	private TimetableService timetableService;
	private DisciplineService disciplineService;
	private ExamNameService examNameService;

	public MainController(ReportService reportService, SchoolService schoolService, StudentService studentService,
			TermService termService, SubjectService subjectService, FormService formService, YearService yearService,
			MarkService markService, UserService userService, RoleService roleService, StreamService streamService,
			TimetableService timetableService, DisciplineService disciplineService, ExamNameService examNameService) {
		super();
		this.reportService = reportService;
		this.schoolService = schoolService;
		this.studentService = studentService;
		this.termService = termService;
		this.subjectService = subjectService;
		this.formService = formService;
		this.yearService = yearService;
		this.markService = markService;
		this.userService = userService;
		this.roleService = roleService;
		this.streamService = streamService;
		this.timetableService = timetableService;
		this.disciplineService = disciplineService;
		this.examNameService = examNameService;
	}

	@GetMapping("index")
	public String index(Principal principal, Model model) {

		User user = new User();

		model.addAttribute("activeUser", user);

		return "index";
	}

	@GetMapping("login")
	public String login(Principal principal, Model model) {

		User user = new User();

		model.addAttribute("activeUser", user);

		return "login";
	}

	@GetMapping("changePassword")
	public String changePassword(Model model) {

		User user = new User();

		model.addAttribute("activeUser", user);

		return "changePassword";

	}

	@PostMapping("/resetPassword/{username}")
	public String postResetPassword(RedirectAttributes redit, @PathVariable String username,
			@RequestParam String currentPassword, @RequestParam String newPassword,
			@RequestParam String confirmNewPassword) {

		User user = userService.getByUsername(username).get();
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		if (newPassword.contentEquals(confirmNewPassword) != true) {

			redit.addFlashAttribute("fail", "New password and Confirm Password do not match");
		} else if (encoder.matches(currentPassword, user.getPassword()) != true) {

			redit.addFlashAttribute("fail", "Current Password is incorrect");
		} else {

			user.setPassword(encoder.encode(newPassword));
			userService.addUser(user);

			redit.addFlashAttribute("success", "Password changed successfully");
		}

		return "redirect:/profile/" + username;

	}

	@PostMapping("/changePassword")
	public String postChangePassword(Model model, @RequestParam int phoneNumber, @RequestParam String username) {

		User user = new User();

		model.addAttribute("activeUser", user);

		if (userService.userExists(username) == true) {

			User testUser = userService.getByUsername(username).get();

			if (testUser.getPhoneNumber() == phoneNumber) {

				String baseUrl = "https://mysms.celcomafrica.com/api/services/sendsms/";
				int partnerId = 1989;
				String apiKey = "da383ff9c9edfb614bc7d1abfe8b1599";
				String shortCode = "analytica";

				Gateway gateway = new Gateway(baseUrl, partnerId, apiKey, shortCode);

				Random random = new Random();
				int otp = random.nextInt(9999);
				otp += 1;

				BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
				testUser.setPassword(encoder.encode(Integer.toString(otp)));
				userService.addUser(testUser);

				try {
					String res = gateway.sendSingleSms(
							"Your Username is: " + testUser.getUsername() + " password is: " + otp
									+ "Thanks for Registering with Analytica Soft.",
							Integer.toString(testUser.getPhoneNumber()));
					System.out.println(res);
				} catch (IOException e) {
					e.printStackTrace();
				}

				model.addAttribute("success", "Password send to phone Number");
				return "login";
			} else {
				model.addAttribute("fail", "Phone number does not match");
				return "changePassword";
			}
		} else {

			model.addAttribute("fail", "A user with this username does not exist");

			return "changePassword";
		}

	}

	@GetMapping("/adminHome")
	public String allSchool(Model model, Principal principal) {

		User activeUser = userService.getByUsername(principal.getName()).get();
		List<School> schools = schoolService.getAllSchools();

		School school = new School();
		Student student = new Student();

		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("schools", schools);
		model.addAttribute("school", school);

		return "adminHome";
	}

	
	@GetMapping("/schools/{code}/student/{admNo}/progress")
	public String viewStudentsProgressReport(@PathVariable int code, @PathVariable String admNo, Model model,
			Principal principal) {

		School school = schoolService.getSchool(code).get();
		Student student = studentService.getStudentInSchool(admNo, code);
		List<Subject> subjects = subjectService.getSubjectDoneByStudent(admNo);
		User activeUser = userService.getByUsername(principal.getName()).get();

		model.addAttribute("activeUser", activeUser);
		model.addAttribute("subjects", subjects);
		model.addAttribute("student", student);
		model.addAttribute("student", school);

		return "progressReport";
	}

	@PostMapping("/schools/{code}/timetable")
	public String getSchoolTimetable(@PathVariable int code, Model model, Principal principal, @RequestParam int year,
			@RequestParam int form, @RequestParam int stream, @RequestParam int term) {

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

		model.addAttribute("year", year);
		model.addAttribute("form", form);
		model.addAttribute("term", term);
		model.addAttribute("stream", streamObj);
		model.addAttribute("years", years);
		model.addAttribute("streams", streams);
		model.addAttribute("timetables", finalTimetables);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("school", school);

		return "timetable";

	}

	@PostMapping("schools/{code}/years/{year}/forms/{form}/terms/{term}/streams/{stream}/timetable")
	public String addTimetableElements(@PathVariable int code, @PathVariable int year, @PathVariable int form,
			@PathVariable int term, @PathVariable int stream, HttpServletRequest request, Model model,
			Principal principal) {

		Student student = new Student();
		User activeUser = userService.getByUsername(principal.getName()).get();

		School school = schoolService.getSchool(code).get();
		Stream streamObj = streamService.getStream(stream);

		List<Timetable> savedTimetable = timetableService.getTimetableBySchoolYearFormStream(code, year, form, term,
				stream);

		for (int i = 0; i < 5; i++) {

			savedTimetable.get(i).setTime1(request.getParameter(i + 1 + "time1"));
			savedTimetable.get(i).setTime2(request.getParameter(i + 1 + "time2"));
			savedTimetable.get(i).setTime4(request.getParameter(i + 1 + "time4"));
			savedTimetable.get(i).setTime5(request.getParameter(i + 1 + "time5"));
			savedTimetable.get(i).setTime7(request.getParameter(i + 1 + "time7"));
			savedTimetable.get(i).setTime8(request.getParameter(i + 1 + "time8"));
			savedTimetable.get(i).setTime10(request.getParameter(i + 1 + "time10"));
			savedTimetable.get(i).setTime11(request.getParameter(i + 1 + "time11"));
			savedTimetable.get(i).setTime12(request.getParameter(i + 1 + "time12"));
			savedTimetable.get(i).setTime13(request.getParameter(i + 1 + "time13"));

			timetableService.saveTimetableItem(savedTimetable.get(i));
		}

		List<Timetable> finalTimetables = timetableService.getTimetableBySchoolYearFormStream(code, year, form, term,
				stream);

		if (finalTimetables == null) {

			finalTimetables = new ArrayList<>();
		}

		List<Stream> streams = streamService.getStreamsInSchool(school.getCode());
		List<Year> years = yearService.getAllYearsInSchool(school.getCode());

		model.addAttribute("year", year);
		model.addAttribute("form", form);
		model.addAttribute("term", term);
		model.addAttribute("stream", streamObj);
		model.addAttribute("years", years);
		model.addAttribute("streams", streams);
		model.addAttribute("timetables", finalTimetables);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("school", school);

		return "timetable";
	}

	@GetMapping("/schools/{code}/student/{admNo}/yearly")
	public String getYearlyReport(@PathVariable int code, @PathVariable String admNo, Model model,
			Principal principal) {

		School school = schoolService.getSchool(code).get();
		Student student = studentService.getStudentInSchool(admNo, code);
		List<Subject> subjects = subjectService.getSubjectDoneByStudent(admNo);
		List<Year> years = yearService.allYearsForStudent(admNo);
		List<Form> forms = formService.studentForms(admNo);
		User activeUser = userService.getByUsername(principal.getName()).get();

		List<Mark> marks = markService.allMarks(admNo);

		model.addAttribute("activeUser", activeUser);
		model.addAttribute("marks", marks);
		model.addAttribute("forms", forms);
		model.addAttribute("years", years);
		model.addAttribute("subjects", subjects);
		model.addAttribute("student", student);
		model.addAttribute("school", school);

		return "yearlyReport";
	}

	@GetMapping("/ceoHome")
	public String ceoHomepage(Model model, Principal principal) {

		User activeUser = userService.getByUsername(principal.getName()).get();
		List<School> schools = schoolService.getAllSchools();

		School school = new School();
		Student student = new Student();

		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("schools", schools);
		model.addAttribute("school", school);

		return "ceoHome";
	}

	@GetMapping("/officeAssistantHome")
	public String officeAssistantHomepage(Model model, Principal principal) {

		User activeUser = userService.getByUsername(principal.getName()).get();
		List<School> schools = schoolService.getAllSchools();

		School school = new School();
		Student student = new Student();

		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("schools", schools);
		model.addAttribute("school", school);

		return "officeAssistantHome";
	}

	@GetMapping("/fieldOfficerHome")
	public String fieldOfficerHomepage(Model model, Principal principal) {

		User activeUser = userService.getByUsername(principal.getName()).get();
		List<School> schools = schoolService.getAllSchools();

		School school = new School();
		Student student = new Student();

		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("schools", schools);
		model.addAttribute("school", school);

		return "fieldOfficerHome";
	}

	@GetMapping("/schools/users/{username}")
	public String deleteSchoolUser(@PathVariable String username, RedirectAttributes redit, Principal principal) {

		if (userService.userExists(username) == true) {

			if (username.contentEquals(principal.getName())) {

				redit.addFlashAttribute("fail", "You cannot delete yourself");
			} else {

				userService.deleteUser(username);

				redit.addFlashAttribute("success", username + " successfully deleted");
			}

		} else {

			redit.addFlashAttribute("fail", "A user with username " + username + " does not exist");
		}

		return "redirect:/principalHome";

	}

	@GetMapping("/schools/principal")
	public String principalHome(Model model, Principal principal) {

		SchoolUser activeUser = (SchoolUser) userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(activeUser.getSchool().getCode()).get();
		Student student = new Student();
		User user = new User();
		List<SchoolUser> schoolUsers = userService.getUsersBySchoolCode(school.getCode());

		List<Year> years = yearService.getAllYearsInSchool(school.getCode());
		List<Stream> streams = streamService.getStreamsInSchool(school.getCode());
		List<Subject> subjects = subjectService.getAllSubjectInSchool(school.getCode());

		model.addAttribute("subjects", subjects);
		model.addAttribute("streams", streams);
		model.addAttribute("years", years);
		model.addAttribute("schoolUsers", schoolUsers);
		model.addAttribute("user", user);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("school", school);

		return "principalHome";
	}

	@GetMapping("/schools/{code}/sms")
	public String schoolSms(Model model, Principal principal) {

		SchoolUser activeUser = (SchoolUser) userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(activeUser.getSchool().getCode()).get();
		Student student = new Student();
		List<SchoolUser> schoolUsers = userService.getUsersBySchoolCode(school.getCode());
		User user = new User();
		List<Year> years = yearService.getAllYearsInSchool(school.getCode());
		List<Stream> streams = streamService.getStreamsInSchool(school.getCode());
		List<Subject> subjects = subjectService.getAllSubjectInSchool(school.getCode());

		model.addAttribute("subjects", subjects);
		model.addAttribute("streams", streams);
		model.addAttribute("years", years);
		model.addAttribute("schoolUsers", schoolUsers);
		model.addAttribute("user", user);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("school", school);

		return "smsHome";
	}

	@PostMapping("/schools/{code}/sms")
	public String sendSchoolSms(RedirectAttributes redit, @PathVariable int code, Principal principal,
			@RequestParam String recipientPhoneNumber, @RequestParam String message) {

		String baseUrl = "https://mysms.celcomafrica.com/api/services/sendsms/";
		int partnerId = 1989; // your ID here
		String apiKey = "da383ff9c9edfb614bc7d1abfe8b1599"; // your API key
		String shortCode = "analytica"; // sender ID here e.g INFOTEXT, Celcom, e.t.c

		Gateway gateway = new Gateway(baseUrl, partnerId, apiKey, shortCode);

		try {
			String res = gateway.sendSingleSms("Hello From Single sms Java", "0707335375");
			System.out.println(res);
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		 * String[] strings = { "0707335375" };
		 *
		 *
		 * try { String res = gateway.sendBulkSms("Hello Bulk from Java API ", strings);
		 * System.out.println(res); } catch (IOException e) { e.printStackTrace(); }
		 * 
		 */
		// }

		redit.addFlashAttribute("success", "Message sent successfully");

		return "redirect:/principalHome";

	}

	@GetMapping("/schools/students")
	public String schoolStudents(Model model, Principal principal) {

		SchoolUser activeUser = (SchoolUser) userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(activeUser.getSchool().getCode()).get();
		Student student = new Student();

		List<Student> students = studentService.getAllStudentsInSchool(school.getCode());
		List<Stream> streams = streamService.getStreamsInSchool(school.getCode());
		List<Subject> subjects = subjectService.getAllSubjectInSchool(school.getCode());
		List<Year> years = yearService.getAllYearsInSchool(activeUser.getSchool().getCode());

		model.addAttribute("years", years);
		model.addAttribute("subjects", subjects);
		model.addAttribute("streams", streams);
		model.addAttribute("students", students);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("school", school);

		return "students";
	}

	@GetMapping("/schools/students/{admNo}")
	public String schoolStudents(Model model, Principal principal, @PathVariable String admNo) {

		SchoolUser activeUser = (SchoolUser) userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(activeUser.getSchool().getCode()).get();
		Student student = new Student();

		List<Form> forms = formService.studentForms(admNo);
		List<Subject> subjects = subjectService.getSubjectDoneByStudent(admNo);
		List<Subject> schoolSubjects = subjectService.getAllSubjectInSchool(school.getCode());

		List<Subject> group1 = new ArrayList<>();
		List<Subject> group2 = new ArrayList<>();
		List<Subject> group3 = new ArrayList<>();
		List<Subject> group4 = new ArrayList<>();
		List<Subject> group5 = new ArrayList<>();

		for (int i = 0; i < subjects.size(); i++) {
			if (subjects.get(i).getName().contains("Mathematics") || subjects.get(i).getName().contains("English")
					|| subjects.get(i).getName().contains("Kiswahili")) {
				group1.add(subjects.get(i));
			} else if (subjects.get(i).getName().contains("Biology") || subjects.get(i).getName().contains("Physics")
					|| subjects.get(i).getName().contains("Chemistry")) {
				group2.add(subjects.get(i));
			} else if (subjects.get(i).getInitials().contains("Hist") || subjects.get(i).getInitials().contains("Geo")
					|| subjects.get(i).getInitials().contains("C.R.E")
					|| subjects.get(i).getInitials().contains("I.R.E")
					|| subjects.get(i).getInitials().contains("H.R.E")) {
				group3.add(subjects.get(i));
			} else if (subjects.get(i).getInitials().contains("Hsci") || subjects.get(i).getInitials().contains("AnD")
					|| subjects.get(i).getInitials().contains("Agric") || subjects.get(i).getInitials().contains("Comp")
					|| subjects.get(i).getInitials().contains("Avi") || subjects.get(i).getInitials().contains("Elec")
					|| subjects.get(i).getInitials().contains("Pwr") || subjects.get(i).getInitials().contains("Wood")
					|| subjects.get(i).getInitials().contains("Metal") || subjects.get(i).getInitials().contains("Bc")
					|| subjects.get(i).getInitials().contains("Dnd")) {
				group4.add(subjects.get(i));
			} else {
				group5.add(subjects.get(i));
			}
		}
		List<Stream> streams = streamService.getStreamsInSchool(school.getCode());
		List<Year> years = yearService.getAllYearsInSchool(school.getCode());

		model.addAttribute("years", years);
		model.addAttribute("streams", streams);
		model.addAttribute("group1", group1);
		model.addAttribute("group2", group2);
		model.addAttribute("group3", group3);
		model.addAttribute("group4", group4);
		model.addAttribute("group5", group5);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("forms", forms);
		model.addAttribute("school", school);
		model.addAttribute("subjects", subjects);
		model.addAttribute("schoolSubjects", schoolSubjects);
		model.addAttribute("student", student);

		return "student";
	}

	@PostMapping("/schools/students")
	public String addSchoolStudents(RedirectAttributes redit, @Valid Student student, BindingResult bindingResult,
			Principal principal, @RequestParam int stream) {

		SchoolUser activeUser = (SchoolUser) userService.getByUsername(principal.getName()).get();

		School school = schoolService.getSchool(activeUser.getSchool().getCode()).get();
		student.setSchool(school);

		if (studentService.ifStudentExistsInSchool(school.getCode() + "_" + student.getAdmNo(),
				school.getCode()) == true) {

			redit.addFlashAttribute("fail", "Student with adm No:" + student.getAdmNo() + " already exists");
		} else {
			List<School> schools = new ArrayList<>();

			schools.add(school);

			Stream streamObj = streamService.getStream(stream);
			student.setStream(streamObj);

			List<Year> years = new ArrayList<>();
			years.add(new Year(student.getYearAdmitted()));
			student.setYears(years);

			List<Term> terms = new ArrayList<>();
			terms.add(new Term(1));
			terms.add(new Term(2));
			terms.add(new Term(3));

			for (int i = 0; i < terms.size(); i++) {
				termService.addTerm(terms.get(i));
			}

			List<Form> forms = new ArrayList<>();

			switch (student.getCurrentForm()) {
			case 1:
				forms.add(new Form(1, terms));
				break;
			case 2:
				forms.add(new Form(1, terms));
				forms.add(new Form(2, terms));
				break;
			case 3:
				forms.add(new Form(1, terms));
				forms.add(new Form(2, terms));
				forms.add(new Form(3, terms));
				break;
			case 4:
				forms.add(new Form(1, terms));
				forms.add(new Form(2, terms));
				forms.add(new Form(3, terms));
				forms.add(new Form(4, terms));
				break;
			default:
				break;
			}

			for (int i = 0; i < forms.size(); i++) {
				formService.addForm(forms.get(i));
			}

			Year year = new Year(student.getYearAdmitted());
			year.setSchools(schools);
			year.setForms(forms);

			yearService.addYear(year);

			student.setForms(forms);
			student.setAdmNo(school.getCode() + "_" + student.getAdmNo());

			studentService.addStudent(student);

			redit.addFlashAttribute("success", "Student with adm No: " + student.getAdmNo() + " added successfully");
		}

		List<Subject> subjects = subjectService.getAllSubjectInSchool(school.getCode());

		List<Subject> group1 = new ArrayList<>();
		List<Subject> group2 = new ArrayList<>();
		List<Subject> group3 = new ArrayList<>();
		List<Subject> group4 = new ArrayList<>();
		List<Subject> group5 = new ArrayList<>();

		for (int i = 0; i < subjects.size(); i++) {
			if (subjects.get(i).getName().contains("Mathematics") || subjects.get(i).getName().contains("English")
					|| subjects.get(i).getName().contains("Kiswahili")) {
				group1.add(subjects.get(i));
			} else if (subjects.get(i).getName().contains("Biology") || subjects.get(i).getName().contains("Physics")
					|| subjects.get(i).getName().contains("Chemistry")) {
				group2.add(subjects.get(i));
			} else if (subjects.get(i).getInitials().contains("Hist") || subjects.get(i).getInitials().contains("Geo")
					|| subjects.get(i).getInitials().contains("C.R.E")
					|| subjects.get(i).getInitials().contains("I.R.E")
					|| subjects.get(i).getInitials().contains("H.R.E")) {
				group3.add(subjects.get(i));
			} else if (subjects.get(i).getInitials().contains("Hsci") || subjects.get(i).getInitials().contains("AnD")
					|| subjects.get(i).getInitials().contains("Agric") || subjects.get(i).getInitials().contains("Comp")
					|| subjects.get(i).getInitials().contains("Avi") || subjects.get(i).getInitials().contains("Elec")
					|| subjects.get(i).getInitials().contains("Pwr") || subjects.get(i).getInitials().contains("Wood")
					|| subjects.get(i).getInitials().contains("Metal") || subjects.get(i).getInitials().contains("Bc")
					|| subjects.get(i).getInitials().contains("Dnd")) {
				group4.add(subjects.get(i));
			} else {
				group5.add(subjects.get(i));
			}
		}

		return "redirect:/teacherHome";

	}

	@GetMapping("/school/teachers")
	public String schoolTeachers(Model model, Principal principal) {

		SchoolUser activeUser = (SchoolUser) userService.getByUsername(principal.getName()).get();

		School school = schoolService.getSchool(activeUser.getSchool().getCode()).get();
		Student student = new Student();
		SchoolUser user = new SchoolUser();

		List<User> teachers = userService.findUserByRole("TEACHER");

		model.addAttribute("user", user);
		model.addAttribute("teachers", teachers);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("school", school);

		return "teachers";

	}

	@PostMapping("/school/teachers")
	public String addTeacher(RedirectAttributes redit, @RequestParam String role, @RequestParam int code,
			@ModelAttribute SchoolUser user, Principal principal) {

		String baseUrl = "https://mysms.celcomafrica.com/api/services/sendsms/";
		int partnerId = 1989;
		String apiKey = "da383ff9c9edfb614bc7d1abfe8b1599";
		String shortCode = "analytica";

		Gateway gateway = new Gateway(baseUrl, partnerId, apiKey, shortCode);

		Random random = new Random();
		int otp = random.nextInt(9999);
		otp += 1;

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		user.setPassword(encoder.encode(Integer.toString(otp)));

		try {
			String res = gateway.sendSingleSms("Your Username is: " + user.getUsername() + " password is:" + otp,
					Integer.toString(user.getPhoneNumber()));
			System.out.println(res);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Role roleObj = new Role();
		user.setSchool(new School("", code));
		roleObj.setName("TEACHER");

		user.setRole(roleObj);
		roleService.addRole(roleObj);

		if (userService.userExists(user.getUsername()) == true) {

			redit.addFlashAttribute("fail", "A user with the username: " + user.getUsername() + " already exists");

		} else {

			userService.addUser(user);

			redit.addFlashAttribute("success", "Teacher saved successfully");
		}

		return "redirect:/school/teachers";
	}

	@GetMapping("/school/teachers/{username}")
	public String deleteTeacher(@PathVariable String username, RedirectAttributes redit, Principal principal) {

		if (userService.userExists(username) == true) {

			if (username.contentEquals(principal.getName())) {

				redit.addFlashAttribute("fail", "You cannot delete yourself");
			} else {

				userService.deleteUser(username);

				redit.addFlashAttribute("success", username + " successfully deleted");
			}

		} else {

			redit.addFlashAttribute("fail", "A teacher with username " + username + " does not exist");
		}

		return "redirect:/school/teachers";
	}

	@GetMapping("/schools/deputyPrincipal")
	public String deputyHome(Model model, Principal principal) {

		SchoolUser activeUser = (SchoolUser) userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(activeUser.getSchool().getCode()).get();
		Student student = new Student();
		User user = new User();
		List<SchoolUser> schoolUsers = userService.getUsersBySchoolCode(school.getCode());

		model.addAttribute("schoolUsers", schoolUsers);
		model.addAttribute("user", user);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("school", school);

		return "deputyPrincipal";
	}

	@GetMapping("/schools/dosHome")
	public String dosHome(Model model, Principal principal) {

		SchoolUser activeUser = (SchoolUser) userService.getByUsername(principal.getName()).get();
		School school = activeUser.getSchool();
		Student student = new Student();
		User user = new User();
		List<SchoolUser> schoolUsers = userService.getUsersBySchoolCode(school.getCode());

		model.addAttribute("schoolUsers", schoolUsers);
		model.addAttribute("user", user);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("school", school);

		return "dosHome";

	}

	@GetMapping("/schools/bursarHome")
	public String bursarHome(Model model, Principal principal) {

		SchoolUser activeUser = (SchoolUser) userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(activeUser.getSchool().getCode()).get();
		Student student = new Student();
		User user = new User();
		List<SchoolUser> schoolUsers = userService.getUsersBySchoolCode(school.getCode());

		List<Student> students = studentService.getAllStudentsInSchool(activeUser.getSchool().getCode());

		model.addAttribute("students", students);
		model.addAttribute("schoolUsers", schoolUsers);
		model.addAttribute("user", user);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("school", school);

		return "bursarHome";

	}

	@GetMapping("/schools/accountsClerkHome")
	public String accountsClerkHome(Model model, Principal principal) {

		User activeUser = userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(((SchoolUser) activeUser).getSchool().getCode()).get();
		Student student = new Student();
		User user = new User();
		List<SchoolUser> schoolUsers = userService.getUsersBySchoolCode(school.getCode());

		List<Student> students = studentService.getAllStudentsInSchool(school.getCode());

		model.addAttribute("students", students);
		model.addAttribute("schoolUsers", schoolUsers);
		model.addAttribute("user", user);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("school", school);

		return "accountsClerkHome";

	}

	@GetMapping("/teacherHome")
	public String teacherHome(Model model, Principal principal) {

		User activeUser = userService.getByUsername(principal.getName()).get();
		School school = ((SchoolUser) activeUser).getSchool();
		List<Student> students = studentService.getAllStudentsInSchool(school.getCode());
		List<Subject> subjects = subjectService.getAllSubjectInSchool(school.getCode());
		List<Stream> streams = streamService.getStreamsInSchool(school.getCode());
		List<Year> years = yearService.getAllYearsInSchool(school.getCode());

		model.addAttribute("years", years);
		model.addAttribute("streams", streams);
		model.addAttribute("subjects", subjects);
		model.addAttribute("student", new Student());
		model.addAttribute("students", students);
		model.addAttribute("school", school);
		model.addAttribute("activeUser", activeUser);

		return "teacherHome";
	}

	@GetMapping("/schools/teachers/{username}")
	public String teacherProfile(Principal principal, Model model) {

		User activeUser = userService.getByUsername(principal.getName()).get();
		School school = ((SchoolUser) activeUser).getSchool();

		model.addAttribute("activeUser", activeUser);

		if (userService.userExists(activeUser.getUsername()) == true) {

			Student student = new Student();
			List<Stream> streams = streamService.getStreamsInSchool(school.getCode());
			List<Year> years = yearService.getAllYearsInSchool(school.getCode());

			model.addAttribute("years", years);
			model.addAttribute("streams", streams);
			model.addAttribute("school", school);
			model.addAttribute("student", student);
			model.addAttribute("user", activeUser);

			return "userHome";
		} else {

			List<Student> students = studentService.getAllStudentsInSchool(school.getCode());
			List<Subject> subjects = subjectService.getAllSubjectInSchool(school.getCode());
			List<Stream> streams = streamService.getStreamsInSchool(school.getCode());
			List<Year> years = yearService.getAllYearsInSchool(school.getCode());

			model.addAttribute("years", years);
			model.addAttribute("streams", streams);
			model.addAttribute("subjects", subjects);
			model.addAttribute("fail", "A teacher with username " + activeUser.getUsername() + " does not exist");
			model.addAttribute("students", students);
			model.addAttribute("school", school);
			model.addAttribute("student", new Student());

			return "teacherHome";
		}

	}

	@GetMapping("/report/print")
	@ResponseBody
	public void generateReport(HttpServletResponse response) throws JRException, IOException {

		JasperPrint jasperPrint = null;

		response.setContentType("application/x-download");
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"users.pdf\""));

		OutputStream out = response.getOutputStream();
		jasperPrint = reportService.exportReport();
		JasperExportManager.exportReportToPdfStream(jasperPrint, out);

	}

	@GetMapping("/report/school/{code}/years/{year}/form/{form}/stream/{stream}/classList")
	@ResponseBody
	public void generateClassList(HttpServletResponse response, @PathVariable int code, @PathVariable int year,
			@PathVariable int form, @PathVariable String stream) throws JRException, IOException {

		JasperPrint jasperPrint = null;

		List<Student> students = studentService.getAllStudentsInSchoolByYearFormandStream(code, year, form, stream);

		response.setContentType("application/x-download");
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"classList.pdf\""));

		OutputStream out = response.getOutputStream();
		jasperPrint = reportService.exportClassList(students);
		JasperExportManager.exportReportToPdfStream(jasperPrint, out);

	}

	@GetMapping("/schools/{code}/discipline")
	public String disciplineReports(@PathVariable int code, Principal principal, Model model) {

		SchoolUser activeUser = (SchoolUser) userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(activeUser.getSchool().getCode()).get();
		Student student = new Student();
		List<SchoolUser> schoolUsers = userService.getUsersBySchoolCode(school.getCode());
		User user = new User();
		List<Year> years = yearService.getAllYearsInSchool(school.getCode());
		List<Stream> streams = streamService.getStreamsInSchool(school.getCode());
		List<Subject> subjects = subjectService.getAllSubjectInSchool(school.getCode());
		List<Discipline> allDisciplineReport = disciplineService.allDisciplineReportBySchoolCode(code);

		model.addAttribute("disciplines", allDisciplineReport);
		model.addAttribute("subjects", subjects);
		model.addAttribute("streams", streams);
		model.addAttribute("years", years);
		model.addAttribute("schoolUsers", schoolUsers);
		model.addAttribute("user", user);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("school", school);

		return "discipline";
	}

	@GetMapping("/schools/{code}/discipline/{id}")
	public String disciplineReport(@PathVariable int code, @PathVariable int id, Principal principal, Model model) {

		SchoolUser activeUser = (SchoolUser) userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(activeUser.getSchool().getCode()).get();
		Discipline discipline = disciplineService.getDisciplineReportById(id).get();

		Student student = discipline.getStudent();

		User user = new User();
		List<Year> years = yearService.getAllYearsInSchool(school.getCode());
		List<Stream> streams = streamService.getStreamsInSchool(school.getCode());
		List<Subject> subjects = subjectService.getAllSubjectInSchool(school.getCode());

		model.addAttribute("discipline", discipline);
		model.addAttribute("subjects", subjects);
		model.addAttribute("streams", streams);
		model.addAttribute("years", years);
		model.addAttribute("user", user);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("school", school);

		return "viewDiscipline";
	}

	@GetMapping("/schools/{code}/disciplines/{id}")
	public RedirectView deleteDisciplineReport(@PathVariable int code, @PathVariable int id, RedirectAttributes redit) {

		disciplineService.deleteDisciplineReport(id);

		RedirectView redirectView = new RedirectView("/schools/" + code + "/discipline", true);
		redit.addFlashAttribute("success", "Discipline Report successfully deleted");

		return redirectView;
	}

	@PostMapping("/schools/{code}/discipline")
	public RedirectView addDisciplineReport(@PathVariable int code, RedirectAttributes redit, @RequestParam int admNo,
			@RequestParam String type, @RequestParam String depature, @RequestParam String arrival,
			@RequestParam String reason) {

		if (studentService.ifStudentExistsInSchool(code + "_" + admNo, code) == true) {
			Student student = studentService.getStudentInSchool(code + "_" + admNo, code);
			Discipline discipline = new Discipline(depature, arrival, reason, type, student);

			disciplineService.saveDisciplineReport(discipline);

			redit.addFlashAttribute("success", "Discipline Report successfully added");
		} else {
			redit.addFlashAttribute("fail", "Student with Admission Number: " + admNo + " does not exist");
		}

		RedirectView redirectView = new RedirectView("/schools/" + code + "/discipline", true);

		return redirectView;
	}

	@GetMapping("/comingSoon")
	public String comingSoon() {

		return "comingSoon";
	}

}


