package com.pensasha.school.interfaceController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pensasha.school.exam.Mark;
import com.pensasha.school.exam.MarkService;
import com.pensasha.school.finance.FeeStructure;
import com.pensasha.school.finance.FeeStructureService;
import com.pensasha.school.form.Form;
import com.pensasha.school.form.FormService;
import com.pensasha.school.role.Role;
import com.pensasha.school.role.RoleService;
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
public class MainController {

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
	private FeeStructureService feeStructureService;

	public MainController(SchoolService schoolService, StudentService studentService, TermService termService,
			SubjectService subjectService, FormService formService, YearService yearService, MarkService markService,
			UserService userService, RoleService roleService, StreamService streamService,
			FeeStructureService feeStructureService) {
		super();
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
		this.feeStructureService = feeStructureService;
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

	@PostMapping("/adminHome")
	public String addSchool(@ModelAttribute School school, Model model, Principal principal) {

		if (schoolService.doesSchoolExists(school.getCode()) == true) {

			Student student = new Student();

			User activeUser = userService.getByUsername(principal.getName()).get();
			List<School> schools = schoolService.getAllSchools();
			School schoolObj = new School();
			Stream stream = new Stream();

			model.addAttribute("stream", stream);
			model.addAttribute("fail", "School with code:" + school.getCode() + " already exists");
			model.addAttribute("addedCode", school.getCode());
			model.addAttribute("school", schoolObj);
			model.addAttribute("activeUser", activeUser);
			model.addAttribute("student", student);
			model.addAttribute("schools", schools);

			return "adminHome";
		} else {

			schoolService.addSchool(school);
			Student student = new Student();

			User activeUser = userService.getByUsername(principal.getName()).get();
			List<School> schools = schoolService.getAllSchools();
			School schoolObj = new School();
			Stream stream = new Stream();

			model.addAttribute("stream", stream);
			model.addAttribute("success", "School with code:" + school.getCode() + " saved successfully");
			model.addAttribute("addedCode", school.getCode());
			model.addAttribute("school", schoolObj);
			model.addAttribute("activeUser", activeUser);
			model.addAttribute("student", student);
			model.addAttribute("schools", schools);

			return "adminHome";
		}

	}

	@GetMapping("/adminHome/schools/{code}")
	public String deleteSchool(@PathVariable int code, Model model, Principal principal) {

		if (schoolService.doesSchoolExists(code) == true) {

			List<Subject> subjects = subjectService.getAllSubjectInSchool(code);

			for (int i = 0; i < subjects.size(); i++) {
				List<School> schools = schoolService.getAllSchoolsWithSubject(subjects.get(i).getInitials());

				schools.remove(schoolService.getSchool(code).get());
				subjects.get(i).setSchools(schools);

				subjectService.addSubject(subjects.get(i));
			}

			List<Student> students = studentService.getAllStudentsInSchool(code);
			for (int i = 0; i < students.size(); i++) {

				List<Mark> marks = markService.allMarks(students.get(i).getAdmNo());
				for (int j = 0; j < marks.size(); j++) {
					markService.deleteMark(marks.get(j).getId());
				}
			}

			List<Stream> streams = streamService.getStreamsInSchool(code);
			for (int i = 0; i < streams.size(); i++) {
				streamService.deleteStream(streams.get(i).getId());
			}

			schoolService.deleteSchool(code);

			model.addAttribute("success", "School of code:" + code + " successfully deleted");
		} else {
			model.addAttribute("fail", "School of code:" + code + " does not exist");
		}

		School school = new School();
		Student student = new Student();

		User activeUser = userService.getByUsername(principal.getName()).get();
		List<School> schools = schoolService.getAllSchools();
		Stream stream = new Stream();

		model.addAttribute("stream", stream);
		model.addAttribute("school", school);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("schools", schools);

		return "adminHome";
	}

	@GetMapping("/adminHome/school/{code}")
	public String school(@PathVariable int code, Model model, Principal principal) {

		User activeUser = userService.getByUsername(principal.getName()).get();

		if (schoolService.doesSchoolExists(code) == true) {

			School school = schoolService.getSchool(code).get();
			List<Subject> subjects = subjectService.getAllSubjectInSchool(code);
			Student student = new Student();
			List<Subject> group1 = new ArrayList<>();
			List<Subject> group2 = new ArrayList<>();
			List<Subject> group3 = new ArrayList<>();
			List<Subject> group4 = new ArrayList<>();
			List<Subject> group5 = new ArrayList<>();

			for (int i = 0; i < subjects.size(); i++) {
				if (subjects.get(i).getName().contains("Mathematics") || subjects.get(i).getName().contains("English")
						|| subjects.get(i).getName().contains("Kiswahili")) {
					group1.add(subjects.get(i));
				} else if (subjects.get(i).getName().contains("Biology")
						|| subjects.get(i).getName().contains("Physics")
						|| subjects.get(i).getName().contains("Chemistry")) {
					group2.add(subjects.get(i));
				} else if (subjects.get(i).getInitials().contains("Hist")
						|| subjects.get(i).getInitials().contains("Geo")
						|| subjects.get(i).getInitials().contains("C.R.E")
						|| subjects.get(i).getInitials().contains("I.R.E")
						|| subjects.get(i).getInitials().contains("H.R.E")) {
					group3.add(subjects.get(i));
				} else if (subjects.get(i).getInitials().contains("Hsci")
						|| subjects.get(i).getInitials().contains("AnD")
						|| subjects.get(i).getInitials().contains("Agric")
						|| subjects.get(i).getInitials().contains("Comp")
						|| subjects.get(i).getInitials().contains("Avi")
						|| subjects.get(i).getInitials().contains("Elec")
						|| subjects.get(i).getInitials().contains("Pwr")
						|| subjects.get(i).getInitials().contains("Wood")
						|| subjects.get(i).getInitials().contains("Metal")
						|| subjects.get(i).getInitials().contains("Bc")
						|| subjects.get(i).getInitials().contains("Dnd")) {
					group4.add(subjects.get(i));
				} else {
					group5.add(subjects.get(i));
				}
			}

			List<Subject> allSubjects = subjectService.getAllSubjects();
			Stream stream = new Stream();
			List<Stream> streams = streamService.getStreamsInSchool(code);

			model.addAttribute("streams", streams);
			model.addAttribute("stream", stream);
			model.addAttribute("group1", group1);
			model.addAttribute("group2", group2);
			model.addAttribute("group3", group3);
			model.addAttribute("group4", group4);
			model.addAttribute("group5", group5);
			model.addAttribute("activeUser", activeUser);
			model.addAttribute("student", student);
			model.addAttribute("school", school);
			model.addAttribute("subjects", allSubjects);

			return "school";

		}

		else {

			School school = new School();
			Student student = new Student();

			List<School> schools = schoolService.getAllSchools();
			Stream stream = new Stream();

			model.addAttribute("stream", stream);
			model.addAttribute("school", school);
			model.addAttribute("activeUser", activeUser);
			model.addAttribute("student", student);
			model.addAttribute("schools", schools);
			model.addAttribute("fail", "School with code:" + code + " does not exist");

			return "adminHome";
		}

	}

	// Adding stream to school
	@PostMapping("/adminHome/school/{code}/streams")
	public String addStreamSchool(Model model, @PathVariable int code, @ModelAttribute Stream stream,
			Principal principal) {

		User activeUser = userService.getByUsername(principal.getName()).get();

		if (schoolService.doesSchoolExists(code) == true) {

			School school = schoolService.getSchool(code).get();

			stream.setSchool(school);
			streamService.addStreamSchool(stream);

			List<Subject> subjects = subjectService.getAllSubjectInSchool(code);
			Student student = new Student();
			List<Subject> group1 = new ArrayList<>();
			List<Subject> group2 = new ArrayList<>();
			List<Subject> group3 = new ArrayList<>();
			List<Subject> group4 = new ArrayList<>();
			List<Subject> group5 = new ArrayList<>();

			for (int i = 0; i < subjects.size(); i++) {
				if (subjects.get(i).getName().contains("Mathematics") || subjects.get(i).getName().contains("English")
						|| subjects.get(i).getName().contains("Kiswahili")) {
					group1.add(subjects.get(i));
				} else if (subjects.get(i).getName().contains("Biology")
						|| subjects.get(i).getName().contains("Physics")
						|| subjects.get(i).getName().contains("Chemistry")) {
					group2.add(subjects.get(i));
				} else if (subjects.get(i).getInitials().contains("Hist")
						|| subjects.get(i).getInitials().contains("Geo")
						|| subjects.get(i).getInitials().contains("C.R.E")
						|| subjects.get(i).getInitials().contains("I.R.E")
						|| subjects.get(i).getInitials().contains("H.R.E")) {
					group3.add(subjects.get(i));
				} else if (subjects.get(i).getInitials().contains("Hsci")
						|| subjects.get(i).getInitials().contains("AnD")
						|| subjects.get(i).getInitials().contains("Agric")
						|| subjects.get(i).getInitials().contains("Comp")
						|| subjects.get(i).getInitials().contains("Avi")
						|| subjects.get(i).getInitials().contains("Elec")
						|| subjects.get(i).getInitials().contains("Pwr")
						|| subjects.get(i).getInitials().contains("Wood")
						|| subjects.get(i).getInitials().contains("Metal")
						|| subjects.get(i).getInitials().contains("Bc")
						|| subjects.get(i).getInitials().contains("Dnd")) {
					group4.add(subjects.get(i));
				} else {
					group5.add(subjects.get(i));
				}
			}

			List<Subject> allSubjects = subjectService.getAllSubjects();
			Stream streamObj = new Stream();
			List<Stream> streams = streamService.getStreamsInSchool(code);

			model.addAttribute("streams", streams);
			model.addAttribute("stream", streamObj);
			model.addAttribute("group1", group1);
			model.addAttribute("group2", group2);
			model.addAttribute("group3", group3);
			model.addAttribute("group4", group4);
			model.addAttribute("group5", group5);
			model.addAttribute("activeUser", activeUser);
			model.addAttribute("student", student);
			model.addAttribute("school", school);
			model.addAttribute("subjects", allSubjects);

			return "school";

		} else {

			School school = new School();
			Student student = new Student();

			List<School> schools = schoolService.getAllSchools();
			Stream streamObj = new Stream();

			model.addAttribute("stream", streamObj);
			model.addAttribute("school", school);
			model.addAttribute("activeUser", activeUser);
			model.addAttribute("student", student);
			model.addAttribute("schools", schools);
			model.addAttribute("fail", "School with code:" + code + " does not exist");

			return "adminHome";
		}
	}

	@GetMapping("/adminHome/school/{code}/streams/{id}")
	public String deleteStream(Model model, @PathVariable int code, @PathVariable int id, @ModelAttribute Stream stream,
			Principal principal) {

		User activeUser = userService.getByUsername(principal.getName()).get();

		if (streamService.doesStreamExistInSchool(id, code) == true) {

			streamService.deleteStream(id);

			model.addAttribute("success", "Stream successfully deleted");
		} else {
			model.addAttribute("fail", "Stream does not exist");
		}

		School school = schoolService.getSchool(code).get();

		List<Subject> subjects = subjectService.getAllSubjectInSchool(code);
		Student student = new Student();

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

		List<Subject> allSubjects = subjectService.getAllSubjects();
		Stream streamObj = new Stream();
		List<Stream> streams = streamService.getStreamsInSchool(code);

		model.addAttribute("streams", streams);
		model.addAttribute("stream", streamObj);
		model.addAttribute("group1", group1);
		model.addAttribute("group2", group2);
		model.addAttribute("group3", group3);
		model.addAttribute("group4", group4);
		model.addAttribute("group5", group5);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("school", school);
		model.addAttribute("subjects", allSubjects);

		return "school";
	}

	@PostMapping("/adminHome/school/{code}/subjects")
	public String addSubjects(Model model, @PathVariable int code, HttpServletRequest request, Principal principal) {

		List<Subject> subjects = new ArrayList<>();
		List<Subject> allSubjects = subjectService.getAllSubjects();
		User activeUser = userService.getByUsername(principal.getName()).get();

		for (int i = 0; i < allSubjects.size(); i++) {
			if (request.getParameter(allSubjects.get(i).getInitials()) != null) {
				subjects.add(subjectService.getSubject(allSubjects.get(i).getInitials()));
			}
		}

		School school = schoolService.getSchool(code).get();

		for (int i = 0; i < subjects.size(); i++) {
			List<School> schools = schoolService.getAllSchoolsWithSubject(subjects.get(i).getInitials());

			for (int j = 0; j < schools.size(); j++) {
				if (schools.get(j) == school) {
					schools.remove(j);
				}
			}

			schools.add(school);
			subjects.get(i).setSchools(schools);
			subjectService.addSubject(subjects.get(i));
		}

		List<Subject> subjects1 = subjectService.getAllSubjectInSchool(code);
		Student student = new Student();

		List<Subject> group1 = new ArrayList<>();
		List<Subject> group2 = new ArrayList<>();
		List<Subject> group3 = new ArrayList<>();
		List<Subject> group4 = new ArrayList<>();
		List<Subject> group5 = new ArrayList<>();

		for (int i = 0; i < subjects1.size(); i++) {
			if (subjects1.get(i).getName().contains("Mathematics") || subjects1.get(i).getName().contains("English")
					|| subjects1.get(i).getName().contains("Kiswahili")) {
				group1.add(subjects1.get(i));
			} else if (subjects1.get(i).getName().contains("Biology") || subjects1.get(i).getName().contains("Physics")
					|| subjects1.get(i).getName().contains("Chemistry")) {
				group2.add(subjects1.get(i));
			} else if (subjects1.get(i).getInitials().contains("Hist") || subjects1.get(i).getInitials().contains("Geo")
					|| subjects1.get(i).getInitials().contains("C.R.E")
					|| subjects1.get(i).getInitials().contains("I.R.E")
					|| subjects1.get(i).getInitials().contains("H.R.E")) {
				group3.add(subjects1.get(i));
			} else if (subjects1.get(i).getInitials().contains("Hsci") || subjects1.get(i).getInitials().contains("AnD")
					|| subjects1.get(i).getInitials().contains("Agric")
					|| subjects1.get(i).getInitials().contains("Comp") || subjects1.get(i).getInitials().contains("Avi")
					|| subjects1.get(i).getInitials().contains("Elec") || subjects1.get(i).getInitials().contains("Pwr")
					|| subjects1.get(i).getInitials().contains("Wood")
					|| subjects1.get(i).getInitials().contains("Metal") || subjects1.get(i).getInitials().contains("Bc")
					|| subjects1.get(i).getInitials().contains("Dnd")) {
				group4.add(subjects1.get(i));
			} else {
				group5.add(subjects1.get(i));
			}
		}
		Stream stream = new Stream();
		List<Stream> streams = streamService.getStreamsInSchool(code);

		model.addAttribute("streams", streams);
		model.addAttribute("stream", stream);
		model.addAttribute("group1", group1);
		model.addAttribute("group2", group2);
		model.addAttribute("group3", group3);
		model.addAttribute("group4", group4);
		model.addAttribute("group5", group5);
		model.addAttribute("success", "Subjects saved successfully");
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("subjects", allSubjects);
		model.addAttribute("addSubjects", subjectService.getAllSubjects());
		model.addAttribute("school", school);

		return "school";
	}

	@GetMapping("/adminHome/schools/{code}/subjects/{initials}")
	public String deleteSubjectFromSchool(@PathVariable int code, @PathVariable String initials, Model model,
			Principal principal) {

		User activeUser = userService.getByUsername(principal.getName()).get();

		if (subjectService.doesSubjectExistsInSchool(initials, code) == true) {

			Subject subject = subjectService.getSubjectInSchool(initials, code);
			subject.getSchools().remove(schoolService.getSchool(code).get());
			subjectService.addSubject(subject);

			model.addAttribute("success", initials + " subject successfully deleted");
		} else {

			model.addAttribute("fail", initials + " subject does not exist");
		}

		School school = schoolService.getSchool(code).get();
		List<Subject> subjects1 = subjectService.getAllSubjectInSchool(code);
		Student student = new Student();
		List<Subject> group1 = new ArrayList<>();
		List<Subject> group2 = new ArrayList<>();
		List<Subject> group3 = new ArrayList<>();
		List<Subject> group4 = new ArrayList<>();
		List<Subject> group5 = new ArrayList<>();

		for (int i = 0; i < subjects1.size(); i++) {
			if (subjects1.get(i).getName().contains("Mathematics") || subjects1.get(i).getName().contains("English")
					|| subjects1.get(i).getName().contains("Kiswahili")) {
				group1.add(subjects1.get(i));
			} else if (subjects1.get(i).getName().contains("Biology") || subjects1.get(i).getName().contains("Physics")
					|| subjects1.get(i).getName().contains("Chemistry")) {
				group2.add(subjects1.get(i));
			} else if (subjects1.get(i).getInitials().contains("Hist") || subjects1.get(i).getInitials().contains("Geo")
					|| subjects1.get(i).getInitials().contains("C.R.E")
					|| subjects1.get(i).getInitials().contains("I.R.E")
					|| subjects1.get(i).getInitials().contains("H.R.E")) {
				group3.add(subjects1.get(i));
			} else if (subjects1.get(i).getInitials().contains("Hsci") || subjects1.get(i).getInitials().contains("AnD")
					|| subjects1.get(i).getInitials().contains("Agric")
					|| subjects1.get(i).getInitials().contains("Comp") || subjects1.get(i).getInitials().contains("Avi")
					|| subjects1.get(i).getInitials().contains("Elec") || subjects1.get(i).getInitials().contains("Pwr")
					|| subjects1.get(i).getInitials().contains("Wood")
					|| subjects1.get(i).getInitials().contains("Metal") || subjects1.get(i).getInitials().contains("Bc")
					|| subjects1.get(i).getInitials().contains("Dnd")) {
				group4.add(subjects1.get(i));
			} else {
				group5.add(subjects1.get(i));
			}
		}

		Stream stream = new Stream();
		List<Stream> streams = streamService.getStreamsInSchool(code);

		model.addAttribute("streams", streams);
		model.addAttribute("stream", stream);
		model.addAttribute("group1", group1);
		model.addAttribute("group2", group2);
		model.addAttribute("group3", group3);
		model.addAttribute("group4", group4);
		model.addAttribute("group5", group5);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("subjects", subjects1);
		model.addAttribute("addSubjects", subjectService.getAllSubjects());
		model.addAttribute("school", school);

		return "school";
	}

	@GetMapping("/adminHome/users")
	public String systemUsers(Model model, Principal principal) {

		List<User> users = userService.findAllUsers();
		List<User> systemUsers = new ArrayList<>();
		List<School> schools = schoolService.getAllSchools();
		User activeUser = userService.getByUsername(principal.getName()).get();

		for (int i = 0; i < users.size(); i++) {
			if (users.get(i).getRole().getName().contains("ADMIN")
					|| users.get(i).getRole().getName().contains("FIELDOFFICER")
					|| users.get(i).getRole().getName().contains("OFFICEASSISTANT")
					|| users.get(i).getRole().getName().contains("C.E.O")) {
				systemUsers.add(users.get(i));
			}
		}

		Student student = new Student();
		School school = new School();
		User user = new User();

		model.addAttribute("activeUser", activeUser);
		model.addAttribute("schools", schools);
		model.addAttribute("user", user);
		model.addAttribute("school", school);
		model.addAttribute("student", student);
		model.addAttribute("users", systemUsers);

		return "users";
	}

	@GetMapping("/adminHome/schoolUsers")
	public String schoolUsers(Model model, Principal principal) {

		List<User> users = userService.findAllUsers();
		List<User> schoolUsers = new ArrayList<>();

		for (int i = 0; i < users.size(); i++) {
			if (users.get(i).getRole().getName().contains("PRINCIPAL")
					|| users.get(i).getRole().getName().contains("DEPUTYPRINCIPAL")
					|| users.get(i).getRole().getName().contains("D.O.S")
					|| users.get(i).getRole().getName().contains("TEACHER")
					|| users.get(i).getRole().getName().contains("BURSAR")
					|| users.get(i).getRole().getName().contains("ACCOUNTSCLERK")) {

				schoolUsers.add(users.get(i));
			}
		}

		List<School> schools = schoolService.getAllSchools();
		User activeUser = userService.getByUsername(principal.getName()).get();

		Student student = new Student();
		School school = new School();
		User user = new User();

		model.addAttribute("activeUser", activeUser);
		model.addAttribute("schools", schools);
		model.addAttribute("user", user);
		model.addAttribute("school", school);
		model.addAttribute("student", student);
		model.addAttribute("users", schoolUsers);

		return "schoolUsers";
	}

	@PostMapping("/adminHome/users")
	public String addSystemUsers(Model model, @RequestParam String role, @RequestParam int school,
			@RequestParam String name, @RequestParam String username, @RequestParam String password,
			Principal principal) {

		User activeUser = userService.getByUsername(principal.getName()).get();
		Student student = new Student();
		School schoolObj = new School();

		List<School> schools = schoolService.getAllSchools();
		User Systemuser = new User();

		if (userService.userExists(username) == true) {

			model.addAttribute("fail", "User with username:" + username + " already exists");

		} else {

			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			User user = new User(username, name, encoder.encode(password));

			Role roleObj = new Role();

			if (school > 0) {

				user.setSchool(new School("", school));
			}

			switch (role) {
			case "admin":
				roleObj.setName("ADMIN");
				break;
			case "ceo":
				roleObj.setName("C.E.O");
				break;
			case "officeAssistant":
				roleObj.setName("OFFICEASSISTANT");
				break;
			case "fieldAssistant":
				roleObj.setName("FIELDOFFICER");
				break;
			case "Principal":
				if (userService.userWithRoleInSchool(role, user.getSchool().getCode()).isEmpty()) {
					roleObj.setName("PRINCIPAL");
					break;
				} else {

					model.addAttribute("fail",
							schoolService.getSchool(school).get().getName() + " already has a principal");

					List<User> users = userService.findAllUsers();

					model.addAttribute("activeUser", activeUser);
					model.addAttribute("schools", schools);
					model.addAttribute("user", Systemuser);
					model.addAttribute("school", schoolObj);
					model.addAttribute("student", student);
					model.addAttribute("users", users);

					return "users";
				}
			case "deputyPrincipal":
				if (userService.userWithRoleInSchool(role, user.getSchool().getCode()).isEmpty()) {
					roleObj.setName("DEPUTYPRINCIPAL");
					break;
				} else if (userService.userWithRoleInSchool("D.O.S", user.getSchool().getCode()).size() < 1) {
					roleObj.setName("D.O.S");
					break;
				} else {
					model.addAttribute("fail",
							schoolService.getSchool(school).get().getName() + " already has two Deputy principal");

					List<User> users = userService.findAllUsers();

					model.addAttribute("activeUser", activeUser);
					model.addAttribute("schools", schools);
					model.addAttribute("user", Systemuser);
					model.addAttribute("school", schoolObj);
					model.addAttribute("student", student);
					model.addAttribute("users", users);

					return "users";
				}
			case "teacher":
				roleObj.setName("TEACHER");
				break;
			case "bursar":
				roleObj.setName("BURSAR");
				break;
			case "accountsClerk":
				roleObj.setName("ACCOUNTSCLERK");
				break;
			default:
				break;
			}

			user.setRole(roleObj);
			roleService.addRole(roleObj);
			userService.addUser(user);

			model.addAttribute("success", username + " successfully added");

		}

		List<User> users = userService.findAllUsers();

		model.addAttribute("activeUser", activeUser);
		model.addAttribute("schools", schools);
		model.addAttribute("user", Systemuser);
		model.addAttribute("school", schoolObj);
		model.addAttribute("student", student);

		User addedUser = userService.getByUsername(username).get();

		if (addedUser.getRole().getName().contains("ADMIN") || addedUser.getRole().getName().contains("FIELDOFFICER")
				|| addedUser.getRole().getName().contains("OFFICEASSISTANT")
				|| addedUser.getRole().getName().contains("C.E.O")) {

			List<User> systemUsers = new ArrayList<>();

			for (int i = 0; i < users.size(); i++) {
				if (users.get(i).getRole().getName().contains("ADMIN")
						|| users.get(i).getRole().getName().contains("FIELDOFFICER")
						|| users.get(i).getRole().getName().contains("OFFICEASSISTANT")
						|| users.get(i).getRole().getName().contains("C.E.O")) {
					systemUsers.add(users.get(i));
				}
			}

			model.addAttribute("users", systemUsers);

			return "users";
		} else {

			List<User> schoolUsers = new ArrayList<>();

			for (int i = 0; i < users.size(); i++) {
				if (users.get(i).getRole().getName().contains("PRINCIPAL")
						|| users.get(i).getRole().getName().contains("DEPUTYPRINCIPAL")
						|| users.get(i).getRole().getName().contains("D.O.S")
						|| users.get(i).getRole().getName().contains("TEACHER")
						|| users.get(i).getRole().getName().contains("BURSAR")
						|| users.get(i).getRole().getName().contains("ACCOUNTSCLERK")) {

					schoolUsers.add(users.get(i));
				}
			}

			model.addAttribute("users", schoolUsers);

			return "schoolUsers";
		}

	}

	@GetMapping("/adminHome/user/{username}")
	public String deleteUser(@PathVariable String username, Model model, Principal principal) {

		if (userService.userExists(username) == true) {

			userService.deleteUser(username);

			model.addAttribute("success", username + " successfully deleted");
		} else {

			model.addAttribute("fail", "A user with username:" + username + " does not exist");
		}

		Student student = new Student();
		School school = new School();

		User user = new User();
		User activeUser = userService.getByUsername(principal.getName()).get();

		List<School> schools = schoolService.getAllSchools();

		model.addAttribute("activeUser", activeUser);
		model.addAttribute("schools", schools);
		model.addAttribute("user", user);
		model.addAttribute("student", student);
		model.addAttribute("school", school);

		List<User> users = userService.findAllUsers();
		List<User> systemUsers = new ArrayList<>();

		for (int i = 0; i < users.size(); i++) {
			if (users.get(i).getRole().getName().contains("ADMIN")
					|| users.get(i).getRole().getName().contains("FIELDOFFICER")
					|| users.get(i).getRole().getName().contains("OFFICEASSISTANT")
					|| users.get(i).getRole().getName().contains("C.E.O")) {
				systemUsers.add(users.get(i));
			}
		}

		model.addAttribute("users", systemUsers);

		return "users";

	}

	@GetMapping("/adminHome/schoolUser/{username}")
	public String deleteSchoolUser1(@PathVariable String username, Model model, Principal principal) {

		if (userService.userExists(username) == true) {

			userService.deleteUser(username);

			model.addAttribute("success", username + " successfully deleted");
		} else {

			model.addAttribute("fail", "A user with username:" + username + " does not exist");
		}

		Student student = new Student();
		School school = new School();

		User user = new User();
		User activeUser = userService.getByUsername(principal.getName()).get();

		List<School> schools = schoolService.getAllSchools();

		model.addAttribute("activeUser", activeUser);
		model.addAttribute("schools", schools);
		model.addAttribute("user", user);
		model.addAttribute("student", student);
		model.addAttribute("school", school);

		List<User> users = userService.findAllUsers();
		List<User> schoolUsers = new ArrayList<>();

		for (int i = 0; i < users.size(); i++) {
			if (users.get(i).getRole().getName().contains("PRINCIPAL")
					|| users.get(i).getRole().getName().contains("DEPUTYPRINCIPAL")
					|| users.get(i).getRole().getName().contains("D.O.S")
					|| users.get(i).getRole().getName().contains("TEACHER")) {

				schoolUsers.add(users.get(i));
			}
		}

		model.addAttribute("users", schoolUsers);

		return "schoolUsers";

	}

	@GetMapping("/adminHome/users/{username}")
	public String getSingleUser(@PathVariable String username, Model model, Principal principal) {

		User activeUser = userService.getByUsername(principal.getName()).get();

		if (userService.userExists(username) == true) {

			School school;

			User user = userService.getByUsername(username).get();

			if (activeUser.getSchool() != null) {
				school = activeUser.getSchool();
			} else {
				school = new School();
			}

			Student student = new Student();

			model.addAttribute("activeUser", activeUser);
			model.addAttribute("school", school);
			model.addAttribute("student", student);
			model.addAttribute("user", user);

			return "userHome";
		} else {

			Student student = new Student();
			School school = new School();
			User user = new User();
			List<User> users = userService.findAllUsers();
			List<School> schools = schoolService.getAllSchools();

			model.addAttribute("fail", "User with username:" + username + " does not exist");
			model.addAttribute("activeUser", activeUser);
			model.addAttribute("schools", schools);
			model.addAttribute("user", user);
			model.addAttribute("student", student);
			model.addAttribute("school", school);
			model.addAttribute("users", users);

			return "users";
		}

	}

	@GetMapping("/adminHome/schools/{code}/students/{admNo}/subjects/{initials}")
	public String deleteSubjectFromStudent(@PathVariable int code, @PathVariable String admNo,
			@PathVariable String initials, Model model, Principal principal) {

		Student student = studentService.getStudentInSchool(admNo, code);

		Subject subject = subjectService.getSubjectByStudent(initials, admNo);
		student.getSubjects().remove(subject);
		studentService.addStudent(student);

		List<Form> forms = formService.studentForms(admNo);
		School school = schoolService.getSchool(code).get();
		List<Subject> subjects = subjectService.getSubjectDoneByStudent(admNo);
		List<Subject> schoolSubjects = subjectService.getAllSubjectInSchool(code);
		User activeUser = userService.getByUsername(principal.getName()).get();
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

	@GetMapping("/schools/{code}/addStudent")
	public String addStudent(Model model, Principal principal, @PathVariable int code) {

		School school = schoolService.getSchool(code).get();
		User activeUser = userService.getByUsername(principal.getName()).get();
		Student student = new Student();
		List<Stream> streams = streamService.getStreamsInSchool(code);

		model.addAttribute("streams", streams);
		model.addAttribute("student", student);
		model.addAttribute("school", school);
		model.addAttribute("activeUser", activeUser);

		return "addStudent";
	}

	@GetMapping("/adminHome/schools/{code}/students")
	public String allStudents(@PathVariable int code, Model model, Principal principal) {

		List<Student> students = studentService.getAllStudentsInSchool(code);
		School school = schoolService.getSchool(code).get();
		User activeUser = userService.getByUsername(principal.getName()).get();
		List<Stream> streams = streamService.getStreamsInSchool(code);

		Student student = new Student();
		List<Subject> subjects = subjectService.getAllSubjectInSchool(code);

		model.addAttribute("subjects", subjects);
		model.addAttribute("streams", streams);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("school", school);
		model.addAttribute("students", students);

		return "students";
	}

	@PostMapping("/adminHome/schools/{code}/students")
	public String addStudent(@PathVariable int code, @Valid Student student, BindingResult bindingResult, Model model,
			Principal principal, @RequestParam int stream) {

		List<School> schools = new ArrayList<>();
		School school = schoolService.getSchool(code).get();
		schools.add(school);

		if (studentService.ifStudentExistsInSchool(code + "_" + student.getAdmNo(), code) == true) {

			model.addAttribute("fail", "Student with admision number:" + student.getAdmNo() + " already exists");

		} else {

			if (student.getAdmNo() == "" || Integer.parseInt(student.getAdmNo()) < 1) {

				model.addAttribute("fail", "Admission number cannot be less than 1");

			} else {

				student.setSchool(schoolService.getSchool(code).get());

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
				case 4:
					forms.add(new Form(1, terms));
					forms.add(new Form(2, terms));
					forms.add(new Form(3, terms));
					forms.add(new Form(4, terms));
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

				model.addAttribute("success",
						"Student with admision number:" + student.getAdmNo() + " saved successfully");

			}

		}

		Student student2 = new Student();
		User activeUser = userService.getByUsername(principal.getName()).get();

		List<Student> students = studentService.getAllStudentsInSchool(code);
		List<Stream> streams = streamService.getStreamsInSchool(code);
		List<Subject> subjects = subjectService.getAllSubjectInSchool(code);

		model.addAttribute("subjects", subjects);
		model.addAttribute("streams", streams);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student2);
		model.addAttribute("students", students);
		model.addAttribute("school", school);

		return "students";
	}

	@GetMapping("/adminHome/schools/{code}/students/{admNo}")
	public String deleteStudent(@PathVariable int code, @PathVariable String admNo, Model model, Principal principal) {

		if (studentService.ifStudentExistsInSchool(admNo, code) == true) {

			List<Mark> marks = markService.allMarks(admNo);
			for (int i = 0; i < marks.size(); i++) {
				markService.deleteMark(marks.get(i).getId());
			}
			studentService.deleteStudent(admNo);

			model.addAttribute("success", "Student with admision number:" + admNo + " successfully deleted");
		} else {

			model.addAttribute("fail", "Student with admision number:" + admNo + " does not exist");
		}

		List<Student> students = studentService.getAllStudentsInSchool(code);
		User activeUser = userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(code).get();
		List<Stream> streams = streamService.getStreamsInSchool(code);
		Student student = new Student();
		List<Subject> subjects = subjectService.getAllSubjectInSchool(code);

		model.addAttribute("subjects", subjects);
		model.addAttribute("streams", streams);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("school", school);
		model.addAttribute("students", students);

		if (activeUser.getRole().getName().contains("TEACHER")) {
			return "teacherHome";
		}

		return "students";
	}

	@GetMapping("/adminHome/schools/{code}/student/{admNo}")
	public String getStudent(@PathVariable int code, @PathVariable String admNo, Model model, Principal principal) {

		Student student = studentService.getStudentInSchool(admNo, code);
		School school = schoolService.getSchool(code).get();
		List<Subject> subjects = subjectService.getSubjectDoneByStudent(admNo);
		List<Subject> schoolSubjects = subjectService.getAllSubjectInSchool(code);
		List<Form> forms = formService.studentForms(admNo);
		List<Year> years = yearService.allYearsForStudent(admNo);
		User activeUser = userService.getByUsername(principal.getName()).get();

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

		model.addAttribute("group1", group1);
		model.addAttribute("group2", group2);
		model.addAttribute("group3", group3);
		model.addAttribute("group4", group4);
		model.addAttribute("group5", group5);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("years", years);
		model.addAttribute("forms", forms);
		model.addAttribute("school", school);
		model.addAttribute("subjects", subjects);
		model.addAttribute("schoolSubjects", schoolSubjects);
		model.addAttribute("student", student);

		return "student";
	}

	@PostMapping("/adminHome/schools/{code}/marks/{exam}")
	public String addMarksToStudentSubjects(@PathVariable int code, @PathVariable String exam,
			HttpServletRequest request, Model model, Principal principal) {

		int form = Integer.parseInt(request.getParameter("form"));
		int year = Integer.parseInt(request.getParameter("year"));
		int term = Integer.parseInt(request.getParameter("term"));
		String subject = request.getParameter("subject");
		Subject subjectObj = subjectService.getSubjectByName(subject);

		List<Student> students = studentService.findAllStudentDoingSubject(code, year, form, term,
				subjectObj.getInitials());

		Year yearObj = yearService.getYearFromSchool(year, code).get();
		Term termObj = termService.getTerm(term, form, year, code);

		for (int i = 0; i < students.size(); i++) {

			Form formObj = formService.getStudentForm(form, students.get(i).getAdmNo());

			Mark mark = new Mark(students.get(i), yearObj, formObj, termObj);
			mark.setSubject(subjectObj);

			switch (exam) {
			case "Cat1":
				mark.setCat1(Integer.parseInt(request.getParameter(students.get(0).getAdmNo() + "mark")));
				break;
			case "Cat2":
				mark.setCat2(Integer.parseInt(request.getParameter(students.get(0).getAdmNo() + "mark")));
				break;
			case "mainExam":
				mark.setMainExam(Integer.parseInt(request.getParameter(students.get(0).getAdmNo() + "mark")));
				break;
			default:
				break;
			}

			markService.addMarksToSubject(mark);
			model.addAttribute("success", "Marks saved successfully");
		}

		if (students.size() == 0) {
			model.addAttribute("fail", "No student. Cannot add marks");
		}

		List<Student> schoolStudents = studentService.getAllStudentsInSchool(code);
		User activeUser = userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(code).get();
		Student student2 = new Student();
		List<Stream> streams = streamService.getStreamsInSchool(code);
		List<Subject> schoolSubjects = subjectService.getAllSubjectInSchool(code);

		model.addAttribute("subjects", schoolSubjects);
		model.addAttribute("streams", streams);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student2);
		model.addAttribute("students", schoolStudents);
		model.addAttribute("school", school);

		return "students";

	}

	@GetMapping("/adminHome/schools/{code}/student/{admNo}/progress")
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

	@PostMapping("/adminHome/schools/{code}/student/{admNo}/subjects")
	public String addSubjectToStudent(Model model, @PathVariable int code, @PathVariable String admNo,
			HttpServletRequest request, @RequestParam int form, @RequestParam int year, Principal principal) {

		Student student = studentService.getStudentInSchool(admNo, code);
		School school = schoolService.getSchool(code).get();
		List<Subject> schoolSubjects = subjectService.getAllSubjectInSchool(code);
		List<Subject> studentSubjects = new ArrayList<>();
		User activeUser = userService.getByUsername(principal.getName()).get();

		List<String> paramList = new ArrayList<>();

		for (int i = 0; i < schoolSubjects.size(); i++) {
			paramList.add(request.getParameter(schoolSubjects.get(i).getInitials()));

			if (paramList.get(i) != null) {
				studentSubjects.add(subjectService.getSubject(paramList.get(i)));
			}
		}

		Form formObj = formService.getStudentForm(form, admNo);
		formObj.setSubjects(studentSubjects);
		formService.addForm(formObj);

		Year yearObj = yearService.getYearFromSchool(2020, code).get();
		yearObj.setSubjects(studentSubjects);
		yearService.addYear(yearObj);

		student.setSubjects(studentSubjects);
		studentService.addStudent(student);

		List<Subject> subjects = subjectService.getSubjectDoneByStudent(admNo);
		List<Form> forms = formService.studentForms(admNo);
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

	@GetMapping("/adminHome/schools/{code}/timetable")
	public String getSchoolTimetable(@PathVariable int code, Model model, Principal principal) {

		School school = schoolService.getSchool(code).get();
		Student student = new Student();
		User activeUser = userService.getByUsername(principal.getName()).get();

		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("school", school);

		return "timetable";
	}

	@GetMapping("/adminHome/schools/{code}/student/{admNo}/yearly")
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

	@PostMapping("/school/users")
	public String addSchoolUsers(Model model, @RequestParam String role, @RequestParam int school,
			@RequestParam String name, @RequestParam String username, @RequestParam String password,
			Principal principal) {

		Student student = new Student();
		School schoolObj = new School();
		List<School> schools = schoolService.getAllSchools();
		User Systemuser = new User();
		Role roleObj = new Role();
		User activeUser = userService.getByUsername(principal.getName()).get();

		if (userService.userExists(username) == true) {

			model.addAttribute("fail", "A user with username " + username + " already exists");

		} else {

			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			User user = new User(username, name, encoder.encode(password));

			user.setSchool(new School("", school));

			switch (role) {
			case "Principal":
				roleObj.setName("PRINCIPAL");
				break;
			case "deputyPrincipal":
				roleObj.setName("DEPUTYPRINCIPAL");
				break;
			case "teacher":
				roleObj.setName("TEACHER");
			default:
				break;
			}

			user.setRole(roleObj);
			roleService.addRole(roleObj);
			userService.addUser(user);

			model.addAttribute("success", username + " saved successfully");
		}

		List<User> schoolUsers = userService.getUsersBySchoolCode(activeUser.getSchool().getCode());

		List<Year> years = yearService.getAllYearsInSchool(school);
		List<Stream> streams = streamService.getStreamsInSchool(school);

		model.addAttribute("streams", streams);
		model.addAttribute("years", years);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("schools", schools);
		model.addAttribute("user", Systemuser);
		model.addAttribute("school", schoolObj);
		model.addAttribute("student", student);
		model.addAttribute("schoolUsers", schoolUsers);

		return "principalHome";

	}

	@GetMapping("/schools/users/{username}")
	public String deleteSchoolUser(@PathVariable String username, Model model, Principal principal) {

		User activeUser = userService.getByUsername(principal.getName()).get();
		Student student = new Student();
		School school = schoolService.getSchool(activeUser.getSchool().getCode()).get();
		User user = new User();

		if (userService.userExists(username) == true) {

			if (username.contentEquals(principal.getName())) {

				model.addAttribute("fail", "You cannot delete yourself");
			} else {

				userService.deleteUser(username);

				model.addAttribute("success", username + " successfully deleted");
			}

		} else {

			model.addAttribute("fail", "A user with username " + username + " does not exist");
		}

		List<School> schools = schoolService.getAllSchools();
		List<User> schoolUsers = userService.getUsersBySchoolCode(activeUser.getSchool().getCode());

		List<Year> years = yearService.getAllYearsInSchool(school.getCode());
		List<Stream> streams = streamService.getStreamsInSchool(school.getCode());

		model.addAttribute("streams", streams);
		model.addAttribute("years", years);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("schools", schools);
		model.addAttribute("user", user);
		model.addAttribute("student", student);
		model.addAttribute("school", school);
		model.addAttribute("schoolUsers", schoolUsers);

		return "principalHome";

	}

	@GetMapping("/schools/principal")
	public String principalHome(Model model, Principal principal) {

		User activeUser = userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(activeUser.getSchool().getCode()).get();
		Student student = new Student();
		User user = new User();
		List<User> schoolUsers = userService.getUsersBySchoolCode(school.getCode());

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

	@GetMapping("/schools/students")
	public String schoolStudents(Model model, Principal principal) {

		User activeUser = userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(activeUser.getSchool().getCode()).get();
		Student student = new Student();

		List<Student> students = studentService.getAllStudentsInSchool(school.getCode());
		List<Stream> streams = streamService.getStreamsInSchool(school.getCode());
		List<Subject> subjects = subjectService.getAllSubjectInSchool(school.getCode());

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

		User activeUser = userService.getByUsername(principal.getName()).get();
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
	public String addSchoolStudents(Model model, @ModelAttribute Student student, Principal principal,
			@RequestParam int stream) {

		User activeUser = userService.getByUsername(principal.getName()).get();

		School school = schoolService.getSchool(activeUser.getSchool().getCode()).get();
		student.setSchool(school);

		if (studentService.ifStudentExistsInSchool(school.getCode() + "_" + student.getAdmNo(),
				school.getCode()) == true) {

			model.addAttribute("fail", "Student with adm No:" + student.getAdmNo() + " already exists");
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

			model.addAttribute("success", "Student with adm No: " + student.getAdmNo() + " added successfully");
		}

		List<Student> students = studentService.getAllStudentsInSchool(school.getCode());
		List<Subject> subjects = subjectService.getAllSubjectInSchool(school.getCode());

		Student student2 = new Student();
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

		model.addAttribute("streams", streams);
		model.addAttribute("group1", group1);
		model.addAttribute("group2", group2);
		model.addAttribute("group3", group3);
		model.addAttribute("group4", group4);
		model.addAttribute("group5", group5);
		model.addAttribute("subjects", subjects);
		model.addAttribute("students", students);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student2);
		model.addAttribute("school", school);

		return "teacherHome";
	}

	@GetMapping("/school/teachers")
	public String schoolTeachers(Model model, Principal principal) {

		User activeUser = userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(activeUser.getSchool().getCode()).get();
		Student student = new Student();
		User user = new User();

		List<User> teachers = userService.findUserByRole("TEACHER");

		model.addAttribute("user", user);
		model.addAttribute("teachers", teachers);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("school", school);

		return "teachers";
	}

	@PostMapping("/school/teachers")
	public String addTeacher(Model model, @RequestParam String role, @RequestParam int school,
			@RequestParam String name, @RequestParam String username, @RequestParam String password,
			Principal principal) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		User user = new User(username, name, encoder.encode(password));

		Student student = new Student();
		School schoolObj = schoolService.getSchool(school).get();
		List<School> schools = schoolService.getAllSchools();
		User Systemuser = new User();
		Role roleObj = new Role();
		User activeUser = userService.getByUsername(principal.getName()).get();
		user.setSchool(new School("", school));
		roleObj.setName("TEACHER");

		user.setRole(roleObj);
		roleService.addRole(roleObj);

		if (userService.userExists(username) == true) {

			model.addAttribute("fail", "A user with the username: " + username + " already exists");

		} else {

			userService.addUser(user);

			model.addAttribute("success", "Teacher saved successfully");
		}

		List<User> teachers = userService.findUserByRole(roleObj.getName());

		model.addAttribute("activeUser", activeUser);
		model.addAttribute("schools", schools);
		model.addAttribute("user", Systemuser);
		model.addAttribute("school", schoolObj);
		model.addAttribute("student", student);
		model.addAttribute("teachers", teachers);

		return "teachers";
	}

	@GetMapping("/school/teachers/{username}")
	public String deleteTeacher(@PathVariable String username, Model model, Principal principal) {

		User activeUser = userService.getByUsername(principal.getName()).get();
		Student student = new Student();
		School school = schoolService.getSchool(activeUser.getSchool().getCode()).get();
		User user = new User();

		userService.deleteUser(username);

		List<User> users = userService.findAllUsers();
		List<School> schools = schoolService.getAllSchools();

		model.addAttribute("activeUser", activeUser);
		model.addAttribute("schools", schools);
		model.addAttribute("user", user);
		model.addAttribute("student", student);
		model.addAttribute("school", school);
		model.addAttribute("users", users);

		return "teachers";
	}

	@GetMapping("/schools/deputyPrincipal")
	public String deputyHome(Model model, Principal principal) {

		User activeUser = userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(activeUser.getSchool().getCode()).get();
		Student student = new Student();
		User user = new User();
		List<User> schoolUsers = userService.getUsersBySchoolCode(school.getCode());

		model.addAttribute("schoolUsers", schoolUsers);
		model.addAttribute("user", user);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("school", school);

		return "deputyPrincipal";
	}

	@GetMapping("/schools/dosHome")
	public String dosHome(Model model, Principal principal) {

		User activeUser = userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(activeUser.getSchool().getCode()).get();
		Student student = new Student();
		User user = new User();
		List<User> schoolUsers = userService.getUsersBySchoolCode(school.getCode());

		model.addAttribute("schoolUsers", schoolUsers);
		model.addAttribute("user", user);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("school", school);

		return "dosHome";
	}

	@GetMapping("/schools/bursarHome")
	public String bursarHome(Model model, Principal principal) {

		User activeUser = userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(activeUser.getSchool().getCode()).get();
		Student student = new Student();
		User user = new User();
		List<User> schoolUsers = userService.getUsersBySchoolCode(school.getCode());

		model.addAttribute("schoolUsers", schoolUsers);
		model.addAttribute("user", user);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("school", school);

		return "bursarHome";
	}

	@GetMapping("schools/bursarHome/createStructure/{form}")
	public String createFeeStructure(Model model, Principal principal, @PathVariable int form) {

		switch (form) {
		case 1:
			model.addAttribute("form", "1");
			break;
		case 2:
			model.addAttribute("form", "2");
			break;
		case 3:
			model.addAttribute("form", "3");
			break;
		case 4:
			model.addAttribute("form", "4");
			break;
		default:
			break;
		}

		User activeUser = userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(activeUser.getSchool().getCode()).get();
		Student student = new Student();
		User user = new User();
		List<User> schoolUsers = userService.getUsersBySchoolCode(school.getCode());
		List<FeeStructure> feeStructures = feeStructureService.allFeeItemInSchoolAndForm(school.getCode(), form);

		model.addAttribute("feeStructures", feeStructures);
		model.addAttribute("schoolUsers", schoolUsers);
		model.addAttribute("user", user);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("school", school);

		return "createFeeStructure";
	}

	@PostMapping("/schools/bursarHome/createStructure/{classes}/items")
	public String addItemToFeeStructure(Model model, Principal principal, @PathVariable int classes,
			@ModelAttribute FeeStructure feeStructure) {

		User activeUser = userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(activeUser.getSchool().getCode()).get();

		Form formObj = formService.getFormByForm(classes);

		if (formObj == null) {
			formObj = new Form(classes);
			formService.addForm(formObj);
		}

		feeStructure.setSchool(school);
		feeStructure.setForm(formObj);

		feeStructureService.addItem(feeStructure);

		Student student = new Student();
		User user = new User();
		List<User> schoolUsers = userService.getUsersBySchoolCode(school.getCode());
		List<FeeStructure> feeStructures = feeStructureService.allFeeItemInSchoolAndForm(school.getCode(), classes);

		model.addAttribute("form", classes);
		model.addAttribute("feeStructures", feeStructures);
		model.addAttribute("schoolUsers", schoolUsers);
		model.addAttribute("user", user);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("school", school);

		return "createFeeStructure";

	}

	@GetMapping("/schools/bursarHome/createStructure/{classes}/items/{id}")
	public String deleteFeeStructureItem(Principal principal, Model model, @PathVariable int classes,
			@PathVariable int id) {

		User activeUser = userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(activeUser.getSchool().getCode()).get();

		Student student = new Student();
		User user = new User();
		List<User> schoolUsers = userService.getUsersBySchoolCode(school.getCode());

		feeStructureService.deleteFeeStructureItem(id);

		List<FeeStructure> feeStructures = feeStructureService.allFeeItemInSchoolAndForm(school.getCode(), classes);

		model.addAttribute("form", classes);
		model.addAttribute("feeStructures", feeStructures);
		model.addAttribute("schoolUsers", schoolUsers);
		model.addAttribute("user", user);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("school", school);

		return "createFeeStructure";
	}

	@GetMapping("/schools/accountsClerkHome")
	public String accountsClerkHome(Model model, Principal principal) {

		User activeUser = userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(activeUser.getSchool().getCode()).get();
		Student student = new Student();
		User user = new User();
		List<User> schoolUsers = userService.getUsersBySchoolCode(school.getCode());

		model.addAttribute("schoolUsers", schoolUsers);
		model.addAttribute("user", user);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("school", school);

		return "accountsClerkHome";
	}

	@GetMapping("/schools/teacherHome")
	public String teacherHome(Model model, Principal principal) {

		User activeUser = userService.getByUsername(principal.getName()).get();
		School school = activeUser.getSchool();
		List<Student> students = studentService.getAllStudentsInSchool(school.getCode());
		List<Subject> subjects = subjectService.getAllSubjectInSchool(school.getCode());
		List<Stream> streams = streamService.getStreamsInSchool(school.getCode());

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

		model.addAttribute("activeUser", activeUser);

		if (userService.userExists(activeUser.getUsername()) == true) {

			Student student = new Student();

			model.addAttribute("activeUser", activeUser);
			model.addAttribute("school", activeUser.getSchool());
			model.addAttribute("student", student);
			model.addAttribute("user", activeUser);

			return "userHome";
		} else {

			School school = activeUser.getSchool();
			List<Student> students = studentService.getAllStudentsInSchool(school.getCode());

			model.addAttribute("fail", "A teacher with username " + activeUser.getUsername() + " does not exist");
			model.addAttribute("students", students);
			model.addAttribute("school", school);
			model.addAttribute("activeUser", activeUser);
			model.addAttribute("student", new Student());

			return "teacherHome";
		}

	}

	@GetMapping("/schools/{code}/years/{year}/forms/{form}/terms/{term}/subjects/{subject}/streams/{stream}/exams/{exam}")
	public String addingMarksForAllStudentSubjects(Model model, Principal principal, @PathVariable int code,
			@PathVariable int year, @PathVariable String subject, @PathVariable int form, @PathVariable int term,
			@PathVariable int stream, @PathVariable String exam) {

		User activeUser = userService.getByUsername(principal.getName()).get();
		Subject subjectObj = subjectService.getSubject(subject);
		School school = schoolService.getSchool(code).get();
		Student student = new Student();
		Stream streamObj = streamService.getStream(stream);

		List<Student> students = studentService.findAllStudentDoingSubject(code, year, form, term, subject);

		model.addAttribute("students", students);
		model.addAttribute("subject", subjectObj.getName());
		model.addAttribute("year", year);
		model.addAttribute("form", form);
		model.addAttribute("term", term);
		model.addAttribute("stream", streamObj.getStream());
		model.addAttribute("exam", exam);
		model.addAttribute("student", student);
		model.addAttribute("school", school);
		model.addAttribute("activeUser", activeUser);

		return "marksEntry";
	}

	@PostMapping("/schools/{code}/classList")
	public String classListByYearFormAndStream(Model model, Principal principal, @PathVariable int code,
			@RequestParam int year, @RequestParam int form, @RequestParam String stream) {

		User activeUser = userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(activeUser.getSchool().getCode()).get();
		Student student = new Student();
		User user = new User();
		List<User> schoolUsers = userService.getUsersBySchoolCode(school.getCode());

		List<Year> years = yearService.getAllYearsInSchool(school.getCode());
		List<Stream> streams = streamService.getStreamsInSchool(school.getCode());

		List<Student> students = studentService.getAllStudentsInSchoolByYearFormandStream(code, year, form, stream);

		model.addAttribute("form", form);
		model.addAttribute("stream", stream);
		model.addAttribute("year", year);
		model.addAttribute("students", students);
		model.addAttribute("streams", streams);
		model.addAttribute("years", years);
		model.addAttribute("schoolUsers", schoolUsers);
		model.addAttribute("user", user);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("school", school);

		return "classList";
	}

	@PostMapping("/schools/{code}/meritList")
	public String getMeritList(Model model, Principal principal, @PathVariable int code, @RequestParam int year,
			@RequestParam int form, @RequestParam int term, @RequestParam String subject, @RequestParam int stream,
			@RequestParam String exam) {

		User activeUser = userService.getByUsername(principal.getName()).get();
		Subject subjectObj = subjectService.getSubject(subject);
		School school = schoolService.getSchool(code).get();
		Student student = new Student();
		Stream streamObj = streamService.getStream(stream);

		List<Student> students = studentService.findAllStudentDoingSubject(code, year, form, term, subject);

		model.addAttribute("students", students);
		model.addAttribute("subject", subjectObj.getName());
		model.addAttribute("year", year);
		model.addAttribute("form", form);
		model.addAttribute("term", term);
		model.addAttribute("stream", streamObj.getStream());
		model.addAttribute("exam", exam);
		model.addAttribute("student", student);
		model.addAttribute("school", school);
		model.addAttribute("activeUser", activeUser);

		return "marksEntry";
	}
}