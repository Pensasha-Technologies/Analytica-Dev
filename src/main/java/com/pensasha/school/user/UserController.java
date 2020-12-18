package com.pensasha.school.user;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.pensasha.school.role.Role;
import com.pensasha.school.role.RoleService;
import com.pensasha.school.school.School;
import com.pensasha.school.school.SchoolService;
import com.pensasha.school.sms.Gateway;
import com.pensasha.school.student.Student;
import com.pensasha.school.subject.Subject;
import com.pensasha.school.subject.SubjectService;

@Controller
public class UserController {

	private UserService userService;
	private SchoolService schoolService;
	private RoleService roleService;
	private SubjectService subjectService;

	public UserController(UserService userService, SchoolService schoolService, RoleService roleService,
			SubjectService subjectService) {
		super();
		this.userService = userService;
		this.schoolService = schoolService;
		this.roleService = roleService;
		this.subjectService = subjectService;
	}

	// All users function
	public void AllUsers(Principal principal, Model model) {

		List<School> schools = schoolService.getAllSchools();
		User activeUser = userService.getByUsername(principal.getName()).get();

		Student student = new Student();
		School school = new School();

		model.addAttribute("activeUser", activeUser);
		model.addAttribute("schools", schools);
		model.addAttribute("school", school);
		model.addAttribute("student", student);
	}

	// Company Users
	@GetMapping("/users")
	public List<User> systemUsers(Model model, Principal principal) {

		List<User> systemUsers = new ArrayList<>();
		User user = userService.getByUsername(principal.getName()).get();
		List<User> users = userService.findAllUsers();

		AllUsers(principal, model);

		for (int i = 0; i < users.size(); i++) {
			if (users.get(i).getRole().getName().contains("ADMIN")
					|| users.get(i).getRole().getName().contains("FIELDOFFICER")
					|| users.get(i).getRole().getName().contains("OFFICEASSISTANT")
					|| users.get(i).getRole().getName().contains("C.E.O")) {
				systemUsers.add(users.get(i));
			}
		}

		model.addAttribute("user", user);
		model.addAttribute("users", systemUsers);

		return users;
	}

	// School Users
	@GetMapping("/schoolUsers")
	public String schoolUsers(Model model, Principal principal) {

		List<User> schoolUsers = new ArrayList<>();
		SchoolUser user = new SchoolUser();
		List<User> users = userService.findAllUsers();

		AllUsers(principal, model);

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

		model.addAttribute("user", user);
		model.addAttribute("users", schoolUsers);

		return "schoolUsers";
	}

	// Adding Users
	@PostMapping("/users")
	public RedirectView addSystemUsers(RedirectAttributes redit, @RequestParam String role, @ModelAttribute User user) {

		if (userService.userExists(user.getUsername()) == true) {

			redit.addFlashAttribute("fail", "User with username:" + user.getUsername() + " already exists");

		} else {

			Role roleObj = new Role();

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
			default:
				break;
			}

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

			redit.addFlashAttribute("success", user.getUsername() + " saved successfully");

			user.setRole(roleObj);
			roleService.addRole(roleObj);
			userService.addUser(user);

			redit.addFlashAttribute("success", user.getUsername() + " successfully added");

		}

		RedirectView redirectView = new RedirectView("/users", true);

		return redirectView;

	}

	// Adding school user
	@PostMapping("/schoolUsers")
	public RedirectView addSchoolUsers(RedirectAttributes redit, @RequestParam String role,
			@ModelAttribute SchoolUser user, @RequestParam int code, Principal principal, HttpServletRequest request) {
		
		
		Role roleObj = new Role();
		User activeUser = userService.getByUsername(principal.getName()).get();

		if (userService.userExists(user.getUsername()) == true) {

			redit.addFlashAttribute("fail", "A user with username " + user.getUsername() + " already exists");

		} else {

			user.setSchool(new School("", code));
			Teacher teacher = new Teacher();

			switch (role) {
			case "Principal":
				roleObj.setName("PRINCIPAL");
				break;
			case "deputyPrincipal":
				roleObj.setName("DEPUTYPRINCIPAL");
				break;
			case "d.o.s":
				roleObj.setName("D.O.S");
				break;
			case "bursar":
				roleObj.setName("BURSAR");
				break;
			case "accountsClerk":
				roleObj.setName("ACCOUNTSCLERK");
				break;
			case "teacher":
				
				teacher = new Teacher(user.getUsername(), user.getFirstname(), user.getSecondname(),
						user.getThirdname(), user.getPassword(), user.getEmail(), user.getPhoneNumber(),
						user.getAddress());
				teacher.setSchool(user.getSchool());
				teacher.setTeacherNumber(request.getParameter("teacherNumber"));
				teacher.setTscNumber(request.getParameter("tscNumber"));
				teacher.setInitials(user.getFirstname().charAt(0) + "." + user.getSecondname().charAt(0) + "."
						+ user.getThirdname().charAt(0));
				roleObj.setName("TEACHER");
				teacher.setRole(roleObj);
				break;
			default:
				break;
			}

			String baseUrl = "https://mysms.celcomafrica.com/api/services/sendsms/";
			int partnerId = 1989;
			String apiKey = "da383ff9c9edfb614bc7d1abfe8b1599";
			String shortCode = "analytica";

			Gateway gateway = new Gateway(baseUrl, partnerId, apiKey, shortCode);

			Random random = new Random();
			int otp = random.nextInt(9999);
			otp += 1;

			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

			user.setRole(roleObj);
			roleService.addRole(roleObj);
			if (user.getRole().getName() == "TEACHER") {
				teacher.setPassword(encoder.encode(Integer.toString(otp)));
				userService.addUser(teacher);
			} else {
				user.setPassword(encoder.encode(Integer.toString(otp)));
				userService.addUser(user);
			}

			try {
				String res = gateway.sendSingleSms("Your Username is: " + user.getUsername() + " password is:" + otp,
						Integer.toString(user.getPhoneNumber()));
				System.out.println(res);
			} catch (IOException e) {
				e.printStackTrace();
			}

			redit.addFlashAttribute("success", user.getUsername() + " saved successfully");
		}

		if (activeUser.getRole().getName().equals("PRINCIPAL")) {

			RedirectView redirectView = new RedirectView("/schools/principal", true);

			return redirectView;

		} else if (activeUser.getRole().getName().contains("DEPUTYPRINCIPAL")) {
			RedirectView redirectView = new RedirectView("/schools/deputyPrincipal", true);

			return redirectView;

		} else {

			RedirectView redirectView = new RedirectView("schoolUsers", true);

			return redirectView;
		}

	}

	// Users profile
	@GetMapping("profile/{username}")
	public String getSingleUser(@PathVariable String username, Model model, Principal principal) {

		User activeUser = userService.getByUsername(principal.getName()).get();

		School school = new School();

		if (userService.userExists(username) == true) {

			if (activeUser.getRole().getName().equals("PRINCIPAL")
					|| activeUser.getRole().getName().equals("DEPUTYPRINCIPAL")
					|| activeUser.getRole().getName().equals("D.O.S") || activeUser.getRole().getName().equals("BURSAR")
					|| activeUser.getRole().getName().equals("TEACHER")
					|| activeUser.getRole().getName().equals("ACCOUNTSCLERK")) {

				SchoolUser schoolUser = (SchoolUser) activeUser;

				school = schoolUser.getSchool();
			} else {

				school = new School();
			}
			User user = userService.getByUsername(username).get();

			if (user.getRole().getName().contains("TEACHER")) {
				Teacher teacher = userService.gettingTeacherByUsername(username);
				List<Subject> subjects = subjectService.getAllSubjectInSchool(teacher.getSchool().getCode());

				for (int i = 0; i < teacher.getSubjects().size(); i++) {
					if (subjects.contains(teacher.getSubjects().get(i))) {
						subjects.remove(teacher.getSubjects().get(i));
					}
				}

				model.addAttribute("subjects", subjects);
				model.addAttribute("teacher", teacher);

			}

			Student student = new Student();

			model.addAttribute("school", school);
			model.addAttribute("activeUser", activeUser);
			model.addAttribute("student", student);
			model.addAttribute("user", user);

			return "userHome";
		} else {

			Student student = new Student();
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

	// Delete System User
	@GetMapping("/user/{username}")
	public RedirectView deleteUser(@PathVariable String username, RedirectAttributes redit, Principal principal) {

		if (userService.userExists(username) == true) {

			userService.deleteUser(username);

			redit.addFlashAttribute("success", username + " successfully deleted");
		} else {

			redit.addFlashAttribute("fail", "A user with username:" + username + " does not exist");
		}

		RedirectView redirectView = new RedirectView("/users", true);

		return redirectView;

	}

	// Delete School User
	@GetMapping("/schoolUser/{username}")
	public RedirectView deleteSchoolUser1(@PathVariable String username, RedirectAttributes redit,
			Principal principal) {

		User activeUser = userService.getByUsername(principal.getName()).get();

		if (userService.userExists(username) == true) {

			userService.deleteUser(username);

			redit.addFlashAttribute("success", username + " successfully deleted");
		} else {

			redit.addFlashAttribute("fail", "A user with username:" + username + " does not exist");
		}

		if (activeUser.getRole().getName().contains("PRINCIPAL")) {
			RedirectView redirectView = new RedirectView("/schools/principal", true);

			return redirectView;
		} else {
			RedirectView redirectView = new RedirectView("/schoolUsers", true);

			return redirectView;
		}

	}
}
