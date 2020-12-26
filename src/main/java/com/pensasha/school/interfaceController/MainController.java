package com.pensasha.school.interfaceController;

import java.io.IOException;
import java.io.OutputStream;
import java.security.Principal;
import java.util.ArrayList;
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
import com.pensasha.school.exam.Mark;
import com.pensasha.school.exam.MarkService;
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
import com.pensasha.school.user.SchoolUser;
import com.pensasha.school.user.Teacher;
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
	private DisciplineService disciplineService;

	public MainController(ReportService reportService, SchoolService schoolService, StudentService studentService,
			TermService termService, SubjectService subjectService, FormService formService, YearService yearService,
			MarkService markService, UserService userService, RoleService roleService, StreamService streamService,
			DisciplineService disciplineService) {
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
		this.disciplineService = disciplineService;
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

	@GetMapping("/schools/{code}/teachers")
	public String teachers(Model model, Principal principal, @PathVariable int code) {

		User activeUser = userService.getByUsername(principal.getName()).get();

		School school = schoolService.getSchool(code).get();
		Student student = new Student();
		SchoolUser user = new SchoolUser();

		List<Teacher> teachers = userService.getTeachersInSchool(code);

		model.addAttribute("user", user);
		model.addAttribute("teachers", teachers);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("school", school);

		return "teachers";

	}

	@PostMapping("/school/teachers")
	public String addTeacher(RedirectAttributes redit, @RequestParam String role, @RequestParam int code,
			@ModelAttribute SchoolUser user, Principal principal, HttpServletRequest request) {
		
		Teacher teacher = new Teacher(user.getUsername(), user.getFirstname(), user.getSecondname(),
				user.getThirdname(), user.getPassword(), user.getEmail(), user.getPhoneNumber(),
				user.getAddress());
		teacher.setSchool(user.getSchool());
		teacher.setTeacherNumber(request.getParameter("teacherNumber"));
		teacher.setTscNumber(request.getParameter("tscNumber"));
		teacher.setInitials(user.getFirstname().charAt(0) + "." + user.getSecondname().charAt(0) + "."
				+ user.getThirdname().charAt(0));
	
		
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		user.setPassword(encoder.encode(user.getUsername()));
/*
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
*/
		Role roleObj = new Role();
		teacher.setSchool(new School("", code));
		roleObj.setName("TEACHER");

		teacher.setRole(roleObj);
		roleService.addRole(roleObj);

		if (userService.userExists(user.getUsername()) == true) {

			redit.addFlashAttribute("fail", "A user with the username: " + user.getUsername() + " already exists");

		} else {

			userService.addUser(teacher);

			redit.addFlashAttribute("success", "Teacher saved successfully");
		}

		return "redirect:/schools/" + code + "/teachers";
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

		SchoolUser activeUser = userService.getSchoolUserByUsername(principal.getName());
		School school = activeUser.getSchool();
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

	@PostMapping("/schools/{code}/assignTeacher")
	public String getYearAssignment(Model model, Principal principal, @PathVariable int code, @RequestParam int year) {

		return "redirect:/schools/" + code + "/years/" + year + "/assignTeacher";
	}

	@GetMapping("/schools/{code}/years/{year}/assignTeacher")
	public String assignTeachers(Model model, Principal principal, @PathVariable int code, @PathVariable int year) {

		User user = userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(code).get();
		Student student = new Student();
		List<Stream> streams = streamService.getStreamsInSchool(code);
		List<Subject> subjects = subjectService.getAllSubjectInSchool(code);

		for (int i = 0; i < subjects.size(); i++) {

			if (subjects.get(i).getInitials() == "C.R.E") {
				model.addAttribute("creTeacher",
						userService.getAllTeachersBySubjectInitials(subjects.get(i).getInitials()));
			} else if (subjects.get(i).getInitials() == "H.R.E") {
				model.addAttribute("hreTeacher",
						userService.getAllTeachersBySubjectInitials(subjects.get(i).getInitials()));
			} else if (subjects.get(i).getInitials() == "I.R.E") {
				model.addAttribute("ireTeacher",
						userService.getAllTeachersBySubjectInitials(subjects.get(i).getInitials()));
			} else {
				model.addAttribute(subjects.get(i).getInitials() + "Teacher",
						userService.getAllTeachersBySubjectInitials(subjects.get(i).getInitials()));
			}
		}

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < streams.size(); j++) {
				model.addAttribute("f" + i + streams.get(j).getId() + "Teachers", userService
						.getAllTeachersByAcademicYearAndSchoolFormStream(code, i, streams.get(j).getId(), year));
			}
		}

		model.addAttribute("activeUser", user);
		model.addAttribute("school", school);
		model.addAttribute("student", student);
		model.addAttribute("streams", streams);
		model.addAttribute("subjects", subjects);
		model.addAttribute("year", year);

		return "assignTeachers";

	}

	@PostMapping("/schools/{code}/years/{year}/forms/{form}/streams/{stream}/assignTeacher")
	public String assignTeachers(RedirectAttributes redit, HttpServletRequest request, @PathVariable int code,
			@PathVariable int year, @PathVariable int form, @PathVariable int stream) {

		List<Subject> subjects = subjectService.getAllSubjectInSchool(code);

		Teacher teacher;

		List<Form> forms = new ArrayList<>();
		Form formObj = formService.getForm(form, year, code).get();
		forms.add(formObj);

		List<Year> years = new ArrayList<>();
		Year yearObj = yearService.getYearFromSchool(year, code).get();
		years.add(yearObj);

		List<Stream> streams = new ArrayList<>();
		Stream streamObj = streamService.getStream(stream);
		streams.add(streamObj);

		for (int i = 0; i < subjects.size(); i++) {
			
			teacher = userService
					.gettingTeacherByUsername(request.getParameter(subjects.get(i).getInitials() + "Teacher"));

			if(teacher != null) {
			teacher.setYears(years);
			teacher.setForms(forms);
			teacher.setStreams(streams);

			List<Teacher> teachers = new ArrayList<>();
			teachers.add(teacher);

			userService.addUser(teacher);
			}
		}

		return "redirect:/schools/" + code + "/years/" + year + "/assignTeacher";

	}

}
