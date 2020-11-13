package com.pensasha.school.subject;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pensasha.school.form.Form;
import com.pensasha.school.form.FormService;
import com.pensasha.school.school.School;
import com.pensasha.school.school.SchoolService;
import com.pensasha.school.student.Student;
import com.pensasha.school.student.StudentService;
import com.pensasha.school.user.Teacher;
import com.pensasha.school.user.UserService;
import com.pensasha.school.year.Year;
import com.pensasha.school.year.YearService;

@Controller
public class SubjectController {

	private SchoolService schoolService;
	private SubjectService subjectService;
	private StudentService studentService;
	private FormService formService;
	private YearService yearService;
	private UserService userService;
	
	public SubjectController(SchoolService schoolService, SubjectService subjectService, StudentService studentService,
			FormService formService, YearService yearService, UserService userService) {
		super();
		this.schoolService = schoolService;
		this.subjectService = subjectService;
		this.studentService = studentService;
		this.formService = formService;
		this.yearService = yearService;
		this.userService = userService;
	}

	@PostMapping("teachers/{username}/subjects")
	public String addSubjectToTeacher(Model model, Principal principal, @PathVariable String username, HttpServletRequest request) {
		
		Teacher teacher = userService.gettingTeacherByUsername(username);
		
		List<Subject> allSubjects = subjectService.getAllSubjectInSchool(teacher.getSchool().getCode());
		List<Subject> subjects = teacher.getSubjects();
		
		for (int i = 0; i < allSubjects.size(); i++) {
			if (request.getParameter(allSubjects.get(i).getInitials()) != null) {
				subjects.add(subjectService.getSubject(allSubjects.get(i).getInitials()));
			}
		}
		
		teacher.setSubjects(subjects);
		userService.addUser(teacher);
		
		return "redirect:/profile/{username}";
	}
	
	@GetMapping("/teachers/{username}/subjects/{initials}")
	public String deleteSubjectFromTeacher(RedirectAttributes redit, Principal principal, @PathVariable String username, @PathVariable String initials) {

		Teacher teacher = userService.gettingTeacherByUsername(username);
		teacher.getSubjects().remove(subjectService.getSubject(initials));
		
		userService.addUser(teacher);
		
		return "redirect:/profile/{username}";
	}

	@PostMapping("/school/{code}/subjects")
	public String addSubjects(Model model, @PathVariable int code, HttpServletRequest request, Principal principal) {

		List<Subject> subjects = new ArrayList<>();
		List<Subject> allSubjects = subjectService.getAllSubjects();

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
		for (int i = 0; i < subjects1.size(); i++) {
			allSubjects.remove(subjects1.get(i));
		}

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
	

		return "redirect:/school/" + code;
	}
	
	@GetMapping("/schools/{code}/subjects/{initials}")
	public String deleteSubjectFromSchool(@PathVariable int code, @PathVariable String initials, Model model,
			Principal principal) {
		
		if (subjectService.doesSubjectExistsInSchool(initials, code) == true) {

			Subject subject = subjectService.getSubjectInSchool(initials, code);
			subject.getSchools().remove(schoolService.getSchool(code).get());
			subjectService.addSubject(subject);

			model.addAttribute("success", initials + " subject successfully deleted");
		} else {

			model.addAttribute("fail", initials + " subject does not exist");
		}

		List<Subject> subjects1 = subjectService.getAllSubjectInSchool(code);
		
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

		List<Subject> allSubjects = subjectService.getAllSubjects();
		for (int i = 0; i < subjects1.size(); i++) {
			allSubjects.remove(subjects1.get(i));
		}

		return "redirect:/school/" + code;
	}
	
	@GetMapping("/schools/{code}/students/{admNo}/subjects/{initials}")
	public String deleteSubjectFromStudent(@PathVariable int code, @PathVariable String admNo,
			@PathVariable String initials, Model model, Principal principal) {

		Student student = studentService.getStudentInSchool(admNo, code);

		Subject subject = subjectService.getSubjectByStudent(initials, admNo);
		student.getSubjects().remove(subject);
		studentService.addStudent(student);

		List<Subject> subjects = subjectService.getSubjectDoneByStudent(admNo);
		List<Subject> schoolSubjects = subjectService.getAllSubjectInSchool(code);

		for (int i = 0; i < subjects.size(); i++) {
			schoolSubjects.remove(subjects.get(i));
		}

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

		return "/schools/" + code + "/student/" + admNo ;
	}

	@PostMapping("/schools/{code}/student/{admNo}/subjects")
	public String addSubjectToStudent(Model model, @PathVariable int code, @PathVariable String admNo,
			HttpServletRequest request, @RequestParam int form, @RequestParam int year, Principal principal) {

		Student student = studentService.getStudentInSchool(admNo, code);
		List<Subject> schoolSubjects = subjectService.getAllSubjectInSchool(code);
		List<Subject> studentSubjects = subjectService.getSubjectDoneByStudent(admNo);
	
		List<String> paramList = new ArrayList<>();

		for (int i = 0; i < schoolSubjects.size(); i++) {
			paramList.add(request.getParameter(schoolSubjects.get(i).getInitials()));

			if (paramList.get(i) != null) {
				if (!studentSubjects.contains(subjectService.getSubject(paramList.get(i)))) {
					studentSubjects.add(subjectService.getSubject(paramList.get(i)));
				}
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

		for (int i = 0; i < studentSubjects.size(); i++) {
			schoolSubjects.remove(studentSubjects.get(i));
		}

		List<Subject> subjects = subjectService.getSubjectDoneByStudent(admNo);
		
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

		return "redirect:/schools/" + code + "/student/" + admNo;
	}
}
