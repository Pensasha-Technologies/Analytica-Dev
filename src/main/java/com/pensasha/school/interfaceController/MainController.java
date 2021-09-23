package com.pensasha.school.interfaceController;

import com.pensasha.school.discipline.Discipline;
import com.pensasha.school.discipline.DisciplineService;
import com.pensasha.school.exam.Mark;
import com.pensasha.school.exam.MarkService;
import com.pensasha.school.form.Form;
import com.pensasha.school.form.FormService;
import com.pensasha.school.role.Role;
import com.pensasha.school.role.RoleService;
import com.pensasha.school.school.School;
import com.pensasha.school.school.SchoolService;
import com.pensasha.school.sms.Gateway;
import com.pensasha.school.stream.Stream;
import com.pensasha.school.stream.StreamService;
import com.pensasha.school.student.Student;
import com.pensasha.school.student.StudentService;
import com.pensasha.school.subject.Subject;
import com.pensasha.school.subject.SubjectService;
import com.pensasha.school.term.Term;
import com.pensasha.school.term.TermService;
import com.pensasha.school.user.SchoolUser;
import com.pensasha.school.user.Teacher;
import com.pensasha.school.user.User;
import com.pensasha.school.user.UserService;
import com.pensasha.school.year.Year;
import com.pensasha.school.year.YearService;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class MainController {
    private SchoolService schoolService;
    private StudentService studentService;
    private TermService termService;
    private SubjectService subjectService;
    private FormService formService;
    private YearService yearService;
    private MarkService markService;
    private UserService userService;
    private RoleService roleService;
    private StreamService streamService;
    private DisciplineService disciplineService;

    public MainController(SchoolService schoolService, StudentService studentService, TermService termService, SubjectService subjectService, FormService formService, YearService yearService, MarkService markService, UserService userService, RoleService roleService, StreamService streamService, DisciplineService disciplineService) {
        this.schoolService = schoolService;
        this.studentService = studentService;
        this.termService = termService;
        this.subjectService = subjectService;
        this.formService = formService;
        this.yearService = yearService;
        this.markService = markService;
        this.userService = userService;
        this.roleService = roleService;
        this.streamService = streamService;
        this.disciplineService = disciplineService;
    }

    @GetMapping(value={"index"})
    public String index(Principal principal, Model model) {
        User user = new User();
        model.addAttribute("activeUser", (Object)user);
        return "index";
    }

    @GetMapping(value={"login"})
    public String login(Principal principal, Model model) {
        User user = new User();
        model.addAttribute("activeUser", (Object)user);
        return "login";
    }

    @GetMapping(value={"changePassword"})
    public String changePassword(Model model) {
        User user = new User();
        model.addAttribute("activeUser", (Object)user);
        return "changePassword";
    }

    @PostMapping(value={"/resetPassword/{username}"})
    public String postResetPassword(RedirectAttributes redit, @PathVariable String username, @RequestParam String currentPassword, @RequestParam String newPassword, @RequestParam String confirmNewPassword) {
        User user = this.userService.getByUsername(username).get();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!newPassword.contentEquals(confirmNewPassword)) {
            redit.addFlashAttribute("fail", (Object)"New password and Confirm Password do not match");
        } else if (!encoder.matches((CharSequence)currentPassword, user.getPassword())) {
            redit.addFlashAttribute("fail", (Object)"Current Password is incorrect");
        } else {
            user.setPassword(encoder.encode((CharSequence)newPassword));
            this.userService.addUser(user);
            redit.addFlashAttribute("success", (Object)"Password changed successfully");
        }
        return "redirect:/profile/" + username;
    }

    @PostMapping(value={"/changePassword"})
    public String postChangePassword(Model model, @RequestParam int phoneNumber, @RequestParam String username) {
        User user = new User();
        model.addAttribute("activeUser", (Object)user);
        if (this.userService.userExists(username).booleanValue()) {
            User testUser = this.userService.getByUsername(username).get();
            if (testUser.getPhoneNumber() == phoneNumber) {
                String baseUrl = "https://mysms.celcomafrica.com/api/services/sendsms/";
                int partnerId = 1989;
                String apiKey = "da383ff9c9edfb614bc7d1abfe8b1599";
                String shortCode = "analytica";
                Gateway gateway = new Gateway(baseUrl, partnerId, apiKey, shortCode);
                Random random = new Random();
                int otp = random.nextInt(9999);
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                testUser.setPassword(encoder.encode((CharSequence)Integer.toString(++otp)));
                this.userService.addUser(testUser);
                try {
                    String res = gateway.sendSingleSms("Your Username is: " + testUser.getUsername() + " password is: " + otp + "Thanks for Registering with Analytica Soft.", Integer.toString(testUser.getPhoneNumber()));
                    System.out.println(res);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                model.addAttribute("success", (Object)"Password send to phone Number");
                return "login";
            }
            model.addAttribute("fail", (Object)"Phone number does not match");
            return "changePassword";
        }
        model.addAttribute("fail", (Object)"A user with this username does not exist");
        return "changePassword";
    }

    @GetMapping(value={"/adminHome"})
    public String allSchool(Model model, Principal principal) {
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        List<School> schools = this.schoolService.getAllSchools();
        School school = new School();
        Student student = new Student();
        model.addAttribute("activeUser", (Object)activeUser);
        model.addAttribute("student", (Object)student);
        model.addAttribute("schools", schools);
        model.addAttribute("school", (Object)school);
        return "adminHome";
    }

    @GetMapping(value={"/schools/{code}/student/{admNo}/progress"})
    public String viewStudentsProgressReport(@PathVariable int code, @PathVariable String admNo, Model model, Principal principal) {
        School school = this.schoolService.getSchool(code).get();
        Student student = this.studentService.getStudentInSchool(admNo, code);
        List<Subject> subjects = this.subjectService.getSubjectDoneByStudent(admNo);
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        model.addAttribute("activeUser", (Object)activeUser);
        model.addAttribute("subjects", subjects);
        model.addAttribute("student", (Object)student);
        model.addAttribute("student", (Object)school);
        return "progressReport";
    }

    @GetMapping(value={"/schools/{code}/student/{admNo}/yearly"})
    public String getYearlyReport(@PathVariable int code, @PathVariable String admNo, Model model, Principal principal) {
        School school = this.schoolService.getSchool(code).get();
        Student student = this.studentService.getStudentInSchool(admNo, code);
        List<Subject> subjects = this.subjectService.getSubjectDoneByStudent(admNo);
        List<Year> years = this.yearService.allYearsForStudent(admNo);
        List<Form> forms = this.formService.studentForms(admNo);
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        List<Mark> marks = this.markService.allMarks(admNo);
        model.addAttribute("activeUser", (Object)activeUser);
        model.addAttribute("marks", marks);
        model.addAttribute("forms", forms);
        model.addAttribute("years", years);
        model.addAttribute("subjects", subjects);
        model.addAttribute("student", (Object)student);
        model.addAttribute("school", (Object)school);
        return "yearlyReport";
    }

    @GetMapping(value={"/ceoHome"})
    public String ceoHomepage(Model model, Principal principal) {
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        List<School> schools = this.schoolService.getAllSchools();
        School school = new School();
        Student student = new Student();
        model.addAttribute("activeUser", (Object)activeUser);
        model.addAttribute("student", (Object)student);
        model.addAttribute("schools", schools);
        model.addAttribute("school", (Object)school);
        return "ceoHome";
    }

    @GetMapping(value={"/officeAssistantHome"})
    public String officeAssistantHomepage(Model model, Principal principal) {
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        List<School> schools = this.schoolService.getAllSchools();
        School school = new School();
        Student student = new Student();
        model.addAttribute("activeUser", (Object)activeUser);
        model.addAttribute("student", (Object)student);
        model.addAttribute("schools", schools);
        model.addAttribute("school", (Object)school);
        return "officeAssistantHome";
    }

    @GetMapping(value={"/fieldOfficerHome"})
    public String fieldOfficerHomepage(Model model, Principal principal) {
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        List<School> schools = this.schoolService.getAllSchools();
        School school = new School();
        Student student = new Student();
        model.addAttribute("activeUser", (Object)activeUser);
        model.addAttribute("student", (Object)student);
        model.addAttribute("schools", schools);
        model.addAttribute("school", (Object)school);
        return "fieldOfficerHome";
    }

    @GetMapping(value={"/schools/users/{username}"})
    public String deleteSchoolUser(@PathVariable String username, RedirectAttributes redit, Principal principal) {
        if (this.userService.userExists(username).booleanValue()) {
            if (username.contentEquals(principal.getName())) {
                redit.addFlashAttribute("fail", (Object)"You cannot delete yourself");
            } else {
                this.userService.deleteUser(username);
                redit.addFlashAttribute("success", (Object)(username + " successfully deleted"));
            }
        } else {
            redit.addFlashAttribute("fail", (Object)("A user with username " + username + " does not exist"));
        }
        return "redirect:/principalHome";
    }

    @GetMapping(value={"/schools/principal"})
    public String principalHome(Model model, Principal principal) {
        SchoolUser activeUser = (SchoolUser)this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(activeUser.getSchool().getCode()).get();
        Student student = new Student();
        User user = new User();
        List<SchoolUser> schoolUsers = this.userService.getUsersBySchoolCode(school.getCode());
        List<Year> years = this.yearService.getAllYearsInSchool(school.getCode());
        List<Stream> streams = this.streamService.getStreamsInSchool(school.getCode());
        List<Subject> subjects = this.subjectService.getAllSubjectInSchool(school.getCode());
        model.addAttribute("subjects", subjects);
        model.addAttribute("streams", streams);
        model.addAttribute("years", years);
        model.addAttribute("schoolUsers", schoolUsers);
        model.addAttribute("user", (Object)user);
        model.addAttribute("activeUser", (Object)activeUser);
        model.addAttribute("student", (Object)student);
        model.addAttribute("school", (Object)school);
        return "principalHome";
    }

    @GetMapping(value={"/schools/{code}/sms"})
    public String schoolSms(Model model, Principal principal) {
        SchoolUser activeUser = (SchoolUser)this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(activeUser.getSchool().getCode()).get();
        Student student = new Student();
        List<SchoolUser> schoolUsers = this.userService.getUsersBySchoolCode(school.getCode());
        User user = new User();
        List<Year> years = this.yearService.getAllYearsInSchool(school.getCode());
        List<Stream> streams = this.streamService.getStreamsInSchool(school.getCode());
        List<Subject> subjects = this.subjectService.getAllSubjectInSchool(school.getCode());
        model.addAttribute("subjects", subjects);
        model.addAttribute("streams", streams);
        model.addAttribute("years", years);
        model.addAttribute("schoolUsers", schoolUsers);
        model.addAttribute("user", (Object)user);
        model.addAttribute("activeUser", (Object)activeUser);
        model.addAttribute("student", (Object)student);
        model.addAttribute("school", (Object)school);
        return "smsHome";
    }

    @PostMapping(value={"/schools/{code}/sms"})
    public String sendSchoolSms(RedirectAttributes redit, @PathVariable int code, Principal principal, @RequestParam String recipientPhoneNumber, @RequestParam String message) {
        String baseUrl = "https://mysms.celcomafrica.com/api/services/sendsms/";
        int partnerId = 1989;
        String apiKey = "da383ff9c9edfb614bc7d1abfe8b1599";
        String shortCode = "analytica";
        Gateway gateway = new Gateway(baseUrl, partnerId, apiKey, shortCode);
        try {
            String res = gateway.sendSingleSms("Hello From Single sms Java", "0707335375");
            System.out.println(res);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        redit.addFlashAttribute("success", (Object)"Message sent successfully");
        return "redirect:/principalHome";
    }

    @GetMapping(value={"/schools/students"})
    public String schoolStudents(Model model, Principal principal) {
        SchoolUser activeUser = (SchoolUser)this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(activeUser.getSchool().getCode()).get();
        Student student = new Student();
        List<Student> students = this.studentService.getAllStudentsInSchool(school.getCode());
        List<Stream> streams = this.streamService.getStreamsInSchool(school.getCode());
        List<Subject> subjects = this.subjectService.getAllSubjectInSchool(school.getCode());
        List<Year> years = this.yearService.getAllYearsInSchool(activeUser.getSchool().getCode());
        model.addAttribute("years", years);
        model.addAttribute("subjects", subjects);
        model.addAttribute("streams", streams);
        model.addAttribute("students", students);
        model.addAttribute("activeUser", (Object)activeUser);
        model.addAttribute("student", (Object)student);
        model.addAttribute("school", (Object)school);
        return "students";
    }

    @GetMapping(value={"/schools/students/{admNo}"})
    public String schoolStudents(Model model, Principal principal, @PathVariable String admNo) {
        SchoolUser activeUser = (SchoolUser)this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(activeUser.getSchool().getCode()).get();
        Student student = new Student();
        List<Form> forms = this.formService.studentForms(admNo);
        List<Subject> subjects = this.subjectService.getSubjectDoneByStudent(admNo);
        List<Subject> schoolSubjects = this.subjectService.getAllSubjectInSchool(school.getCode());
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
        List<Stream> streams = this.streamService.getStreamsInSchool(school.getCode());
        List<Year> years = this.yearService.getAllYearsInSchool(school.getCode());
        model.addAttribute("years", years);
        model.addAttribute("streams", streams);
        model.addAttribute("group1", group1);
        model.addAttribute("group2", group2);
        model.addAttribute("group3", group3);
        model.addAttribute("group4", group4);
        model.addAttribute("group5", group5);
        model.addAttribute("activeUser", (Object)activeUser);
        model.addAttribute("forms", forms);
        model.addAttribute("school", (Object)school);
        model.addAttribute("subjects", subjects);
        model.addAttribute("schoolSubjects", schoolSubjects);
        model.addAttribute("student", (Object)student);
        return "student";
    }

    @PostMapping(value={"/schools/students"})
    public String addSchoolStudents(RedirectAttributes redit, @Valid Student student, BindingResult bindingResult, Principal principal, @RequestParam int stream) {
        SchoolUser activeUser = (SchoolUser)this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(activeUser.getSchool().getCode()).get();
        student.setSchool(school);
        if (this.studentService.ifStudentExistsInSchool(school.getCode() + "_" + student.getAdmNo(), school.getCode()).booleanValue()) {
            redit.addFlashAttribute("fail", (Object)("Student with adm No:" + student.getAdmNo() + " already exists"));
        } else {
            ArrayList<School> schools = new ArrayList<School>();
            schools.add(school);
            Stream streamObj = this.streamService.getStream(stream);
            student.setStream(streamObj);
            ArrayList<Year> years = new ArrayList<Year>();
            years.add(new Year(student.getYearAdmitted()));
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
                    break;
                }
                case 4: {
                    forms.add(new Form(1, terms));
                    forms.add(new Form(2, terms));
                    forms.add(new Form(3, terms));
                    forms.add(new Form(4, terms));
                    break;
                }
            }
            for (int i = 0; i < forms.size(); ++i) {
                this.formService.addForm((Form)forms.get(i));
            }
            Year year = new Year(student.getYearAdmitted());
            year.setSchools(schools);
            year.setForms(forms);
            this.yearService.addYear(year);
            student.setForms(forms);
            student.setAdmNo(school.getCode() + "_" + student.getAdmNo());
            this.studentService.addStudent(student);
            redit.addFlashAttribute("success", (Object)("Student with adm No: " + student.getAdmNo() + " added successfully"));
        }
        List<Subject> subjects = this.subjectService.getAllSubjectInSchool(school.getCode());
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
        return "redirect:/teacherHome";
    }

    @GetMapping(value={"/school/teachers"})
    public String schoolTeachers(Model model, Principal principal) {
        SchoolUser activeUser = (SchoolUser)this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(activeUser.getSchool().getCode()).get();
        Student student = new Student();
        SchoolUser user = new SchoolUser();
        List<Stream> streams = this.streamService.getStreamsInSchool(school.getCode());
        List<User> teachers = this.userService.findUserByRole("TEACHER");
        model.addAttribute("streams", streams);
        model.addAttribute("user", (Object)user);
        model.addAttribute("teachers", teachers);
        model.addAttribute("activeUser", (Object)activeUser);
        model.addAttribute("student", (Object)student);
        model.addAttribute("school", (Object)school);
        return "teachers";
    }

    @GetMapping(value={"/schools/{code}/teachers"})
    public String teachers(Model model, Principal principal, @PathVariable int code) {
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(code).get();
        Student student = new Student();
        SchoolUser user = new SchoolUser();
        List<Stream> streams = this.streamService.getStreamsInSchool(code);
        List<Teacher> teachers = this.userService.getTeachersInSchool(code);
        model.addAttribute("streams", streams);
        model.addAttribute("user", (Object)user);
        model.addAttribute("teachers", teachers);
        model.addAttribute("activeUser", (Object)activeUser);
        model.addAttribute("student", (Object)student);
        model.addAttribute("school", (Object)school);
        return "teachers";
    }

    @PostMapping(value={"/school/teachers"})
    public String addTeacher(RedirectAttributes redit, @RequestParam String role, @RequestParam int code, @ModelAttribute SchoolUser user, Principal principal, HttpServletRequest request) {
        Teacher teacher = new Teacher(user.getUsername(), user.getFirstname(), user.getSecondname(), user.getThirdname(), user.getPassword(), user.getEmail(), user.getPhoneNumber(), user.getAddress());
        teacher.setSchool(user.getSchool());
        teacher.setTeacherNumber(request.getParameter("teacherNumber"));
        teacher.setTscNumber(request.getParameter("tscNumber"));
        teacher.setInitials(user.getFirstname().charAt(0) + "." + user.getSecondname().charAt(0) + "." + user.getThirdname().charAt(0));
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode((CharSequence)user.getUsername()));
        Role roleObj = new Role();
        teacher.setSchool(new School("", code));
        roleObj.setName("TEACHER");
        teacher.setRole(roleObj);
        this.roleService.addRole(roleObj);
        if (this.userService.userExists(user.getUsername()).booleanValue()) {
            redit.addFlashAttribute("fail", (Object)("A user with the username: " + user.getUsername() + " already exists"));
        } else {
            this.userService.addUser(teacher);
            redit.addFlashAttribute("success", (Object)"Teacher saved successfully");
        }
        return "redirect:/schools/" + code + "/teachers";
    }

    @GetMapping(value={"/school/teachers/{username}"})
    public String deleteTeacher(@PathVariable String username, RedirectAttributes redit, Principal principal) {
        if (this.userService.userExists(username).booleanValue()) {
            if (username.contentEquals(principal.getName())) {
                redit.addFlashAttribute("fail", (Object)"You cannot delete yourself");
            } else {
                this.userService.deleteUser(username);
                redit.addFlashAttribute("success", (Object)(username + " successfully deleted"));
            }
        } else {
            redit.addFlashAttribute("fail", (Object)("A teacher with username " + username + " does not exist"));
        }
        return "redirect:/school/teachers";
    }

    @GetMapping(value={"/schools/deputyPrincipal"})
    public String deputyHome(Model model, Principal principal) {
        SchoolUser activeUser = (SchoolUser)this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(activeUser.getSchool().getCode()).get();
        Student student = new Student();
        User user = new User();
        List<SchoolUser> schoolUsers = this.userService.getUsersBySchoolCode(school.getCode());
        model.addAttribute("schoolUsers", schoolUsers);
        model.addAttribute("user", (Object)user);
        model.addAttribute("activeUser", (Object)activeUser);
        model.addAttribute("student", (Object)student);
        model.addAttribute("school", (Object)school);
        return "deputyPrincipal";
    }

    @GetMapping(value={"/schools/dosHome"})
    public String dosHome(Model model, Principal principal) {
        SchoolUser activeUser = (SchoolUser)this.userService.getByUsername(principal.getName()).get();
        School school = activeUser.getSchool();
        Student student = new Student();
        User user = new User();
        List<SchoolUser> schoolUsers = this.userService.getUsersBySchoolCode(school.getCode());
        model.addAttribute("schoolUsers", schoolUsers);
        model.addAttribute("user", (Object)user);
        model.addAttribute("activeUser", (Object)activeUser);
        model.addAttribute("student", (Object)student);
        model.addAttribute("school", (Object)school);
        return "dosHome";
    }

    @GetMapping(value={"/schools/bursarHome"})
    public String bursarHome(Model model, Principal principal) {
        SchoolUser activeUser = (SchoolUser)this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(activeUser.getSchool().getCode()).get();
        Student student = new Student();
        User user = new User();
        List<SchoolUser> schoolUsers = this.userService.getUsersBySchoolCode(school.getCode());
        List<Stream> streams = this.streamService.getStreamsInSchool(school.getCode());
        List<Student> students = this.studentService.getAllStudentsInSchool(activeUser.getSchool().getCode());
        model.addAttribute("students", students);
        model.addAttribute("schoolUsers", schoolUsers);
        model.addAttribute("user", (Object)user);
        model.addAttribute("activeUser", (Object)activeUser);
        model.addAttribute("student", (Object)student);
        model.addAttribute("school", (Object)school);
        model.addAttribute("streams", streams);
        return "bursarHome";
    }

    @GetMapping(value={"/schools/accountsClerkHome"})
    public String accountsClerkHome(Model model, Principal principal) {
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(((SchoolUser)activeUser).getSchool().getCode()).get();
        Student student = new Student();
        User user = new User();
        List<SchoolUser> schoolUsers = this.userService.getUsersBySchoolCode(school.getCode());
        List<Student> students = this.studentService.getAllStudentsInSchool(school.getCode());
        List<Stream> streams = this.streamService.getStreamsInSchool(school.getCode());

        model.addAttribute("streams", streams);
        model.addAttribute("students", students);
        model.addAttribute("schoolUsers", schoolUsers);
        model.addAttribute("user", (Object)user);
        model.addAttribute("activeUser", (Object)activeUser);
        model.addAttribute("student", (Object)student);
        model.addAttribute("school", (Object)school);

        return "accountsClerkHome";
    }

    @GetMapping(value={"/teacherHome"})
    public String teacherHome(Model model, Principal principal) {
        SchoolUser activeUser = this.userService.getSchoolUserByUsername(principal.getName());
        School school = activeUser.getSchool();
        List<Student> students = this.studentService.getAllStudentsInSchool(school.getCode());
        List<Subject> subjects = this.subjectService.getAllSubjectInSchool(school.getCode());
        List<Stream> streams = this.streamService.getStreamsInSchool(school.getCode());
        List<Year> years = this.yearService.getAllYearsInSchool(school.getCode());
        model.addAttribute("years", years);
        model.addAttribute("streams", streams);
        model.addAttribute("subjects", subjects);
        model.addAttribute("student", (Object)new Student());
        model.addAttribute("students", students);
        model.addAttribute("school", (Object)school);
        model.addAttribute("activeUser", (Object)activeUser);
        return "teacherHome";
    }

    @GetMapping(value={"/schools/teachers/{username}"})
    public String teacherProfile(Principal principal, Model model) {
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        School school = ((SchoolUser)activeUser).getSchool();
        model.addAttribute("activeUser", (Object)activeUser);
        if (this.userService.userExists(activeUser.getUsername()).booleanValue()) {
            Student student = new Student();
            List<Stream> streams = this.streamService.getStreamsInSchool(school.getCode());
            List<Year> years = this.yearService.getAllYearsInSchool(school.getCode());
            model.addAttribute("years", years);
            model.addAttribute("streams", streams);
            model.addAttribute("school", (Object)school);
            model.addAttribute("student", (Object)student);
            model.addAttribute("user", (Object)activeUser);
            return "userHome";
        }
        List<Student> students = this.studentService.getAllStudentsInSchool(school.getCode());
        List<Subject> subjects = this.subjectService.getAllSubjectInSchool(school.getCode());
        List<Stream> streams = this.streamService.getStreamsInSchool(school.getCode());
        List<Year> years = this.yearService.getAllYearsInSchool(school.getCode());
        model.addAttribute("years", years);
        model.addAttribute("streams", streams);
        model.addAttribute("subjects", subjects);
        model.addAttribute("fail", (Object)("A teacher with username " + activeUser.getUsername() + " does not exist"));
        model.addAttribute("students", students);
        model.addAttribute("school", (Object)school);
        model.addAttribute("student", (Object)new Student());
        return "teacherHome";
    }

    @GetMapping(value={"/schools/{code}/discipline"})
    public String disciplineReports(@PathVariable int code, Principal principal, Model model) {
        SchoolUser activeUser = (SchoolUser)this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(activeUser.getSchool().getCode()).get();
        Student student = new Student();
        List<SchoolUser> schoolUsers = this.userService.getUsersBySchoolCode(school.getCode());
        User user = new User();
        List<Year> years = this.yearService.getAllYearsInSchool(school.getCode());
        List<Stream> streams = this.streamService.getStreamsInSchool(school.getCode());
        List<Subject> subjects = this.subjectService.getAllSubjectInSchool(school.getCode());
        List<Discipline> allDisciplineReport = this.disciplineService.allDisciplineReportBySchoolCode(code);
        model.addAttribute("disciplines", allDisciplineReport);
        model.addAttribute("subjects", subjects);
        model.addAttribute("streams", streams);
        model.addAttribute("years", years);
        model.addAttribute("schoolUsers", schoolUsers);
        model.addAttribute("user", (Object)user);
        model.addAttribute("activeUser", (Object)activeUser);
        model.addAttribute("student", (Object)student);
        model.addAttribute("school", (Object)school);
        return "discipline";
    }

    @GetMapping(value={"/schools/{code}/discipline/{id}"})
    public String disciplineReport(@PathVariable int code, @PathVariable int id, Principal principal, Model model) {
        SchoolUser activeUser = (SchoolUser)this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(activeUser.getSchool().getCode()).get();
        Discipline discipline = this.disciplineService.getDisciplineReportById(id).get();
        Student student = discipline.getStudent();
        User user = new User();
        List<Year> years = this.yearService.getAllYearsInSchool(school.getCode());
        List<Stream> streams = this.streamService.getStreamsInSchool(school.getCode());
        List<Subject> subjects = this.subjectService.getAllSubjectInSchool(school.getCode());
        model.addAttribute("discipline", (Object)discipline);
        model.addAttribute("subjects", subjects);
        model.addAttribute("streams", streams);
        model.addAttribute("years", years);
        model.addAttribute("user", (Object)user);
        model.addAttribute("activeUser", (Object)activeUser);
        model.addAttribute("student", (Object)student);
        model.addAttribute("school", (Object)school);
        return "viewDiscipline";
    }

    @GetMapping(value={"/schools/{code}/disciplines/{id}"})
    public RedirectView deleteDisciplineReport(@PathVariable int code, @PathVariable int id, RedirectAttributes redit) {
        this.disciplineService.deleteDisciplineReport(id);
        RedirectView redirectView = new RedirectView("/schools/" + code + "/discipline", true);
        redit.addFlashAttribute("success", (Object)"Discipline Report successfully deleted");
        return redirectView;
    }

    @PostMapping(value={"/schools/{code}/discipline"})
    public RedirectView addDisciplineReport(@PathVariable int code, RedirectAttributes redit, @RequestParam int admNo, @RequestParam String type, @RequestParam String depature, @RequestParam String arrival, @RequestParam String reason) {
        if (this.studentService.ifStudentExistsInSchool(code + "_" + admNo, code).booleanValue()) {
            Student student = this.studentService.getStudentInSchool(code + "_" + admNo, code);
            Discipline discipline = new Discipline(depature, arrival, reason, type, student);
            this.disciplineService.saveDisciplineReport(discipline);
            redit.addFlashAttribute("success", (Object)"Discipline Report successfully added");
        } else {
            redit.addFlashAttribute("fail", (Object)("Student with Admission Number: " + admNo + " does not exist"));
        }
        RedirectView redirectView = new RedirectView("/schools/" + code + "/discipline", true);
        return redirectView;
    }

    @GetMapping(value={"/comingSoon"})
    public String comingSoon() {
        return "comingSoon";
    }

    @PostMapping(value={"/schools/{code}/assignTeacher"})
    public String getYearAssignment(Model model, Principal principal, @PathVariable int code, @RequestParam int year) {
        return "redirect:/schools/" + code + "/years/" + year + "/assignTeacher";
    }

    @GetMapping(value={"/schools/{code}/years/{year}/assignTeacher"})
    public String assignTeachers(Model model, Principal principal, @PathVariable int code, @PathVariable int year) {
        int i;
        User user = this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(code).get();
        Student student = new Student();
        List<Stream> streams = this.streamService.getStreamsInSchool(code);
        List<Subject> subjects = this.subjectService.getAllSubjectInSchool(code);
        for (i = 0; i < subjects.size(); ++i) {
            if (subjects.get(i).getInitials() == "C.R.E") {
                model.addAttribute("creTeacher", this.userService.getAllTeachersBySubjectInitials(subjects.get(i).getInitials()));
                continue;
            }
            if (subjects.get(i).getInitials() == "H.R.E") {
                model.addAttribute("hreTeacher", this.userService.getAllTeachersBySubjectInitials(subjects.get(i).getInitials()));
                continue;
            }
            if (subjects.get(i).getInitials() == "I.R.E") {
                model.addAttribute("ireTeacher", this.userService.getAllTeachersBySubjectInitials(subjects.get(i).getInitials()));
                continue;
            }
            model.addAttribute(subjects.get(i).getInitials() + "Teacher", this.userService.getAllTeachersBySubjectInitials(subjects.get(i).getInitials()));
        }
        for (i = 0; i < 4; ++i) {
            for (int j = 0; j < streams.size(); ++j) {
                model.addAttribute("f" + i + streams.get(j).getId() + "Teachers", this.userService.getAllTeachersByAcademicYearAndSchoolFormStream(code, i, streams.get(j).getId(), year));
            }
        }
        model.addAttribute("activeUser", (Object)user);
        model.addAttribute("school", (Object)school);
        model.addAttribute("student", (Object)student);
        model.addAttribute("streams", streams);
        model.addAttribute("subjects", subjects);
        model.addAttribute("year", (Object)year);
        return "assignTeachers";
    }

    @PostMapping(value={"/schools/{code}/years/{year}/forms/{form}/streams/{stream}/assignTeacher"})
    public String assignTeachers(RedirectAttributes redit, HttpServletRequest request, @PathVariable int code, @PathVariable int year, @PathVariable int form, @PathVariable int stream) {
        List<Subject> subjects = this.subjectService.getAllSubjectInSchool(code);
        ArrayList<Form> forms = new ArrayList<Form>();
        Form formObj = this.formService.getForm(form, year, code).get();
        forms.add(formObj);
        ArrayList<Year> years = new ArrayList<Year>();
        Year yearObj = this.yearService.getYearFromSchool(year, code).get();
        years.add(yearObj);
        ArrayList<Stream> streams = new ArrayList<Stream>();
        Stream streamObj = this.streamService.getStream(stream);
        streams.add(streamObj);
        for (int i = 0; i < subjects.size(); ++i) {
            Teacher teacher = this.userService.gettingTeacherByUsername(request.getParameter(subjects.get(i).getInitials() + "Teacher"));
            if (teacher == null) continue;
            teacher.setYears(years);
            teacher.setForms(forms);
            teacher.setStreams(streams);
            ArrayList<Teacher> teachers = new ArrayList<Teacher>();
            teachers.add(teacher);
            this.userService.addUser(teacher);
        }
        return "redirect:/schools/" + code + "/years/" + year + "/assignTeacher";
    }
}