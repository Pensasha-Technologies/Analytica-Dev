package com.pensasha.school.timetable;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pensasha.school.form.Form;
import com.pensasha.school.form.FormService;
import com.pensasha.school.school.School;
import com.pensasha.school.school.SchoolService;
import com.pensasha.school.stream.Stream;
import com.pensasha.school.stream.StreamService;
import com.pensasha.school.student.Student;
import com.pensasha.school.subject.Subject;
import com.pensasha.school.subject.SubjectService;
import com.pensasha.school.term.Term;
import com.pensasha.school.term.TermService;
import com.pensasha.school.user.User;
import com.pensasha.school.user.UserService;
import com.pensasha.school.year.Year;
import com.pensasha.school.year.YearService;

@Controller
public class TimetableController {

	private SchoolService schoolService;
	private YearService yearService;
	private FormService formService;
	private TermService termService;
	private StreamService streamService;
	private UserService userService;
	private SubjectService subjectService;
	private TimetableService timetableService;
	
	public TimetableController(SchoolService schoolService, YearService yearService, FormService formService,
			TermService termService, StreamService streamService, UserService userService,
			SubjectService subjectService, TimetableService timetableService) {
		super();
		this.schoolService = schoolService;
		this.yearService = yearService;
		this.formService = formService;
		this.termService = termService;
		this.streamService = streamService;
		this.userService = userService;
		this.subjectService = subjectService;
		this.timetableService = timetableService;
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
	
}