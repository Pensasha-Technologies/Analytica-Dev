package com.pensasha.school.stream;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pensasha.school.school.School;
import com.pensasha.school.school.SchoolService;
import com.pensasha.school.student.Student;
import com.pensasha.school.student.StudentFormYear;
import com.pensasha.school.student.StudentFormYearService;
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
    private StudentFormYearService studentFormYearService;
    
    public StreamController(SchoolService schoolService, StreamService streamService, SubjectService subjectService,
			UserService userService, StudentService studentService, YearService yearService,
			StudentFormYearService studentFormYearService) {
		super();
		this.schoolService = schoolService;
		this.streamService = streamService;
		this.subjectService = subjectService;
		this.userService = userService;
		this.studentService = studentService;
		this.yearService = yearService;
		this.studentFormYearService = studentFormYearService;
	}

	@PostMapping(value={"/school/{code}/streams"})
    public String addStreamSchool(Model model, @PathVariable int code, @ModelAttribute Stream stream, Principal principal) {
        if (this.schoolService.doesSchoolExists(code).booleanValue()) {
            School school = this.schoolService.getSchool(code).get();
            stream.setSchool(school);
            this.streamService.addStreamSchool(stream);
            List<Subject> subjects = this.subjectService.getAllSubjectInSchool(code);
            ArrayList<Subject> group1 = new ArrayList<Subject>();
            ArrayList<Subject> group2 = new ArrayList<Subject>();
            ArrayList<Subject> group3 = new ArrayList<Subject>();
            ArrayList<Subject> group4 = new ArrayList<Subject>();
            ArrayList<Subject> group5 = new ArrayList<Subject>();
            for (int i = 0; i < subjects.size(); ++i) {
                if (subjects.get(i).getName().contains("Mathematics") || subjects.get(i).getName().contains("English") || subjects.get(i).getName().contains("Kiswahili")) {
                    group1.add(subjects.get(i));
                    continue;
                }
                if (subjects.get(i).getName().contains("Biology") || subjects.get(i).getName().contains("Physics") || subjects.get(i).getName().contains("Chemistry")) {
                    group2.add(subjects.get(i));
                    continue;
                }
                if (subjects.get(i).getInitials().contains("Hist") || subjects.get(i).getInitials().contains("Geo") || subjects.get(i).getInitials().contains("C.R.E") || subjects.get(i).getInitials().contains("I.R.E") || subjects.get(i).getInitials().contains("H.R.E")) {
                    group3.add(subjects.get(i));
                    continue;
                }
                if (subjects.get(i).getInitials().contains("Hsci") || subjects.get(i).getInitials().contains("AnD") || subjects.get(i).getInitials().contains("Agric") || subjects.get(i).getInitials().contains("Comp") || subjects.get(i).getInitials().contains("Avi") || subjects.get(i).getInitials().contains("Elec") || subjects.get(i).getInitials().contains("Pwr") || subjects.get(i).getInitials().contains("Wood") || subjects.get(i).getInitials().contains("Metal") || subjects.get(i).getInitials().contains("Bc") || subjects.get(i).getInitials().contains("Dnd")) {
                    group4.add(subjects.get(i));
                    continue;
                }
                group5.add(subjects.get(i));
            }
            List<Subject> allSubjects = this.subjectService.getAllSubjects();
            for (int i = 0; i < subjects.size(); ++i) {
                allSubjects.remove(subjects.get(i));
            }
            return "redirect:/school/" + code;
        }
        return "redirect:/adminHome";
    }

    @GetMapping(value={"/school/{code}/streams/{id}"})
    public String deleteStream(Model model, @PathVariable int code, @PathVariable int id, @ModelAttribute Stream stream, Principal principal) {
        if (this.streamService.doesStreamExistInSchool(id, code)) {
            this.streamService.deleteStream(id);
            model.addAttribute("success", (Object)"Stream successfully deleted");
        } else {
            model.addAttribute("fail", (Object)"Stream does not exist");
        }
        List<Subject> subjects = this.subjectService.getAllSubjectInSchool(code);
        ArrayList<Subject> group1 = new ArrayList<Subject>();
        ArrayList<Subject> group2 = new ArrayList<Subject>();
        ArrayList<Subject> group3 = new ArrayList<Subject>();
        ArrayList<Subject> group4 = new ArrayList<Subject>();
        ArrayList<Subject> group5 = new ArrayList<Subject>();
        for (int i = 0; i < subjects.size(); ++i) {
            if (subjects.get(i).getName().contains("Mathematics") || subjects.get(i).getName().contains("English") || subjects.get(i).getName().contains("Kiswahili")) {
                group1.add(subjects.get(i));
                continue;
            }
            if (subjects.get(i).getName().contains("Biology") || subjects.get(i).getName().contains("Physics") || subjects.get(i).getName().contains("Chemistry")) {
                group2.add(subjects.get(i));
                continue;
            }
            if (subjects.get(i).getInitials().contains("Hist") || subjects.get(i).getInitials().contains("Geo") || subjects.get(i).getInitials().contains("C.R.E") || subjects.get(i).getInitials().contains("I.R.E") || subjects.get(i).getInitials().contains("H.R.E")) {
                group3.add(subjects.get(i));
                continue;
            }
            if (subjects.get(i).getInitials().contains("Hsci") || subjects.get(i).getInitials().contains("AnD") || subjects.get(i).getInitials().contains("Agric") || subjects.get(i).getInitials().contains("Comp") || subjects.get(i).getInitials().contains("Avi") || subjects.get(i).getInitials().contains("Elec") || subjects.get(i).getInitials().contains("Pwr") || subjects.get(i).getInitials().contains("Wood") || subjects.get(i).getInitials().contains("Metal") || subjects.get(i).getInitials().contains("Bc") || subjects.get(i).getInitials().contains("Dnd")) {
                group4.add(subjects.get(i));
                continue;
            }
            group5.add(subjects.get(i));
        }
        List<Subject> allSubjects = this.subjectService.getAllSubjects();
        for (int i = 0; i < subjects.size(); ++i) {
            allSubjects.remove(subjects.get(i));
        }
        return "redirect:/school/" + code;
    }

    @PostMapping(value={"/schools/{code}/classList"})
    public String classListByYearFormAndStream(@PathVariable int code, @RequestParam int year, @RequestParam int form, @RequestParam String stream) {
        return "redirect:/schools/" + code + "/years/" + year + "/forms/" + form + "/streams/" + stream + "/classList";
    }

    @GetMapping(value={"/schools/{code}/years/{year}/forms/{form}/streams/{stream}/classList"})
    public String getClassList(@PathVariable int code, @PathVariable int year, @PathVariable int form, @PathVariable String stream, Model model, Principal principal) {
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(code).get();
        Student student = new Student();
        User user = new User();
        List<SchoolUser> schoolUsers = this.userService.getUsersBySchoolCode(school.getCode());
        List<Year> years = this.yearService.getAllYearsInSchool(school.getCode());
        List<Stream> streams = this.streamService.getStreamsInSchool(school.getCode());
        Set<Student> orderedStudents = new HashSet<>();
        List<Student> students = new ArrayList<>();
        List<StudentFormYear> studentsFormYear = this.studentFormYearService.getAllStudentFormYearbyFormYearandStream(code, year, form, stream);
        for(StudentFormYear studentFormYear : studentsFormYear) {
        	orderedStudents.add(studentFormYear.getStudent());
        }
        students.addAll(orderedStudents);
        List<Subject> subjects = this.subjectService.getAllSubjectInSchool(code);
        model.addAttribute("subjects", subjects);
        model.addAttribute("form", (Object)form);
        model.addAttribute("stream", (Object)stream);
        model.addAttribute("year", (Object)year);
        model.addAttribute("students", students);
        model.addAttribute("streams", streams);
        model.addAttribute("years", years);
        model.addAttribute("schoolUsers", schoolUsers);
        model.addAttribute("user", (Object)user);
        model.addAttribute("activeUser", (Object)activeUser);
        model.addAttribute("student", (Object)student);
        model.addAttribute("school", (Object)school);
        return "classList";
    }
}