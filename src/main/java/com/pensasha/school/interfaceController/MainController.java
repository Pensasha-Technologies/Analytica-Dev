package com.pensasha.school.interfaceController;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

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

import com.pensasha.school.discipline.Discipline;
import com.pensasha.school.discipline.DisciplineService;
import com.pensasha.school.exam.Mark;
import com.pensasha.school.exam.MarkService;
import com.pensasha.school.finance.FeeStructure;
import com.pensasha.school.finance.FeeStructureService;
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
import com.pensasha.school.user.TeacherYearFormStream;
import com.pensasha.school.user.TeacherYearFormStreamService;
import com.pensasha.school.user.User;
import com.pensasha.school.user.UserService;
import com.pensasha.school.year.Year;
import com.pensasha.school.year.YearService;

@Controller
public class MainController {

    private final SchoolService schoolService;
    private final StudentService studentService;
    private final TermService termService;
    private final SubjectService subjectService;
    private final FormService formService;
    private final YearService yearService;
    private final MarkService markService;
    private final UserService userService;
    private final RoleService roleService;
    private final StreamService streamService;
    private final DisciplineService disciplineService;
    private final TeacherYearFormStreamService teacherYearFormStreamService;
    private final FeeStructureService feeStructureService;

    public MainController(SchoolService schoolService, StudentService studentService, TermService termService,
            SubjectService subjectService, FormService formService, YearService yearService, MarkService markService,
            UserService userService, RoleService roleService, StreamService streamService,
            DisciplineService disciplineService, TeacherYearFormStreamService teacherYearFormStreamService,
            FeeStructureService feeStructureService) {
        super();
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
        this.teacherYearFormStreamService = teacherYearFormStreamService;
        this.feeStructureService = feeStructureService;
    }

    @GetMapping(value = {"index"})
    public String index(Principal principal, Model model) {
        User user = new User();
        model.addAttribute("activeUser", user);
        return "index";
    }

    @GetMapping(value = {"login"})
    public String login(Principal principal, Model model) {
        User user = new User();
        model.addAttribute("activeUser", user);
        return "login";
    }

    @GetMapping(value = {"changePassword"})
    public String changePassword(Model model) {
        User user = new User();
        model.addAttribute("activeUser", user);
        return "changePassword";
    }

    @PostMapping(value = {"/resetPassword/{username}"})
    public String postResetPassword(RedirectAttributes redit, @PathVariable String username, @RequestParam String currentPassword, @RequestParam String newPassword, @RequestParam String confirmNewPassword) {
        User user = this.userService.getByUsername(username).get();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!newPassword.contentEquals(confirmNewPassword)) {
            redit.addFlashAttribute("fail", "New password and Confirm Password do not match");
        } else if (!encoder.matches(currentPassword, user.getPassword())) {
            redit.addFlashAttribute("fail", "Current Password is incorrect");
        } else {
            user.setPassword(encoder.encode(newPassword));
            this.userService.addUser(user);
            redit.addFlashAttribute("success", "Password changed successfully");
        }
        return "redirect:/profile/" + username;
    }

    @PostMapping(value = {"/changePassword"})
    public String postChangePassword(Model model, @RequestParam int phoneNumber, @RequestParam String username) {
        User user = new User();
        model.addAttribute("activeUser", user);
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
                testUser.setPassword(encoder.encode(Integer.toString(++otp)));
                this.userService.addUser(testUser);
                try {
                    String res = gateway.sendSingleSms("Your Username is: " + testUser.getUsername() + " password is: " + otp + "Thanks for Registering with Analytica Soft.", Integer.toString(testUser.getPhoneNumber()));
                    System.out.println(res);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                model.addAttribute("success", "Password send to phone Number");
                return "login";
            }
            model.addAttribute("fail", "Phone number does not match");
            return "changePassword";
        }
        model.addAttribute("fail", "A user with this username does not exist");
        return "changePassword";
    }

    @GetMapping(value = {"/adminHome"})
    public String allSchool(Model model, Principal principal) {
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        List<School> schools = this.schoolService.getAllSchools();
        School school = new School();
        Student student = new Student();
        model.addAttribute("activeUser", activeUser);
        model.addAttribute("student", student);
        model.addAttribute("schools", schools);
        model.addAttribute("school", school);
        return "adminHome";
    }

    @GetMapping(value = {"/schools/{code}/student/{admNo}/progress"})
    public String viewStudentsProgressReport(@PathVariable int code, @PathVariable String admNo, Model model, Principal principal) {
        School school = this.schoolService.getSchool(code).get();
        Student student = this.studentService.getStudentInSchool(admNo, code);
        List<Subject> subjects = this.subjectService.getSubjectDoneByStudent(admNo);
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        model.addAttribute("activeUser", activeUser);
        model.addAttribute("subjects", subjects);
        model.addAttribute("student", student);
        model.addAttribute("student", school);
        return "progressReport";
    }

    @GetMapping(value = {"/schools/{code}/student/{admNo}/yearly"})
    public String getYearlyReport(@PathVariable int code, @PathVariable String admNo, Model model, Principal principal) {
        School school = this.schoolService.getSchool(code).get();
        Student student = this.studentService.getStudentInSchool(admNo, code);
        List<Subject> subjects = this.subjectService.getSubjectDoneByStudent(admNo);
        List<Year> years = this.yearService.allYearsForStudent(admNo);
        List<Form> forms = this.formService.studentForms(admNo);
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        List<Mark> marks = this.markService.allMarks(admNo);
        model.addAttribute("activeUser", activeUser);
        model.addAttribute("marks", marks);
        model.addAttribute("forms", forms);
        model.addAttribute("years", years);
        model.addAttribute("subjects", subjects);
        model.addAttribute("student", student);
        model.addAttribute("school", school);
        return "yearlyReport";
    }

    @GetMapping(value = {"/ceoHome"})
    public String ceoHomepage(Model model, Principal principal) {
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        List<School> schools = this.schoolService.getAllSchools();
        School school = new School();
        Student student = new Student();
        model.addAttribute("activeUser", activeUser);
        model.addAttribute("student", student);
        model.addAttribute("schools", schools);
        model.addAttribute("school", school);
        return "ceoHome";
    }

    @GetMapping(value = {"/officeAssistantHome"})
    public String officeAssistantHomepage(Model model, Principal principal) {
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        List<School> schools = this.schoolService.getAllSchools();
        School school = new School();
        Student student = new Student();
        model.addAttribute("activeUser", activeUser);
        model.addAttribute("student", student);
        model.addAttribute("schools", schools);
        model.addAttribute("school", school);
        return "officeAssistantHome";
    }

    @GetMapping(value = {"/fieldOfficerHome"})
    public String fieldOfficerHomepage(Model model, Principal principal) {
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        List<School> schools = this.schoolService.getAllSchools();
        School school = new School();
        Student student = new Student();
        model.addAttribute("activeUser", activeUser);
        model.addAttribute("student", student);
        model.addAttribute("schools", schools);
        model.addAttribute("school", school);
        return "fieldOfficerHome";
    }

    @GetMapping(value = {"/schools/users/{username}"})
    public String deleteSchoolUser(@PathVariable String username, RedirectAttributes redit, Principal principal) {
        if (this.userService.userExists(username).booleanValue()) {
            if (username.contentEquals(principal.getName())) {
                redit.addFlashAttribute("fail", "You cannot delete yourself");
            } else {
                this.userService.deleteUser(username);
                redit.addFlashAttribute("success", username + " successfully deleted");
            }
        } else {
            redit.addFlashAttribute("fail", "A user with username " + username + " does not exist");
        }
        return "redirect:/principalHome";
    }

    @GetMapping(value = {"/schools/principal"})
    public String principalHome(Model model, Principal principal) {
        SchoolUser activeUser = (SchoolUser) this.userService.getByUsername(principal.getName()).get();
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
        model.addAttribute("user", user);
        model.addAttribute("activeUser", activeUser);
        model.addAttribute("student", student);
        model.addAttribute("school", school);
        return "principalHome";
    }

    @GetMapping(value = {"/schools/{code}/sms"})
    public String schoolSms(@PathVariable int code, Model model, Principal principal) {
        //SchoolUser activeUser = (SchoolUser)this.userService.getByUsername(principal.getName()).get();
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(code).get();
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
        model.addAttribute("user", user);
        model.addAttribute("activeUser", activeUser);
        model.addAttribute("student", student);
        model.addAttribute("school", school);
        return "smsHome";
    }

    @PostMapping(value = {"/schools/{code}/sms"})
    public String sendSchoolSms(RedirectAttributes redit, @PathVariable int code, Principal principal /*, @RequestParam String recipientPhoneNumber, @RequestParam String message*/) {

        User activeUser = userService.findOneUser(principal.getName()).get();
        /*
        // Your apikey key
        String apiKey = "c2d53e12fda16949112964997b1d1e170ff14310";
        // OR
        String userId = "analytica";
        String password = "xsnggpmG";

        // Message type text/unicode/flash
        String msgType = "text";

        // Multiple mobiles numbers separated by comma
        String mobile = "254707335375";
        // Your approved sender id
        String senderId = "SENDER";
        // Your message to terminate, URLEncode the content
        String msg = "This is a test message in Java";
        // DLT PE ID
        String dltEntityId = "xxxxxxxxxxxxx";
        // DLT Template ID
        String dltTemplateId = "xxxxxxxxxxxxx";
        // response format
        String output = "json";

        // Prepare Url
        URLConnection myURLConnection = null;
        URL myURL = null;
        BufferedReader reader = null;

        // URL encode message
        String urlencodedmsg = "";
        try {
            urlencodedmsg = URLEncoder.encode(msg, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            System.out.println("Exception while encoding msg");
            e1.printStackTrace();
        }

        // API End Point
        String mainUrl = "https://portal.zettatel.com/SMSApi/send?";

        // API Paramters
        StringBuilder sendSmsData = new StringBuilder(mainUrl);
        sendSmsData.append("apikey=" + apiKey);
        sendSmsData.append("&userid=" + userId);
        sendSmsData.append("&password=" + password);
        sendSmsData.append("&type=" + msgType);
        sendSmsData.append("&mobile=" + mobile);
        sendSmsData.append("&senderid=" + senderId);
        sendSmsData.append("&text=" + urlencodedmsg);

        sendSmsData.append("&dltEntityId=" + dltEntityId);
        sendSmsData.append("&dltTemplateId=" + dltTemplateId);
        sendSmsData.append("&output=" + output);
        // final string
        mainUrl = sendSmsData.toString();
        try {
            // prepare connection
            myURL = new URL(mainUrl);
            myURLConnection = myURL.openConnection();
            myURLConnection.connect();
            reader = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
            // reading response
            String response;
            while ((response = reader.readLine()) != null) // print response
            {
                System.out.println(response);
            }

            // finally close connection
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
         */
     
        redit.addFlashAttribute("fail", "Message not sent due to missed API data");

        switch (activeUser.getRole().getName()) {
            case "BURSAR":
                return "redirect:/bursarHome";
            case "D.O.S":
                return "redirect:/dosHome";
            case "DEPUTYPRINCIPAL":
                return "redirect:/deputyPrincipalHome";
            case "FIELDOFFICER":
                return "redirect:/fieldOfficerHome";
            case "OFFICEASSISTANT":
                return "redirect:/officeAssistantHome";
            case "PRINCIPAL":
                return "redirect:/principalHome";
            default:
                return "redirect:/adminHome";

        }

    }

    @GetMapping(value = {"/schools/students"})
    public String schoolStudents(Model model, Principal principal) {
        SchoolUser activeUser = (SchoolUser) this.userService.getByUsername(principal.getName()).get();
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
        model.addAttribute("activeUser", activeUser);
        model.addAttribute("student", student);
        model.addAttribute("school", school);
        return "students";
    }

    @GetMapping(value = {"/schools/students/{admNo}"})
    public String schoolStudents(Model model, Principal principal, @PathVariable String admNo) {
        SchoolUser activeUser = (SchoolUser) this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(activeUser.getSchool().getCode()).get();
        Student student = new Student();
        List<Form> forms = this.formService.studentForms(admNo);
        List<Subject> subjects = this.subjectService.getSubjectDoneByStudent(admNo);
        List<Subject> schoolSubjects = this.subjectService.getAllSubjectInSchool(school.getCode());
        ArrayList<Subject> group1 = new ArrayList<>();
        ArrayList<Subject> group2 = new ArrayList<>();
        ArrayList<Subject> group3 = new ArrayList<>();
        ArrayList<Subject> group4 = new ArrayList<>();
        ArrayList<Subject> group5 = new ArrayList<>();
        for (Subject subject : subjects) {
            if (subject.getName().contains("Mathematics") || subject.getName().contains("English") || subject.getName().contains("Kiswahili")) {
                group1.add(subject);
                continue;
            }
            if (subject.getName().contains("Biology") || subject.getName().contains("Physics") || subject.getName().contains("Chemistry")) {
                group2.add(subject);
                continue;
            }
            if (subject.getInitials().contains("Hist") || subject.getInitials().contains("Geo") || subject.getInitials().contains("C.R.E") || subject.getInitials().contains("I.R.E") || subject.getInitials().contains("H.R.E")) {
                group3.add(subject);
                continue;
            }
            if (subject.getInitials().contains("Hsci") || subject.getInitials().contains("AnD") || subject.getInitials().contains("Agric") || subject.getInitials().contains("Comp") || subject.getInitials().contains("Avi") || subject.getInitials().contains("Elec") || subject.getInitials().contains("Pwr") || subject.getInitials().contains("Wood") || subject.getInitials().contains("Metal") || subject.getInitials().contains("Bc") || subject.getInitials().contains("Dnd")) {
                group4.add(subject);
                continue;
            }
            group5.add(subject);
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
        model.addAttribute("activeUser", activeUser);
        model.addAttribute("forms", forms);
        model.addAttribute("school", school);
        model.addAttribute("subjects", subjects);
        model.addAttribute("schoolSubjects", schoolSubjects);
        model.addAttribute("student", student);
        return "student";
    }

    @PostMapping(value = {"/schools/students"})
    public String addSchoolStudents(RedirectAttributes redit, @Valid Student student, BindingResult bindingResult, Principal principal, @RequestParam int stream) {
        SchoolUser activeUser = (SchoolUser) this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(activeUser.getSchool().getCode()).get();
        student.setSchool(school);
        if (this.studentService.ifStudentExistsInSchool(school.getCode() + "_" + student.getAdmNo(), school.getCode()).booleanValue()) {
            redit.addFlashAttribute("fail", "Student with adm No:" + student.getAdmNo() + " already exists");
        } else {
            ArrayList<School> schools = new ArrayList<>();
            schools.add(school);
            Stream streamObj = this.streamService.getStream(stream);
            student.setStream(streamObj);
            ArrayList<Year> years = new ArrayList<>();
            years.add(new Year(student.getYearAdmitted()));
            student.setYears(years);
            ArrayList<Term> terms = new ArrayList<>();
            terms.add(new Term(1));
            terms.add(new Term(2));
            terms.add(new Term(3));
            for (Term term : terms) {
                this.termService.addTerm(term);
            }
            ArrayList<Form> forms = new ArrayList<>();
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
            for (Form form : forms) {
                this.formService.addForm(form);
            }
            Year year = new Year(student.getYearAdmitted());
            year.setSchools(schools);
            year.setForms(forms);
            this.yearService.addYear(year);
            student.setForms(forms);
            student.setAdmNo(school.getCode() + "_" + student.getAdmNo());
            this.studentService.addStudent(student);
            redit.addFlashAttribute("success", "Student with adm No: " + student.getAdmNo() + " added successfully");
        }
        List<Subject> subjects = this.subjectService.getAllSubjectInSchool(school.getCode());
        ArrayList<Subject> group1 = new ArrayList<>();
        ArrayList<Subject> group2 = new ArrayList<>();
        ArrayList<Subject> group3 = new ArrayList<>();
        ArrayList<Subject> group4 = new ArrayList<>();
        ArrayList<Subject> group5 = new ArrayList<>();
        for (Subject subject : subjects) {
            if (subject.getName().contains("Mathematics") || subject.getName().contains("English") || subject.getName().contains("Kiswahili")) {
                group1.add(subject);
                continue;
            }
            if (subject.getName().contains("Biology") || subject.getName().contains("Physics") || subject.getName().contains("Chemistry")) {
                group2.add(subject);
                continue;
            }
            if (subject.getInitials().contains("Hist") || subject.getInitials().contains("Geo") || subject.getInitials().contains("C.R.E") || subject.getInitials().contains("I.R.E") || subject.getInitials().contains("H.R.E")) {
                group3.add(subject);
                continue;
            }
            if (subject.getInitials().contains("Hsci") || subject.getInitials().contains("AnD") || subject.getInitials().contains("Agric") || subject.getInitials().contains("Comp") || subject.getInitials().contains("Avi") || subject.getInitials().contains("Elec") || subject.getInitials().contains("Pwr") || subject.getInitials().contains("Wood") || subject.getInitials().contains("Metal") || subject.getInitials().contains("Bc") || subject.getInitials().contains("Dnd")) {
                group4.add(subject);
                continue;
            }
            group5.add(subject);
        }
        return "redirect:/teacherHome";
    }

    @GetMapping(value = {"/school/teachers"})
    public String schoolTeachers(Model model, Principal principal) {
        SchoolUser activeUser = (SchoolUser) this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(activeUser.getSchool().getCode()).get();
        Student student = new Student();
        SchoolUser user = new SchoolUser();
        List<Stream> streams = this.streamService.getStreamsInSchool(school.getCode());
        List<User> teachers = this.userService.findUserByRole("TEACHER");
        model.addAttribute("streams", streams);
        model.addAttribute("user", user);
        model.addAttribute("teachers", teachers);
        model.addAttribute("activeUser", activeUser);
        model.addAttribute("student", student);
        model.addAttribute("school", school);
        return "teachers";
    }

    @GetMapping(value = {"/schools/{code}/teachers"})
    public String teachers(Model model, Principal principal, @PathVariable int code) {
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(code).get();
        Student student = new Student();
        SchoolUser user = new SchoolUser();
        List<Stream> streams = this.streamService.getStreamsInSchool(code);
        List<Teacher> teachers = this.userService.getTeachersInSchool(code);
        model.addAttribute("streams", streams);
        model.addAttribute("user", user);
        model.addAttribute("teachers", teachers);
        model.addAttribute("activeUser", activeUser);
        model.addAttribute("student", student);
        model.addAttribute("school", school);
        return "teachers";
    }

    @PostMapping(value = {"/school/teachers"})
    public String addTeacher(RedirectAttributes redit, @RequestParam String role, @RequestParam int code, @ModelAttribute SchoolUser user, Principal principal, HttpServletRequest request) {
        Teacher teacher = new Teacher(user.getUsername(), user.getFirstname(), user.getSecondname(), user.getThirdname(), user.getPassword(), user.getEmail(), user.getPhoneNumber(), user.getAddress());
        teacher.setSchool(user.getSchool());
        teacher.setTeacherNumber(request.getParameter("teacherNumber"));
        teacher.setTscNumber(request.getParameter("tscNumber"));
        teacher.setInitials(user.getFirstname().charAt(0) + "." + user.getSecondname().charAt(0) + "." + user.getThirdname().charAt(0));
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getUsername()));
        Role roleObj = new Role();
        teacher.setSchool(new School("", code));
        roleObj.setName("TEACHER");
        teacher.setRole(roleObj);
        this.roleService.addRole(roleObj);
        if (this.userService.userExists(user.getUsername()).booleanValue()) {
            redit.addFlashAttribute("fail", "A user with the username: " + user.getUsername() + " already exists");
        } else {
            this.userService.addUser(teacher);
            redit.addFlashAttribute("success", "Teacher saved successfully");
        }
        return "redirect:/schools/" + code + "/teachers";
    }

    @GetMapping(value = {"/school/teachers/{username}"})
    public String deleteTeacher(@PathVariable String username, RedirectAttributes redit, Principal principal) {
        if (this.userService.userExists(username).booleanValue()) {
            if (username.contentEquals(principal.getName())) {
                redit.addFlashAttribute("fail", "You cannot delete yourself");
            } else {
                this.userService.deleteUser(username);
                redit.addFlashAttribute("success", username + " successfully deleted");
            }
        } else {
            redit.addFlashAttribute("fail", "A teacher with username " + username + " does not exist");
        }
        return "redirect:/school/teachers";
    }

    @GetMapping(value = {"/schools/deputyPrincipal"})
    public String deputyHome(Model model, Principal principal) {
        SchoolUser activeUser = (SchoolUser) this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(activeUser.getSchool().getCode()).get();
        Student student = new Student();
        User user = new User();
        List<SchoolUser> schoolUsers = this.userService.getUsersBySchoolCode(school.getCode());
        model.addAttribute("schoolUsers", schoolUsers);
        model.addAttribute("user", user);
        model.addAttribute("activeUser", activeUser);
        model.addAttribute("student", student);
        model.addAttribute("school", school);
        return "deputyPrincipal";
    }

    @GetMapping(value = {"/schools/dosHome"})
    public String dosHome(Model model, Principal principal) {
        SchoolUser activeUser = (SchoolUser) this.userService.getByUsername(principal.getName()).get();
        School school = activeUser.getSchool();
        Student student = new Student();
        User user = new User();
        List<SchoolUser> schoolUsers = this.userService.getUsersBySchoolCode(school.getCode());
        model.addAttribute("schoolUsers", schoolUsers);
        model.addAttribute("user", user);
        model.addAttribute("activeUser", activeUser);
        model.addAttribute("student", student);
        model.addAttribute("school", school);
        model.addAttribute("streams", this.streamService.getStreamsInSchool(school.getCode()));
        return "dosHome";
    }

    @GetMapping(value = {"/schools/bursarHome"})
    public String bursarHome(Model model, Principal principal) {

        SchoolUser activeUser = (SchoolUser) this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(activeUser.getSchool().getCode()).get();
        Student student = new Student();
        User user = new User();
        List<SchoolUser> schoolUsers = this.userService.getUsersBySchoolCode(school.getCode());
        List<Stream> streams = this.streamService.getStreamsInSchool(school.getCode());
        List<Student> students = this.studentService.getAllStudentsInSchool(activeUser.getSchool().getCode());

        Set<FeeStructure> feeStructures = new HashSet<>();
        List<FeeStructure> feeStructure = this.feeStructureService.allFeeItemInSchool(school.getCode());
        List<String> stringFee = new ArrayList<>();
        for (FeeStructure fee : feeStructure) {
            if (!stringFee.contains(fee.getName())) {
                stringFee.add(fee.getName());
                feeStructures.add(fee);
            }
        }

        model.addAttribute("feeStructures", feeStructures);
        model.addAttribute("students", students);
        model.addAttribute("schoolUsers", schoolUsers);
        model.addAttribute("user", user);
        model.addAttribute("activeUser", activeUser);
        model.addAttribute("student", student);
        model.addAttribute("school", school);
        model.addAttribute("streams", streams);

        return "bursarHome";

    }

    @GetMapping(value = {"/schools/accountsClerkHome"})
    public String accountsClerkHome(Model model, Principal principal) {
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(((SchoolUser) activeUser).getSchool().getCode()).get();
        Student student = new Student();
        User user = new User();
        List<SchoolUser> schoolUsers = this.userService.getUsersBySchoolCode(school.getCode());
        List<Student> students = this.studentService.getAllStudentsInSchool(school.getCode());
        List<Stream> streams = this.streamService.getStreamsInSchool(school.getCode());

        model.addAttribute("streams", streams);
        model.addAttribute("students", students);
        model.addAttribute("schoolUsers", schoolUsers);
        model.addAttribute("user", user);
        model.addAttribute("activeUser", activeUser);
        model.addAttribute("student", student);
        model.addAttribute("school", school);

        return "accountsClerkHome";
    }

    @GetMapping(value = {"/teacherHome"})
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
        model.addAttribute("student", new Student());
        model.addAttribute("students", students);
        model.addAttribute("school", school);
        model.addAttribute("activeUser", activeUser);
        return "teacherHome";
    }

    @GetMapping(value = {"/schools/teachers/{username}"})
    public String teacherProfile(Principal principal, Model model) {
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        School school = ((SchoolUser) activeUser).getSchool();
        model.addAttribute("activeUser", activeUser);
        if (this.userService.userExists(activeUser.getUsername()).booleanValue()) {
            Student student = new Student();
            List<Stream> streams = this.streamService.getStreamsInSchool(school.getCode());
            List<Year> years = this.yearService.getAllYearsInSchool(school.getCode());
            model.addAttribute("years", years);
            model.addAttribute("streams", streams);
            model.addAttribute("school", school);
            model.addAttribute("student", student);
            model.addAttribute("user", activeUser);
            return "userHome";
        }
        List<Student> students = this.studentService.getAllStudentsInSchool(school.getCode());
        List<Subject> subjects = this.subjectService.getAllSubjectInSchool(school.getCode());
        List<Stream> streams = this.streamService.getStreamsInSchool(school.getCode());
        List<Year> years = this.yearService.getAllYearsInSchool(school.getCode());
        model.addAttribute("years", years);
        model.addAttribute("streams", streams);
        model.addAttribute("subjects", subjects);
        model.addAttribute("fail", "A teacher with username " + activeUser.getUsername() + " does not exist");
        model.addAttribute("students", students);
        model.addAttribute("school", school);
        model.addAttribute("student", new Student());
        return "teacherHome";
    }

    @GetMapping(value = {"/schools/{code}/discipline"})
    public String disciplineReports(@PathVariable int code, Principal principal, Model model) {
        SchoolUser activeUser = (SchoolUser) this.userService.getByUsername(principal.getName()).get();
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
        model.addAttribute("user", user);
        model.addAttribute("activeUser", activeUser);
        model.addAttribute("student", student);
        model.addAttribute("school", school);
        return "discipline";
    }

    @GetMapping(value = {"/schools/{code}/discipline/{id}"})
    public String disciplineReport(@PathVariable int code, @PathVariable int id, Principal principal, Model model) {
        SchoolUser activeUser = (SchoolUser) this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(activeUser.getSchool().getCode()).get();
        Discipline discipline = this.disciplineService.getDisciplineReportById(id).get();
        Student student = discipline.getStudent();
        User user = new User();
        List<Year> years = this.yearService.getAllYearsInSchool(school.getCode());
        List<Stream> streams = this.streamService.getStreamsInSchool(school.getCode());
        List<Subject> subjects = this.subjectService.getAllSubjectInSchool(school.getCode());
        model.addAttribute("discipline", discipline);
        model.addAttribute("subjects", subjects);
        model.addAttribute("streams", streams);
        model.addAttribute("years", years);
        model.addAttribute("user", user);
        model.addAttribute("activeUser", activeUser);
        model.addAttribute("student", student);
        model.addAttribute("school", school);
        return "viewDiscipline";
    }

    @GetMapping(value = {"/schools/{code}/disciplines/{id}"})
    public RedirectView deleteDisciplineReport(@PathVariable int code, @PathVariable int id, RedirectAttributes redit) {
        this.disciplineService.deleteDisciplineReport(id);
        RedirectView redirectView = new RedirectView("/schools/" + code + "/discipline", true);
        redit.addFlashAttribute("success", "Discipline Report successfully deleted");
        return redirectView;
    }

    @PostMapping(value = {"/schools/{code}/discipline"})
    public RedirectView addDisciplineReport(@PathVariable int code, RedirectAttributes redit, @RequestParam int admNo, @RequestParam String type, @RequestParam String depature, @RequestParam String arrival, @RequestParam String reason) {
        if (this.studentService.ifStudentExistsInSchool(code + "_" + admNo, code).booleanValue()) {
            Student student = this.studentService.getStudentInSchool(code + "_" + admNo, code);
            Discipline discipline = new Discipline(depature, arrival, reason, type, student);
            this.disciplineService.saveDisciplineReport(discipline);
            redit.addFlashAttribute("success", "Discipline Report successfully added");
        } else {
            redit.addFlashAttribute("fail", "Student with Admission Number: " + admNo + " does not exist");
        }
        RedirectView redirectView = new RedirectView("/schools/" + code + "/discipline", true);
        return redirectView;
    }

    @GetMapping(value = {"/comingSoon"})
    public String comingSoon() {
        return "comingSoon";
    }

    @PostMapping(value = {"/schools/{code}/assignTeacher"})
    public String getYearAssignment(Model model, Principal principal, @PathVariable int code, @RequestParam int year) {
        return "redirect:/schools/" + code + "/years/" + year + "/assignTeacher";
    }

    @GetMapping(value = {"/schools/{code}/years/{year}/assignTeacher"})
    public String assignTeachers(Model model, Principal principal, @PathVariable int code, @PathVariable int year) {

        User user = this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(code).get();
        Student student = new Student();
        List<Stream> streams = this.streamService.getStreamsInSchool(code);
        List<Subject> subjects = this.subjectService.getAllSubjectInSchool(code);
        for (Subject subject : subjects) {
            if (subject.getInitials() == "C.R.E") {
                model.addAttribute("creTeacher", this.userService.getAllTeachersBySubjectInitials(code, subject.getInitials()));
                continue;
            }
            if (subject.getInitials() == "H.R.E") {
                model.addAttribute("hreTeacher", this.userService.getAllTeachersBySubjectInitials(code, subject.getInitials()));
                continue;
            }
            if (subject.getInitials() == "I.R.E") {
                model.addAttribute("ireTeacher", this.userService.getAllTeachersBySubjectInitials(code, subject.getInitials()));
                continue;
            }
            model.addAttribute(subject.getInitials() + "Teacher", this.userService.getAllTeachersBySubjectInitials(code, subject.getInitials()));
        }
        for (int i = 0; i < 4; ++i) {
            for (Stream stream : streams) {
                model.addAttribute("f" + i + stream.getId() + "Teachers", this.teacherYearFormStreamService.getAllTeachersTeachingInYearFormAndStream(code, year, i, stream.getId()));
            }
        }
        model.addAttribute("activeUser", user);
        model.addAttribute("school", school);
        model.addAttribute("student", student);
        model.addAttribute("streams", streams);
        model.addAttribute("subjects", subjects);
        model.addAttribute("year", year);

        return "assignTeachers";

    }

    @PostMapping(value = {"/schools/{code}/years/{year}/forms/{form}/streams/{stream}/assignTeacher"})
    public String assignTeachers(RedirectAttributes redit, HttpServletRequest request, @PathVariable int code, @PathVariable int year, @PathVariable int form, @PathVariable int stream) {
        List<Subject> subjects = this.subjectService.getAllSubjectInSchool(code);

        for (int i = 0; i < subjects.size(); ++i) {
            Teacher teacher = this.userService.gettingTeacherByUsername(request.getParameter(subjects.get(i).getInitials() + "Teacher"));

            if (teacher == null) {
                continue;
            }

            List<Form> UpdatedForms = this.formService.getAllFormsByTeacher(teacher.getUsername());
            Form formObj = this.formService.getForm(form, year, code).get();
            UpdatedForms.add(formObj);
            Set<Form> frms = new HashSet<>();
            frms.addAll(UpdatedForms);
            List<Form> forms = new ArrayList<>();
            forms.addAll(frms);

            List<Year> UpdatedYears = this.yearService.getAllYearsByTeacher(teacher.getUsername());
            Year yearObj = this.yearService.getYearFromSchool(year, code).get();
            UpdatedYears.add(yearObj);
            Set<Year> yrs = new HashSet<>();
            yrs.addAll(UpdatedYears);
            List<Year> years = new ArrayList<>();
            years.addAll(yrs);

            List<Stream> UpdatedStreams = this.streamService.getAllStreamOfTeacher(teacher.getUsername());
            Stream streamObj = this.streamService.getStream(stream);
            UpdatedStreams.add(streamObj);
            Set<Stream> strms = new HashSet<>();
            strms.addAll(UpdatedStreams);
            List<Stream> streams = new ArrayList<>();
            streams.addAll(strms);

            teacher.setYears(years);
            teacher.setForms(forms);
            teacher.setStreams(streams);

            TeacherYearFormStream teacherYearFormStream = new TeacherYearFormStream(teacher, yearObj, formObj, streamObj, subjects.get(i));
            if (!this.teacherYearFormStreamService.isThereATeacherInStream(year, form, stream, subjects.get(i).getInitials())) {
                teacherYearFormStreamService.addTeacherTeachingSubjectToStream(teacherYearFormStream);
                this.userService.addUser(teacher);
            }

        }

        return "redirect:/schools/" + code + "/years/" + year + "/assignTeacher";

    }

}
