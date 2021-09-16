package com.pensasha.school.exam;

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
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
    private ExamNameService examNameService;

    public MarkController(StudentService studentService, MarkService markService, FormService formService, YearService yearService, TermService termService, SubjectService subjectService, UserService userService, StreamService streamService, SchoolService schoolService, ExamNameService examNameService) {
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
    }

    @GetMapping(value={"/schools/{code}/years/{year}/examination"})
    public String examinations(@PathVariable int code, @PathVariable int year, Model model, Principal principal) {
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(code).get();
        Student student = new Student();
        List<ExamName> examNames = this.examNameService.getExamBySchoolYear(code, year);
        ArrayList<ExamName> eNs = new ArrayList<ExamName>();
        for (int i = 0; i < examNames.size(); ++i) {
            if (eNs.size() > 0) {
                for (int k = 0; k < eNs.size(); ++k) {
                    if (((ExamName)eNs.get(k)).getName().equals(examNames.get(i).getName())) {
                        eNs.remove(examNames.get(i));
                        continue;
                    }
                    eNs.add(examNames.get(i));
                }
                continue;
            }
            eNs.add(examNames.get(i));
        }
        List<ExamName> form1term1 = this.examNameService.getExamBySchoolYearFormTerm(code, year, 1, 1);
        List<ExamName> form1term2 = this.examNameService.getExamBySchoolYearFormTerm(code, year, 1, 2);
        List<ExamName> form1term3 = this.examNameService.getExamBySchoolYearFormTerm(code, year, 1, 3);
        model.addAttribute("activeUser", (Object)activeUser);
        model.addAttribute("school", (Object)school);
        model.addAttribute("subjects", school.getSubjects());
        model.addAttribute("streams", this.streamService.getStreamsInSchool(code));
        model.addAttribute("student", (Object)student);
        model.addAttribute("examNames", eNs);
        model.addAttribute("year", (Object)year);
        model.addAttribute("form1term1", form1term1);
        model.addAttribute("form1term2", form1term2);
        model.addAttribute("form1term3", form1term3);
        return "examination";
    }

    @GetMapping(value={"/schools/{code}/years/{year}/forms/{form}/terms/{term}/exams"})
    @ResponseBody
    public List<ExamName> examNames(@PathVariable int code, @PathVariable int year, @PathVariable int form, @PathVariable int term) {
        List<ExamName> examNames = this.examNameService.getExamBySchoolYearFormTerm(code, year, form, term);
        ArrayList<ExamName> eNs = new ArrayList<ExamName>();
        for (int i = 0; i < examNames.size(); ++i) {
            if (eNs.size() > 0) {
                for (int k = 0; k < eNs.size(); ++k) {
                    if (((ExamName)eNs.get(k)).getName().equals(examNames.get(i).getName())) {
                        eNs.remove(examNames.get(i));
                        continue;
                    }
                    eNs.add(examNames.get(i));
                }
                continue;
            }
            eNs.add(examNames.get(i));
        }
        return eNs;
    }

    @PostMapping(value={"/schools/{code}/examination"})
    public String allExamination(@PathVariable int code, @RequestParam int year) {
        return "redirect:/schools/" + code + "/years/" + year + "/examination";
    }

    @PostMapping(value={"/schools/{code}/years/{year}/examination"})
    public String addingExamination(@PathVariable int code, @PathVariable int year, @RequestParam String name, @RequestParam int form, @RequestParam int term, HttpServletRequest request) {
        School school = this.schoolService.getSchool(code).get();
        ArrayList subjects = new ArrayList();
        school.getSubjects().forEach(subject -> subjects.add(subject));
        for (int i = 0; i < subjects.size(); ++i) {
            ExamName examName = new ExamName();
            examName.setName(name);
            ArrayList<School> schools = new ArrayList<School>();
            schools.add(school);
            examName.setSchools(schools);
            Year yearObj = this.yearService.getYearFromSchool(year, code).get();
            ArrayList<Year> years = new ArrayList<Year>();
            years.add(yearObj);
            examName.setYears(years);
            Form formObj = this.formService.getForm(form, year, code).get();
            ArrayList<Form> forms = new ArrayList<Form>();
            forms.add(formObj);
            examName.setForms(forms);
            Term termObj = this.termService.getTerm(term, form, year, code);
            ArrayList<Term> terms = new ArrayList<Term>();
            terms.add(termObj);
            examName.setTerms(terms);
            examName.setOutOf(Integer.parseInt(request.getParameter(((Subject)subjects.get(i)).getInitials() + "OutOf")));
            examName.setSubject((Subject)subjects.get(i));
            this.examNameService.addExam(examName);
        }
        return "redirect:/schools/" + code + "/years/" + year + "/examination";
    }

    @GetMapping(value={"/schools/{code}/years/{year}/forms/{form}/terms/{term}/examination/{name}"})
    public String deleteExamFromSchool(@PathVariable int code, @PathVariable String name, @PathVariable int year, @PathVariable int form, @PathVariable int term) {
        List<ExamName> examNames = this.examNameService.getExamByCodeNameYearFormTermAndExamName(name, code, year, form, term);
        List<Mark> marks = this.markService.getMarksBySchoolOnSubjectByExamName(code, year, form, term, name);
        marks.forEach(mark -> this.markService.deleteMark(mark.getId()));
        examNames.forEach(examName -> this.examNameService.deleteExam(examName.getId()));
        return "redirect:/schools/" + code + "/years/" + year + "/examination";
    }

    @PostMapping(value={"/schools/{code}/stream/{stream}/marks/{exam}"})
    public String addMarksToStudentSubjects(@PathVariable int code, @PathVariable int stream, @PathVariable int exam, HttpServletRequest request, Model model, Principal principal) {
        int form = Integer.parseInt(request.getParameter("form"));
        int year = Integer.parseInt(request.getParameter("year"));
        int term = Integer.parseInt(request.getParameter("term"));
        Year yearObj = this.yearService.getYearFromSchool(year, code).get();
        Term termObj = this.termService.getTerm(term, form, year, code);
        Form formObj = this.formService.getFormByForm(form);
        ExamName examName = this.examNameService.getExam(exam);
        String subject = request.getParameter("subject");
        Subject subjectObj = this.subjectService.getSubject(subject);

        List<Student> students = this.studentService.findAllStudentDoingSubjectInStream(code, year, form, term, subject, stream);
        for (int i = 0; i < students.size(); ++i) {
            Mark mark = new Mark();
            if (this.markService.getMarksByStudentOnSubjectByExamId(students.get(i).getAdmNo(), year, form, term, subject, exam) != null) {
                mark = this.markService.getMarksByStudentOnSubjectByExamId(students.get(i).getAdmNo(), year, form, term, subject, exam);
                mark.setMark(Integer.parseInt(request.getParameter(students.get(i).getAdmNo() + "mark")));
                this.markService.addMarksToSubject(mark);
                continue;
            }
            mark = new Mark(students.get(i), yearObj, formObj, termObj, subjectObj);
            mark.setMark(Integer.parseInt(request.getParameter(students.get(i).getAdmNo() + "mark")));
            mark.setExamName(examName);
            this.markService.addMarksToSubject(mark);
        }
        model.addAttribute("success", (Object)"Marks saved successfully");
        if (students.size() == 0) {
            model.addAttribute("fail", (Object)"No student. Cannot add marks");
        }
        return "redirect:/schools/" + code + "/years/" + year + "/forms/" + form + "/terms/" + term + "/subjects/" + subject + "/streams/" + stream + "/exams/" + exam;

    }

    @PostMapping(value={"/schools/{code}/marksSheet"})
    public String postMarkSheet(Model model, Principal principal, @PathVariable int code, @RequestParam int year, @RequestParam int form, @RequestParam int term, @RequestParam String subject, @RequestParam int stream, @RequestParam int examType) {
        return "redirect:/schools/" + code + "/years/" + year + "/forms/" + form + "/terms/" + term + "/subjects/" + subject + "/streams/" + stream + "/exams/" + examType;
    }

    @GetMapping(value={"/schools/{code}/years/{year}/forms/{form}/terms/{term}/subjects/{subject}/streams/{stream}/exams/{exam}"})
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
        List<Student> students = this.studentService.findAllStudentDoingSubjectInStream(code, year, form, term, subject, stream);
        List<Stream> streams = this.streamService.getStreamsInSchool(school.getCode());
        List<Year> years = this.yearService.getAllYearsInSchool(school.getCode());
        List<Subject> subjects = this.subjectService.getAllSubjectInSchool(school.getCode());
        ArrayList<Mark> marks = new ArrayList<Mark>();

        for (int i = 0; i < students.size(); ++i) {
            Mark mark = new Mark();
            if (this.markService.getMarksByStudentOnSubjectByExamId(students.get(i).getAdmNo(), year, form, term, subject, exam) != null) {
                marks.add(this.markService.getMarksByStudentOnSubjectByExamId(students.get(i).getAdmNo(), year, form, term, subject, exam));
                continue;
            }
            mark.setStudent(students.get(i));
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

    @PostMapping(value={"/schools/{code}/meritList"})
    public String studentsMeritList(@PathVariable int code, @RequestParam int year, @RequestParam int form, @RequestParam int term, Model model, Principal principal) {
        return "redirect:/schools/" + code + "/years/" + year + "/forms/" + form + "/terms/" + term + "/meritList";
    }

    @GetMapping(value={"/schools/{code}/years/{year}/forms/{form}/terms/{term}/meritList"})
    public String getMeritList(@PathVariable int code, @PathVariable int year, @PathVariable int form, @PathVariable int term, Model model, Principal principal) {
        int i;
        GradeCount gradeCount;
        int i2;
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(code).get();
        Student student = new Student();
        List<Student> students = this.studentService.getAllStudentsInSchoolByYearFormTerm(code, year, form, term);
        List<Subject> subjects = this.subjectService.getAllSubjectInSchool(code);
        List<Mark> allMarks = this.markService.getAllStudentsMarksBySchoolYearFormAndTerm(code, form, term, year);
        ArrayList<Student> studentsWithoutMarks = new ArrayList<Student>();
        ArrayList<Student> studentsWithMarks = new ArrayList<Student>();
        for (int i3 = 0; i3 < students.size(); ++i3) {
            if (!this.markService.getMarkByAdm(students.get(i3).getAdmNo()).booleanValue()) {
                studentsWithoutMarks.add(students.get(i3));
                continue;
            }
            studentsWithMarks.add(students.get(i3));
        }
        ArrayList<MeritList> meritLists = new ArrayList<MeritList>();
        ArrayList<GradeCount> gradeCounts = new ArrayList<GradeCount>();
        int mathsCount = 0;
        int engCount = 0;
        int kisCount = 0;
        int bioCount = 0;
        int chemCount = 0;
        int phyCount = 0;
        int histCount = 0;
        int creCount = 0;
        int geoCount = 0;
        int ireCount = 0;
        int hreCount = 0;
        int hsciCount = 0;
        int andCount = 0;
        int agricCount = 0;
        int compCount = 0;
        int aviCount = 0;
        int elecCount = 0;
        int pwrCount = 0;
        int woodCount = 0;
        int metalCount = 0;
        int bcCount = 0;
        int frenCount = 0;
        int germCount = 0;
        int arabCount = 0;
        int mscCount = 0;
        int bsCount = 0;
        int dndCount = 0;

        for (i2 = 0; i2 < studentsWithMarks.size(); ++i2) {
            gradeCount = new GradeCount();
            MeritList meritList = new MeritList();
            int count = 0;
            block118: for (int j = 0; j < subjects.size(); ++j) {
                meritList.setFirstname(students.get(i2).getFirstname());
                meritList.setSecondname(students.get(i2).getThirdname());
                meritList.setAdmNo(students.get(i2).getAdmNo());
                meritList.setKcpe(students.get(i2).getKcpeMarks());
                meritList.setStream(students.get(i2).getStream().getStream());
                gradeCount.setFirstname(students.get(i2).getFirstname());
                gradeCount.setSecondname(students.get(i2).getThirdname());
                gradeCount.setAdmNo(students.get(i2).getAdmNo());
                List<Mark> marks = new ArrayList();
                int sum = 0;
                int totalOutOf = 0;
                switch (subjects.get(j).getInitials()) {
                    case "Maths": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += ((Mark)marks.get(k)).getMark();
                            totalOutOf += ((Mark)marks.get(k)).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++mathsCount;
                        }
                        if (totalOutOf > 0) {
                            meritList.setMaths(sum * 100 / totalOutOf);
                            gradeCount.setMaths(this.getGrade(meritList.getMaths()));
                            continue block118;
                        }
                        meritList.setMaths(sum);
                        gradeCount.setMaths(this.getGrade(meritList.getMaths()));
                        continue block118;
                    }
                    case "Eng": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += ((Mark)marks.get(k)).getMark();
                            totalOutOf += ((Mark)marks.get(k)).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++engCount;
                        }
                        if (totalOutOf > 0) {
                            meritList.setEng(sum * 100 / totalOutOf);
                            gradeCount.setEng(this.getGrade(meritList.getEng()));
                            continue block118;
                        }
                        meritList.setEng(sum);
                        gradeCount.setEng(this.getGrade(meritList.getEng()));
                        continue block118;
                    }
                    case "Kis": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += ((Mark)marks.get(k)).getMark();
                            totalOutOf += ((Mark)marks.get(k)).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++kisCount;
                        }
                        if (totalOutOf > 0) {
                            meritList.setKis(sum * 100 / totalOutOf);
                            gradeCount.setKis(this.getGrade(meritList.getKis()));
                            continue block118;
                        }
                        meritList.setKis(sum);
                        gradeCount.setKis(this.getGrade(meritList.getKis()));
                        continue block118;
                    }
                    case "Bio": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += ((Mark)marks.get(k)).getMark();
                            totalOutOf += ((Mark)marks.get(k)).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++bioCount;
                        }
                        if (totalOutOf > 0) {
                            meritList.setBio(sum * 100 / totalOutOf);
                            gradeCount.setBio(this.getGrade(meritList.getBio()));
                            continue block118;
                        }
                        meritList.setBio(sum);
                        gradeCount.setBio(this.getGrade(meritList.getBio()));
                        continue block118;
                    }
                    case "Chem": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += ((Mark)marks.get(k)).getMark();
                            totalOutOf += ((Mark)marks.get(k)).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++chemCount;
                        }
                        if (totalOutOf > 0) {
                            meritList.setChem(sum * 100 / totalOutOf);
                            gradeCount.setChem(this.getGrade(meritList.getChem()));
                            continue block118;
                        }
                        meritList.setChem(sum);
                        gradeCount.setChem(this.getGrade(meritList.getChem()));
                        continue block118;
                    }
                    case "Phy": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += ((Mark)marks.get(k)).getMark();
                            totalOutOf += ((Mark)marks.get(k)).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++phyCount;
                        }
                        if (totalOutOf > 0) {
                            meritList.setPhy(sum * 100 / totalOutOf);
                            gradeCount.setPhy(this.getGrade(meritList.getPhy()));
                            continue block118;
                        }
                        meritList.setPhy(sum);
                        gradeCount.setPhy(this.getGrade(meritList.getPhy()));
                        continue block118;
                    }
                    case "Hist": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += ((Mark)marks.get(k)).getMark();
                            totalOutOf += ((Mark)marks.get(k)).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++histCount;
                        }
                        if (totalOutOf > 0) {
                            meritList.setHist(sum * 100 / totalOutOf);
                            gradeCount.setHist(this.getGrade(meritList.getHist()));
                            continue block118;
                        }
                        meritList.setHist(sum);
                        gradeCount.setHist(this.getGrade(meritList.getHist()));
                        continue block118;
                    }
                    case "C.R.E": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += ((Mark)marks.get(k)).getMark();
                            totalOutOf += ((Mark)marks.get(k)).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++creCount;
                        }
                        if (totalOutOf > 0) {
                            meritList.setCre(sum * 100 / totalOutOf);
                            gradeCount.setCre(this.getGrade(meritList.getCre()));
                            continue block118;
                        }
                        meritList.setCre(sum);
                        gradeCount.setCre(this.getGrade(meritList.getCre()));
                        continue block118;
                    }
                    case "Geo": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += ((Mark)marks.get(k)).getMark();
                            totalOutOf += ((Mark)marks.get(k)).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++geoCount;
                        }
                        if (totalOutOf > 0) {
                            meritList.setGeo(sum * 100 / totalOutOf);
                            gradeCount.setGeo(this.getGrade(meritList.getGeo()));
                            continue block118;
                        }
                        meritList.setGeo(sum);
                        gradeCount.setGeo(this.getGrade(meritList.getGeo()));
                        continue block118;
                    }
                    case "I.R.E": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += ((Mark)marks.get(k)).getMark();
                            totalOutOf += ((Mark)marks.get(k)).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++ireCount;
                        }
                        if (totalOutOf > 0) {
                            meritList.setIre(sum * 100 / totalOutOf);
                            gradeCount.setIre(this.getGrade(meritList.getIre()));
                            continue block118;
                        }
                        meritList.setIre(sum);
                        gradeCount.setIre(this.getGrade(meritList.getIre()));
                        continue block118;
                    }
                    case "H.R.E": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += ((Mark)marks.get(k)).getMark();
                            totalOutOf += ((Mark)marks.get(k)).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++hreCount;
                        }
                        if (totalOutOf > 0) {
                            meritList.setHre(sum * 100 / totalOutOf);
                            gradeCount.setHre(this.getGrade(meritList.getHre()));
                            continue block118;
                        }
                        meritList.setHre(sum);
                        gradeCount.setHre(this.getGrade(meritList.getHre()));
                        continue block118;
                    }
                    case "Hsci": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += ((Mark)marks.get(k)).getMark();
                            totalOutOf += ((Mark)marks.get(k)).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++hsciCount;
                        }
                        if (totalOutOf > 0) {
                            meritList.setHsci(sum * 100 / totalOutOf);
                            gradeCount.setHsci(this.getGrade(meritList.getHsci()));
                            continue block118;
                        }
                        meritList.setHsci(sum);
                        gradeCount.setHsci(this.getGrade(meritList.getHsci()));
                        continue block118;
                    }
                    case "AnD": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += ((Mark)marks.get(k)).getMark();
                            totalOutOf += ((Mark)marks.get(k)).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++andCount;
                        }
                        if (totalOutOf > 0) {
                            meritList.setAnD(sum * 100 / totalOutOf);
                            gradeCount.setAnd(this.getGrade(meritList.getAnD()));
                            continue block118;
                        }
                        meritList.setAnD(sum);
                        gradeCount.setAnd(this.getGrade(meritList.getAnD()));
                        continue block118;
                    }
                    case "Agric": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += ((Mark)marks.get(k)).getMark();
                            totalOutOf += ((Mark)marks.get(k)).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++agricCount;
                        }
                        if (totalOutOf > 0) {
                            meritList.setAgric(sum * 100 / totalOutOf);
                            gradeCount.setAgric(this.getGrade(meritList.getAgric()));
                            continue block118;
                        }
                        meritList.setAgric(sum);
                        gradeCount.setAgric(this.getGrade(meritList.getAgric()));
                        continue block118;
                    }
                    case "Comp": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += ((Mark)marks.get(k)).getMark();
                            totalOutOf += ((Mark)marks.get(k)).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++compCount;
                        }
                        if (totalOutOf > 0) {
                            meritList.setComp(sum * 100 / totalOutOf);
                            gradeCount.setComp(this.getGrade(meritList.getComp()));
                            continue block118;
                        }
                        meritList.setComp(sum);
                        gradeCount.setComp(this.getGrade(meritList.getComp()));
                        continue block118;
                    }
                    case "Avi": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += ((Mark)marks.get(k)).getMark();
                            totalOutOf += ((Mark)marks.get(k)).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++aviCount;
                        }
                        if (totalOutOf > 0) {
                            meritList.setAvi(sum * 100 / totalOutOf);
                            gradeCount.setAvi(this.getGrade(meritList.getAvi()));
                            continue block118;
                        }
                        meritList.setAvi(sum);
                        gradeCount.setAvi(this.getGrade(meritList.getAvi()));
                        continue block118;
                    }
                    case "Elec": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += ((Mark)marks.get(k)).getMark();
                            totalOutOf += ((Mark)marks.get(k)).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++elecCount;
                        }
                        if (totalOutOf > 0) {
                            meritList.setElec(sum * 100 / totalOutOf);
                            gradeCount.setElec(this.getGrade(meritList.getElec()));
                            continue block118;
                        }
                        meritList.setElec(sum);
                        gradeCount.setElec(this.getGrade(meritList.getElec()));
                        continue block118;
                    }
                    case "Pwr": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += ((Mark)marks.get(k)).getMark();
                            totalOutOf += ((Mark)marks.get(k)).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++pwrCount;
                        }
                        if (totalOutOf > 0) {
                            meritList.setPwr(sum * 100 / totalOutOf);
                            gradeCount.setPwr(this.getGrade(meritList.getPwr()));
                            continue block118;
                        }
                        meritList.setPwr(sum);
                        gradeCount.setPwr(this.getGrade(meritList.getPwr()));
                        continue block118;
                    }
                    case "Wood": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += ((Mark)marks.get(k)).getMark();
                            totalOutOf += ((Mark)marks.get(k)).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++woodCount;
                        }
                        if (totalOutOf > 0) {
                            meritList.setWood(sum * 100 / totalOutOf);
                            gradeCount.setWood(this.getGrade(meritList.getWood()));
                            continue block118;
                        }
                        meritList.setWood(sum);
                        gradeCount.setWood(this.getGrade(meritList.getWood()));
                        continue block118;
                    }
                    case "Metal": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += ((Mark)marks.get(k)).getMark();
                            totalOutOf += ((Mark)marks.get(k)).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++metalCount;
                        }
                        if (totalOutOf > 0) {
                            meritList.setMetal(sum * 100 / totalOutOf);
                            gradeCount.setMetal(this.getGrade(meritList.getMetal()));
                            continue block118;
                        }
                        meritList.setMetal(sum);
                        gradeCount.setMetal(this.getGrade(meritList.getMetal()));
                        continue block118;
                    }
                    case "Bc": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += ((Mark)marks.get(k)).getMark();
                            totalOutOf += ((Mark)marks.get(k)).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++bcCount;
                        }
                        if (totalOutOf > 0) {
                            meritList.setBc(sum * 100 / totalOutOf);
                            gradeCount.setBc(this.getGrade(meritList.getBc()));
                            continue block118;
                        }
                        meritList.setBc(sum);
                        gradeCount.setBc(this.getGrade(meritList.getBc()));
                        continue block118;
                    }
                    case "Fren": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += ((Mark)marks.get(k)).getMark();
                            totalOutOf += ((Mark)marks.get(k)).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++frenCount;
                        }
                        if (totalOutOf > 0) {
                            meritList.setFren(sum * 100 / totalOutOf);
                            gradeCount.setFren(this.getGrade(meritList.getFren()));
                            continue block118;
                        }
                        meritList.setFren(sum);
                        gradeCount.setFren(this.getGrade(meritList.getFren()));
                        continue block118;
                    }
                    case "Germ": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += ((Mark)marks.get(k)).getMark();
                            totalOutOf += ((Mark)marks.get(k)).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++germCount;
                        }
                        if (totalOutOf > 0) {
                            meritList.setGerm(sum * 100 / totalOutOf);
                            gradeCount.setGerm(this.getGrade(meritList.getGerm()));
                            continue block118;
                        }
                        meritList.setGerm(sum);
                        gradeCount.setGerm(this.getGrade(meritList.getGerm()));
                        continue block118;
                    }
                    case "Arab": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += ((Mark)marks.get(k)).getMark();
                            totalOutOf += ((Mark)marks.get(k)).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++arabCount;
                        }
                        if (totalOutOf > 0) {
                            meritList.setArab(sum * 100 / totalOutOf);
                            gradeCount.setArab(this.getGrade(meritList.getArab()));
                            continue block118;
                        }
                        meritList.setArab(sum);
                        gradeCount.setArab(this.getGrade(meritList.getArab()));
                        continue block118;
                    }
                    case "Msc": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += ((Mark)marks.get(k)).getMark();
                            totalOutOf += ((Mark)marks.get(k)).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++mscCount;
                        }
                        if (totalOutOf > 0) {
                            meritList.setMsc(sum * 100 / totalOutOf);
                            gradeCount.setMsc(this.getGrade(meritList.getMsc()));
                            continue block118;
                        }
                        meritList.setMsc(sum);
                        gradeCount.setMsc(this.getGrade(meritList.getMsc()));
                        continue block118;
                    }
                    case "Bs": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += ((Mark)marks.get(k)).getMark();
                            totalOutOf += ((Mark)marks.get(k)).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++bsCount;
                        }
                        if (totalOutOf > 0) {
                            meritList.setBs(sum * 100 / totalOutOf);
                            gradeCount.setBs(this.getGrade(meritList.getBs()));
                            continue block118;
                        }
                        meritList.setBs(sum);
                        gradeCount.setBs(this.getGrade(meritList.getBs()));
                        continue block118;
                    }
                    case "Dnd": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += ((Mark)marks.get(k)).getMark();
                            totalOutOf += ((Mark)marks.get(k)).getExamName().getOutOf();
                        }
                        if (sum > 0) {
                            ++count;
                            ++dndCount;
                        }
                        if (totalOutOf > 0) {
                            meritList.setDnd(sum * 100 / totalOutOf);
                            gradeCount.setDnd(this.getGrade(meritList.getDnd()));
                            continue block118;
                        }
                        meritList.setDnd(sum);
                        gradeCount.setDnd(this.getGrade(meritList.getDnd()));
                    }
                }
            }
            meritList.setTotal(meritList.getMaths() + meritList.getEng() + meritList.getKis() + meritList.getBio() + meritList.getChem() + meritList.getPhy() + meritList.getHist() + meritList.getCre() + meritList.getGeo() + meritList.getIre() + meritList.getHre() + meritList.getHsci() + meritList.getAnD() + meritList.getAgric() + meritList.getComp() + meritList.getAvi() + meritList.getElec() + meritList.getPwr() + meritList.getWood() + meritList.getMetal() + meritList.getBc() + meritList.getFren() + meritList.getGerm() + meritList.getArab() + meritList.getMsc() + meritList.getBs() + meritList.getDnd());
            if (count > 0) {
                meritList.setAverage(meritList.getTotal() / count);
            }
            meritList.setDeviation(meritList.getAverage() - students.get(i2).getKcpeMarks() / 5);
            meritLists.add(meritList);
            gradeCounts.add(gradeCount);
        }
        for (i2 = 0; i2 < studentsWithoutMarks.size(); ++i2) {
            MeritList meritList = new MeritList();
            meritList.setFirstname(((Student)studentsWithoutMarks.get(i2)).getFirstname());
            meritList.setSecondname(((Student)studentsWithoutMarks.get(i2)).getThirdname());
            meritList.setAdmNo(((Student)studentsWithoutMarks.get(i2)).getAdmNo());
            meritList.setKcpe(((Student)studentsWithoutMarks.get(i2)).getKcpeMarks());
            meritList.setStream(((Student)studentsWithoutMarks.get(i2)).getStream().getStream());
            meritList.setTotal(0);
            gradeCount = new GradeCount();
            gradeCount.setFirstname(((Student)studentsWithoutMarks.get(i2)).getFirstname());
            gradeCount.setSecondname(((Student)studentsWithoutMarks.get(i2)).getThirdname());
            gradeCount.setAdmNo(((Student)studentsWithoutMarks.get(i2)).getAdmNo());
            meritLists.add(meritList);
            gradeCounts.add(gradeCount);
        }
        String[] grades = new String[]{"A", "Am", "Bp", "B", "Bm", "Cp", "C", "Cm", "Dp", "D", "Dm", "E"};
        String[] gds = new String[]{"A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "D-", "E"};
        model.addAttribute("grades", (Object)grades);
        model.addAttribute("gradeCounts", gradeCounts);
        Collections.sort(meritLists, new SortByDeviation().reversed());
        if (meritLists.size() > 0) {
            model.addAttribute("mostImproved", meritLists.get(0));
        }
        List<MeritList> mathsMerits = this.getSubjectMeritList(code, year, form, term, "Maths", meritLists);
        Collections.sort(mathsMerits, new SortByMaths().reversed());
        if (mathsMerits.size() > 0) {
            model.addAttribute("MathsGiant", (Object)mathsMerits.get(0));
        }
        List<MeritList> engMerits = this.getSubjectMeritList(code, year, form, term, "Eng", meritLists);
        Collections.sort(engMerits, new SortByEng().reversed());
        if (engMerits.size() > 0) {
            model.addAttribute("EngGiant", (Object)engMerits.get(0));
        }
        List<MeritList> kisMerits = this.getSubjectMeritList(code, year, form, term, "Kis", meritLists);
        Collections.sort(kisMerits, new SortByKis().reversed());
        if (kisMerits.size() > 0) {
            model.addAttribute("KisGiant", (Object)kisMerits.get(0));
        }
        List<MeritList> bioMerits = this.getSubjectMeritList(code, year, form, term, "Bio", meritLists);
        Collections.sort(bioMerits, new SortByBio().reversed());
        if (bioMerits.size() > 0) {
            model.addAttribute("BioGiant", (Object)bioMerits.get(0));
        }
        List<MeritList> chemMerits = this.getSubjectMeritList(code, year, form, term, "Chem", meritLists);
        Collections.sort(chemMerits, new SortByChem().reversed());
        if (meritLists.size() > 0) {
            model.addAttribute("ChemGiant", (Object)chemMerits.get(0));
        }
        List<MeritList> phyMerits = this.getSubjectMeritList(code, year, form, term, "Phy", meritLists);
        Collections.sort(phyMerits, new SortByPhy().reversed());
        if (phyMerits.size() > 0) {
            model.addAttribute("PhyGiant", (Object)phyMerits.get(0));
        }
        List<MeritList> histMerits = this.getSubjectMeritList(code, year, form, term, "Hist", meritLists);
        Collections.sort(histMerits, new SortByHist().reversed());
        if (histMerits.size() > 0) {
            model.addAttribute("HistGiant", (Object)histMerits.get(0));
        }
        List<MeritList> creMerits = this.getSubjectMeritList(code, year, form, term, "C.R.E", meritLists);
        Collections.sort(creMerits, new SortByCre().reversed());
        if (creMerits.size() > 0) {
            model.addAttribute("creGiant", (Object)creMerits.get(0));
        }
        List<MeritList> geoMerits = this.getSubjectMeritList(code, year, form, term, "Geo", meritLists);
        Collections.sort(geoMerits, new SortByGeo().reversed());
        if (geoMerits.size() > 0) {
            model.addAttribute("GeoGiant", (Object)geoMerits.get(0));
        }
        List<MeritList> ireMerits = this.getSubjectMeritList(code, year, form, term, "I.R.E", meritLists);
        Collections.sort(ireMerits, new SortByIre().reversed());
        if (ireMerits.size() > 0) {
            model.addAttribute("ireGiant", (Object)ireMerits.get(0));
        }
        List<MeritList> hreMerits = this.getSubjectMeritList(code, year, form, term, "H.R.E", meritLists);
        Collections.sort(hreMerits, new SortByHre().reversed());
        if (hreMerits.size() > 0) {
            model.addAttribute("hreGiant", (Object)hreMerits.get(0));
        }
        List<MeritList> hsciMerits = this.getSubjectMeritList(code, year, form, term, "Hsci", meritLists);
        Collections.sort(hsciMerits, new SortByHsci().reversed());
        if (hsciMerits.size() > 0) {
            model.addAttribute("HsciGiant", (Object)hsciMerits.get(0));
        }
        List<MeritList> andMerits = this.getSubjectMeritList(code, year, form, term, "AnD", meritLists);
        Collections.sort(andMerits, new SortByAnd().reversed());
        if (andMerits.size() > 0) {
            model.addAttribute("AnDGiant", (Object)andMerits.get(0));
        }
        List<MeritList> agricMerits = this.getSubjectMeritList(code, year, form, term, "Agric", meritLists);
        Collections.sort(agricMerits, new SortByAgric().reversed());
        if (agricMerits.size() > 0) {
            model.addAttribute("AgricGiant", (Object)agricMerits.get(0));
        }
        List<MeritList> compMerits = this.getSubjectMeritList(code, year, form, term, "Comp", meritLists);
        Collections.sort(compMerits, new SortByComp().reversed());
        if (compMerits.size() > 0) {
            model.addAttribute("CompGiant", (Object)compMerits.get(0));
        }
        List<MeritList> aviMerits = this.getSubjectMeritList(code, year, form, term, "Avi", meritLists);
        Collections.sort(aviMerits, new SortByAvi().reversed());
        if (aviMerits.size() > 0) {
            model.addAttribute("AviGiant", (Object)aviMerits.get(0));
        }
        List<MeritList> elecMerits = this.getSubjectMeritList(code, year, form, term, "Elec", meritLists);
        Collections.sort(elecMerits, new SortByElec().reversed());
        if (elecMerits.size() > 0) {
            model.addAttribute("ElecGiant", (Object)elecMerits.get(0));
        }
        List<MeritList> pwrMerits = this.getSubjectMeritList(code, year, form, term, "Pwr", meritLists);
        Collections.sort(pwrMerits, new SortByPwr().reversed());
        if (pwrMerits.size() > 0) {
            model.addAttribute("PwrGiant", (Object)pwrMerits.get(0));
        }
        List<MeritList> woodMerits = this.getSubjectMeritList(code, year, form, term, "Wood", meritLists);
        Collections.sort(woodMerits, new SortByWood().reversed());
        if (woodMerits.size() > 0) {
            model.addAttribute("WoodGiant", (Object)woodMerits.get(0));
        }
        List<MeritList> metalMerits = this.getSubjectMeritList(code, year, form, term, "Metal", meritLists);
        Collections.sort(metalMerits, new SortByMetal().reversed());
        if (metalMerits.size() > 0) {
            model.addAttribute("MetalGiant", (Object)metalMerits.get(0));
        }
        List<MeritList> bcMerits = this.getSubjectMeritList(code, year, form, term, "Bc", meritLists);
        Collections.sort(bcMerits, new SortByBc().reversed());
        if (bcMerits.size() > 0) {
            model.addAttribute("BcGiant", (Object)bcMerits.get(0));
        }
        List<MeritList> frenMerits = this.getSubjectMeritList(code, year, form, term, "Fren", meritLists);
        Collections.sort(frenMerits, new SortByFren().reversed());
        if (frenMerits.size() > 0) {
            model.addAttribute("FrenGiant", (Object)frenMerits.get(0));
        }
        List<MeritList> germMerits = this.getSubjectMeritList(code, year, form, term, "Germ", meritLists);
        Collections.sort(germMerits, new SortByGerm().reversed());
        if (germMerits.size() > 0) {
            model.addAttribute("GermGiant", (Object)germMerits.get(0));
        }
        List<MeritList> arabMerits = this.getSubjectMeritList(code, year, form, term, "Arab", meritLists);
        Collections.sort(arabMerits, new SortByArab().reversed());
        if (arabMerits.size() > 0) {
            model.addAttribute("ArabGiant", (Object)arabMerits.get(0));
        }
        List<MeritList> mscMerits = this.getSubjectMeritList(code, year, form, term, "Msc", meritLists);
        Collections.sort(mscMerits, new SortByMsc().reversed());
        if (mscMerits.size() > 0) {
            model.addAttribute("MscGiant", (Object)mscMerits.get(0));
        }
        List<MeritList> bsMerits = this.getSubjectMeritList(code, year, form, term, "Bs", meritLists);
        Collections.sort(bsMerits, new SortByBs().reversed());
        if (bsMerits.size() > 0) {
            model.addAttribute("BsGiant", (Object)bsMerits.get(0));
        }
        List<MeritList> dndMerits = this.getSubjectMeritList(code, year, form, term, "Dnd", meritLists);
        Collections.sort(dndMerits, new SortByDnd().reversed());
        if (dndMerits.size() > 0) {
            model.addAttribute("DndGiant", (Object)dndMerits.get(0));
        }
        Collections.sort(meritLists, new SortByTotal().reversed());
        for (int j = 0; j < gds.length; ++j) {
            int totalS = 0;
            block148: for (int i4 = 0; i4 < subjects.size(); ++i4) {
                int count = 0;
                switch (subjects.get(i4).getInitials()) {
                    case "Maths": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (((GradeCount)gradeCounts.get(k)).getMaths() != gds[j]) continue;
                            ++count;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                        continue block148;
                    }
                    case "Eng": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (((GradeCount)gradeCounts.get(k)).getEng() != gds[j]) continue;
                            ++count;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                        continue block148;
                    }
                    case "Kis": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (((GradeCount)gradeCounts.get(k)).getKis() != gds[j]) continue;
                            ++count;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                        continue block148;
                    }
                    case "Bio": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (((GradeCount)gradeCounts.get(k)).getBio() != gds[j]) continue;
                            ++count;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                        continue block148;
                    }
                    case "Chem": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (((GradeCount)gradeCounts.get(k)).getChem() != gds[j]) continue;
                            ++count;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                        continue block148;
                    }
                    case "Phy": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (((GradeCount)gradeCounts.get(k)).getPhy() != gds[j]) continue;
                            ++count;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                        continue block148;
                    }
                    case "Hist": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (((GradeCount)gradeCounts.get(k)).getHist() != gds[j]) continue;
                            ++count;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                        continue block148;
                    }
                    case "C.R.E": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (((GradeCount)gradeCounts.get(k)).getCre() != gds[j]) continue;
                            ++count;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + "CreCount", (Object)count);
                        continue block148;
                    }
                    case "Geo": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (((GradeCount)gradeCounts.get(k)).getGeo() != gds[j]) continue;
                            ++count;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                        continue block148;
                    }
                    case "I.R.E": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (((GradeCount)gradeCounts.get(k)).getIre() != gds[j]) continue;
                            ++count;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + "IreCount", (Object)count);
                        continue block148;
                    }
                    case "H.R.E": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (((GradeCount)gradeCounts.get(k)).getHre() != gds[j]) continue;
                            ++count;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + "HreCount", (Object)count);
                        continue block148;
                    }
                    case "Hsci": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (((GradeCount)gradeCounts.get(k)).getHsci() != gds[j]) continue;
                            ++count;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                        continue block148;
                    }
                    case "AnD": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (((GradeCount)gradeCounts.get(k)).getAnd() != gds[j]) continue;
                            ++count;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                        continue block148;
                    }
                    case "Agric": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (((GradeCount)gradeCounts.get(k)).getAgric() != gds[j]) continue;
                            ++count;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                        continue block148;
                    }
                    case "Comp": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (((GradeCount)gradeCounts.get(k)).getComp() != gds[j]) continue;
                            ++count;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                        continue block148;
                    }
                    case "Avi": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (((GradeCount)gradeCounts.get(k)).getAvi() != gds[j]) continue;
                            ++count;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                        continue block148;
                    }
                    case "Elec": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (((GradeCount)gradeCounts.get(k)).getElec() != gds[j]) continue;
                            ++count;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                        continue block148;
                    }
                    case "Pwr": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (((GradeCount)gradeCounts.get(k)).getPwr() != gds[j]) continue;
                            ++count;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                        continue block148;
                    }
                    case "Wood": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (((GradeCount)gradeCounts.get(k)).getWood() != gds[j]) continue;
                            ++count;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                        continue block148;
                    }
                    case "Metal": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (((GradeCount)gradeCounts.get(k)).getMetal() != gds[j]) continue;
                            ++count;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                        continue block148;
                    }
                    case "Bc": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (((GradeCount)gradeCounts.get(k)).getBc() != gds[j]) continue;
                            ++count;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                        continue block148;
                    }
                    case "Fren": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (((GradeCount)gradeCounts.get(k)).getFren() != gds[j]) continue;
                            ++count;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                        continue block148;
                    }
                    case "Germ": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (((GradeCount)gradeCounts.get(k)).getGerm() != gds[j]) continue;
                            ++count;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                        continue block148;
                    }
                    case "Arab": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (((GradeCount)gradeCounts.get(k)).getArab() != gds[j]) continue;
                            ++count;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                        continue block148;
                    }
                    case "Msc": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (((GradeCount)gradeCounts.get(k)).getMsc() != gds[j]) continue;
                            ++count;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                        continue block148;
                    }
                    case "Bs": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (((GradeCount)gradeCounts.get(k)).getBs() != gds[j]) continue;
                            ++count;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                        continue block148;
                    }
                    case "Dnd": {
                        int k;
                        for (k = 0; k < gradeCounts.size(); ++k) {
                            if (((GradeCount)gradeCounts.get(k)).getDnd() != gds[j]) continue;
                            ++count;
                            ++totalS;
                        }
                        model.addAttribute(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                    }
                }
            }
            model.addAttribute(grades[j] + "Count", (Object)totalS);
        }
        for (i = 0; i < students.size(); ++i) {
            model.addAttribute("Points" + students.get(i).getAdmNo(), (Object)this.getPoints(students.get(i).getKcpeMarks() / 5));
        }
        for (i = 0; i < meritLists.size(); ++i) {
            model.addAttribute("Mpoints" + ((MeritList)meritLists.get(i)).getAdmNo(), (Object)this.getPoints(((MeritList)meritLists.get(i)).getAverage()));
        }
        model.addAttribute("meritLists", meritLists);
        model.addAttribute("activeUser", (Object)activeUser);
        model.addAttribute("school", (Object)school);
        model.addAttribute("student", (Object)student);
        model.addAttribute("year", (Object)year);
        model.addAttribute("form", (Object)form);
        model.addAttribute("term", (Object)term);
        model.addAttribute("marks", allMarks);
        model.addAttribute("subjects", subjects);
        model.addAttribute("streams", this.streamService.getStreamsInSchool(code));
        model.addAttribute("students", students);
        model.addAttribute("studentsWithoutMarks", studentsWithoutMarks);
        model.addAttribute("MathsCount", (Object)mathsCount);
        model.addAttribute("EngCount", (Object)engCount);
        model.addAttribute("KisCount", (Object)kisCount);
        model.addAttribute("BioCount", (Object)bioCount);
        model.addAttribute("ChemCount", (Object)chemCount);
        model.addAttribute("PhyCount", (Object)phyCount);
        model.addAttribute("HistCount", (Object)histCount);
        model.addAttribute("creCount", (Object)creCount);
        model.addAttribute("GeoCount", (Object)geoCount);
        model.addAttribute("ireCount", (Object)ireCount);
        model.addAttribute("hreCount", (Object)hreCount);
        model.addAttribute("HsciCount", (Object)hsciCount);
        model.addAttribute("AnDCount", (Object)andCount);
        model.addAttribute("AgricCount", (Object)agricCount);
        model.addAttribute("CompCount", (Object)compCount);
        model.addAttribute("AviCount", (Object)aviCount);
        model.addAttribute("ElecCount", (Object)elecCount);
        model.addAttribute("PwrCount", (Object)pwrCount);
        model.addAttribute("WoodCount", (Object)woodCount);
        model.addAttribute("MetalCount", (Object)metalCount);
        model.addAttribute("BcCount", (Object)bcCount);
        model.addAttribute("FrenCount", (Object)frenCount);
        model.addAttribute("GermCount", (Object)germCount);
        model.addAttribute("ArabCount", (Object)arabCount);
        model.addAttribute("MscCount", (Object)mscCount);
        model.addAttribute("BsCount", (Object)bsCount);
        model.addAttribute("DndCount", (Object)dndCount);
        return "meritList";
    }

    public String getGrade(int mark) {
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
    }

    public int getPoints(int mark) {
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

    public List<MeritList> getSubjectMeritList(int code, int year, int form, int term, String initials, List<MeritList> meritLists) {
        List<Student> subjectStudents = this.studentService.findAllStudentDoingSubject(code, year, form, term, initials);
        ArrayList<MeritList> subjectMerits = new ArrayList<MeritList>();
        for (int i = 0; i < meritLists.size(); ++i) {
            if (subjectStudents.size() == 0) {
                MeritList meritList = new MeritList("-", "-", "-", "-");
                subjectMerits.add(meritList);
                break;
            }
            for (int j = 0; j < subjectStudents.size(); ++j) {
                if (meritLists.get(i).getAdmNo() != subjectStudents.get(j).getAdmNo()) continue;
                subjectMerits.add(meritLists.get(i));
            }
        }
        return subjectMerits;
    }
}

class SortByTotal implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getTotal() - b.getTotal();
	}
}

class SortByMaths implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getMaths() - b.getMaths();
	}
}

class SortByEng implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getEng() - b.getEng();
	}
}

class SortByKis implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getKis() - b.getKis();
	}
}

class SortByBio implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getBio() - b.getBio();
	}
}

class SortByChem implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getChem() - b.getChem();
	}
}

class SortByPhy implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getPhy() - b.getPhy();
	}
}

class SortByHist implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getHist() - b.getHist();
	}
}

class SortByCre implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getCre() - b.getCre();
	}
}

class SortByGeo implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getGeo() - b.getGeo();
	}
}

class SortByIre implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getIre() - b.getIre();
	}
}

class SortByHre implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getHre() - b.getHre();
	}
}

class SortByHsci implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getHsci() - b.getHsci();
	}
}

class SortByAnd implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getAnD() - b.getAnD();
	}
}

class SortByAgric implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getAgric() - b.getAgric();
	}
}

class SortByComp implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getComp() - b.getComp();
	}
}

class SortByAvi implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getAvi() - b.getAvi();
	}
}

class SortByElec implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getElec() - b.getElec();
	}
}

class SortByPwr implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getPwr() - b.getPwr();
	}
}

class SortByWood implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getWood() - b.getWood();
	}
}

class SortByMetal implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getMetal() - b.getMetal();
	}
}

class SortByBc implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getBc() - b.getBc();
	}
}

class SortByFren implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getFren() - b.getFren();
	}
}

class SortByGerm implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getGerm() - b.getGerm();
	}
}

class SortByArab implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getArab() - b.getArab();
	}
}

class SortByMsc implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getMsc() - b.getMsc();
	}
}

class SortByBs implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getBs() - b.getBs();
	}
}

class SortByDnd implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getDnd() - b.getDnd();
	}
}

class SortByDeviation implements Comparator<MeritList> {
	public int compare(MeritList a, MeritList b) {
		return a.getDeviation() - b.getDeviation();
	}
}