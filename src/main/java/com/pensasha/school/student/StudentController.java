package com.pensasha.school.student;

import com.pensasha.school.exam.Mark;
import com.pensasha.school.exam.MarkService;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
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

    public StudentController(StudentService studentService, SchoolService schoolService, UserService userService, StreamService streamService, YearService yearService, SubjectService subjectService, FormService formService, TermService termService, MarkService markService) {
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

    @GetMapping(value={"/schools/{code}/students"})
    public String allStudents(@PathVariable int code, Model model, Principal principal) {
        List<Student> students = this.studentService.getAllStudentsInSchool(code);
        School school = this.schoolService.getSchool(code).get();
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        List<Stream> streams = this.streamService.getStreamsInSchool(code);
        Student student = new Student();
        List<Subject> subjects = this.subjectService.getAllSubjectInSchool(code);
        List<Year> years = this.yearService.getAllYearsInSchool(code);
        model.addAttribute("years", years);
        model.addAttribute("subjects", subjects);
        model.addAttribute("streams", streams);
        model.addAttribute("activeUser", (Object)activeUser);
        model.addAttribute("student", (Object)student);
        model.addAttribute("school", (Object)school);
        model.addAttribute("students", students);
        return "students";
    }

    @GetMapping(value={"/schools/{code}/addStudent"})
    public String addStudent(Model model, Principal principal, @PathVariable int code) {
        School school = this.schoolService.getSchool(code).get();
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        List<Stream> streams = this.streamService.getStreamsInSchool(code);
        if (!model.containsAttribute("student")) {
            model.addAttribute("student", (Object)new Student());
        }
        model.addAttribute("streams", streams);
        model.addAttribute("school", (Object)school);
        model.addAttribute("activeUser", (Object)activeUser);
        return "addStudent";
    }

    @GetMapping(value={"/schools/{code}/editStudent/{admNo}"})
    public String editStudent(Model model, Principal principal, @PathVariable int code, @PathVariable String admNo) {
        School school = this.schoolService.getSchool(code).get();
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        List<Stream> streams = this.streamService.getStreamsInSchool(code);
        if (!model.containsAttribute("student")) {
            model.addAttribute("student", (Object)this.studentService.getStudentInSchool(admNo, code));
        }
        model.addAttribute("streams", streams);
        model.addAttribute("school", (Object)school);
        model.addAttribute("activeUser", (Object)activeUser);
        return "editStudent";
    }

    @PostMapping(value={"/schools/{code}/editStudent/{admNo}"})
    public String updateStudent(@PathVariable int code, @ModelAttribute @Valid Student student, @PathVariable String admNo, BindingResult bindingResult, RedirectAttributes redit, Principal principal, @RequestParam int stream) throws IOException {
        String view;
        if (bindingResult.hasErrors()) {
            redit.addFlashAttribute("org.springframework.validation.BindingResult.student", (Object)bindingResult);
            redit.addFlashAttribute("student", (Object)student);
            view = "redirect:/schools/" + code + "/addStudent";
        } else {
            Student studentObj = this.studentService.getStudentInSchool(admNo, code);
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
            studentObj.setStream(this.streamService.getStream(stream));
            studentObj.setGender(student.getGender());
            studentObj.setSponsor(student.getSponsor());
            studentObj.setKcpeMarks(student.getKcpeMarks());
            studentObj.setScholar(student.getScholar());
            studentObj.setYearAdmitted(student.getYearAdmitted());
            studentObj.setCurrentForm(student.getCurrentForm());
            this.studentService.addStudent(studentObj);
            String[] admString = student.getAdmNo().split("_");
            redit.addFlashAttribute("success", (Object)("Student with admision number: " + admString[0] + " updated successfully"));
            view = "redirect:/schools/" + code + "/students";
        }
        return view;
    }

    @PostMapping(value={"/schools/{code}/students"})
    public String addStudent(@RequestParam(value="file") MultipartFile file, @PathVariable int code, @ModelAttribute @Valid Student student, BindingResult bindingResult, RedirectAttributes redit, Principal principal, @RequestParam int stream) throws IOException {
        String view;
        String[] admString = student.getAdmNo().split("_");
        if (bindingResult.hasErrors()) {
            redit.addFlashAttribute("org.springframework.validation.BindingResult.student", (Object)bindingResult);
            redit.addFlashAttribute("student", (Object)student);
            view = "redirect:/schools/" + code + "/addStudent";
        } else {
            ArrayList<School> schools = new ArrayList<School>();
            School school = this.schoolService.getSchool(code).get();
            schools.add(school);
            if (this.studentService.ifStudentExistsInSchool(code + "_" + student.getAdmNo(), code).booleanValue()) {
                redit.addFlashAttribute("fail", (Object)("Student with admision number: " + admString[0] + " already exists"));
            } else if (student.getAdmNo() == "" || Integer.parseInt(student.getAdmNo()) < 1) {
                redit.addFlashAttribute("fail", (Object)"Admission number cannot be less than 1");
            } else {
                Iterator<Subject> iterator;
                student.setSchool(this.schoolService.getSchool(code).get());
                if (file.isEmpty()) {
                    student.setPhoto("noStudent");
                } else {

                    String path = new File("src/main/resources/static/studImg").getAbsolutePath();
                    String fileName = school.getCode() + "_" + student.getAdmNo();

                    OutputStream out = null;
                    InputStream filecontent = null;
                    try {
                        out = new FileOutputStream(new File(path + File.separator + fileName));
                        filecontent = file.getInputStream();
                        int read = 0;
                        byte[] bytes = new byte[1024];
                        while ((read = filecontent.read(bytes)) != -1) {
                            out.write(bytes, 0, read);
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally {
                        if (out != null) {
                            out.close();
                        }
                        if (filecontent != null) {
                            filecontent.close();
                        }
                    }
                    student.setPhoto(student.getAdmNo());
                }
                Stream streamObj = this.streamService.getStream(stream);
                student.setStream(streamObj);
                Year year = new Year(student.getYearAdmitted());
                ArrayList<Year> years = new ArrayList<Year>();
                years.add(year);
                student.setYears(years);
                ArrayList<Term> terms = new ArrayList<Term>();
                terms.add(new Term(1));
                terms.add(new Term(2));
                terms.add(new Term(3));
                for (int i = 0; i < terms.size(); ++i) {
                    this.termService.addTerm((Term)terms.get(i));
                }
                ArrayList<Form> forms = new ArrayList<Form>();
                switch (student.getCurrentForm()) {
                    case 1: {
                        forms.add(new Form(1, terms));
                        break;
                    }
                    case 2: {
                        forms.add(new Form(1, terms));
                        forms.add(new Form(2, terms));
                        break;
                    }
                    case 3: {
                        forms.add(new Form(1, terms));
                        forms.add(new Form(2, terms));
                        forms.add(new Form(3, terms));
                    }
                    case 4: {
                        forms.add(new Form(1, terms));
                        forms.add(new Form(2, terms));
                        forms.add(new Form(3, terms));
                        forms.add(new Form(4, terms));
                    }
                }
                for (int i = 0; i < forms.size(); ++i) {
                    this.formService.addForm((Form)forms.get(i));
                }
                if (!this.yearService.doesYearExist(year.getYear())) {
                    year.setSchools(schools);
                    year.setForms(forms);
                    this.yearService.addYear(year);
                } else {
                    List<School> newSchools = this.schoolService.getSchoolsByYear(year.getYear());
                    if (newSchools.contains(school)) {
                        newSchools.remove(school);
                    }
                    schools.addAll(newSchools);
                    year.setSchools(schools);
                    ArrayList<Form> newForms = new ArrayList<Form>();
                    for (int i = 0; i < forms.size(); ++i) {
                        if (this.formService.doesFormExists(((Form)forms.get(i)).getForm()).booleanValue()) {
                            newForms.add(this.formService.getFormByForm(((Form)forms.get(i)).getForm()));
                            continue;
                        }
                        newForms.add((Form)forms.get(i));
                    }
                    year.setForms(newForms);
                    this.yearService.addYear(year);
                }
                student.setForms(forms);
                student.setAdmNo(school.getCode() + "_" + student.getAdmNo());
                ArrayList<Subject> subjects = new ArrayList<Subject>();
                if (student.getCurrentForm() == 1 || student.getCurrentForm() == 2) {
                    iterator = school.getCompSubjectF1F2().iterator();
                    while (iterator.hasNext()) {
                        subjects.add(iterator.next());
                    }
                } else if (student.getCurrentForm() == 3 || student.getCurrentForm() == 4) {
                    iterator = school.getCompSubjectF3F4().iterator();
                    while (iterator.hasNext()) {
                        subjects.add(iterator.next());
                    }
                }
                student.setSubjects(subjects);
                this.studentService.addStudent(student);
                redit.addFlashAttribute("success", (Object)("Student with admision number: " + admString[0] + " saved successfully"));
            }
            view = "redirect:/schools/" + code + "/students";
        }
        return view;
    }

    @GetMapping(value={"/schools/{code}/students/{admNo}"})
    public String deleteStudent(@PathVariable int code, @PathVariable String admNo, Model model, Principal principal) {
        if (this.studentService.ifStudentExistsInSchool(admNo, code).booleanValue()) {
            List<Mark> marks = this.markService.allMarks(admNo);
            for (int i = 0; i < marks.size(); ++i) {
                this.markService.deleteMark(marks.get(i).getId());
            }
            this.studentService.deleteStudent(admNo);
            model.addAttribute("success", (Object)("Student with admision number:" + admNo + " successfully deleted"));
        } else {
            model.addAttribute("fail", (Object)("Student with admision number:" + admNo + " does not exist"));
        }
        List<Student> students = this.studentService.getAllStudentsInSchool(code);
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(code).get();
        List<Stream> streams = this.streamService.getStreamsInSchool(code);
        Student student = new Student();
        List<Subject> subjects = this.subjectService.getAllSubjectInSchool(code);
        List<Year> years = this.yearService.getAllYearsInSchool(code);
        model.addAttribute("years", years);
        model.addAttribute("subjects", subjects);
        model.addAttribute("streams", streams);
        model.addAttribute("activeUser", (Object)activeUser);
        model.addAttribute("student", (Object)student);
        model.addAttribute("school", (Object)school);
        model.addAttribute("students", students);
        if (activeUser.getRole().getName().contains("TEACHER")) {
            return "teacherHome";
        }
        return "students";
    }

    @GetMapping(value={"/schools/{code}/student/{admNo}"})
    public String getStudent(@PathVariable int code, @PathVariable String admNo, Model model, Principal principal) {
        Student student = this.studentService.getStudentInSchool(admNo, code);
        School school = this.schoolService.getSchool(code).get();
        List<Subject> subjects = this.subjectService.getSubjectDoneByStudent(admNo);
        List<Subject> schoolSubjects = this.subjectService.getAllSubjectInSchool(code);
        for (int i = 0; i < subjects.size(); ++i) {
            schoolSubjects.remove(subjects.get(i));
        }
        List<Form> forms = this.formService.studentForms(admNo);
        User activeUser = this.userService.getByUsername(principal.getName()).get();
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
        List<Stream> streams = this.streamService.getStreamsInSchool(code);
        List<Year> years = this.yearService.getAllYearsInSchool(code);
        model.addAttribute("streams", streams);
        model.addAttribute("group1", group1);
        model.addAttribute("group2", group2);
        model.addAttribute("group3", group3);
        model.addAttribute("group4", group4);
        model.addAttribute("group5", group5);
        model.addAttribute("activeUser", (Object)activeUser);
        model.addAttribute("years", years);
        model.addAttribute("forms", forms);
        model.addAttribute("school", (Object)school);
        model.addAttribute("subjects", subjects);
        model.addAttribute("schoolSubjects", schoolSubjects);
        model.addAttribute("student", (Object)student);
        return "student";
    }
}