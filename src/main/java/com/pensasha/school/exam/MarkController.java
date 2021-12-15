package com.pensasha.school.exam;

import java.security.Principal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.pensasha.school.form.Form;
import com.pensasha.school.form.FormService;
import com.pensasha.school.school.School;
import com.pensasha.school.school.SchoolService;
import com.pensasha.school.stream.Stream;
import com.pensasha.school.stream.StreamService;
import com.pensasha.school.student.Student;
import com.pensasha.school.student.StudentFormYear;
import com.pensasha.school.student.StudentFormYearService;
import com.pensasha.school.student.StudentService;
import com.pensasha.school.subject.Subject;
import com.pensasha.school.subject.SubjectService;
import com.pensasha.school.term.Term;
import com.pensasha.school.term.TermService;
import com.pensasha.school.user.TeacherYearFormStream;
import com.pensasha.school.user.TeacherYearFormStreamService;
import com.pensasha.school.user.User;
import com.pensasha.school.user.UserService;
import com.pensasha.school.year.Year;
import com.pensasha.school.year.YearService;
import java.util.Arrays;

@Controller
public class MarkController {

    private final StudentService studentService;
    private final MarkService markService;
    private final FormService formService;
    private final YearService yearService;
    private final TermService termService;
    private final SubjectService subjectService;
    private final UserService userService;
    private final StreamService streamService;
    private final SchoolService schoolService;
    private final ExamNameService examNameService;
    private final StudentFormYearService studentFormYearService;
    private final TeacherYearFormStreamService teacherYearFormStreamService;

    public MarkController(StudentService studentService, MarkService markService, FormService formService,
            YearService yearService, TermService termService, SubjectService subjectService, UserService userService,
            StreamService streamService, SchoolService schoolService, ExamNameService examNameService,
            StudentFormYearService studentFormYearService, TeacherYearFormStreamService teacherYearFormStreamService) {
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
        this.examNameService = examNameService;
        this.studentFormYearService = studentFormYearService;
        this.teacherYearFormStreamService = teacherYearFormStreamService;
    }

    @GetMapping(value = {"/schools/{code}/years/{year}/examination"})
    public String examinations(@PathVariable int code, @PathVariable int year, Model model, Principal principal) {

        User activeUser = this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(code).get();
        Student student = new Student();
        List<ExamName> examNames = new ArrayList<>();

        for (int m = 1; m <= 4; m++) {
            for (int n = 1; n <= 3; n++) {
                List<ExamName> endNames = new ArrayList<>();
                List<ExamName> eNs = this.examNameService.getExamBySchoolYearFormTerm(code, year, m, n);

                for (int i = 0; i < eNs.size(); ++i) {

                    endNames.add(eNs.get(i));

                    if (endNames.size() > 0) {
                        for (int k = 0; k < endNames.size(); ++k) {
                            if (k > 0) {
                                if (endNames.get(k - 1).getName().equals(endNames.get(k).getName())) {
                                    endNames.remove(endNames.get(k));
                                    continue;
                                }
                            }
                        }

                    }

                }

                examNames.addAll(endNames);
            }

        }

        model.addAttribute("activeUser", activeUser);
        model.addAttribute("school", school);
        model.addAttribute("subjects", school.getSubjects());
        model.addAttribute("streams", this.streamService.getStreamsInSchool(code));
        model.addAttribute("student", student);
        model.addAttribute("examNames", examNames);
        model.addAttribute("year", year);

        return "examination";

    }

    @GetMapping(value = {"/schools/{code}/years/{year}/forms/{form}/terms/{term}/exams"})
    @ResponseBody
    public List<ExamName> examNames(@PathVariable int code, @PathVariable int year, @PathVariable int form, @PathVariable int term) {

        List<ExamName> endNames = new ArrayList<>();
        List<ExamName> eNs = this.examNameService.getExamBySchoolYearFormTerm(code, year, form, term);

        for (int i = 0; i < eNs.size(); ++i) {

            endNames.add(eNs.get(i));

            if (endNames.size() > 0) {
                for (int k = 0; k < endNames.size(); ++k) {
                    if (k > 0) {
                        if (endNames.get(k - 1).getName().equals(endNames.get(k).getName())) {
                            endNames.remove(endNames.get(k));
                            continue;
                        }
                    }
                }

            }

        }

        return endNames;
    }

    @PostMapping(value = {"/schools/{code}/examination"})
    public String allExamination(@PathVariable int code, @RequestParam int year) {
        return "redirect:/schools/" + code + "/years/" + year + "/examination";
    }

    @PostMapping(value = {"/schools/{code}/years/{year}/examination"})
    public String addingExamination(@PathVariable int code, @PathVariable int year, @RequestParam String name, @RequestParam int form, @RequestParam int term, HttpServletRequest request) {

        School school = this.schoolService.getSchool(code).get();
        ArrayList<Subject> subjects = new ArrayList<>();

        school.getSubjects().forEach(subject -> subjects.add(subject));
        for (int i = 0; i < subjects.size(); ++i) {

            ExamName examName = new ExamName();
            examName.setName(name);

            ArrayList<School> schools = new ArrayList<>();
            schools.add(school);
            examName.setSchools(schools);

            Year yearObj = this.yearService.getYearFromSchool(year, code).get();
            ArrayList<Year> years = new ArrayList<>();
            years.add(yearObj);
            examName.setYears(years);

            Form formObj = this.formService.getForm(form, year, code).get();
            ArrayList<Form> forms = new ArrayList<>();
            forms.add(formObj);
            examName.setForms(forms);

            Term termObj = this.termService.getTerm(term, form, year, code);
            ArrayList<Term> terms = new ArrayList<>();
            terms.add(termObj);
            examName.setTerms(terms);

            examName.setOutOf(Integer.parseInt(request.getParameter(subjects.get(i).getInitials() + "OutOf")));
            examName.setSubject(subjects.get(i));

            this.examNameService.addExam(examName);
        }

        return "redirect:/schools/" + code + "/years/" + year + "/examination";

    }

    @GetMapping(value = {"/schools/{code}/years/{year}/forms/{form}/terms/{term}/examination/{name}"})
    public String deleteExamFromSchool(@PathVariable int code, @PathVariable String name, @PathVariable int year, @PathVariable int form, @PathVariable int term) {
        List<ExamName> examNames = this.examNameService.getExamByCodeNameYearFormTermAndExamName(name, code, year, form, term);
        List<Mark> marks = this.markService.getMarksBySchoolOnSubjectByExamName(code, year, form, term, name);
        marks.forEach(mark -> this.markService.deleteMark(mark.getId()));
        examNames.forEach(examName -> this.examNameService.deleteExam(examName.getId()));
        return "redirect:/schools/" + code + "/years/" + year + "/examination";
    }

    @PostMapping(value = {"/schools/{code}/stream/{stream}/marks/{exam}"})
    public RedirectView addMarksToStudentSubjects(@PathVariable int code, @PathVariable int stream, @PathVariable int exam, HttpServletRequest request, RedirectAttributes redit, Principal principal) {
        int form = Integer.parseInt(request.getParameter("form"));
        int year = Integer.parseInt(request.getParameter("year"));
        int term = Integer.parseInt(request.getParameter("term"));
        Year yearObj = this.yearService.getYearFromSchool(year, code).get();
        Term termObj = this.termService.getTerm(term, form, year, code);
        Form formObj = this.formService.getFormByForm(form);
        ExamName examName = this.examNameService.getExam(exam);
        String subject = request.getParameter("subject");
        Subject subjectObj = this.subjectService.getSubject(subject);

        List<Student> students = new ArrayList<>();
        List<StudentFormYear> studentsFormYear = this.studentFormYearService.findAllStudentDoingSubjectInStream(code, year, form, term, subject, stream);

        for (StudentFormYear studentFormYear : studentsFormYear) {
            if (!students.contains(studentFormYear.getStudent())) {
                students.add(studentFormYear.getStudent());
            }
        }

        for (Student student : students) {
            Mark mark = new Mark();
            if (this.markService.getMarksByStudentOnSubjectByExamId(student.getAdmNo(), year, form, term, subject, exam) != null) {
                mark = this.markService.getMarksByStudentOnSubjectByExamId(student.getAdmNo(), year, form, term, subject, exam);
                String mrk = request.getParameter(student.getAdmNo() + "mark");

                int mark1;
                try {
                    mark1 = Integer.parseInt(mrk);
                    if (mark1 > examName.getOutOf() || mark1 < 0) {
                        mark1 = -1;
                    }
                } catch (NumberFormatException e) {
                    mark1 = -1;
                }

                mark.setMark(mark1);
                this.markService.addMarksToSubject(mark);
                continue;
            }
            mark = new Mark(student, yearObj, formObj, termObj, subjectObj);
            mark.setMark(Integer.parseInt(request.getParameter(student.getAdmNo() + "mark")));
            mark.setExamName(examName);
            this.markService.addMarksToSubject(mark);
        }
        redit.addFlashAttribute("success", "Marks saved successfully");
        if (students.size() == 0) {
            redit.addFlashAttribute("fail", "No student. Cannot add marks");
        }
        RedirectView redirectView = new RedirectView("/schools/" + code + "/years/" + year + "/forms/" + form + "/terms/" + term + "/subjects/" + subject + "/streams/" + stream + "/exams/" + exam, true);

        return redirectView;

    }

    @PostMapping(value = {"/schools/{code}/marksSheet"})
    public String postMarkSheet(Model model, Principal principal, @PathVariable int code, @RequestParam int year, @RequestParam int form, @RequestParam int term, @RequestParam String subject, @RequestParam int stream, @RequestParam int examType) {
        return "redirect:/schools/" + code + "/years/" + year + "/forms/" + form + "/terms/" + term + "/subjects/" + subject + "/streams/" + stream + "/exams/" + examType;
    }

    @GetMapping(value = {"/schools/{code}/years/{year}/forms/{form}/terms/{term}/subjects/{subject}/streams/{stream}/exams/{exam}"})
    public String getMarkSheet(Model model, Principal principal, @PathVariable int code, @PathVariable int year, @PathVariable String subject, @PathVariable int form, @PathVariable int term, @PathVariable int stream, @PathVariable int exam) {

        User activeUser = this.userService.getByUsername(principal.getName()).get();
        Subject subjectObj = this.subjectService.getSubject(subject);
        School school = this.schoolService.getSchool(code).get();
        Student student = new Student();
        Term termObj = this.termService.getTerm(term, form, year, code);
        Year yearObj = this.yearService.getYearFromSchool(year, code).get();
        Form formObj = this.formService.getFormByForm(form);
        Stream streamObj = this.streamService.getStream(stream);
        ExamName examName = examNameService.getExam(exam);
        List<Student> students = new ArrayList<>();
        List<StudentFormYear> studentsFormYear = this.studentFormYearService.findAllStudentDoingSubjectInStream(code, year, form, term, subject, stream);

        for (StudentFormYear studentFormYear : studentsFormYear) {
            if (!students.contains(studentFormYear.getStudent())) {
                students.add(studentFormYear.getStudent());
            }
        }

        List<Stream> streams = this.streamService.getStreamsInSchool(school.getCode());
        List<Year> years = this.yearService.getAllYearsInSchool(school.getCode());
        List<Subject> subjects = this.subjectService.getAllSubjectInSchool(school.getCode());
        ArrayList<Mark> marks = new ArrayList<>();

        for (Student student2 : students) {
            Mark mark = new Mark();
            if (this.markService.getMarksByStudentOnSubjectByExamId(student2.getAdmNo(), year, form, term, subject, exam) != null) {
                marks.add(this.markService.getMarksByStudentOnSubjectByExamId(student2.getAdmNo(), year, form, term, subject, exam));
                continue;
            }
            mark.setStudent(student2);
            mark.setSubject(subjectObj);
            mark.setTerm(termObj);
            mark.setYear(yearObj);
            mark.setForm(formObj);
            mark.setExamName(examName);
            marks.add(mark);
            this.markService.addMarksToSubject(mark);
        }
        model.addAttribute("marks", marks);
        model.addAttribute("subjects", subjects);
        model.addAttribute("streams", streams);
        model.addAttribute("years", years);
        model.addAttribute("students", students);
        model.addAttribute("subject", subjectObj);
        model.addAttribute("year", year);
        model.addAttribute("form", form);
        model.addAttribute("term", term);
        model.addAttribute("stream", streamObj);
        model.addAttribute("examName", examName);
        model.addAttribute("student", student);
        model.addAttribute("school", school);
        model.addAttribute("activeUser", activeUser);

        return "marksEntry";

    }

    @PostMapping(value = {"/schools/{code}/meritList"})
    public String studentsMeritList(@PathVariable int code, @RequestParam int year, @RequestParam int form, @RequestParam int term, Model model, Principal principal) {
        return "redirect:/schools/" + code + "/years/" + year + "/forms/" + form + "/terms/" + term + "/meritList";
    }

    @GetMapping(value = {"/schools/{code}/years/{year}/forms/{form}/terms/{term}/meritList"})
    public String getMeritList(@PathVariable int code, @PathVariable int year, @PathVariable int form, @PathVariable int term, Model model, Principal principal) {

        int i;
        GradeCount gradeCount;
        int i2;
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(code).get();
        Student student = new Student();

        List<Student> students = new ArrayList<>();
        List<StudentFormYear> studentsFormYear = this.studentFormYearService.getAllStudentsInSchoolByYearFormTerm(code, year, form, term);

        for (StudentFormYear studentFormYear : studentsFormYear) {
            if (!students.contains(studentFormYear.getStudent())) {
                students.add(studentFormYear.getStudent());
            }
        }

        List<Subject> subjects = this.subjectService.getAllSubjectInSchool(code);
        List<Mark> allMarks = this.markService.getAllStudentsMarksBySchoolYearFormAndTerm(code, form, term, year);

        ArrayList<Student> studentsWithoutMarks = new ArrayList<>();
        ArrayList<Student> studentsWithMarks = new ArrayList<>();

        for (Student student2 : students) {
            if (!this.markService.getMarkByAdm(student2.getAdmNo()).booleanValue()) {
                studentsWithoutMarks.add(student2);
                continue;
            }
            studentsWithMarks.add(student2);
        }
        ArrayList<MeritList> meritLists = new ArrayList<>();
        ArrayList<GradeCount> gradeCounts = new ArrayList<>();

        int mathsCount = 0, engCount = 0, kisCount = 0, bioCount = 0, chemCount = 0, phyCount = 0, histCount = 0,
                creCount = 0, geoCount = 0, ireCount = 0, hreCount = 0, hsciCount = 0, andCount = 0, agricCount = 0,
                compCount = 0, aviCount = 0, elecCount = 0, pwrCount = 0, woodCount = 0, metalCount = 0, bcCount = 0,
                frenCount = 0, germCount = 0, arabCount = 0, mscCount = 0, bsCount = 0, dndCount = 0, overallCount = 0,
                overallFCount = 0, overallMCount = 0;

        for (i2 = 0; i2 < studentsWithMarks.size(); ++i2) {
            List<SubjectMarks> compulsoryMarks = new ArrayList<>();
            List<SubjectMarks> scienceMarks = new ArrayList<>();
            List<SubjectMarks> humanitiesMarks = new ArrayList<>();
            List<SubjectMarks> technicalMarks = new ArrayList<>();
            gradeCount = new GradeCount();
            MeritList meritList = new MeritList();
            int count = 0, fcount = 0, mcount = 0;
            block118:
            for (Subject subject : subjects) {
                meritList.setFirstname(students.get(i2).getFirstname());
                meritList.setSecondname(students.get(i2).getThirdname());
                meritList.setAdmNo(students.get(i2).getAdmNo());
                meritList.setKcpe(students.get(i2).getKcpeMarks());
                meritList.setStream(students.get(i2).getStream().getStream());
                meritList.setGender(students.get(i2).getGender());
                gradeCount.setFirstname(students.get(i2).getFirstname());
                gradeCount.setSecondname(students.get(i2).getThirdname());
                gradeCount.setAdmNo(students.get(i2).getAdmNo());
                List<Mark> marks = new ArrayList<>();
                SubjectMarks subjectMarks;
                int sum = 0;
                int totalOutOf = 0;
                switch (subject.getInitials()) {
                    case "Maths": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subject.getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                            totalOutOf += marks.get(k).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++mathsCount;
                            if (meritList.getGender().equals("Female")) {
                                fcount++;
                            } else if (meritList.getGender().equals("Male")) {
                                mcount++;
                            }
                        }
                        if (totalOutOf > 0) {
                            meritList.setMaths(sum * 100 / totalOutOf);
                            subjectMarks = new SubjectMarks("Maths", meritList.getMaths());
                            compulsoryMarks.add(subjectMarks);
                            gradeCount.setMaths(this.getSubjectGrade(meritList.getMaths(), "Maths"));
                            continue block118;
                        }
                        if (sum < 0) {
                            meritList.setMaths(-1);
                        } else {
                            meritList.setMaths(sum);
                        }

                        gradeCount.setMaths(this.getSubjectGrade(meritList.getMaths(), "Maths"));
                        continue block118;
                    }
                    case "Eng": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subject.getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                            totalOutOf += marks.get(k).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++engCount;
                            if (meritList.getGender().equals("Female")) {
                                fcount++;
                            } else if (meritList.getGender().equals("Male")) {
                                mcount++;
                            }
                        }
                        if (totalOutOf > 0) {
                            meritList.setEng(sum * 100 / totalOutOf);
                            subjectMarks = new SubjectMarks("Eng", meritList.getEng());
                            compulsoryMarks.add(subjectMarks);
                            gradeCount.setEng(this.getSubjectGrade(meritList.getEng(), "Eng"));
                            continue block118;
                        }

                        if (sum < 0) {
                            meritList.setEng(-1);
                        } else {
                            meritList.setEng(sum);
                        }

                        gradeCount.setEng(this.getSubjectGrade(meritList.getEng(), "Eng"));
                        continue block118;
                    }
                    case "Kis": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subject.getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                            totalOutOf += marks.get(k).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++kisCount;
                            if (meritList.getGender().equals("Female")) {
                                fcount++;
                            } else if (meritList.getGender().equals("Male")) {
                                mcount++;
                            }
                        }
                        if (totalOutOf > 0) {
                            meritList.setKis(sum * 100 / totalOutOf);
                            subjectMarks = new SubjectMarks("Kis", meritList.getKis());
                            compulsoryMarks.add(subjectMarks);
                            gradeCount.setKis(this.getSubjectGrade(meritList.getKis(), "Kis"));
                            continue block118;
                        }

                        if (sum < 0) {
                            meritList.setKis(-1);
                        } else {
                            meritList.setKis(sum);
                        }

                        gradeCount.setKis(this.getSubjectGrade(meritList.getKis(), "Kis"));
                        continue block118;
                    }
                    case "Bio": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subject.getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                            totalOutOf += marks.get(k).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++bioCount;
                            if (meritList.getGender().equals("Female")) {
                                fcount++;
                            } else if (meritList.getGender().equals("Male")) {
                                mcount++;
                            }
                        }
                        if (totalOutOf > 0) {
                            meritList.setBio(sum * 100 / totalOutOf);
                            subjectMarks = new SubjectMarks("Bio", meritList.getBio());
                            scienceMarks.add(subjectMarks);
                            gradeCount.setBio(this.getSubjectGrade(meritList.getBio(), "Bio"));
                            continue block118;
                        }

                        if (sum < 0) {
                            meritList.setBio(-1);
                        } else {
                            meritList.setBio(sum);
                        }

                        gradeCount.setBio(this.getSubjectGrade(meritList.getBio(), "Bio"));
                        continue block118;
                    }
                    case "Chem": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subject.getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                            totalOutOf += marks.get(k).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++chemCount;
                            if (meritList.getGender().equals("Female")) {
                                fcount++;
                            } else if (meritList.getGender().equals("Male")) {
                                mcount++;
                            }
                        }
                        if (totalOutOf > 0) {
                            meritList.setChem(sum * 100 / totalOutOf);
                            subjectMarks = new SubjectMarks("Chem", meritList.getChem());
                            scienceMarks.add(subjectMarks);
                            gradeCount.setChem(this.getSubjectGrade(meritList.getChem(), "Chem"));
                            continue block118;
                        }

                        if (sum < 0) {
                            meritList.setChem(-1);
                        } else {
                            meritList.setChem(sum);
                        }

                        gradeCount.setChem(this.getSubjectGrade(meritList.getChem(), "Chem"));
                        continue block118;
                    }
                    case "Phy": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subject.getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                            totalOutOf += marks.get(k).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++phyCount;
                            if (meritList.getGender().equals("Female")) {
                                fcount++;
                            } else if (meritList.getGender().equals("Male")) {
                                mcount++;
                            }
                        }
                        if (totalOutOf > 0) {
                            meritList.setPhy(sum * 100 / totalOutOf);
                            subjectMarks = new SubjectMarks("Phy", meritList.getPhy());
                            scienceMarks.add(subjectMarks);
                            gradeCount.setPhy(this.getSubjectGrade(meritList.getPhy(), "Phy"));
                            continue block118;
                        }

                        if (sum < 0) {
                            meritList.setChem(-1);
                        } else {
                            meritList.setChem(sum);
                        }

                        gradeCount.setPhy(this.getSubjectGrade(meritList.getPhy(), "Phy"));
                        continue block118;
                    }
                    case "Hist": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subject.getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                            totalOutOf += marks.get(k).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++histCount;
                            if (meritList.getGender().equals("Female")) {
                                fcount++;
                            } else if (meritList.getGender().equals("Male")) {
                                mcount++;
                            }
                        }
                        if (totalOutOf > 0) {
                            meritList.setHist(sum * 100 / totalOutOf);
                            subjectMarks = new SubjectMarks("Hist", meritList.getHist());
                            humanitiesMarks.add(subjectMarks);
                            gradeCount.setHist(this.getSubjectGrade(meritList.getHist(), "Hist"));
                            continue block118;
                        }

                        if (sum < 0) {
                            meritList.setHist(-1);
                        } else {
                            meritList.setHist(sum);
                        }

                        gradeCount.setHist(this.getSubjectGrade(meritList.getHist(), "Hist"));
                        continue block118;
                    }
                    case "C.R.E": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subject.getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                            totalOutOf += marks.get(k).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++creCount;
                            if (meritList.getGender().equals("Female")) {
                                fcount++;
                            } else if (meritList.getGender().equals("Male")) {
                                mcount++;
                            }
                        }
                        if (totalOutOf > 0) {
                            meritList.setCre(sum * 100 / totalOutOf);
                            subjectMarks = new SubjectMarks("C.R.E", meritList.getCre());
                            humanitiesMarks.add(subjectMarks);
                            gradeCount.setCre(this.getSubjectGrade(meritList.getCre(), "C.R.E"));
                            continue block118;
                        }

                        if (sum < 0) {
                            meritList.setCre(-1);
                        } else {
                            meritList.setCre(sum);
                        }

                        gradeCount.setCre(this.getSubjectGrade(meritList.getCre(), "C.R.E"));
                        continue block118;
                    }
                    case "Geo": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subject.getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                            totalOutOf += marks.get(k).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++geoCount;
                            if (meritList.getGender().equals("Female")) {
                                fcount++;
                            } else if (meritList.getGender().equals("Male")) {
                                mcount++;
                            }
                        }
                        if (totalOutOf > 0) {
                            meritList.setGeo(sum * 100 / totalOutOf);
                            subjectMarks = new SubjectMarks("Geo", meritList.getGeo());
                            humanitiesMarks.add(subjectMarks);
                            gradeCount.setGeo(this.getSubjectGrade(meritList.getGeo(), "Geo"));
                            continue block118;
                        }

                        if (sum < 0) {
                            meritList.setGeo(-1);
                        } else {
                            meritList.setGeo(sum);
                        }

                        gradeCount.setGeo(this.getSubjectGrade(meritList.getGeo(), "Geo"));
                        continue block118;
                    }
                    case "I.R.E": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subject.getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                            totalOutOf += marks.get(k).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++ireCount;
                            if (meritList.getGender().equals("Female")) {
                                fcount++;
                            } else if (meritList.getGender().equals("Male")) {
                                mcount++;
                            }
                        }
                        if (totalOutOf > 0) {
                            meritList.setIre(sum * 100 / totalOutOf);
                            subjectMarks = new SubjectMarks("I.R.E", meritList.getIre());
                            humanitiesMarks.add(subjectMarks);
                            gradeCount.setIre(this.getSubjectGrade(meritList.getIre(), "I.R.E"));
                            continue block118;
                        }

                        if (sum < 0) {
                            meritList.setIre(-1);
                        } else {
                            meritList.setIre(sum);
                        }

                        gradeCount.setIre(this.getSubjectGrade(meritList.getIre(), "I.R.E"));
                        continue block118;
                    }
                    case "H.R.E": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subject.getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                            totalOutOf += marks.get(k).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++hreCount;
                            if (meritList.getGender().equals("Female")) {
                                fcount++;
                            } else if (meritList.getGender().equals("Male")) {
                                mcount++;
                            }
                        }
                        if (totalOutOf > 0) {
                            meritList.setHre(sum * 100 / totalOutOf);
                            subjectMarks = new SubjectMarks("H.R.E", meritList.getHre());
                            humanitiesMarks.add(subjectMarks);
                            gradeCount.setHre(this.getSubjectGrade(meritList.getHre(), "H.R.E"));
                            continue block118;
                        }

                        if (sum < 0) {
                            meritList.setHre(-1);
                        } else {
                            meritList.setHre(sum);
                        }

                        gradeCount.setHre(this.getSubjectGrade(meritList.getHre(), "H.R.E"));
                        continue block118;
                    }
                    case "Hsci": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subject.getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                            totalOutOf += marks.get(k).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++hsciCount;
                            if (meritList.getGender().equals("Female")) {
                                fcount++;
                            } else if (meritList.getGender().equals("Male")) {
                                mcount++;
                            }
                        }
                        if (totalOutOf > 0) {
                            meritList.setHsci(sum * 100 / totalOutOf);
                            subjectMarks = new SubjectMarks("Hsci", meritList.getHsci());
                            technicalMarks.add(subjectMarks);
                            gradeCount.setHsci(this.getSubjectGrade(meritList.getHsci(), "Hsci"));
                            continue block118;
                        }

                        if (sum < 0) {
                            meritList.setHsci(-1);
                        } else {
                            meritList.setHsci(sum);
                        }

                        gradeCount.setHsci(this.getSubjectGrade(meritList.getHsci(), "Hsci"));
                        continue block118;
                    }
                    case "AnD": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subject.getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                            totalOutOf += marks.get(k).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++andCount;
                            if (meritList.getGender().equals("Female")) {
                                fcount++;
                            } else if (meritList.getGender().equals("Male")) {
                                mcount++;
                            }
                        }
                        if (totalOutOf > 0) {
                            meritList.setAnD(sum * 100 / totalOutOf);
                            subjectMarks = new SubjectMarks("AnD", meritList.getAnD());
                            technicalMarks.add(subjectMarks);
                            gradeCount.setAnd(this.getSubjectGrade(meritList.getAnD(), "AnD"));
                            continue block118;
                        }

                        if (sum < 0) {
                            meritList.setAnD(-1);
                        } else {
                            meritList.setAnD(sum);
                        }

                        gradeCount.setAnd(this.getSubjectGrade(meritList.getAnD(), "AnD"));
                        continue block118;
                    }
                    case "Agric": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subject.getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                            totalOutOf += marks.get(k).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++agricCount;
                            if (meritList.getGender().equals("Female")) {
                                fcount++;
                            } else if (meritList.getGender().equals("Male")) {
                                mcount++;
                            }
                        }
                        if (totalOutOf > 0) {
                            meritList.setAgric(sum * 100 / totalOutOf);
                            subjectMarks = new SubjectMarks("Agric", meritList.getAgric());
                            technicalMarks.add(subjectMarks);
                            gradeCount.setAgric(this.getSubjectGrade(meritList.getAgric(), "Agric"));
                            continue block118;
                        }

                        if (sum < 0) {
                            meritList.setAgric(-1);
                        } else {
                            meritList.setAgric(sum);
                        }

                        gradeCount.setAgric(this.getSubjectGrade(meritList.getAgric(), "Agric"));
                        continue block118;
                    }
                    case "Comp": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subject.getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                            totalOutOf += marks.get(k).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++compCount;
                            if (meritList.getGender().equals("Female")) {
                                fcount++;
                            } else if (meritList.getGender().equals("Male")) {
                                mcount++;
                            }
                        }
                        if (totalOutOf > 0) {
                            meritList.setComp(sum * 100 / totalOutOf);
                            subjectMarks = new SubjectMarks("Comp", meritList.getComp());
                            technicalMarks.add(subjectMarks);
                            gradeCount.setComp(this.getSubjectGrade(meritList.getComp(), "Comp"));
                            continue block118;
                        }

                        if (sum < 0) {
                            meritList.setComp(-1);
                        } else {
                            meritList.setComp(sum);
                        }

                        gradeCount.setComp(this.getSubjectGrade(meritList.getComp(), "Comp"));
                        continue block118;
                    }
                    case "Avi": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subject.getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                            totalOutOf += marks.get(k).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++aviCount;
                            if (meritList.getGender().equals("Female")) {
                                fcount++;
                            } else if (meritList.getGender().equals("Male")) {
                                mcount++;
                            }
                        }
                        if (totalOutOf > 0) {
                            meritList.setAvi(sum * 100 / totalOutOf);
                            subjectMarks = new SubjectMarks("Avi", meritList.getAvi());
                            technicalMarks.add(subjectMarks);
                            gradeCount.setAvi(this.getSubjectGrade(meritList.getAvi(), "Avi"));
                            continue block118;
                        }

                        if (sum < 0) {
                            meritList.setAvi(-1);
                        } else {
                            meritList.setAvi(sum);
                        }

                        gradeCount.setAvi(this.getSubjectGrade(meritList.getAvi(), "Avi"));
                        continue block118;
                    }
                    case "Elec": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subject.getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                            totalOutOf += marks.get(k).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++elecCount;
                            if (meritList.getGender().equals("Female")) {
                                fcount++;
                            } else if (meritList.getGender().equals("Male")) {
                                mcount++;
                            }
                        }
                        if (totalOutOf > 0) {
                            meritList.setElec(sum * 100 / totalOutOf);
                            subjectMarks = new SubjectMarks("Elec", meritList.getElec());
                            technicalMarks.add(subjectMarks);
                            gradeCount.setElec(this.getSubjectGrade(meritList.getElec(), "Elec"));
                            continue block118;
                        }

                        if (sum < 0) {
                            meritList.setElec(-1);
                        } else {
                            meritList.setElec(sum);
                        }

                        gradeCount.setElec(this.getSubjectGrade(meritList.getElec(), "Elec"));
                        continue block118;
                    }
                    case "Pwr": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subject.getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                            totalOutOf += marks.get(k).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++pwrCount;
                            if (meritList.getGender().equals("Female")) {
                                fcount++;
                            } else if (meritList.getGender().equals("Male")) {
                                mcount++;
                            }
                        }
                        if (totalOutOf > 0) {
                            meritList.setPwr(sum * 100 / totalOutOf);
                            subjectMarks = new SubjectMarks("Pwr", meritList.getPwr());
                            technicalMarks.add(subjectMarks);
                            gradeCount.setPwr(this.getSubjectGrade(meritList.getPwr(), "Pwr"));
                            continue block118;
                        }

                        if (sum < 0) {
                            meritList.setPwr(-1);
                        } else {
                            meritList.setPwr(sum);
                        }

                        gradeCount.setPwr(this.getSubjectGrade(meritList.getPwr(), "Pwr"));
                        continue block118;
                    }
                    case "Wood": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subject.getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                            totalOutOf += marks.get(k).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++woodCount;
                            if (meritList.getGender().equals("Female")) {
                                fcount++;
                            } else if (meritList.getGender().equals("Male")) {
                                mcount++;
                            }
                        }
                        if (totalOutOf > 0) {
                            meritList.setWood(sum * 100 / totalOutOf);
                            subjectMarks = new SubjectMarks("Wood", meritList.getWood());
                            technicalMarks.add(subjectMarks);
                            gradeCount.setWood(this.getSubjectGrade(meritList.getWood(), "Wood"));
                            continue block118;
                        }

                        if (sum < 0) {
                            meritList.setWood(-1);
                        } else {
                            meritList.setWood(sum);
                        }

                        gradeCount.setWood(this.getSubjectGrade(meritList.getWood(), "Wood"));
                        continue block118;
                    }
                    case "Metal": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subject.getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                            totalOutOf += marks.get(k).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++metalCount;
                            if (meritList.getGender().equals("Female")) {
                                fcount++;
                            } else if (meritList.getGender().equals("Male")) {
                                mcount++;
                            }
                        }
                        if (totalOutOf > 0) {
                            meritList.setMetal(sum * 100 / totalOutOf);
                            subjectMarks = new SubjectMarks("Metal", meritList.getMetal());
                            technicalMarks.add(subjectMarks);
                            gradeCount.setMetal(this.getSubjectGrade(meritList.getMetal(), "Metal"));
                            continue block118;
                        }

                        if (sum < 0) {
                            meritList.setMetal(-1);
                        } else {
                            meritList.setMetal(sum);
                        }

                        gradeCount.setMetal(this.getSubjectGrade(meritList.getMetal(), "Metal"));
                        continue block118;
                    }
                    case "Bc": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subject.getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                            totalOutOf += marks.get(k).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++bcCount;
                            if (meritList.getGender().equals("Female")) {
                                fcount++;
                            } else if (meritList.getGender().equals("Male")) {
                                mcount++;
                            }
                        }
                        if (totalOutOf > 0) {
                            meritList.setBc(sum * 100 / totalOutOf);
                            subjectMarks = new SubjectMarks("Bc", meritList.getBc());
                            technicalMarks.add(subjectMarks);
                            gradeCount.setBc(this.getSubjectGrade(meritList.getBc(), "Bc"));
                            continue block118;
                        }

                        if (sum < 0) {
                            meritList.setBc(-1);
                        } else {
                            meritList.setBc(sum);
                        }

                        gradeCount.setBc(this.getSubjectGrade(meritList.getBc(), "Bc"));
                        continue block118;
                    }
                    case "Fren": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subject.getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                            totalOutOf += marks.get(k).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++frenCount;
                            if (meritList.getGender().equals("Female")) {
                                fcount++;
                            } else if (meritList.getGender().equals("Male")) {
                                mcount++;
                            }
                        }
                        if (totalOutOf > 0) {
                            meritList.setFren(sum * 100 / totalOutOf);
                            subjectMarks = new SubjectMarks("Fren", meritList.getFren());
                            technicalMarks.add(subjectMarks);
                            gradeCount.setFren(this.getSubjectGrade(meritList.getFren(), "Fren"));
                            continue block118;
                        }

                        if (sum < 0) {
                            meritList.setFren(-1);
                        } else {
                            meritList.setFren(sum);
                        }

                        gradeCount.setFren(this.getSubjectGrade(meritList.getFren(), "Fren"));
                        continue block118;
                    }
                    case "Germ": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subject.getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                            totalOutOf += marks.get(k).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++germCount;
                            if (meritList.getGender().equals("Female")) {
                                fcount++;
                            } else if (meritList.getGender().equals("Male")) {
                                mcount++;
                            }
                        }
                        if (totalOutOf > 0) {
                            meritList.setGerm(sum * 100 / totalOutOf);
                            subjectMarks = new SubjectMarks("Germ", meritList.getGerm());
                            technicalMarks.add(subjectMarks);
                            gradeCount.setGerm(this.getSubjectGrade(meritList.getGerm(), "Germ"));
                            continue block118;
                        }

                        if (sum < 0) {
                            meritList.setGerm(-1);
                        } else {
                            meritList.setGerm(sum);
                        }

                        gradeCount.setGerm(this.getSubjectGrade(meritList.getGerm(), "Germ"));
                        continue block118;
                    }
                    case "Arab": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subject.getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                            totalOutOf += marks.get(k).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++arabCount;
                            if (meritList.getGender().equals("Female")) {
                                fcount++;
                            } else if (meritList.getGender().equals("Male")) {
                                mcount++;
                            }
                        }
                        if (totalOutOf > 0) {
                            meritList.setArab(sum * 100 / totalOutOf);
                            subjectMarks = new SubjectMarks("Arab", meritList.getArab());
                            technicalMarks.add(subjectMarks);
                            gradeCount.setArab(this.getSubjectGrade(meritList.getArab(), "Arab"));
                            continue block118;
                        }

                        if (sum < 0) {
                            meritList.setArab(-1);
                        } else {
                            meritList.setArab(sum);
                        }

                        gradeCount.setArab(this.getSubjectGrade(meritList.getArab(), "Arab"));
                        continue block118;
                    }
                    case "Msc": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subject.getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                            totalOutOf += marks.get(k).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++mscCount;
                            if (meritList.getGender().equals("Female")) {
                                fcount++;
                            } else if (meritList.getGender().equals("Male")) {
                                mcount++;
                            }
                        }
                        if (totalOutOf > 0) {
                            meritList.setMsc(sum * 100 / totalOutOf);
                            subjectMarks = new SubjectMarks("Msc", meritList.getMsc());
                            technicalMarks.add(subjectMarks);
                            gradeCount.setMsc(this.getSubjectGrade(meritList.getMsc(), "Msc"));
                            continue block118;
                        }

                        if (sum < 0) {
                            meritList.setMsc(-1);
                        } else {
                            meritList.setMsc(sum);
                        }

                        gradeCount.setMsc(this.getSubjectGrade(meritList.getMsc(), "Msc"));
                        continue block118;
                    }
                    case "Bs": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subject.getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                            totalOutOf += marks.get(k).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++bsCount;
                            if (meritList.getGender().equals("Female")) {
                                fcount++;
                            } else if (meritList.getGender().equals("Male")) {
                                mcount++;
                            }
                        }
                        if (totalOutOf > 0) {
                            meritList.setBs(sum * 100 / totalOutOf);
                            subjectMarks = new SubjectMarks("Bs", meritList.getBs());
                            technicalMarks.add(subjectMarks);
                            gradeCount.setBs(this.getSubjectGrade(meritList.getBs(), "Bs"));
                            continue block118;
                        }

                        if (sum < 0) {
                            meritList.setBs(-1);
                        } else {
                            meritList.setBs(sum);
                        }

                        gradeCount.setBs(this.getSubjectGrade(meritList.getBs(), "Bs"));
                        continue block118;
                    }
                    case "Dnd": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subject.getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                            totalOutOf += marks.get(k).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++dndCount;
                            if (meritList.getGender().equals("Female")) {
                                fcount++;
                            } else if (meritList.getGender().equals("Male")) {
                                mcount++;
                            }
                        }
                        if (totalOutOf > 0) {
                            meritList.setDnd(sum * 100 / totalOutOf);
                            subjectMarks = new SubjectMarks("Dnd", meritList.getDnd());
                            technicalMarks.add(subjectMarks);
                            gradeCount.setDnd(this.getSubjectGrade(meritList.getDnd(), "Dnd"));
                            continue block118;
                        }

                        if (sum < 0) {
                            meritList.setDnd(-1);
                        } else {
                            meritList.setDnd(sum);
                        }

                        gradeCount.setDnd(this.getSubjectGrade(meritList.getDnd(), "Dnd"));
                    }
                }
            }

            int maths01 = 0;
            byte mathsPoints = 0;
            if (meritList.getMaths() >= 0) {
                maths01 = meritList.getMaths();
                mathsPoints = this.getSubjectPoints(meritList.getMaths(), "Maths");
            }

            int eng01 = 0;
            byte engPoints = 0;
            if (meritList.getEng() >= 0) {
                eng01 = meritList.getEng();
                engPoints = this.getSubjectPoints(meritList.getEng(), "Eng");
            }

            int kis01 = 0;
            byte kisPoints = 0;
            if (meritList.getKis() >= 0) {
                kis01 = meritList.getKis();
                kisPoints = this.getSubjectPoints(meritList.getKis(), "Kis");
            }

            int bio01 = 0;
            byte bioPoints = 0;
            if (meritList.getBio() >= 0) {
                bio01 = meritList.getBio();
                bioPoints = this.getSubjectPoints(meritList.getBio(), "Bio");
            }

            int chem01 = 0;
            byte chemPoints = 0;
            if (meritList.getChem() >= 0) {
                chem01 = meritList.getChem();
                chemPoints = this.getSubjectPoints(meritList.getChem(), "Chem");
            }

            int phy01 = 0;
            byte phyPoints = 0;
            if (meritList.getPhy() >= 0) {
                phy01 = meritList.getPhy();
                phyPoints = this.getSubjectPoints(meritList.getPhy(), "Phy");
            }

            int hist01 = 0;
            byte histPoints = 0;
            if (meritList.getHist() >= 0) {
                hist01 = meritList.getHist();
                histPoints = this.getSubjectPoints(meritList.getHist(), "Hist");
            }

            int cre01 = 0;
            byte crePoints = 0;
            if (meritList.getCre() >= 0) {
                cre01 = meritList.getCre();
                crePoints = this.getSubjectPoints(meritList.getCre(), "C.R.E");
            }

            int geo01 = 0;
            byte geoPoints = 0;
            if (meritList.getGeo() >= 0) {
                geo01 = meritList.getGeo();
                geoPoints = this.getSubjectPoints(meritList.getGeo(), "Geo");
            }

            int ire01 = 0;
            byte irePoints = 0;
            if (meritList.getIre() >= 0) {
                ire01 = meritList.getIre();
                irePoints = this.getSubjectPoints(meritList.getIre(), "I.R.E");
            }

            int hre01 = 0;
            byte hrePoints = 0;
            if (meritList.getHre() >= 0) {
                hre01 = meritList.getHre();
                hrePoints = this.getSubjectPoints(meritList.getHre(), "H.R.E");
            }

            int hsci01 = 0;
            byte hsciPoints = 0;
            if (meritList.getHsci() >= 0) {
                hsci01 = meritList.getHsci();
                hsciPoints = this.getSubjectPoints(meritList.getHsci(), "Hsci");
            }

            int and01 = 0;
            byte andPoints = 0;
            if (meritList.getAnD() >= 0) {
                and01 = meritList.getAnD();
                andPoints = this.getSubjectPoints(meritList.getAnD(), "AnD");
            }

            int agric01 = 0;
            byte agricPoints = 0;
            if (meritList.getAgric() >= 0) {
                agric01 = meritList.getAgric();
                agricPoints = this.getSubjectPoints(meritList.getAgric(), "Agric");
            }

            int comp01 = 0;
            byte compPoints = 0;
            if (meritList.getComp() >= 0) {
                comp01 = meritList.getComp();
                compPoints = this.getSubjectPoints(meritList.getComp(), "Comp");
            }

            int avi01 = 0;
            byte aviPoints = 0;
            if (meritList.getAvi() >= 0) {
                avi01 = meritList.getAvi();
                aviPoints = this.getSubjectPoints(meritList.getAvi(), "Avi");
            }

            int elec01 = 0;
            byte elecPoints = 0;
            if (meritList.getElec() >= 0) {
                elec01 = meritList.getElec();
                elecPoints = this.getSubjectPoints(meritList.getElec(), "Elec");
            }

            int pwr01 = 0;
            byte pwrPoints = 0;
            if (meritList.getPwr() >= 0) {
                pwr01 = meritList.getPwr();
                pwrPoints = this.getSubjectPoints(meritList.getPwr(), "Pwr");
            }

            int wood01 = 0;
            byte woodPoints = 0;
            if (meritList.getWood() >= 0) {
                wood01 = meritList.getWood();
                woodPoints = this.getSubjectPoints(meritList.getWood(), "Wood");
            }

            int metal01 = 0;
            byte metalPoints = 0;
            if (meritList.getMetal() >= 0) {
                metal01 = meritList.getMetal();
                metalPoints = this.getSubjectPoints(meritList.getMetal(), "Metal");
            }

            int bc01 = 0;
            byte bcPoints = 0;
            if (meritList.getBc() >= 0) {
                bc01 = meritList.getBc();
                bcPoints = this.getSubjectPoints(meritList.getBc(), "Bc");
            }

            int fren01 = 0;
            byte frenPoints = 0;
            if (meritList.getFren() >= 0) {
                fren01 = meritList.getFren();
                frenPoints = this.getSubjectPoints(meritList.getFren(), "Fren");
            }

            int germ01 = 0;
            byte germPoints = 0;
            if (meritList.getGerm() >= 0) {
                germ01 = meritList.getGerm();
                germPoints = this.getSubjectPoints(meritList.getGerm(), "Germ");
            }

            int arab01 = 0;
            byte arabPoints = 0;
            if (meritList.getArab() >= 0) {
                arab01 = meritList.getArab();
                arabPoints = this.getSubjectPoints(meritList.getArab(), "Arab");
            }

            int msc01 = 0;
            byte mscPoints = 0;
            if (meritList.getMsc() >= 0) {
                msc01 = meritList.getMsc();
                mscPoints = this.getSubjectPoints(meritList.getMsc(), "Msc");
            }

            int bs01 = 0;
            byte bsPoints = 0;
            if (meritList.getBs() >= 0) {
                bs01 = meritList.getBs();
                bsPoints = this.getSubjectPoints(meritList.getBs(), "Bs");
            }

            int dnd01 = 0;
            byte dndPoints = 0;
            if (meritList.getDnd() >= 0) {
                dnd01 = meritList.getDnd();
                dndPoints = this.getSubjectPoints(meritList.getDnd(), "Dnd");
            }

            DecimalFormat df = new DecimalFormat("#.####");
            byte totalPoints = 0;
            int totalMarks = 0;
            float average;

            if (form == 3 || form == 4) {
                boolean status = false;
                int tempCount = 0;
                for (SubjectMarks sMarks : compulsoryMarks) {
                    if (sMarks.getMark() > 0) {
                        tempCount++;
                    }
                }

                if (tempCount == 3) {

                    List<SubjectMarks> addingTotals = new ArrayList<>();

                    for (SubjectMarks sMarks : compulsoryMarks) {
                        totalMarks += sMarks.getMark();
                        totalPoints += this.getSubjectPoints(sMarks.getMark(), sMarks.getName());
                    }

                    if (scienceMarks.size() > 2) {
                        if (humanitiesMarks.size() > 1) {
                            for (SubjectMarks sMarks : scienceMarks) {
                                addingTotals.add(sMarks);
                            }
                            for (SubjectMarks sMarks : humanitiesMarks) {
                                addingTotals.add(sMarks);
                            }

                        } else {
                            for (SubjectMarks sMarks : humanitiesMarks) {
                                totalMarks += sMarks.getMark();
                                totalPoints += this.getSubjectPoints(sMarks.getMark(), sMarks.getName());
                            }
                            for (SubjectMarks sMarks : scienceMarks) {
                                addingTotals.add(sMarks);
                            }
                            for (SubjectMarks sMarks : technicalMarks) {
                                addingTotals.add(sMarks);
                            }

                        }
                    } else {
                        for (SubjectMarks sMarks : scienceMarks) {
                            totalMarks += sMarks.getMark();
                            totalPoints += this.getSubjectPoints(sMarks.getMark(), sMarks.getName());
                        }
                        if (humanitiesMarks.size() > 1) {
                            for (SubjectMarks sMarks : humanitiesMarks) {
                                addingTotals.add(sMarks);
                            }
                            for (SubjectMarks sMarks : technicalMarks) {
                                addingTotals.add(sMarks);
                            }
                        } else {
                            for (SubjectMarks sMarks : humanitiesMarks) {
                                totalMarks += sMarks.getMark();
                            }
                            for (SubjectMarks sMarks : technicalMarks) {
                                totalMarks += sMarks.getMark();
                            }
                        }
                    }
                    Collections.sort(addingTotals, new SortByMarks());
                    for (int k = 1; k < addingTotals.size(); k++) {
                        totalMarks += addingTotals.get(k).getMark();
                        totalPoints += this.getSubjectPoints(addingTotals.get(k).getMark(), addingTotals.get(k).getName());
                    }

                    meritList.setTotal(totalMarks);
                    average = (float) totalPoints / 7;
                    meritList.setAverage(Float.valueOf(df.format(average)));
                    meritList.setGrade(this.getGrades(meritList.getAverage()));
                } else {
                    meritList.setTotal(0);
                }

            } else {

                meritList.setTotal(maths01 + eng01 + kis01 + bio01 + chem01 + phy01 + hist01 + cre01 + geo01 + ire01 + hre01 + hsci01 + and01 + agric01 + comp01 + avi01 + elec01 + pwr01 + wood01 + metal01 + bc01 + fren01 + germ01 + arab01 + msc01 + bs01 + dnd01);
                totalPoints = (byte) (mathsPoints + engPoints + kisPoints + bioPoints + chemPoints + phyPoints + histPoints + crePoints + geoPoints + irePoints + hrePoints + hsciPoints + andPoints + agricPoints + compPoints + aviPoints + elecPoints + pwrPoints + woodPoints + metalPoints + bcPoints + frenPoints + germPoints + arabPoints + mscPoints + bsPoints + dndPoints);

                average = (float) totalPoints / 11;
                meritList.setAverage(Float.valueOf(df.format(average)));
                meritList.setGrade(this.getGrades(meritList.getAverage()));

            }

            meritList.setDeviation(meritList.getAverage() - students.get(i2).getKcpeMarks() / 5);
            meritList.setPoints(totalPoints);

            meritLists.add(meritList);
            gradeCounts.add(gradeCount);

            overallCount += count;
            overallFCount += fcount;
            overallMCount += mcount;

        }

        List<Stream> streams = this.streamService.getStreamsInSchool(code);

        List<StreamPoints> overallStreamPoints = new ArrayList<>();
        int overallTotalPoints = 0, overallFemalePoints = 0, overallMalePoints = 0;

        for (int j = 0; j < subjects.size(); j++) {

            int totalPoints = 0, femalePoints = 0, malePoints = 0, fcount = 0, count = 0, mcount = 0;

            List<StreamPoints> streamsMeanPoints = new ArrayList<>();
            List<Student> studentsDoingSubject = this.studentService.findAllStudentDoingSubject(code, year, form, term, subjects.get(j).getInitials());
            Set<String> admNumbers = new HashSet<>();

            for (Student stude : studentsDoingSubject) {
                admNumbers.add(stude.getAdmNo());
            }

            for (MeritList meritList : meritLists) {

                StreamPoints streamPoints = new StreamPoints();
                switch (subjects.get(j).getInitials()) {
                    case "Maths":
                        if (meritList.getMaths() > 0) {
                            totalPoints += this.getSubjectPoints(meritList.getMaths(), "Maths");
                            count++;
                        }
                        if (meritList.getGender().equals("Female")) {
                            if (meritList.getMaths() > 0) {
                                femalePoints += this.getSubjectPoints(meritList.getMaths(), "Maths");
                                fcount++;
                            }
                        } else if (meritList.getGender().equals("Male")) {
                            if (meritList.getMaths() > 0) {
                                malePoints += this.getSubjectPoints(meritList.getMaths(), "Maths");
                                mcount++;
                            }
                        }

                        for (int l = 0; l < streams.size(); l++) {
                            if (meritList.getStream().equals(streams.get(l).getStream())) {

                                streamPoints.setStream(streams.get(l).getStream());
                                streamPoints.setPoints(this.getSubjectPoints(meritList.getMaths(), "Maths"));
                                streamPoints.setCount(1);

                                streamsMeanPoints.add(streamPoints);
                            }
                        }

                        break;

                    case "Eng":
                        if (meritList.getEng() > 0) {
                            totalPoints += this.getSubjectPoints(meritList.getEng(), "Eng");
                            count++;
                        }
                        if (meritList.getGender().equals("Female")) {
                            if (meritList.getEng() > 0) {
                                femalePoints += this.getSubjectPoints(meritList.getEng(), "Eng");
                                fcount++;
                            }
                        } else if (meritList.getGender().equals("Male")) {
                            if (meritList.getEng() > 0) {
                                malePoints += this.getSubjectPoints(meritList.getEng(), "Eng");
                                mcount++;
                            }
                        }
                        for (int l = 0; l < streams.size(); l++) {
                            if (meritList.getStream().equals(streams.get(l).getStream())) {

                                streamPoints.setStream(streams.get(l).getStream());
                                streamPoints.setPoints(this.getSubjectPoints(meritList.getEng(), "Eng"));
                                streamPoints.setCount(1);

                                streamsMeanPoints.add(streamPoints);
                            }
                        }

                        break;

                    case "Kis":
                        if (meritList.getKis() > 0) {
                            totalPoints += this.getSubjectPoints(meritList.getKis(), "Kis");
                            count++;
                        }
                        if (meritList.getGender().equals("Female")) {
                            if (meritList.getKis() > 0) {
                                femalePoints += this.getSubjectPoints(meritList.getKis(), "Kis");
                                fcount++;
                            }
                        } else if (meritList.getGender().equals("Male")) {
                            if (meritList.getKis() > 0) {
                                malePoints += this.getSubjectPoints(meritList.getKis(), "Kis");
                                mcount++;
                            }
                        }
                        for (int l = 0; l < streams.size(); l++) {
                            if (meritList.getStream().equals(streams.get(l).getStream())) {

                                streamPoints.setStream(streams.get(l).getStream());
                                streamPoints.setPoints(this.getSubjectPoints(meritList.getKis(), "Kis"));
                                streamPoints.setCount(1);

                                streamsMeanPoints.add(streamPoints);
                            }
                        }

                        break;

                    case "Bio":
                        if (meritList.getBio() > 0) {
                            totalPoints += this.getSubjectPoints(meritList.getBio(), "Bio");
                            count++;
                        }
                        if (meritList.getGender().equals("Female")) {
                            if (meritList.getBio() > 0) {
                                femalePoints += this.getSubjectPoints(meritList.getBio(), "Bio");
                                fcount++;
                            }
                        } else if (meritList.getGender().equals("Male")) {
                            if (meritList.getBio() > 0) {
                                malePoints += this.getSubjectPoints(meritList.getBio(), "Bio");
                                mcount++;
                            }
                        }
                        for (int l = 0; l < streams.size(); l++) {
                            if (meritList.getStream().equals(streams.get(l).getStream())) {

                                streamPoints.setStream(streams.get(l).getStream());
                                streamPoints.setPoints(this.getSubjectPoints(meritList.getBio(), "Bio"));
                                streamPoints.setCount(1);

                                streamsMeanPoints.add(streamPoints);
                            }
                        }

                        break;

                    case "Chem":
                        if (meritList.getChem() > 0) {
                            totalPoints += this.getSubjectPoints(meritList.getChem(), "Chem");
                            count++;
                        }
                        if (meritList.getGender().equals("Female")) {
                            if (meritList.getChem() > 0) {
                                femalePoints += this.getSubjectPoints(meritList.getChem(), "Chem");
                                fcount++;
                            }
                        } else if (meritList.getGender().equals("Male")) {
                            if (meritList.getChem() > 0) {
                                malePoints += this.getSubjectPoints(meritList.getChem(), "Chem");
                                mcount++;
                            }
                        }
                        for (int l = 0; l < streams.size(); l++) {
                            if (meritList.getStream().equals(streams.get(l).getStream())) {

                                streamPoints.setStream(streams.get(l).getStream());
                                streamPoints.setPoints(this.getSubjectPoints(meritList.getChem(), "Chem"));
                                streamPoints.setCount(1);

                                streamsMeanPoints.add(streamPoints);
                            }
                        }

                        break;

                    case "Phy":
                        if (meritList.getPhy() > 0) {
                            totalPoints += this.getSubjectPoints(meritList.getPhy(), "Phy");
                            count++;
                        }
                        if (meritList.getGender().equals("Female")) {
                            if (meritList.getPhy() > 0) {
                                femalePoints += this.getSubjectPoints(meritList.getPhy(), "Phy");
                                fcount++;
                            }
                        } else if (meritList.getGender().equals("Male")) {
                            if (meritList.getPhy() > 0) {
                                malePoints += this.getSubjectPoints(meritList.getPhy(), "Phy");
                                mcount++;
                            }
                        }
                        for (int l = 0; l < streams.size(); l++) {
                            if (meritList.getStream().equals(streams.get(l).getStream())) {

                                streamPoints.setStream(streams.get(l).getStream());
                                streamPoints.setPoints(this.getSubjectPoints(meritList.getPhy(), "Phy"));
                                streamPoints.setCount(1);

                                streamsMeanPoints.add(streamPoints);
                            }
                        }

                        break;

                    case "Hist":
                        if (meritList.getHist() > 0) {
                            totalPoints += this.getSubjectPoints(meritList.getHist(), "Hist");
                            count++;
                        }
                        if (meritList.getGender().equals("Female")) {
                            if (meritList.getHist() > 0) {
                                femalePoints += this.getSubjectPoints(meritList.getHist(), "Hist");
                                fcount++;
                            }
                        } else if (meritList.getGender().equals("Male")) {
                            if (meritList.getHist() > 0) {
                                malePoints += this.getSubjectPoints(meritList.getHist(), "Hist");
                                mcount++;
                            }
                        }
                        for (int l = 0; l < streams.size(); l++) {
                            if (meritList.getStream().equals(streams.get(l).getStream())) {

                                streamPoints.setStream(streams.get(l).getStream());
                                streamPoints.setPoints(this.getSubjectPoints(meritList.getHist(), "Hist"));
                                streamPoints.setCount(1);

                                streamsMeanPoints.add(streamPoints);
                            }
                        }

                        break;

                    case "C.R.E":
                        if (meritList.getCre() > 0) {
                            totalPoints += this.getSubjectPoints(meritList.getCre(), "C.R.E");
                            count++;
                        }
                        if (meritList.getGender().equals("Female")) {
                            if (meritList.getCre() > 0) {
                                femalePoints += this.getSubjectPoints(meritList.getCre(), "C.R.E");
                                fcount++;
                            }
                        } else if (meritList.getGender().equals("Male")) {
                            if (meritList.getCre() > 0) {
                                malePoints += this.getSubjectPoints(meritList.getCre(), "C.R.E");
                                mcount++;
                            }
                        }
                        for (int l = 0; l < streams.size(); l++) {
                            if (meritList.getStream().equals(streams.get(l).getStream())) {

                                streamPoints.setStream(streams.get(l).getStream());
                                streamPoints.setPoints(this.getSubjectPoints(meritList.getCre(), "C.R.E"));
                                streamPoints.setCount(1);

                                streamsMeanPoints.add(streamPoints);
                            }
                        }

                        break;

                    case "Geo":
                        if (meritList.getGeo() > 0) {
                            totalPoints += this.getSubjectPoints(meritList.getGeo(), "Geo");
                            count++;
                        }
                        if (meritList.getGender().equals("Female")) {
                            if (meritList.getGeo() > 0) {
                                femalePoints += this.getSubjectPoints(meritList.getGeo(), "Geo");
                                fcount++;
                            }
                        } else if (meritList.getGender().equals("Male")) {
                            if (meritList.getGeo() > 0) {
                                malePoints += this.getSubjectPoints(meritList.getGeo(), "Geo");
                                mcount++;
                            }
                        }
                        for (int l = 0; l < streams.size(); l++) {
                            if (meritList.getStream().equals(streams.get(l).getStream())) {

                                streamPoints.setStream(streams.get(l).getStream());
                                streamPoints.setPoints(this.getSubjectPoints(meritList.getGeo(), "Geo"));
                                streamPoints.setCount(1);

                                streamsMeanPoints.add(streamPoints);
                            }
                        }

                        break;

                    case "I.R.E":
                        if (meritList.getIre() > 0) {
                            totalPoints += this.getSubjectPoints(meritList.getIre(), "I.R.E");
                            count++;
                        }
                        if (meritList.getGender().equals("Female")) {
                            if (meritList.getIre() > 0) {
                                femalePoints += this.getSubjectPoints(meritList.getIre(), "I.R.E");
                                fcount++;
                            }
                        } else if (meritList.getGender().equals("Male")) {
                            if (meritList.getIre() > 0) {
                                malePoints += this.getSubjectPoints(meritList.getIre(), "I.R.E");
                                mcount++;
                            }
                        }
                        for (int l = 0; l < streams.size(); l++) {
                            if (meritList.getStream().equals(streams.get(l).getStream())) {

                                streamPoints.setStream(streams.get(l).getStream());
                                streamPoints.setPoints(this.getSubjectPoints(meritList.getIre(), "I.R.E"));
                                streamPoints.setCount(1);

                                streamsMeanPoints.add(streamPoints);
                            }
                        }

                        break;

                    case "H.R.E":
                        if (meritList.getHre() > 0) {
                            totalPoints += this.getSubjectPoints(meritList.getHre(), "H.R.E");
                            count++;
                        }
                        if (meritList.getGender().equals("Female")) {
                            if (meritList.getHre() > 0) {
                                femalePoints += this.getSubjectPoints(meritList.getHre(), "H.R.E");
                                fcount++;
                            }
                        } else if (meritList.getGender().equals("Male")) {
                            if (meritList.getHre() > 0) {
                                malePoints += this.getSubjectPoints(meritList.getHre(), "H.R.E");
                                mcount++;
                            }
                        }
                        for (int l = 0; l < streams.size(); l++) {
                            if (meritList.getStream().equals(streams.get(l).getStream())) {

                                streamPoints.setStream(streams.get(l).getStream());
                                streamPoints.setPoints(this.getSubjectPoints(meritList.getHre(), "H.R.E"));
                                streamPoints.setCount(1);

                                streamsMeanPoints.add(streamPoints);
                            }
                        }

                        break;

                    case "Hsci":
                        if (meritList.getHsci() > 0) {
                            totalPoints += this.getSubjectPoints(meritList.getHsci(), "Hsci");
                            count++;
                        }
                        if (meritList.getGender().equals("Female")) {
                            if (meritList.getHsci() > 0) {
                                femalePoints += this.getSubjectPoints(meritList.getHsci(), "Hsci");
                                fcount++;
                            }
                        } else if (meritList.getGender().equals("Male")) {
                            if (meritList.getHsci() > 0) {
                                malePoints += this.getSubjectPoints(meritList.getHsci(), "Hsci");
                                mcount++;
                            }
                        }
                        for (int l = 0; l < streams.size(); l++) {
                            if (meritList.getStream().equals(streams.get(l).getStream())) {

                                streamPoints.setStream(streams.get(l).getStream());
                                streamPoints.setPoints(this.getSubjectPoints(meritList.getHsci(), "Hsci"));
                                streamPoints.setCount(1);

                                streamsMeanPoints.add(streamPoints);
                            }
                        }

                        break;

                    case "AnD":
                        if (meritList.getAnD() > 0) {
                            totalPoints += this.getSubjectPoints(meritList.getAnD(), "AnD");
                            count++;
                        }
                        if (meritList.getGender().equals("Female")) {
                            if (meritList.getAnD() > 0) {
                                femalePoints += this.getSubjectPoints(meritList.getAnD(), "AnD");
                                fcount++;
                            }
                        } else if (meritList.getGender().equals("Male")) {
                            if (meritList.getAnD() > 0) {
                                malePoints += this.getSubjectPoints(meritList.getAnD(), "AnD");
                                mcount++;
                            }
                        }
                        for (int l = 0; l < streams.size(); l++) {
                            if (meritList.getStream().equals(streams.get(l).getStream())) {

                                streamPoints.setStream(streams.get(l).getStream());
                                streamPoints.setPoints(this.getSubjectPoints(meritList.getAnD(), "AnD"));
                                streamPoints.setCount(1);

                                streamsMeanPoints.add(streamPoints);
                            }
                        }

                        break;

                    case "Agric":
                        if (meritList.getAgric() > 0) {
                            totalPoints += this.getSubjectPoints(meritList.getAgric(), "Agric");
                            count++;
                        }
                        if (meritList.getGender().equals("Female")) {
                            if (meritList.getAgric() > 0) {
                                femalePoints += this.getSubjectPoints(meritList.getAgric(), "Agric");
                                fcount++;
                            }
                        } else if (meritList.getGender().equals("Male")) {
                            if (meritList.getAgric() > 0) {
                                malePoints += this.getSubjectPoints(meritList.getAgric(), "Agric");
                                mcount++;
                            }
                        }
                        for (int l = 0; l < streams.size(); l++) {
                            if (meritList.getStream().equals(streams.get(l).getStream())) {

                                streamPoints.setStream(streams.get(l).getStream());
                                streamPoints.setPoints(this.getSubjectPoints(meritList.getAgric(), "Agric"));
                                streamPoints.setCount(1);

                                streamsMeanPoints.add(streamPoints);
                            }
                        }

                        break;

                    case "Comp":
                        if (meritList.getComp() > 0) {
                            totalPoints += this.getSubjectPoints(meritList.getComp(), "Comp");
                            count++;
                        }
                        if (meritList.getGender().equals("Female")) {
                            if (meritList.getComp() > 0) {
                                femalePoints += this.getSubjectPoints(meritList.getComp(), "Comp");
                                fcount++;
                            }
                        } else if (meritList.getGender().equals("Male")) {
                            if (meritList.getComp() > 0) {
                                malePoints += this.getSubjectPoints(meritList.getComp(), "Comp");
                                mcount++;
                            }
                        }
                        for (int l = 0; l < streams.size(); l++) {
                            if (meritList.getStream().equals(streams.get(l).getStream())) {

                                streamPoints.setStream(streams.get(l).getStream());
                                streamPoints.setPoints(this.getSubjectPoints(meritList.getComp(), "Comp"));
                                streamPoints.setCount(1);

                                streamsMeanPoints.add(streamPoints);
                            }
                        }

                        break;

                    case "Avi":
                        if (admNumbers.contains(meritList.getAdmNo())) {
                            if (meritList.getAvi() > 0) {
                                totalPoints += this.getSubjectPoints(meritList.getAvi(), "Avi");
                                count++;
                            }
                        }
                        if (meritList.getGender().equals("Female")) {
                            if (meritList.getAvi() > 0) {
                                femalePoints += this.getSubjectPoints(meritList.getAvi(), "Avi");
                                fcount++;
                            }
                        } else if (meritList.getGender().equals("Male")) {
                            if (meritList.getAvi() > 0) {
                                malePoints += this.getSubjectPoints(meritList.getAvi(), "Avi");
                                mcount++;
                            }
                        }
                        for (int l = 0; l < streams.size(); l++) {
                            if (meritList.getStream().equals(streams.get(l).getStream())) {

                                streamPoints.setStream(streams.get(l).getStream());
                                streamPoints.setPoints(this.getSubjectPoints(meritList.getAvi(), "Avi"));
                                streamPoints.setCount(1);

                                streamsMeanPoints.add(streamPoints);
                            }
                        }

                        break;

                    case "Elec":
                        if (meritList.getElec() > 0) {
                            totalPoints += this.getSubjectPoints(meritList.getElec(), "Elec");
                            count++;
                        }
                        if (meritList.getGender().equals("Female")) {
                            if (meritList.getElec() > 0) {
                                femalePoints += this.getSubjectPoints(meritList.getElec(), "Elec");
                                fcount++;
                            }
                        } else if (meritList.getGender().equals("Male")) {
                            if (meritList.getElec() > 0) {
                                malePoints += this.getSubjectPoints(meritList.getElec(), "Elec");
                                mcount++;
                            }
                        }
                        for (int l = 0; l < streams.size(); l++) {
                            if (meritList.getStream().equals(streams.get(l).getStream())) {

                                streamPoints.setStream(streams.get(l).getStream());
                                streamPoints.setPoints(this.getSubjectPoints(meritList.getElec(), "Elec"));
                                streamPoints.setCount(1);

                                streamsMeanPoints.add(streamPoints);
                            }
                        }

                        break;

                    case "Pwr":
                        if (meritList.getPwr() > 0) {
                            totalPoints += this.getSubjectPoints(meritList.getPwr(), "Pwr");
                            count++;
                        }
                        if (meritList.getGender().equals("Female")) {
                            if (meritList.getPwr() > 0) {
                                femalePoints += this.getSubjectPoints(meritList.getPwr(), "Pwr");
                                fcount++;
                            }
                        } else if (meritList.getGender().equals("Male")) {
                            if (meritList.getPwr() > 0) {
                                malePoints += this.getSubjectPoints(meritList.getPwr(), "Pwr");
                                mcount++;
                            }
                        }
                        for (int l = 0; l < streams.size(); l++) {
                            if (meritList.getStream().equals(streams.get(l).getStream())) {

                                streamPoints.setStream(streams.get(l).getStream());
                                streamPoints.setPoints(this.getSubjectPoints(meritList.getPwr(), "Pwr"));
                                streamPoints.setCount(1);

                                streamsMeanPoints.add(streamPoints);
                            }
                        }

                        break;

                    case "Wood":
                        if (meritList.getWood() > 0) {
                            totalPoints += this.getSubjectPoints(meritList.getWood(), "Wood");
                            count++;
                        }
                        if (meritList.getGender().equals("Female")) {
                            if (meritList.getWood() > 0) {
                                femalePoints += this.getSubjectPoints(meritList.getWood(), "Wood");
                                fcount++;
                            }
                        } else if (meritList.getGender().equals("Male")) {
                            if (meritList.getWood() > 0) {
                                malePoints += this.getSubjectPoints(meritList.getWood(), "Wood");
                                mcount++;
                            }
                        }
                        for (int l = 0; l < streams.size(); l++) {
                            if (meritList.getStream().equals(streams.get(l).getStream())) {

                                streamPoints.setStream(streams.get(l).getStream());
                                streamPoints.setPoints(this.getSubjectPoints(meritList.getWood(), "Wood"));
                                streamPoints.setCount(1);

                                streamsMeanPoints.add(streamPoints);
                            }
                        }

                        break;

                    case "Metal":
                        if (meritList.getMetal() > 0) {
                            totalPoints += this.getSubjectPoints(meritList.getMetal(), "Metal");
                            count++;
                        }
                        if (meritList.getGender().equals("Female")) {
                            if (meritList.getMetal() > 0) {
                                femalePoints += this.getSubjectPoints(meritList.getMetal(), "Metal");
                                fcount++;
                            }
                        } else if (meritList.getGender().equals("Male")) {
                            if (meritList.getMetal() > 0) {
                                malePoints += this.getSubjectPoints(meritList.getMetal(), "Metal");
                                mcount++;
                            }
                        }
                        for (int l = 0; l < streams.size(); l++) {
                            if (meritList.getStream().equals(streams.get(l).getStream())) {

                                streamPoints.setStream(streams.get(l).getStream());
                                streamPoints.setPoints(this.getSubjectPoints(meritList.getMetal(), "Metal"));
                                streamPoints.setCount(1);

                                streamsMeanPoints.add(streamPoints);
                            }
                        }

                        break;

                    case "Bc":
                        if (meritList.getBc() > 0) {
                            totalPoints += this.getSubjectPoints(meritList.getBc(), "Bc");
                            count++;
                        }
                        if (meritList.getGender().equals("Female")) {
                            if (meritList.getBc() > 0) {
                                femalePoints += this.getSubjectPoints(meritList.getBc(), "Bc");
                                fcount++;
                            }
                        } else if (meritList.getGender().equals("Male")) {
                            if (meritList.getBc() > 0) {
                                malePoints += this.getSubjectPoints(meritList.getBc(), "Bc");
                                mcount++;
                            }
                        }
                        for (int l = 0; l < streams.size(); l++) {
                            if (meritList.getStream().equals(streams.get(l).getStream())) {

                                streamPoints.setStream(streams.get(l).getStream());
                                streamPoints.setPoints(this.getSubjectPoints(meritList.getBc(), "Bc"));
                                streamPoints.setCount(1);

                                streamsMeanPoints.add(streamPoints);
                            }
                        }

                        break;

                    case "Fren":
                        if (meritList.getFren() > 0) {
                            totalPoints += this.getSubjectPoints(meritList.getFren(), "Fren");
                            count++;
                        }
                        if (meritList.getGender().equals("Female")) {
                            if (meritList.getFren() > 0) {
                                femalePoints += this.getSubjectPoints(meritList.getFren(), "Fren");
                                fcount++;
                            }
                        } else if (meritList.getGender().equals("Male")) {
                            if (meritList.getFren() > 0) {
                                malePoints += this.getSubjectPoints(meritList.getFren(), "Fren");
                                mcount++;
                            }
                        }
                        for (int l = 0; l < streams.size(); l++) {
                            if (meritList.getStream().equals(streams.get(l).getStream())) {

                                streamPoints.setStream(streams.get(l).getStream());
                                streamPoints.setPoints(this.getSubjectPoints(meritList.getFren(), "Fren"));
                                streamPoints.setCount(1);

                                streamsMeanPoints.add(streamPoints);
                            }
                        }

                        break;

                    case "Germ":
                        if (meritList.getGerm() > 0) {
                            totalPoints += this.getSubjectPoints(meritList.getGerm(), "Germ");
                            count++;
                        }
                        if (meritList.getGender().equals("Female")) {
                            if (meritList.getGerm() > 0) {
                                femalePoints += this.getSubjectPoints(meritList.getGerm(), "Germ");
                                fcount++;
                            }
                        } else if (meritList.getGender().equals("Male")) {
                            if (meritList.getGerm() > 0) {
                                malePoints += this.getSubjectPoints(meritList.getGerm(), "Germ");
                                mcount++;
                            }
                        }
                        for (int l = 0; l < streams.size(); l++) {
                            if (meritList.getStream().equals(streams.get(l).getStream())) {

                                streamPoints.setStream(streams.get(l).getStream());
                                streamPoints.setPoints(this.getSubjectPoints(meritList.getGerm(), "Germ"));
                                streamPoints.setCount(1);

                                streamsMeanPoints.add(streamPoints);
                            }
                        }

                        break;

                    case "Arab":
                        if (meritList.getArab() > 0) {
                            totalPoints += this.getSubjectPoints(meritList.getArab(), "Arab");
                            count++;
                        }
                        if (meritList.getGender().equals("Female")) {
                            if (meritList.getArab() > 0) {
                                femalePoints += this.getSubjectPoints(meritList.getArab(), "Arab");
                                fcount++;
                            }
                        } else if (meritList.getGender().equals("Male")) {
                            if (meritList.getArab() > 0) {
                                malePoints += this.getSubjectPoints(meritList.getArab(), "Arab");
                                mcount++;
                            }
                        }
                        for (int l = 0; l < streams.size(); l++) {
                            if (meritList.getStream().equals(streams.get(l).getStream())) {

                                streamPoints.setStream(streams.get(l).getStream());
                                streamPoints.setPoints(this.getSubjectPoints(meritList.getArab(), "Arab"));
                                streamPoints.setCount(1);

                                streamsMeanPoints.add(streamPoints);
                            }
                        }

                        break;

                    case "Msc":
                        if (meritList.getMsc() > 0) {
                            totalPoints += this.getSubjectPoints(meritList.getMsc(), "Msc");
                            count++;
                        }
                        if (meritList.getGender().equals("Female")) {
                            if (meritList.getMsc() > 0) {
                                femalePoints += this.getSubjectPoints(meritList.getMsc(), "Msc");
                                fcount++;
                            }
                        } else if (meritList.getGender().equals("Male")) {
                            if (meritList.getMsc() > 0) {
                                malePoints += this.getSubjectPoints(meritList.getMsc(), "Msc");
                                mcount++;
                            }
                        }
                        for (int l = 0; l < streams.size(); l++) {
                            if (meritList.getStream().equals(streams.get(l).getStream())) {

                                streamPoints.setStream(streams.get(l).getStream());
                                streamPoints.setPoints(this.getSubjectPoints(meritList.getMsc(), "Msc"));
                                streamPoints.setCount(1);

                                streamsMeanPoints.add(streamPoints);
                            }
                        }

                        break;

                    case "Bs":
                        if (meritList.getBs() > 0) {
                            totalPoints += this.getSubjectPoints(meritList.getBs(), "Bs");
                            count++;
                        }
                        if (meritList.getGender().equals("Female")) {
                            if (meritList.getBs() > 0) {
                                femalePoints += this.getSubjectPoints(meritList.getBs(), "Bs");
                                fcount++;
                            }
                        } else if (meritList.getGender().equals("Male")) {
                            if (meritList.getBs() > 0) {
                                malePoints += this.getSubjectPoints(meritList.getBs(), "Bs");
                                mcount++;
                            }
                        }
                        for (int l = 0; l < streams.size(); l++) {
                            if (meritList.getStream().equals(streams.get(l).getStream())) {

                                streamPoints.setStream(streams.get(l).getStream());
                                streamPoints.setPoints(this.getSubjectPoints(meritList.getBs(), "Bs"));
                                streamPoints.setCount(1);

                                streamsMeanPoints.add(streamPoints);
                            }
                        }

                        break;

                    case "Dnd":
                        if (meritList.getDnd() > 0) {
                            totalPoints += this.getSubjectPoints(meritList.getDnd(), "Dnd");
                            count++;
                        }
                        if (meritList.getGender().equals("Female")) {
                            if (meritList.getDnd() > 0) {
                                femalePoints += this.getSubjectPoints(meritList.getDnd(), "Dnd");
                                fcount++;
                            }
                        } else if (meritList.getGender().equals("Male")) {
                            if (meritList.getDnd() > 0) {
                                malePoints += this.getSubjectPoints(meritList.getDnd(), "Dnd");
                                mcount++;
                            }
                        }
                        for (int l = 0; l < streams.size(); l++) {
                            if (meritList.getStream().equals(streams.get(l).getStream())) {

                                streamPoints.setStream(streams.get(l).getStream());
                                streamPoints.setPoints(this.getSubjectPoints(meritList.getDnd(), "Dnd"));
                                streamPoints.setCount(1);

                                streamsMeanPoints.add(streamPoints);
                            }
                        }

                        break;

                }

            }

            overallStreamPoints.addAll(streamsMeanPoints);
            overallTotalPoints += totalPoints;

            overallFemalePoints += femalePoints;
            overallMalePoints += malePoints;

            DecimalFormat df = new DecimalFormat("#.####");
            float avg = (float) 0.000;
            if (count != 0) {
                avg = (float) totalPoints / count;
            }

            float favg = (float) 0.000;
            if (fcount != 0) {
                favg = (float) femalePoints / fcount;
            }

            float mavg = (float) 0.000;
            if (mcount != 0) {
                mavg = (float) malePoints / mcount;
            }

            for (Stream stream : streams) {
                int sPoints = 0;
                int sCount = 0;

                for (StreamPoints streamsMeanPoint : streamsMeanPoints) {
                    if (stream.getStream().equals(streamsMeanPoint.getStream())) {
                        if (streamsMeanPoint.getPoints() > 0) {
                            sPoints += streamsMeanPoint.getPoints();
                            sCount += streamsMeanPoint.getCount();
                        }
                    }
                }

                float savg = (float) 0.000;
                if (sCount > 0) {
                    savg = (float) sPoints / sCount;
                }

                if (subjects.get(j).getInitials().equals("C.R.E")) {
                    model.addAttribute("cre" + stream.getStream() + "MeanPoints", Float.valueOf(df.format(savg)));
                } else if (subjects.get(j).getInitials().equals("H.R.E")) {
                    model.addAttribute("hre" + stream.getStream() + "MeanPoints", Float.valueOf(df.format(savg)));
                } else if (subjects.get(j).getInitials().equals("I.R.E")) {
                    model.addAttribute("ire" + stream.getStream() + "MeanPoints", Float.valueOf(df.format(savg)));
                } else if (subjects.get(j).getInitials() != "C.R.E" && subjects.get(j).getInitials() != "H.R.E" && subjects.get(j).getInitials() != "I.R.E") {
                    model.addAttribute(subjects.get(j).getInitials() + stream.getStream() + "MeanPoints", Float.valueOf(df.format(savg)));
                }

            }

            if (subjects.get(j).getInitials().equals("C.R.E")) {
                model.addAttribute("creMeanPoints", Float.valueOf(df.format(avg)));
                model.addAttribute("creFemaleMeanPoints", Float.valueOf(df.format(favg)));
                model.addAttribute("creMaleMeanPoints", Float.valueOf(df.format(mavg)));
            } else if (subjects.get(j).getInitials().equals("H.R.E")) {
                model.addAttribute("hreMeanPoints", Float.valueOf(df.format(avg)));
                model.addAttribute("hreFemaleMeanPoints", Float.valueOf(df.format(favg)));
                model.addAttribute("hreMaleMeanPoints", Float.valueOf(df.format(mavg)));
            } else if (subjects.get(j).getInitials().equals("I.R.E")) {
                model.addAttribute("ireMeanPoints", Float.valueOf(df.format(avg)));
                model.addAttribute("ireFemaleMeanPoints", Float.valueOf(df.format(favg)));
                model.addAttribute("ireMaleMeanPoints", Float.valueOf(df.format(mavg)));
            } else if (subjects.get(j).getInitials() != "C.R.E" && subjects.get(j).getInitials() != "H.R.E" && subjects.get(j).getInitials() != "I.R.E") {
                model.addAttribute(subjects.get(j).getInitials() + "MeanPoints", Float.valueOf(df.format(avg)));
                model.addAttribute(subjects.get(j).getInitials() + "FemaleMeanPoints", Float.valueOf(df.format(favg)));
                model.addAttribute(subjects.get(j).getInitials() + "MaleMeanPoints", Float.valueOf(df.format(mavg)));
            }

        }

        DecimalFormat df = new DecimalFormat("#.####");

        float avg = (float) 0.000;
        if (overallCount != 0) {
            avg = (float) overallTotalPoints / overallCount;
        }

        float mavg = (float) 0.000;
        if (overallMCount != 0) {
            mavg = (float) overallMalePoints / overallMCount;
        }

        float favg = (float) 0.000;
        if (overallFCount != 0) {
            favg = (float) overallFemalePoints / overallFCount;
        }

        model.addAttribute("Overall", Float.valueOf(df.format(mavg)));
        model.addAttribute("overallMalePoints", Float.valueOf(df.format(mavg)));
        model.addAttribute("overallFemalePoints", Float.valueOf(df.format(favg)));

        for (Stream stream : streams) {
            int oPoints = 0;
            int sCount = 0;
            for (StreamPoints streamsMeanPoint : overallStreamPoints) {
                if (stream.getStream().equals(streamsMeanPoint.getStream())) {
                    if (streamsMeanPoint.getPoints() > 0) {
                        oPoints += streamsMeanPoint.getPoints();
                        sCount += streamsMeanPoint.getCount();
                    }
                }
            }

            float savg = (float) 0.000;
            if (sCount > 0) {
                savg = (float) oPoints / sCount;
            }

            model.addAttribute(stream.getStream() + "StreamOverall", Float.valueOf(df.format(savg)));

        }

        List<TeacherYearFormStream> teachersYearFormStream = new ArrayList<>();
        for (int k = 0; k < streams.size(); k++) {
            List<TeacherYearFormStream> teachers = this.teacherYearFormStreamService.getAllTeachersTeachingInYearFormAndStream(code, year, form, streams.get(k).getId());
            teachersYearFormStream.addAll(teachers);
        }
        model.addAttribute("teachers", teachersYearFormStream);

        for (i2 = 0; i2 < studentsWithoutMarks.size(); ++i2) {
            MeritList meritList = new MeritList();
            meritList.setFirstname(studentsWithoutMarks.get(i2).getFirstname());
            meritList.setSecondname(studentsWithoutMarks.get(i2).getThirdname());
            meritList.setAdmNo(studentsWithoutMarks.get(i2).getAdmNo());
            meritList.setKcpe(studentsWithoutMarks.get(i2).getKcpeMarks());
            meritList.setStream(studentsWithoutMarks.get(i2).getStream().getStream());
            meritList.setTotal(0);
            gradeCount = new GradeCount();
            gradeCount.setFirstname(studentsWithoutMarks.get(i2).getFirstname());
            gradeCount.setSecondname(studentsWithoutMarks.get(i2).getThirdname());
            gradeCount.setAdmNo(studentsWithoutMarks.get(i2).getAdmNo());
            meritLists.add(meritList);
            gradeCounts.add(gradeCount);
        }
        String[] grades = new String[]{"A", "Am", "Bp", "B", "Bm", "Cp", "C", "Cm", "Dp", "D", "Dm", "E"};
        String[] gds = new String[]{"A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "D-", "E"};
        model.addAttribute("grades", grades);
        model.addAttribute("gradeCounts", gradeCounts);
        Collections.sort(meritLists, new SortByDeviation().reversed());
        if (meritLists.size() > 0) {
            model.addAttribute("mostImproved", meritLists.get(0));
        }
        List<MeritList> mathsMerits = this.getSubjectMeritList(code, year, form, term, "Maths", meritLists);
        Collections.sort(mathsMerits, new SortByMaths().reversed());
        if (mathsMerits.size() > 0) {

            List<MeritList> mathsGiant = new ArrayList<>();

            int mostMarks = mathsMerits.get(0).getMaths();
            for (int j = 0; j < mathsMerits.size(); j++) {
                if (mathsMerits.get(j).getMaths() == mostMarks) {
                    mathsGiant.add(mathsMerits.get(j));
                }
            }
            model.addAttribute("MathsGiants", mathsGiant);
        }
        List<MeritList> engMerits = this.getSubjectMeritList(code, year, form, term, "Eng", meritLists);
        Collections.sort(engMerits, new SortByEng().reversed());
        if (engMerits.size() > 0) {

            List<MeritList> engGiant = new ArrayList<>();

            int mostMarks = engMerits.get(0).getEng();
            for (int j = 0; j < engMerits.size(); j++) {
                if (engMerits.get(j).getEng() == mostMarks) {
                    engGiant.add(engMerits.get(j));
                }
            }
            model.addAttribute("EngGiants", engGiant);
        }
        List<MeritList> kisMerits = this.getSubjectMeritList(code, year, form, term, "Kis", meritLists);
        Collections.sort(kisMerits, new SortByKis().reversed());
        if (kisMerits.size() > 0) {

            List<MeritList> kisGiant = new ArrayList<>();

            int mostMarks = kisMerits.get(0).getKis();
            for (int j = 0; j < kisMerits.size(); j++) {
                if (kisMerits.get(j).getKis() == mostMarks) {
                    kisGiant.add(kisMerits.get(j));
                }
            }
            model.addAttribute("KisGiants", kisGiant);

        }
        List<MeritList> bioMerits = this.getSubjectMeritList(code, year, form, term, "Bio", meritLists);
        Collections.sort(bioMerits, new SortByBio().reversed());
        if (bioMerits.size() > 0) {

            List<MeritList> bioGiant = new ArrayList<>();

            int mostMarks = bioMerits.get(0).getBio();
            for (int j = 0; j < bioMerits.size(); j++) {
                if (bioMerits.get(j).getBio() == mostMarks) {
                    bioGiant.add(bioMerits.get(j));
                }
            }
            model.addAttribute("BioGiants", bioGiant);

        }
        List<MeritList> chemMerits = this.getSubjectMeritList(code, year, form, term, "Chem", meritLists);
        Collections.sort(chemMerits, new SortByChem().reversed());
        if (chemMerits.size() > 0) {

            List<MeritList> chemGiant = new ArrayList<>();

            int mostMarks = chemMerits.get(0).getChem();
            for (int j = 0; j < chemMerits.size(); j++) {
                if (chemMerits.get(j).getChem() == mostMarks) {
                    chemGiant.add(chemMerits.get(j));
                }
            }
            model.addAttribute("ChemGiants", chemGiant);
        }
        List<MeritList> phyMerits = this.getSubjectMeritList(code, year, form, term, "Phy", meritLists);
        Collections.sort(phyMerits, new SortByPhy().reversed());
        if (phyMerits.size() > 0) {

            List<MeritList> phyGiant = new ArrayList<>();

            int mostMarks = phyMerits.get(0).getPhy();
            for (int j = 0; j < phyMerits.size(); j++) {
                if (phyMerits.get(j).getPhy() == mostMarks) {
                    phyGiant.add(phyMerits.get(j));
                }
            }
            model.addAttribute("PhyGiants", phyGiant);
        }
        List<MeritList> histMerits = this.getSubjectMeritList(code, year, form, term, "Hist", meritLists);
        Collections.sort(histMerits, new SortByHist().reversed());
        if (histMerits.size() > 0) {

            List<MeritList> histGiant = new ArrayList<>();

            int mostMarks = histMerits.get(0).getHist();
            for (int j = 0; j < histMerits.size(); j++) {
                if (histMerits.get(j).getHist() == mostMarks) {
                    histGiant.add(histMerits.get(j));
                }
            }
            model.addAttribute("HistGiants", histGiant);

        }

        List<MeritList> creMerits = this.getSubjectMeritList(code, year, form, term, "C.R.E", meritLists);
        Collections.sort(creMerits, new SortByCre().reversed());
        if (creMerits.size() > 0) {

            List<MeritList> creGiant = new ArrayList<>();

            int mostMarks = creMerits.get(0).getCre();

            for (int j = 0; j < creMerits.size(); j++) {
                if (creMerits.get(j).getCre() == mostMarks) {
                    creGiant.add(creMerits.get(j));
                }
            }
            model.addAttribute("CreGiants", creGiant);

        }

        List<MeritList> geoMerits = this.getSubjectMeritList(code, year, form, term, "Geo", meritLists);
        Collections.sort(geoMerits, new SortByGeo().reversed());
        if (geoMerits.size() > 0) {

            List<MeritList> geoGiant = new ArrayList<>();

            int mostMarks = geoMerits.get(0).getGeo();
            for (int j = 0; j < geoMerits.size(); j++) {
                if (geoMerits.get(j).getGeo() == mostMarks) {
                    geoGiant.add(geoMerits.get(j));
                }
            }
            model.addAttribute("GeoGiants", geoGiant);

        }

        List<MeritList> ireMerits = this.getSubjectMeritList(code, year, form, term, "I.R.E", meritLists);
        Collections.sort(ireMerits, new SortByIre().reversed());
        if (ireMerits.size() > 0) {

            List<MeritList> ireGiant = new ArrayList<>();

            int mostMarks = ireMerits.get(0).getIre();
            for (int j = 0; j < ireMerits.size(); j++) {
                if (ireMerits.get(j).getIre() == mostMarks) {
                    ireGiant.add(ireMerits.get(j));
                }
            }
            model.addAttribute("IreGiants", ireGiant);

        }
        List<MeritList> hreMerits = this.getSubjectMeritList(code, year, form, term, "H.R.E", meritLists);
        Collections.sort(hreMerits, new SortByHre().reversed());
        if (hreMerits.size() > 0) {

            List<MeritList> hreGiant = new ArrayList<>();

            int mostMarks = hreMerits.get(0).getHre();
            for (int j = 0; j < hreMerits.size(); j++) {
                if (hreMerits.get(j).getHre() == mostMarks) {
                    hreGiant.add(hreMerits.get(j));
                }
            }
            model.addAttribute("HreGiants", hreGiant);

        }
        List<MeritList> hsciMerits = this.getSubjectMeritList(code, year, form, term, "Hsci", meritLists);
        Collections.sort(hsciMerits, new SortByHsci().reversed());
        if (hsciMerits.size() > 0) {

            List<MeritList> hsciGiant = new ArrayList<>();

            int mostMarks = hsciMerits.get(0).getHsci();
            for (int j = 0; j < hsciMerits.size(); j++) {
                if (hsciMerits.get(j).getHsci() == mostMarks) {
                    hsciGiant.add(hsciMerits.get(j));
                }
            }
            model.addAttribute("HsciGiants", hsciGiant);

        }
        List<MeritList> andMerits = this.getSubjectMeritList(code, year, form, term, "AnD", meritLists);
        Collections.sort(andMerits, new SortByAnd().reversed());
        if (andMerits.size() > 0) {

            List<MeritList> andGiant = new ArrayList<>();

            int mostMarks = andMerits.get(0).getAnD();
            for (int j = 0; j < andMerits.size(); j++) {
                if (andMerits.get(j).getAnD() == mostMarks) {
                    andGiant.add(andMerits.get(j));
                }
            }

            model.addAttribute("AnDGiants", andGiant);
        }

        Collections.sort(meritLists, new SortByAgric().reversed());
        if (meritLists.size() > 0) {

            List<MeritList> agricGiant = new ArrayList<>();

            int mostMarks = meritLists.get(0).getAgric();
            for (int j = 0; j < meritLists.size(); j++) {
                if (meritLists.get(j).getAgric() == mostMarks) {
                    agricGiant.add(meritLists.get(j));
                }
            }

            model.addAttribute("AgricGiants", agricGiant);
        }

        Collections.sort(meritLists, new SortByComp().reversed());
        if (meritLists.size() > 0) {

            List<MeritList> compGiant = new ArrayList<>();

            int mostMarks = meritLists.get(0).getComp();
            for (int j = 0; j < meritLists.size(); j++) {
                if (meritLists.get(j).getComp() == mostMarks) {
                    compGiant.add(meritLists.get(j));
                }
            }
            model.addAttribute("CompGiants", compGiant);
        }
        List<MeritList> aviMerits = this.getSubjectMeritList(code, year, form, term, "Avi", meritLists);
        Collections.sort(aviMerits, new SortByAvi().reversed());
        if (aviMerits.size() > 0) {

            List<MeritList> aviGiant = new ArrayList<>();

            int mostMarks = aviMerits.get(0).getAvi();
            for (int j = 0; j < aviMerits.size(); j++) {
                if (aviMerits.get(j).getAvi() == mostMarks) {
                    aviGiant.add(aviMerits.get(j));
                }
            }
            model.addAttribute("AviGiants", aviGiant);
        }
        List<MeritList> elecMerits = this.getSubjectMeritList(code, year, form, term, "Elec", meritLists);
        Collections.sort(elecMerits, new SortByElec().reversed());
        if (elecMerits.size() > 0) {

            List<MeritList> elecGiant = new ArrayList<>();

            int mostMarks = elecMerits.get(0).getElec();
            for (int j = 0; j < elecMerits.size(); j++) {
                if (elecMerits.get(j).getElec() == mostMarks) {
                    elecGiant.add(elecMerits.get(j));
                }
            }
            model.addAttribute("ElecGiants", elecGiant);
        }
        List<MeritList> pwrMerits = this.getSubjectMeritList(code, year, form, term, "Pwr", meritLists);
        Collections.sort(pwrMerits, new SortByPwr().reversed());
        if (pwrMerits.size() > 0) {

            List<MeritList> pwrGiant = new ArrayList<>();

            int mostMarks = pwrMerits.get(0).getPwr();
            for (int j = 0; j < pwrMerits.size(); j++) {
                if (pwrMerits.get(j).getPwr() == mostMarks) {
                    pwrGiant.add(pwrMerits.get(j));
                }
            }

            model.addAttribute("PwrGiants", pwrGiant);

        }
        List<MeritList> woodMerits = this.getSubjectMeritList(code, year, form, term, "Wood", meritLists);
        Collections.sort(woodMerits, new SortByWood().reversed());
        if (woodMerits.size() > 0) {

            List<MeritList> woodGiant = new ArrayList<>();

            int mostMarks = woodMerits.get(0).getWood();
            for (int j = 0; j < woodMerits.size(); j++) {
                if (woodMerits.get(j).getWood() == mostMarks) {
                    woodGiant.add(woodMerits.get(j));
                }
            }

            model.addAttribute("WoodGiants", woodGiant);
        }
        List<MeritList> metalMerits = this.getSubjectMeritList(code, year, form, term, "Metal", meritLists);
        Collections.sort(metalMerits, new SortByMetal().reversed());
        if (metalMerits.size() > 0) {

            List<MeritList> metalGiant = new ArrayList<>();

            int mostMarks = metalMerits.get(0).getMetal();
            for (int j = 0; j < metalMerits.size(); j++) {
                if (metalMerits.get(j).getMetal() == mostMarks) {
                    metalGiant.add(metalMerits.get(j));
                }
            }

            model.addAttribute("MetalGiants", metalGiant);
        }
        List<MeritList> bcMerits = this.getSubjectMeritList(code, year, form, term, "Bc", meritLists);
        Collections.sort(bcMerits, new SortByBc().reversed());
        if (bcMerits.size() > 0) {

            List<MeritList> bcGiant = new ArrayList<>();

            int mostMarks = bcMerits.get(0).getBc();
            for (int j = 0; j < bcMerits.size(); j++) {
                if (bcMerits.get(j).getBc() == mostMarks) {
                    bcGiant.add(bcMerits.get(j));
                }
            }

            model.addAttribute("BcGiants", bcGiant);
        }
        List<MeritList> frenMerits = this.getSubjectMeritList(code, year, form, term, "Fren", meritLists);
        Collections.sort(frenMerits, new SortByFren().reversed());
        if (frenMerits.size() > 0) {

            List<MeritList> frenGiant = new ArrayList<>();

            int mostMarks = frenMerits.get(0).getFren();
            for (int j = 0; j < frenMerits.size(); j++) {
                if (frenMerits.get(j).getFren() == mostMarks) {
                    frenGiant.add(frenMerits.get(j));
                }
            }

            model.addAttribute("FrenGiants", frenGiant);
        }
        List<MeritList> germMerits = this.getSubjectMeritList(code, year, form, term, "Germ", meritLists);
        Collections.sort(germMerits, new SortByGerm().reversed());
        if (germMerits.size() > 0) {

            List<MeritList> germGiant = new ArrayList<>();

            int mostMarks = germMerits.get(0).getGerm();
            for (int j = 0; j < germMerits.size(); j++) {
                if (germMerits.get(j).getGerm() == mostMarks) {
                    germGiant.add(germMerits.get(j));
                }
            }

            model.addAttribute("GermGiants", germGiant);
        }
        List<MeritList> arabMerits = this.getSubjectMeritList(code, year, form, term, "Arab", meritLists);
        Collections.sort(arabMerits, new SortByArab().reversed());
        if (arabMerits.size() > 0) {

            List<MeritList> arabGiant = new ArrayList<>();

            int mostMarks = arabMerits.get(0).getArab();
            for (int j = 0; j < arabMerits.size(); j++) {
                if (arabMerits.get(j).getArab() == mostMarks) {
                    arabGiant.add(arabMerits.get(j));
                }
            }

            model.addAttribute("ArabGiants", arabGiant);
        }
        List<MeritList> mscMerits = this.getSubjectMeritList(code, year, form, term, "Msc", meritLists);
        Collections.sort(mscMerits, new SortByMsc().reversed());
        if (mscMerits.size() > 0) {

            List<MeritList> mscGiant = new ArrayList<>();

            int mostMarks = mscMerits.get(0).getMsc();
            for (int j = 0; j < mscMerits.size(); j++) {
                if (mscMerits.get(j).getMsc() == mostMarks) {
                    mscGiant.add(mscMerits.get(j));
                }
            }

            model.addAttribute("MscGiants", mscGiant);
        }
        List<MeritList> bsMerits = this.getSubjectMeritList(code, year, form, term, "Bs", meritLists);
        Collections.sort(bsMerits, new SortByBs().reversed());
        if (bsMerits.size() > 0) {

            List<MeritList> bsGiant = new ArrayList<>();

            int mostMarks = bsMerits.get(0).getBs();
            for (int j = 0; j < bsMerits.size(); j++) {
                if (bsMerits.get(j).getBs() == mostMarks) {
                    bsGiant.add(bsMerits.get(j));
                }
            }

            model.addAttribute("BsGiants", bsGiant);
        }
        List<MeritList> dndMerits = this.getSubjectMeritList(code, year, form, term, "Dnd", meritLists);
        Collections.sort(dndMerits, new SortByDnd().reversed());
        if (dndMerits.size() > 0) {

            List<MeritList> dndGiant = new ArrayList<>();

            int mostMarks = dndMerits.get(0).getDnd();
            for (int j = 0; j < dndMerits.size(); j++) {
                if (dndMerits.get(j).getDnd() == mostMarks) {
                    dndGiant.add(dndMerits.get(j));
                }
            }

            model.addAttribute("DndGiants", dndGiant);
        }
        Collections.sort(meritLists, new SortByAverage().reversed());
        for (int j = 0; j < gds.length; ++j) {
            int totalS = 0;
            block148:
            for (Subject subject : subjects) {
                int counts = 0;
                switch (subject.getInitials()) {
                    case "Maths": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (gradeCounts.get(k).getMaths() != gds[j]) {
                                continue;
                            }
                            ++counts;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subject.getInitials() + "Count", counts);
                        continue block148;
                    }
                    case "Eng": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (gradeCounts.get(k).getEng() != gds[j]) {
                                continue;
                            }
                            ++counts;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subject.getInitials() + "Count", counts);
                        continue block148;
                    }
                    case "Kis": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (gradeCounts.get(k).getKis() != gds[j]) {
                                continue;
                            }
                            ++counts;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subject.getInitials() + "Count", counts);
                        continue block148;
                    }
                    case "Bio": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (gradeCounts.get(k).getBio() != gds[j]) {
                                continue;
                            }
                            ++counts;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subject.getInitials() + "Count", counts);
                        continue block148;
                    }
                    case "Chem": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (gradeCounts.get(k).getChem() != gds[j]) {
                                continue;
                            }
                            ++counts;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subject.getInitials() + "Count", counts);
                        continue block148;
                    }
                    case "Phy": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (gradeCounts.get(k).getPhy() != gds[j]) {
                                continue;
                            }
                            ++counts;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subject.getInitials() + "Count", counts);
                        continue block148;
                    }
                    case "Hist": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (gradeCounts.get(k).getHist() != gds[j]) {
                                continue;
                            }
                            ++counts;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subject.getInitials() + "Count", counts);
                        continue block148;
                    }
                    case "C.R.E": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (gradeCounts.get(k).getCre() != gds[j]) {
                                continue;
                            }
                            ++counts;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + "CreCount", counts);
                        continue block148;
                    }
                    case "Geo": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (gradeCounts.get(k).getGeo() != gds[j]) {
                                continue;
                            }
                            ++counts;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subject.getInitials() + "Count", counts);
                        continue block148;
                    }
                    case "I.R.E": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (gradeCounts.get(k).getIre() != gds[j]) {
                                continue;
                            }
                            ++counts;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + "IreCount", counts);
                        continue block148;
                    }
                    case "H.R.E": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (gradeCounts.get(k).getHre() != gds[j]) {
                                continue;
                            }
                            ++counts;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + "HreCount", counts);
                        continue block148;
                    }
                    case "Hsci": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (gradeCounts.get(k).getHsci() != gds[j]) {
                                continue;
                            }
                            ++counts;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subject.getInitials() + "Count", counts);
                        continue block148;
                    }
                    case "AnD": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (gradeCounts.get(k).getAnd() != gds[j]) {
                                continue;
                            }
                            ++counts;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subject.getInitials() + "Count", counts);
                        continue block148;
                    }
                    case "Agric": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (gradeCounts.get(k).getAgric() != gds[j]) {
                                continue;
                            }
                            ++counts;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subject.getInitials() + "Count", counts);
                        continue block148;
                    }
                    case "Comp": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (gradeCounts.get(k).getComp() != gds[j]) {
                                continue;
                            }
                            ++counts;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subject.getInitials() + "Count", counts);
                        continue block148;
                    }
                    case "Avi": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (gradeCounts.get(k).getAvi() != gds[j]) {
                                continue;
                            }
                            ++counts;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subject.getInitials() + "Count", counts);
                        continue block148;
                    }
                    case "Elec": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (gradeCounts.get(k).getElec() != gds[j]) {
                                continue;
                            }
                            ++counts;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subject.getInitials() + "Count", counts);
                        continue block148;
                    }
                    case "Pwr": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (gradeCounts.get(k).getPwr() != gds[j]) {
                                continue;
                            }
                            ++counts;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subject.getInitials() + "Count", counts);
                        continue block148;
                    }
                    case "Wood": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (gradeCounts.get(k).getWood() != gds[j]) {
                                continue;
                            }
                            ++counts;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subject.getInitials() + "Count", counts);
                        continue block148;
                    }
                    case "Metal": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (gradeCounts.get(k).getMetal() != gds[j]) {
                                continue;
                            }
                            ++counts;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subject.getInitials() + "Count", counts);
                        continue block148;
                    }
                    case "Bc": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (gradeCounts.get(k).getBc() != gds[j]) {
                                continue;
                            }
                            ++counts;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subject.getInitials() + "Count", counts);
                        continue block148;
                    }
                    case "Fren": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (gradeCounts.get(k).getFren() != gds[j]) {
                                continue;
                            }
                            ++counts;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subject.getInitials() + "Count", counts);
                        continue block148;
                    }
                    case "Germ": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (gradeCounts.get(k).getGerm() != gds[j]) {
                                continue;
                            }
                            ++counts;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subject.getInitials() + "Count", counts);
                        continue block148;
                    }
                    case "Arab": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (gradeCounts.get(k).getArab() != gds[j]) {
                                continue;
                            }
                            ++counts;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subject.getInitials() + "Count", counts);
                        continue block148;
                    }
                    case "Msc": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (gradeCounts.get(k).getMsc() != gds[j]) {
                                continue;
                            }
                            ++counts;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subject.getInitials() + "Count", counts);
                        continue block148;
                    }
                    case "Bs": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (gradeCounts.get(k).getBs() != gds[j]) {
                                continue;
                            }
                            ++counts;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subject.getInitials() + "Count", counts);
                        continue block148;
                    }
                    case "Dnd": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (gradeCounts.get(k).getDnd() != gds[j]) {
                                continue;
                            }
                            ++counts;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subject.getInitials() + "Count", counts);
                    }
                }
            }
            model.addAttribute(grades[j] + "Count", totalS);
        }
        for (i = 0; i < students.size(); ++i) {
            model.addAttribute("Points" + students.get(i).getAdmNo(), this.getPoints(students.get(i).getKcpeMarks() / 5));
        }

        Collections.sort(meritLists, new SortByAverage().reversed());

        int counter = 0;
        int rank = 0;

        for (int j = 0; j < meritLists.size(); j++) {
            counter++;
            rank++;
            if (counter > 1) {
                if (meritLists.get(j).getPoints() == meritLists.get(j - 1).getPoints()) {
                    counter--;
                } else {
                    counter = rank;
                }
            }
            meritLists.get(j).setRank(counter);
        }

        List<MeritList> topStudents = new ArrayList<>();
        List<MeritList> bottomStudents = new ArrayList<>();

        //Top Students
        if (meritLists.size() < 5) {
            for (MeritList meritList : meritLists) {
                if (meritList.getTotal() > 0) {
                    topStudents.add(meritList);
                }
            }
        } else if (meritLists.size() > 5) {
            for (int j = 0; j < 5; j++) {
                if (meritLists.get(j).getTotal() > 0) {
                    topStudents.add(meritLists.get(j));
                }
            }
        }

        //Bottom Students
        if (meritLists.size() > 10 && meritLists.size() < 15) {
            for (int j = meritLists.size() - 3; j < meritLists.size() - 1; j++) {
                    bottomStudents.add(meritLists.get(j));
                
            }
        } else if (meritLists.size() > 15 && meritLists.size() < 40) {
            for (int j = meritLists.size() - 5; j < meritLists.size() - 1; j++) {
                    bottomStudents.add(meritLists.get(j));
                    
            }
        } else if (meritLists.size() > 40) {
            for (int j = meritLists.size() - 10; j < meritLists.size() - 1; j++) {
                    bottomStudents.add(meritLists.get(j));
                
            }
        }

        //All student mean based on subjects
        model.addAttribute("topStudents", topStudents);
        model.addAttribute("bottomStudents", bottomStudents);
        model.addAttribute("meritLists", meritLists);
        model.addAttribute("activeUser", activeUser);
        model.addAttribute("school", school);
        model.addAttribute("student", student);
        model.addAttribute("year", year);
        model.addAttribute("form", form);
        model.addAttribute("term", term);
        model.addAttribute("marks", allMarks);
        model.addAttribute("subjects", subjects);
        model.addAttribute("streams", streams);
        model.addAttribute("students", students);
        model.addAttribute("studentsWithoutMarks", studentsWithoutMarks);
        model.addAttribute("MathsCount", mathsCount);
        model.addAttribute("EngCount", engCount);
        model.addAttribute("KisCount", kisCount);
        model.addAttribute("BioCount", bioCount);
        model.addAttribute("ChemCount", chemCount);
        model.addAttribute("PhyCount", phyCount);
        model.addAttribute("HistCount", histCount);
        model.addAttribute("creCount", creCount);
        model.addAttribute("GeoCount", geoCount);
        model.addAttribute("ireCount", ireCount);
        model.addAttribute("hreCount", hreCount);
        model.addAttribute("HsciCount", hsciCount);
        model.addAttribute("AnDCount", andCount);
        model.addAttribute("AgricCount", agricCount);
        model.addAttribute("CompCount", compCount);
        model.addAttribute("AviCount", aviCount);
        model.addAttribute("ElecCount", elecCount);
        model.addAttribute("PwrCount", pwrCount);
        model.addAttribute("WoodCount", woodCount);
        model.addAttribute("MetalCount", metalCount);
        model.addAttribute("BcCount", bcCount);
        model.addAttribute("FrenCount", frenCount);
        model.addAttribute("GermCount", germCount);
        model.addAttribute("ArabCount", arabCount);
        model.addAttribute("MscCount", mscCount);
        model.addAttribute("BsCount", bsCount);
        model.addAttribute("DndCount", dndCount);

        return "meritList";

    }

    public String getGrades(float mark) {
        if (mark <= 12 && mark >= 11.5) {
            return "A";
        }
        if (mark < 11.5 && mark >= 10.5) {
            return "A-";
        }
        if (mark < 10.5 && mark >= 9.5) {
            return "B+";
        }
        if (mark < 9.5 && mark >= 8.5) {
            return "B";
        }
        if (mark < 8.5 && mark >= 7.5) {
            return "B-";
        }
        if (mark < 7.5 && mark >= 6.5) {
            return "C+";
        }
        if (mark < 6.5 && mark >= 5.5) {
            return "C";
        }
        if (mark < 5.5 && mark >= 4.5) {
            return "C-";
        }
        if (mark < 4.5 && mark >= 3.5) {
            return "D+";
        }
        if (mark < 3.5 && mark >= 2.5) {
            return "D";
        }
        if (mark < 2.5 && mark >= 1.5) {
            return "D-";
        }
        if (mark < 1.5 && mark > 0) {
            return "E";
        }
        return "-";
    }

    public String getSubjectGrade(int mark, String initials) {

        if (initials.equals("Maths") || initials.equals("Bio") || initials.equals("Phy") || initials.equals("Chem")) {
            if (mark <= 100 && mark >= 80) {
                return "A";
            }
            if (mark < 80 && mark >= 70) {
                return "A-";
            }
            if (mark < 70 && mark >= 65) {
                return "B+";
            }
            if (mark < 65 && mark >= 60) {
                return "B";
            }
            if (mark < 60 && mark >= 50) {
                return "B-";
            }
            if (mark < 50 && mark >= 45) {
                return "C+";
            }
            if (mark < 45 && mark >= 40) {
                return "C";
            }
            if (mark < 40 && mark >= 35) {
                return "C-";
            }
            if (mark < 35 && mark >= 30) {
                return "D+";
            }
            if (mark < 30 && mark >= 25) {
                return "D";
            }
            if (mark < 25 && mark >= 20) {
                return "D-";
            }
            if (mark < 20 && mark > 0) {
                return "E";
            }
            return "-";
        } else if (initials.equals("Eng") || initials.equals("Kis") || initials.equals("Hist") || initials.equals("Geo")
                || initials.equals("C.R.E") || initials.equals("I.R.E") || initials.equals("H.R.E")) {

            if (mark <= 100 && mark >= 80) {
                return "A";
            }
            if (mark < 80 && mark >= 75) {
                return "A-";
            }
            if (mark < 75 && mark >= 70) {
                return "B+";
            }
            if (mark < 70 && mark >= 65) {
                return "B";
            }
            if (mark < 65 && mark >= 60) {
                return "B-";
            }
            if (mark < 60 && mark >= 55) {
                return "C+";
            }
            if (mark < 55 && mark >= 50) {
                return "C";
            }
            if (mark < 50 && mark >= 45) {
                return "C-";
            }
            if (mark < 45 && mark >= 40) {
                return "D+";
            }
            if (mark < 40 && mark >= 35) {
                return "D";
            }
            if (mark < 35 && mark >= 30) {
                return "D-";
            }
            if (mark < 30 && mark > 0) {
                return "E";
            }
            return "-";
        } else if (initials.equals("Hsci") || initials.equals("AnD") || initials.equals("Agric") || initials.equals("Wood")
                || initials.equals("Metal") || initials.equals("Bc") || initials.equals("Pwr") || initials.equals("Elec")
                || initials.equals("Dnd") || initials.equals("Avi") || initials.equals("Comp") || initials.equals("Fren")
                || initials.equals("Germ") || initials.equals("Arab") || initials.equals("Msc") || initials.equals("Bs")) {
            if (mark <= 100 && mark >= 85) {
                return "A";
            }
            if (mark < 85 && mark >= 80) {
                return "A-";
            }
            if (mark < 80 && mark >= 75) {
                return "B+";
            }
            if (mark < 75 && mark >= 70) {
                return "B";
            }
            if (mark < 70 && mark >= 65) {
                return "B-";
            }
            if (mark < 65 && mark >= 60) {
                return "C+";
            }
            if (mark < 60 && mark >= 55) {
                return "C";
            }
            if (mark < 55 && mark >= 50) {
                return "C-";
            }
            if (mark < 50 && mark >= 45) {
                return "D+";
            }
            if (mark < 45 && mark >= 40) {
                return "D";
            }
            if (mark < 40 && mark >= 35) {
                return "D-";
            }
            if (mark < 35 && mark > 0) {
                return "E";
            }
            return "-";
        }

        return "-";
    }

    public byte getPoints(int mark) {
        if (mark <= 100 && mark >= 80) {
            return 12;
        }
        if (mark < 80 && mark >= 75) {
            return 11;
        }
        if (mark < 75 && mark >= 70) {
            return 10;
        }
        if (mark < 70 && mark >= 65) {
            return 9;
        }
        if (mark < 65 && mark >= 60) {
            return 8;
        }
        if (mark < 60 && mark >= 55) {
            return 7;
        }
        if (mark < 55 && mark >= 50) {
            return 6;
        }
        if (mark < 50 && mark >= 45) {
            return 5;
        }
        if (mark < 45 && mark >= 40) {
            return 4;
        }
        if (mark < 40 && mark >= 35) {
            return 3;
        }
        if (mark < 35 && mark >= 30) {
            return 2;
        }
        if (mark < 30 && mark > 0) {
            return 1;
        }
        return 0;
    }

    public byte getSubjectPoints(int mark, String initials) {
        if (initials.equals("Maths") || initials.equals("Bio") || initials.equals("Phy") || initials.equals("Chem")) {
            if (mark <= 100 && mark >= 80) {
                return 12;
            }
            if (mark < 80 && mark >= 70) {
                return 11;
            }
            if (mark < 70 && mark >= 65) {
                return 10;
            }
            if (mark < 65 && mark >= 60) {
                return 9;
            }
            if (mark < 60 && mark >= 50) {
                return 8;
            }
            if (mark < 50 && mark >= 45) {
                return 7;
            }
            if (mark < 45 && mark >= 40) {
                return 6;
            }
            if (mark < 40 && mark >= 35) {
                return 5;
            }
            if (mark < 35 && mark >= 30) {
                return 4;
            }
            if (mark < 30 && mark >= 25) {
                return 3;
            }
            if (mark < 25 && mark >= 20) {
                return 2;
            }
            if (mark < 20 && mark > 0) {
                return 1;
            }
            return 0;
        } else if (initials.equals("Eng") || initials.equals("Kis") || initials.equals("Hist") || initials.equals("Geo")
                || initials.equals("C.R.E") || initials.equals("I.R.E") || initials.equals("H.R.E")) {

            if (mark <= 100 && mark >= 80) {
                return 12;
            }
            if (mark < 80 && mark >= 75) {
                return 11;
            }
            if (mark < 75 && mark >= 70) {
                return 10;
            }
            if (mark < 70 && mark >= 65) {
                return 9;
            }
            if (mark < 65 && mark >= 60) {
                return 8;
            }
            if (mark < 60 && mark >= 55) {
                return 7;
            }
            if (mark < 55 && mark >= 50) {
                return 6;
            }
            if (mark < 50 && mark >= 45) {
                return 5;
            }
            if (mark < 45 && mark >= 40) {
                return 4;
            }
            if (mark < 40 && mark >= 35) {
                return 3;
            }
            if (mark < 35 && mark >= 30) {
                return 2;
            }
            if (mark < 30 && mark > 0) {
                return 1;
            }
            return 0;
        } else if (initials.equals("Hsci") || initials.equals("AnD") || initials.equals("Agric") || initials.equals("Wood")
                || initials.equals("Metal") || initials.equals("Bc") || initials.equals("Pwr") || initials.equals("Elec")
                || initials.equals("Dnd") || initials.equals("Avi") || initials.equals("Comp") || initials.equals("Fren")
                || initials.equals("Germ") || initials.equals("Arab") || initials.equals("Msc") || initials.equals("Bs")) {
            if (mark <= 100 && mark >= 85) {
                return 12;
            }
            if (mark < 85 && mark >= 80) {
                return 11;
            }
            if (mark < 80 && mark >= 75) {
                return 10;
            }
            if (mark < 75 && mark >= 70) {
                return 9;
            }
            if (mark < 70 && mark >= 65) {
                return 8;
            }
            if (mark < 65 && mark >= 60) {
                return 7;
            }
            if (mark < 60 && mark >= 55) {
                return 6;
            }
            if (mark < 55 && mark >= 50) {
                return 5;
            }
            if (mark < 50 && mark >= 45) {
                return 4;
            }
            if (mark < 45 && mark >= 40) {
                return 3;
            }
            if (mark < 40 && mark >= 35) {
                return 2;
            }
            if (mark < 35 && mark > 0) {
                return 1;
            }
            return 0;
        }

        return 0;
    }

    public List<MeritList> getSubjectMeritList(int code, int year, int form, int term, String initials, List<MeritList> meritLists) {
        List<Student> subjectStudents = this.studentService.findAllStudentDoingSubject(code, year, form, term, initials);

        ArrayList<MeritList> subjectMerits = new ArrayList<>();

        if (subjectStudents.size() > 0) {
            for (MeritList meritList2 : meritLists) {
                // for(Student student : subjectStudents) {
                //   if(meritList2.getAdmNo() == student.getAdmNo()) {
                subjectMerits.add(meritList2);
                //  }
                // }
            }
        } else {
            MeritList meritList = new MeritList("-", "-", "-", "-");
            subjectMerits.add(meritList);
        }

        return subjectMerits;
    }

}

class SortByAverage implements Comparator<MeritList> {

    @Override
    public int compare(MeritList a, MeritList b) {
        return Float.compare(a.getAverage(), b.getAverage());
    }
}

class SortByPoints implements Comparator<MeritList> {

    @Override
    public int compare(MeritList a, MeritList b) {
        return a.getPoints() - b.getPoints();
    }
}

class SortByMaths implements Comparator<MeritList> {

    @Override
    public int compare(MeritList a, MeritList b) {
        return a.getMaths() - b.getMaths();
    }
}

class SortByEng implements Comparator<MeritList> {

    @Override
    public int compare(MeritList a, MeritList b) {
        return a.getEng() - b.getEng();
    }
}

class SortByKis implements Comparator<MeritList> {

    @Override
    public int compare(MeritList a, MeritList b) {
        return a.getKis() - b.getKis();
    }
}

class SortByBio implements Comparator<MeritList> {

    @Override
    public int compare(MeritList a, MeritList b) {
        return a.getBio() - b.getBio();
    }
}

class SortByChem implements Comparator<MeritList> {

    @Override
    public int compare(MeritList a, MeritList b) {
        return a.getChem() - b.getChem();
    }
}

class SortByPhy implements Comparator<MeritList> {

    @Override
    public int compare(MeritList a, MeritList b) {
        return a.getPhy() - b.getPhy();
    }
}

class SortByHist implements Comparator<MeritList> {

    @Override
    public int compare(MeritList a, MeritList b) {
        return a.getHist() - b.getHist();
    }
}

class SortByCre implements Comparator<MeritList> {

    @Override
    public int compare(MeritList a, MeritList b) {
        return a.getCre() - b.getCre();
    }
}

class SortByGeo implements Comparator<MeritList> {

    @Override
    public int compare(MeritList a, MeritList b) {
        return a.getGeo() - b.getGeo();
    }
}

class SortByIre implements Comparator<MeritList> {

    @Override
    public int compare(MeritList a, MeritList b) {
        return a.getIre() - b.getIre();
    }
}

class SortByHre implements Comparator<MeritList> {

    @Override
    public int compare(MeritList a, MeritList b) {
        return a.getHre() - b.getHre();
    }
}

class SortByHsci implements Comparator<MeritList> {

    @Override
    public int compare(MeritList a, MeritList b) {
        return a.getHsci() - b.getHsci();
    }
}

class SortByAnd implements Comparator<MeritList> {

    @Override
    public int compare(MeritList a, MeritList b) {
        return a.getAnD() - b.getAnD();
    }
}

class SortByAgric implements Comparator<MeritList> {

    @Override
    public int compare(MeritList a, MeritList b) {
        return a.getAgric() - b.getAgric();
    }
}

class SortByComp implements Comparator<MeritList> {

    @Override
    public int compare(MeritList a, MeritList b) {
        return a.getComp() - b.getComp();
    }
}

class SortByAvi implements Comparator<MeritList> {

    @Override
    public int compare(MeritList a, MeritList b) {
        return a.getAvi() - b.getAvi();
    }
}

class SortByElec implements Comparator<MeritList> {

    @Override
    public int compare(MeritList a, MeritList b) {
        return a.getElec() - b.getElec();
    }
}

class SortByPwr implements Comparator<MeritList> {

    @Override
    public int compare(MeritList a, MeritList b) {
        return a.getPwr() - b.getPwr();
    }
}

class SortByWood implements Comparator<MeritList> {

    @Override
    public int compare(MeritList a, MeritList b) {
        return a.getWood() - b.getWood();
    }
}

class SortByMetal implements Comparator<MeritList> {

    @Override
    public int compare(MeritList a, MeritList b) {
        return a.getMetal() - b.getMetal();
    }
}

class SortByBc implements Comparator<MeritList> {

    @Override
    public int compare(MeritList a, MeritList b) {
        return a.getBc() - b.getBc();
    }
}

class SortByFren implements Comparator<MeritList> {

    @Override
    public int compare(MeritList a, MeritList b) {
        return a.getFren() - b.getFren();
    }
}

class SortByGerm implements Comparator<MeritList> {

    @Override
    public int compare(MeritList a, MeritList b) {
        return a.getGerm() - b.getGerm();
    }
}

class SortByArab implements Comparator<MeritList> {

    @Override
    public int compare(MeritList a, MeritList b) {
        return a.getArab() - b.getArab();
    }
}

class SortByMsc implements Comparator<MeritList> {

    @Override
    public int compare(MeritList a, MeritList b) {
        return a.getMsc() - b.getMsc();
    }
}

class SortByBs implements Comparator<MeritList> {

    @Override
    public int compare(MeritList a, MeritList b) {
        return a.getBs() - b.getBs();
    }
}

class SortByDnd implements Comparator<MeritList> {

    @Override
    public int compare(MeritList a, MeritList b) {
        return a.getDnd() - b.getDnd();
    }
}

class SortByDeviation implements Comparator<MeritList> {

    @Override
    public int compare(MeritList a, MeritList b) {
        return (int) (a.getDeviation() - b.getDeviation());
    }
}

class SortByMarks implements Comparator<SubjectMarks> {

    @Override
    public int compare(SubjectMarks a, SubjectMarks b) {
        return a.getMark() - b.getMark();
    }
}

class StreamPoints {

    private String stream;
    private int points;
    private int count;

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
