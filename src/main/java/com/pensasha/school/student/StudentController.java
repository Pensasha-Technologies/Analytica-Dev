package com.pensasha.school.student;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pensasha.school.exam.Mark;
import com.pensasha.school.exam.MarkService;
import com.pensasha.school.form.Form;
import com.pensasha.school.form.FormService;
import com.pensasha.school.school.School;
import com.pensasha.school.school.SchoolService;
import com.pensasha.school.stream.Stream;
import com.pensasha.school.stream.StreamService;
import com.pensasha.school.subject.Subject;
import com.pensasha.school.subject.SubjectService;
import com.pensasha.school.term.Term;
import com.pensasha.school.term.TermService;
import com.pensasha.school.user.User;
import com.pensasha.school.user.UserService;
import com.pensasha.school.year.Year;
import com.pensasha.school.year.YearService;

@Controller
public class StudentController {

	private StudentService studentService;
	private SchoolService schoolService;
	private UserService userService;
	private StreamService streamService;
	private YearService yearService;
	private SubjectService subjectService;
	private FormService formService;
	private TermService termService;
	private MarkService markService;

	public StudentController(StudentService studentService, SchoolService schoolService, UserService userService,
			StreamService streamService, YearService yearService, SubjectService subjectService,
			FormService formService, TermService termService, MarkService markService) {
		super();
		this.studentService = studentService;
		this.schoolService = schoolService;
		this.userService = userService;
		this.streamService = streamService;
		this.yearService = yearService;
		this.subjectService = subjectService;
		this.formService = formService;
		this.termService = termService;
		this.markService = markService;
	}

	@GetMapping("/schools/{code}/students")
	public String allStudents(@PathVariable int code, Model model, Principal principal) {

		List<Student> students = studentService.getAllStudentsInSchool(code);
		School school = schoolService.getSchool(code).get();
		User activeUser = userService.getByUsername(principal.getName()).get();
		List<Stream> streams = streamService.getStreamsInSchool(code);

		Student student = new Student();
		List<Subject> subjects = subjectService.getAllSubjectInSchool(code);
		List<Year> years = yearService.getAllYearsInSchool(code);

		model.addAttribute("years", years);
		model.addAttribute("subjects", subjects);
		model.addAttribute("streams", streams);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("school", school);
		model.addAttribute("students", students);

		return "students";
	}

	@GetMapping("/schools/{code}/addStudent")
	public String addStudent(Model model, Principal principal, @PathVariable int code) {

		School school = schoolService.getSchool(code).get();
		User activeUser = userService.getByUsername(principal.getName()).get();

		List<Stream> streams = streamService.getStreamsInSchool(code);

		if (!model.containsAttribute("student")) {
			model.addAttribute("student", new Student());
		}

		model.addAttribute("streams", streams);
		model.addAttribute("school", school);
		model.addAttribute("activeUser", activeUser);

		return "addStudent";

	}

	@GetMapping("/schools/{code}/editStudent/{admNo}")
	public String editStudent(Model model, Principal principal, @PathVariable int code, @PathVariable String admNo) {

		School school = schoolService.getSchool(code).get();
		User activeUser = userService.getByUsername(principal.getName()).get();

		List<Stream> streams = streamService.getStreamsInSchool(code);

		if (!model.containsAttribute("student")) {
			model.addAttribute("student", studentService.getStudentInSchool(admNo, code));
		}

		model.addAttribute("streams", streams);
		model.addAttribute("school", school);
		model.addAttribute("activeUser", activeUser);

		return "editStudent";
	}

	@PostMapping("/schools/{code}/editStudent/{admNo}")
	public String updateStudent(@PathVariable int code, @ModelAttribute @Valid Student student,
			@PathVariable String admNo, BindingResult bindingResult, RedirectAttributes redit, Principal principal,
			@RequestParam int stream) throws IOException {

		final String view;

		if (bindingResult.hasErrors()) {

			redit.addFlashAttribute("org.springframework.validation.BindingResult.student", bindingResult);
			redit.addFlashAttribute("student", student);

			view = "redirect:/schools/" + code + "/addStudent";

		} else {

			Student studentObj = studentService.getStudentInSchool(admNo, code);

			studentObj.setFirstname(student.getFirstname());
			studentObj.setSecondname(student.getSecondname());
			studentObj.setThirdname(student.getThirdname());
			studentObj.setUpiNo(student.getUpiNo());
			studentObj.setHudumaNo(student.getHudumaNo());
			studentObj.setBirthNo(student.getBirthNo());
			studentObj.setDob(student.getDob());
			studentObj.setF_firstname(student.getF_firstname());
			studentObj.setF_secondname(student.getF_secondname());
			studentObj.setF_thirdname(student.getF_thirdname());
			studentObj.setF_phoneNumber(student.getF_phoneNumber());
			studentObj.setF_email(student.getF_email());
			studentObj.setM_firstname(student.getM_firstname());
			studentObj.setM_secondname(student.getM_secondname());
			studentObj.setM_thirdname(student.getM_thirdname());
			studentObj.setM_phoneNumber(student.getM_phoneNumber());
			studentObj.setM_email(student.getM_email());
			studentObj.setG_firstname(student.getG_firstname());
			studentObj.setG_secondname(student.getG_secondname());
			studentObj.setG_thirdname(student.getG_thirdname());
			studentObj.setG_phoneNumber(student.getG_phoneNumber());
			studentObj.setG_email(student.getG_email());
			studentObj.setStream(streamService.getStream(stream));
			studentObj.setGender(student.getGender());
			studentObj.setSponsor(student.getSponsor());
			studentObj.setKcpeMarks(student.getKcpeMarks());
			studentObj.setScholar(student.getScholar());
			studentObj.setYearAdmitted(student.getYearAdmitted());
			studentObj.setCurrentForm(student.getCurrentForm());

			String[] admString = student.getAdmNo().split("_");

			redit.addFlashAttribute("success", "Student with admision number: " + admString[0] + " saved successfully");

			view = "redirect:/schools/" + code + "/students";
		}

		return view;
	}

	@PostMapping("/schools/{code}/students")
	public String addStudent(@RequestParam("file") MultipartFile file, @PathVariable int code,
			@ModelAttribute @Valid Student student, BindingResult bindingResult, RedirectAttributes redit,
			Principal principal, @RequestParam int stream) throws IOException {

		final String view;

		if (bindingResult.hasErrors()) {

			redit.addFlashAttribute("org.springframework.validation.BindingResult.student", bindingResult);
			redit.addFlashAttribute("student", student);

			view = "redirect:/schools/" + code + "/addStudent";

		} else {
			List<School> schools = new ArrayList<>();
			School school = schoolService.getSchool(code).get();
			schools.add(school);

			String[] admString = student.getAdmNo().split("_");

			if (studentService.ifStudentExistsInSchool(code + "_" + student.getAdmNo(), code) == true) {

				redit.addFlashAttribute("fail", "Student with admision number: " + admString[0] + " already exists");

			} else {

				if (student.getAdmNo() == "" || Integer.parseInt(student.getAdmNo()) < 1) {

					redit.addFlashAttribute("fail", "Admission number cannot be less than 1");

				} else {

					student.setSchool(schoolService.getSchool(code).get());

					final String path = new File("src/main/resources/static/studImg").getAbsolutePath();
					final String fileName = school.getCode() + "_" + student.getAdmNo();

					OutputStream out = null;
					InputStream filecontent = null;

					try {
						out = new FileOutputStream(new File(path + File.separator + fileName));
						filecontent = file.getInputStream();

						int read = 0;
						final byte[] bytes = new byte[1024];

						while ((read = filecontent.read(bytes)) != -1) {
							out.write(bytes, 0, read);
						}

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						if (out != null) {
							out.close();
						}
						if (filecontent != null) {
							filecontent.close();
						}
					}

					student.setPhoto(student.getAdmNo());
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

					if (student.getCurrentForm() == 1 || student.getCurrentForm() == 2) {

						Collection<Subject> subjects = school.getCompSubjectF1F2();
						student.setSubjects(subjects);

					} else if (student.getCurrentForm() == 3 || student.getCurrentForm() == 4) {
						
						Collection<Subject> subjects = school.getCompSubjectF3F4();
						student.setSubjects(subjects);
					}

					studentService.addStudent(student);

					redit.addFlashAttribute("success",
							"Student with admision number: " + admString[0] + " saved successfully");

				}

			}

			view = "redirect:/schools/" + code + "/students";

		}

		return view;

	}

	@GetMapping("/schools/{code}/students/{admNo}")
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
		List<Year> years = yearService.getAllYearsInSchool(code);

		model.addAttribute("years", years);
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

	@GetMapping("/schools/{code}/student/{admNo}")
	public String getStudent(@PathVariable int code, @PathVariable String admNo, Model model, Principal principal) {

		Student student = studentService.getStudentInSchool(admNo, code);
		School school = schoolService.getSchool(code).get();
		List<Subject> subjects = subjectService.getSubjectDoneByStudent(admNo);
		List<Subject> schoolSubjects = subjectService.getAllSubjectInSchool(code);

		for (int i = 0; i < subjects.size(); i++) {
			schoolSubjects.remove(subjects.get(i));
		}

		List<Form> forms = formService.studentForms(admNo);
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
		List<Stream> streams = streamService.getStreamsInSchool(code);
		List<Year> years = yearService.getAllYearsInSchool(code);

		model.addAttribute("streams", streams);
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
}
