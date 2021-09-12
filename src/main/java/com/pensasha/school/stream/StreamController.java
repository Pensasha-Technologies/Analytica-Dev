package com.pensasha.school.stream;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import com.pensasha.school.user.Teacher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.pensasha.school.school.School;
import com.pensasha.school.school.SchoolService;
import com.pensasha.school.student.Student;
import com.pensasha.school.student.StudentService;
import com.pensasha.school.subject.Subject;
import com.pensasha.school.subject.SubjectService;
import com.pensasha.school.user.SchoolUser;
import com.pensasha.school.user.User;
import com.pensasha.school.user.UserService;
import com.pensasha.school.year.Year;
import com.pensasha.school.year.YearService;

@Controller
public class StreamController {

	private SchoolService schoolService;
	private StreamService streamService;
	private SubjectService subjectService;
	private UserService userService;
	private StudentService studentService;
	private YearService yearService;

	public StreamController(SchoolService schoolService, StreamService streamService, SubjectService subjectService,
			UserService userService, StudentService studentService, YearService yearService) {
		super();
		this.schoolService = schoolService;
		this.streamService = streamService;
		this.subjectService = subjectService;
		this.userService = userService;
		this.studentService = studentService;
		this.yearService = yearService;
	}

	// Adding stream to school
	@PostMapping("/school/{code}/streams")
	public String addStreamSchool(Model model, @PathVariable int code, @ModelAttribute Stream stream,
			Principal principal) {

		if (schoolService.doesSchoolExists(code) == true) {

			School school = schoolService.getSchool(code).get();

			stream.setSchool(school);
			streamService.addStreamSchool(stream);

			List<Subject> subjects = subjectService.getAllSubjectInSchool(code);

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
			for (int i = 0; i < subjects.size(); i++) {
				allSubjects.remove(subjects.get(i));
			}

			return "redirect:/school/" + code;

		} else {

			return "redirect:/adminHome";
		}
	}

	@GetMapping("/school/{code}/streams/{id}")
	public String deleteStream(Model model, @PathVariable int code, @PathVariable int id, @ModelAttribute Stream stream,
									  Principal principal) 	{


		if (streamService.doesStreamExistInSchool(id, code) == true) {

			Stream stream1 = streamService.getStream(id);
			List<Stream> streams = new ArrayList<>();
			List<Teacher> teachers = userService.getTeachersInSchoolAndStream(code, id);

			for(int i=0;i<teachers.size(); i++){

				for(int j=0;j<teachers.get(i).getStreams().size();j++){
					streams.add(teachers.get(i).getStreams().get(j));
					streams.remove(stream1);
					teachers.get(i).setStreams(streams);
					userService.addTeacher(code, teachers.get(i));
				}

			}

			streamService.deleteStream(id);

			model.addAttribute("success", "Stream successfully deleted");
		} else {
			model.addAttribute("fail", "Stream does not exist");
		}

		List<Subject> subjects = subjectService.getAllSubjectInSchool(code);

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
		for (int i = 0; i < subjects.size(); i++) {
			allSubjects.remove(subjects.get(i));
		}

		return "redirect:/school/" + code;


	}

	@PostMapping("/schools/{code}/classList")
	public String classListByYearFormAndStream(Model model, Principal principal, @PathVariable int code,
			@RequestParam int year, @RequestParam int form, @RequestParam String stream) {

		User activeUser = userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(code).get();
		Student student = new Student();
		User user = new User();
		List<SchoolUser> schoolUsers = userService.getUsersBySchoolCode(school.getCode());

		List<Year> years = yearService.getAllYearsInSchool(school.getCode());
		List<Stream> streams = streamService.getStreamsInSchool(school.getCode());

		List<Student> students = studentService.getAllStudentsInSchoolByYearFormandStream(code, year, form, stream);
		List<Subject> subjects = subjectService.getAllSubjectInSchool(code);

		model.addAttribute("subjects", subjects);
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

}
