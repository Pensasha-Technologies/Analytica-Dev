package com.pensasha.school.exam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
	
	public MarkController(StudentService studentService, MarkService markService, FormService formService,
			YearService yearService, TermService termService, SubjectService subjectService, UserService userService,
			StreamService streamService, SchoolService schoolService) {
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
	}

	@PostMapping("schools/{code}/marks/{exam}")
	public String addMarksToStudentSubjects(@PathVariable int code, @PathVariable String exam,
			HttpServletRequest request, Model model, Principal principal) {

		int form = Integer.parseInt(request.getParameter("form"));
		int year = Integer.parseInt(request.getParameter("year"));
		int term = Integer.parseInt(request.getParameter("term"));
		int stream = Integer.parseInt(request.getParameter("stream"));
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

			if (markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term, subject) != null) {

				mark = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term, subject);

			} else {
				mark = new Mark(students.get(i), yearObj, formObj, termObj);
				mark.setSubject(subjectObj);
			}

			switch (exam) {
			case "Cat1":
				if (markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
						subject) == null) {

					mark.setCat1(Integer.parseInt(request.getParameter(students.get(i).getAdmNo() + "mark")));
				} else if (mark.getCat1() == 0) {
					mark.setCat1(Integer.parseInt(request.getParameter(students.get(i).getAdmNo() + "mark")));
				}
				break;
			case "Cat2":
				if (markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
						subject) == null) {

					mark.setCat2(Integer.parseInt(request.getParameter(students.get(i).getAdmNo() + "mark")));
				} else if (markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term, subject)
						.getCat2() == 0) {
					mark.setCat2(Integer.parseInt(request.getParameter(students.get(i).getAdmNo() + "mark")));
				}
				break;
			case "mainExam":
				if (markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
						subject) == null) {

					mark.setMainExam(Integer.parseInt(request.getParameter(students.get(i).getAdmNo() + "mark")));
				} else if (markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term, subject)
						.getMainExam() == 0) {
					mark.setMainExam(Integer.parseInt(request.getParameter(students.get(i).getAdmNo() + "mark")));
				}
				break;
			default:
				break;
			}

			marks.add(markService.addMarksToSubject(mark));
		}

		model.addAttribute("success", "Marks saved successfully");

		if (students.size() == 0) {
			model.addAttribute("fail", "No student. Cannot add marks");
		}

		User activeUser = userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(code).get();
		Student student = new Student();
		Stream streamObj = streamService.getStream(stream);
		List<Stream> streams = streamService.getStreamsInSchool(school.getCode());

		List<Subject> subjects = subjectService.getAllSubjectInSchool(code);
		List<Year> years = yearService.getAllYearsInSchool(school.getCode());

		model.addAttribute("marks", marks);
		model.addAttribute("subjects", subjects);
		model.addAttribute("students", students);
		model.addAttribute("subject", subjectObj);
		model.addAttribute("year", year);
		model.addAttribute("form", form);
		model.addAttribute("streams", streams);
		model.addAttribute("years", years);
		model.addAttribute("term", term);
		model.addAttribute("stream", streamObj);
		model.addAttribute("exam", exam);
		model.addAttribute("student", student);
		model.addAttribute("school", school);
		model.addAttribute("activeUser", activeUser);

		return "marksEntry";

	}

}
