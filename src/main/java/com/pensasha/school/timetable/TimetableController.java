package com.pensasha.school.timetable;

import com.pensasha.school.exam.MeritList;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

@Controller
public class TimetableController {
    private final SchoolService schoolService;
    private final YearService yearService;
    private final FormService formService;
    private final TermService termService;
    private final StreamService streamService;
    private final UserService userService;
    private final SubjectService subjectService;
    private final TimetableService timetableService;

    public TimetableController(SchoolService schoolService, YearService yearService, FormService formService, TermService termService, StreamService streamService, UserService userService, SubjectService subjectService, TimetableService timetableService) {
        this.schoolService = schoolService;
        this.yearService = yearService;
        this.formService = formService;
        this.termService = termService;
        this.streamService = streamService;
        this.userService = userService;
        this.subjectService = subjectService;
        this.timetableService = timetableService;
    }

    @PostMapping(value={"/schools/{code}/timetable"})
    public String getSchoolTimetable(@PathVariable int code, Model model, Principal principal, @RequestParam int year, @RequestParam int form, @RequestParam int stream, @RequestParam int term) {
        List<Timetable> finalTimetables;
        int i;
        School school = this.schoolService.getSchool(code).get();
        Year yearObj = this.yearService.getYearFromSchool(year, code).get();
        Form formObj = this.formService.getFormByForm(form);
        Term termObj = this.termService.getTerm(term, form, year, code);
        Stream streamObj = this.streamService.getStream(stream);
        Student student = new Student();
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        List<Subject> subjects = this.subjectService.getAllSubjectInSchool(code);
        Random rand = new Random();
        ArrayList<String> days = new ArrayList<String>();
        days.add("Mon");
        days.add("Tue");
        days.add("Wed");
        days.add("Thu");
        days.add("Fri");
        Timetable timetable = new Timetable();
        List<Timetable> timetables = this.timetableService.getTimetableBySchoolYearFormStream(code, year, form, term, stream);
        String[] breaks = new String[]{"B", "R", "E", "A", "K"};
        String[] lunch = new String[]{"L", "U", "N", "C", "H"};
        if (timetables.isEmpty()) {
            for (i = 0; i < days.size(); ++i) {
                timetable = new Timetable(days.get(i), subjects.get(rand.nextInt(subjects.size())).getInitials(), subjects.get(rand.nextInt(subjects.size())).getInitials(), subjects.get(rand.nextInt(subjects.size())).getInitials(), subjects.get(rand.nextInt(subjects.size())).getInitials(), subjects.get(rand.nextInt(subjects.size())).getInitials(), subjects.get(rand.nextInt(subjects.size())).getInitials(), subjects.get(rand.nextInt(subjects.size())).getInitials(), subjects.get(rand.nextInt(subjects.size())).getInitials(), subjects.get(rand.nextInt(subjects.size())).getInitials(), subjects.get(rand.nextInt(subjects.size())).getInitials(), subjects.get(rand.nextInt(subjects.size())).getInitials(), subjects.get(rand.nextInt(subjects.size())).getInitials(), subjects.get(rand.nextInt(subjects.size())).getInitials(), school, yearObj, formObj, termObj, streamObj);
                timetables.add(timetable);
            }
        }
        for (i = 0; i < 5; ++i) {
            for (int j = 0; j < 10; ++j) {
                if (j == 2) {
                    timetables.get(i).setTime3(breaks[i]);
                    continue;
                }
                if (j == 4) {
                    timetables.get(i).setTime6(breaks[i]);
                    continue;
                }
                if (j != 6) continue;
                timetables.get(i).setTime9(lunch[i]);
            }
        }
        if (this.formService.getFormByForm(form) != null) {
            for (i = 0; i < timetables.size(); ++i) {
                this.timetableService.saveTimetableItem(timetables.get(i));
            }
        }
        if ((finalTimetables = this.timetableService.getTimetableBySchoolYearFormStream(code, year, form, term, stream)) == null) {
            finalTimetables = new ArrayList<Timetable>();
        }
        List<Stream> streams = this.streamService.getStreamsInSchool(school.getCode());
        List<Year> years = this.yearService.getAllYearsInSchool(school.getCode());
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

    @PostMapping(value={"schools/{code}/years/{year}/forms/{form}/terms/{term}/streams/{stream}/timetable"})
    public String addTimetableElements(@PathVariable int code, @PathVariable int year, @PathVariable int form, @PathVariable int term, @PathVariable int stream, HttpServletRequest request, Model model, Principal principal) {
        Student student = new Student();
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(code).get();
        Stream streamObj = this.streamService.getStream(stream);
        List<Timetable> savedTimetable = this.timetableService.getTimetableBySchoolYearFormStream(code, year, form, term, stream);
        for (int i = 0; i < 5; ++i) {
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
            this.timetableService.saveTimetableItem(savedTimetable.get(i));
        }
        List<Timetable> finalTimetables = this.timetableService.getTimetableBySchoolYearFormStream(code, year, form, term, stream);
        if (finalTimetables == null) {
            finalTimetables = new ArrayList<Timetable>();
        }
        List<Stream> streams = this.streamService.getStreamsInSchool(school.getCode());
        List<Year> years = this.yearService.getAllYearsInSchool(school.getCode());
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

    @GetMapping(value={"/schools/{code}/generateTimetable"})
    public String generateTimetable(@PathVariable int code, Model model, Principal principal) {
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(code).get();
        Student student = new Student();
        model.addAttribute("student", student);
        model.addAttribute("school", school);
        model.addAttribute("activeUser", activeUser);
        return "generateTimetable";
    }
}

class SortByTotal implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getTotal() - b.getTotal();
	}
}
