package com.pensasha.school.subject;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pensasha.school.exam.MeritList;
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

    public SubjectController(SchoolService schoolService, SubjectService subjectService, StudentService studentService, FormService formService, YearService yearService, UserService userService) {
        this.schoolService = schoolService;
        this.subjectService = subjectService;
        this.studentService = studentService;
        this.formService = formService;
        this.yearService = yearService;
        this.userService = userService;
    }

    @PostMapping(value={"/schools/{code}/compF1F2subjects"})
    public String addingCompF1F2Subject(Model model, Principal principal, HttpServletRequest request, @PathVariable int code) {
        School school = this.schoolService.getSchool(code).get();
        List<Subject> allSubjects = this.subjectService.getAllSubjectInSchool(code);
        Collection<Subject> subjects = school.getCompSubjectF1F2();
        for (int i = 0; i < allSubjects.size(); ++i) {
            if (request.getParameter(allSubjects.get(i).getInitials()) == null) continue;
            subjects.add(allSubjects.get(i));
        }
        school.setCompSubjectF1F2(subjects);
        this.schoolService.addSchool(school);
        return "redirect:/school/" + code;
    }

    @GetMapping(value={"/schools/{code}/compF1F2subjects/{initials}"})
    public String deleteCompf1f2SubjectFromSchool(RedirectAttributes redit, Principal principal, @PathVariable int code, @PathVariable String initials) {
        School school = this.schoolService.getSchool(code).get();
        school.getCompSubjectF1F2().remove(this.subjectService.getSubject(initials));
        this.schoolService.addSchool(school);
        return "redirect:/school/" + code;
    }

    @GetMapping(value={"/schools/{code}/compF3F4subjects/{initials}"})
    public String deleteCompf3f4SubjectFromSchool(RedirectAttributes redit, Principal principal, @PathVariable int code, @PathVariable String initials) {
        School school = this.schoolService.getSchool(code).get();
        school.getCompSubjectF3F4().remove(this.subjectService.getSubject(initials));
        this.schoolService.addSchool(school);
        return "redirect:/school/" + code;
    }

    @PostMapping(value={"/schools/{code}/compF3F4subjects"})
    public String addingCompF3F4Subject(Model model, Principal principal, HttpServletRequest request, @PathVariable int code) {
        School school = this.schoolService.getSchool(code).get();
        List<Subject> allSubjects = this.subjectService.getAllSubjectInSchool(code);
        Collection<Subject> subjects = school.getCompSubjectF3F4();
        for (int i = 0; i < allSubjects.size(); ++i) {
            if (request.getParameter(allSubjects.get(i).getInitials()) == null) continue;
            subjects.add(allSubjects.get(i));
        }
        school.setCompSubjectF3F4(subjects);
        this.schoolService.addSchool(school);
        return "redirect:/school/" + code;
    }

    @PostMapping(value={"teachers/{username}/subjects"})
    public String addSubjectToTeacher(Model model, Principal principal, @PathVariable String username, HttpServletRequest request) {
        Teacher teacher = this.userService.gettingTeacherByUsername(username);
        List<Subject> allSubjects = this.subjectService.getAllSubjectInSchool(teacher.getSchool().getCode());
        List<Subject> subjects = teacher.getSubjects();
        for (int i = 0; i < allSubjects.size(); ++i) {
            if (request.getParameter(allSubjects.get(i).getInitials()) == null) continue;
            subjects.add(this.subjectService.getSubject(allSubjects.get(i).getInitials()));
        }
        teacher.setSubjects(subjects);
        this.userService.addUser(teacher);
        return "redirect:/profile/{username}";
    }

    @GetMapping(value={"/teachers/{username}/subjects/{initials}"})
    public String deleteSubjectFromTeacher(RedirectAttributes redit, Principal principal, @PathVariable String username, @PathVariable String initials) {
        Teacher teacher = this.userService.gettingTeacherByUsername(username);
        teacher.getSubjects().remove(this.subjectService.getSubject(initials));
        this.userService.addUser(teacher);
        return "redirect:/profile/{username}";
    }

    @PostMapping(value={"/school/{code}/subjects"})
    public String addSubjects(Model model, @PathVariable int code, HttpServletRequest request, Principal principal) {
        ArrayList<Subject> subjects = new ArrayList<Subject>();
        List<Subject> allSubjects = this.subjectService.getAllSubjects();
        for (int i = 0; i < allSubjects.size(); ++i) {
            if (request.getParameter(allSubjects.get(i).getInitials()) == null) continue;
            subjects.add(this.subjectService.getSubject(allSubjects.get(i).getInitials()));
        }
        School school = this.schoolService.getSchool(code).get();
        for (int i = 0; i < subjects.size(); ++i) {
            List<School> schools = this.schoolService.getAllSchoolsWithSubject(subjects.get(i).getInitials());
            for (int j = 0; j < schools.size(); ++j) {
                if (schools.get(j) != school) continue;
                schools.remove(j);
            }
            schools.add(school);
            subjects.get(i).setSchools(schools);
            this.subjectService.addSubject(subjects.get(i));
        }
        List<Subject> subjects1 = this.subjectService.getAllSubjectInSchool(code);
        for (int i = 0; i < subjects1.size(); ++i) {
            allSubjects.remove(subjects1.get(i));
        }
        ArrayList<Subject> group1 = new ArrayList<Subject>();
        ArrayList<Subject> group2 = new ArrayList<Subject>();
        ArrayList<Subject> group3 = new ArrayList<Subject>();
        ArrayList<Subject> group4 = new ArrayList<Subject>();
        ArrayList<Subject> group5 = new ArrayList<Subject>();
        for (int i = 0; i < subjects1.size(); ++i) {
            if (subjects1.get(i).getName().contains("Mathematics") || subjects1.get(i).getName().contains("English") || subjects1.get(i).getName().contains("Kiswahili")) {
                group1.add(subjects1.get(i));
                continue;
            }
            if (subjects1.get(i).getName().contains("Biology") || subjects1.get(i).getName().contains("Physics") || subjects1.get(i).getName().contains("Chemistry")) {
                group2.add(subjects1.get(i));
                continue;
            }
            if (subjects1.get(i).getInitials().contains("Hist") || subjects1.get(i).getInitials().contains("Geo") || subjects1.get(i).getInitials().contains("C.R.E") || subjects1.get(i).getInitials().contains("I.R.E") || subjects1.get(i).getInitials().contains("H.R.E")) {
                group3.add(subjects1.get(i));
                continue;
            }
            if (subjects1.get(i).getInitials().contains("Hsci") || subjects1.get(i).getInitials().contains("AnD") || subjects1.get(i).getInitials().contains("Agric") || subjects1.get(i).getInitials().contains("Comp") || subjects1.get(i).getInitials().contains("Avi") || subjects1.get(i).getInitials().contains("Elec") || subjects1.get(i).getInitials().contains("Pwr") || subjects1.get(i).getInitials().contains("Wood") || subjects1.get(i).getInitials().contains("Metal") || subjects1.get(i).getInitials().contains("Bc") || subjects1.get(i).getInitials().contains("Dnd")) {
                group4.add(subjects1.get(i));
                continue;
            }
            group5.add(subjects1.get(i));
        }
        return "redirect:/school/" + code;
    }

    @GetMapping(value={"/schools/{code}/subjects/{initials}"})
    public String deleteSubjectFromSchool(@PathVariable int code, @PathVariable String initials, Model model, Principal principal) {
        if (this.subjectService.doesSubjectExistsInSchool(initials, code).booleanValue()) {
            Subject subject = this.subjectService.getSubjectInSchool(initials, code);
            subject.getSchools().remove(this.schoolService.getSchool(code).get());
            this.subjectService.addSubject(subject);
            model.addAttribute("success", (Object)(initials + " subject successfully deleted"));
        } else {
            model.addAttribute("fail", (Object)(initials + " subject does not exist"));
        }
        List<Subject> subjects1 = this.subjectService.getAllSubjectInSchool(code);
        ArrayList<Subject> group1 = new ArrayList<Subject>();
        ArrayList<Subject> group2 = new ArrayList<Subject>();
        ArrayList<Subject> group3 = new ArrayList<Subject>();
        ArrayList<Subject> group4 = new ArrayList<Subject>();
        ArrayList<Subject> group5 = new ArrayList<Subject>();
        for (int i = 0; i < subjects1.size(); ++i) {
            if (subjects1.get(i).getName().contains("Mathematics") || subjects1.get(i).getName().contains("English") || subjects1.get(i).getName().contains("Kiswahili")) {
                group1.add(subjects1.get(i));
                continue;
            }
            if (subjects1.get(i).getName().contains("Biology") || subjects1.get(i).getName().contains("Physics") || subjects1.get(i).getName().contains("Chemistry")) {
                group2.add(subjects1.get(i));
                continue;
            }
            if (subjects1.get(i).getInitials().contains("Hist") || subjects1.get(i).getInitials().contains("Geo") || subjects1.get(i).getInitials().contains("C.R.E") || subjects1.get(i).getInitials().contains("I.R.E") || subjects1.get(i).getInitials().contains("H.R.E")) {
                group3.add(subjects1.get(i));
                continue;
            }
            if (subjects1.get(i).getInitials().contains("Hsci") || subjects1.get(i).getInitials().contains("AnD") || subjects1.get(i).getInitials().contains("Agric") || subjects1.get(i).getInitials().contains("Comp") || subjects1.get(i).getInitials().contains("Avi") || subjects1.get(i).getInitials().contains("Elec") || subjects1.get(i).getInitials().contains("Pwr") || subjects1.get(i).getInitials().contains("Wood") || subjects1.get(i).getInitials().contains("Metal") || subjects1.get(i).getInitials().contains("Bc") || subjects1.get(i).getInitials().contains("Dnd")) {
                group4.add(subjects1.get(i));
                continue;
            }
            group5.add(subjects1.get(i));
        }
        List<Subject> allSubjects = this.subjectService.getAllSubjects();
        for (int i = 0; i < subjects1.size(); ++i) {
            allSubjects.remove(subjects1.get(i));
        }
        return "redirect:/school/" + code;
    }

    @GetMapping(value={"/schools/{code}/students/{admNo}/subjects/{initials}"})
    public String deleteSubjectFromStudent(@PathVariable int code, @PathVariable String admNo, @PathVariable String initials, Model model, Principal principal) {
        Student student = this.studentService.getStudentInSchool(admNo, code);
        Subject subject = this.subjectService.getSubjectByStudent(initials, admNo);
        student.getSubjects().remove(subject);
        this.studentService.addStudent(student);
        List<Subject> subjects = this.subjectService.getSubjectDoneByStudent(admNo);
        List<Subject> schoolSubjects = this.subjectService.getAllSubjectInSchool(code);
        for (int i = 0; i < subjects.size(); ++i) {
            schoolSubjects.remove(subjects.get(i));
        }
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
        return "redirect:/schools/" + code + "/student/" + admNo;
    }

    @PostMapping(value={"/schools/{code}/student/{admNo}/subjects"})
    public String addSubjectToStudent(Model model, @PathVariable int code, @PathVariable String admNo, HttpServletRequest request, @RequestParam int form, @RequestParam int year, Principal principal) {
        Student student = this.studentService.getStudentInSchool(admNo, code);
        List<Subject> schoolSubjects = this.subjectService.getAllSubjectInSchool(code);
        List<Subject> studentSubjects = this.subjectService.getSubjectDoneByStudent(admNo);
        ArrayList<String> paramList = new ArrayList<String>();
        for (int i = 0; i < schoolSubjects.size(); ++i) {
            paramList.add(request.getParameter(schoolSubjects.get(i).getInitials()));
            if (paramList.get(i) == null || studentSubjects.contains(this.subjectService.getSubject(paramList.get(i)))) continue;
            studentSubjects.add(this.subjectService.getSubject(paramList.get(i)));
        }
        Form formObj = this.formService.getStudentForm(form, admNo);
        formObj.setSubjects(studentSubjects);
        this.formService.addForm(formObj);
        Year yearObj = this.yearService.getYearFromSchool(year, code).get();
        yearObj.setSubjects(studentSubjects);
        this.yearService.addYear(yearObj);
        student.setSubjects(studentSubjects);
        this.studentService.addStudent(student);
        for (int i = 0; i < studentSubjects.size(); ++i) {
            schoolSubjects.remove(studentSubjects.get(i));
        }
        List<Subject> subjects = this.subjectService.getSubjectDoneByStudent(admNo);
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
        return "redirect:/schools/" + code + "/student/" + admNo;
    }
}
