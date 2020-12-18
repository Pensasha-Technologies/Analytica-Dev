package com.pensasha.school.stream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
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
	private final TemplateEngine templateEngine;

	public StreamController(SchoolService schoolService, StreamService streamService, SubjectService subjectService,
			UserService userService, StudentService studentService, YearService yearService,
			TemplateEngine templateEngine, ServletContext servletContext) {
		super();
		this.schoolService = schoolService;
		this.streamService = streamService;
		this.subjectService = subjectService;
		this.userService = userService;
		this.studentService = studentService;
		this.yearService = yearService;
		this.templateEngine = templateEngine;
		this.servletContext = servletContext;
	}

	@Autowired
	ServletContext servletContext;

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
			Principal principal) {

		if (streamService.doesStreamExistInSchool(id, code) == true) {

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
		converterProperties.setBaseUri("http://analytica-env.eba-iigws4mq.us-east-2.elasticbeanstalk.com/");

		/* Call convert method */
		HtmlConverter.convertToPdf(classListHtml, target, converterProperties);

		/* extract output as bytes */
		byte[] bytes = target.toByteArray();

		/* Send the response as downloadable PDF */

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(bytes);

	}

}
