package com.pensasha.school.user;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.pensasha.school.role.Role;
import com.pensasha.school.role.RoleService;
import com.pensasha.school.school.School;
import com.pensasha.school.school.SchoolService;
import com.pensasha.school.stream.Stream;
import com.pensasha.school.stream.StreamService;
import com.pensasha.school.student.Student;
import com.pensasha.school.subject.Subject;
import com.pensasha.school.subject.SubjectService;

@Controller
public class UserController {
    private UserService userService;
    private SchoolService schoolService;
    private RoleService roleService;
    private SubjectService subjectService;
    private StreamService streamService;

    public UserController(UserService userService, SchoolService schoolService, RoleService roleService, SubjectService subjectService, StreamService streamService) {
        this.userService = userService;
        this.schoolService = schoolService;
        this.roleService = roleService;
        this.subjectService = subjectService;
        this.streamService = streamService;
    }

    public void AllUsers(Principal principal, Model model) {
        List<School> schools = this.schoolService.getAllSchools();
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        Student student = new Student();
        School school = new School();
        model.addAttribute("activeUser", (Object)activeUser);
        model.addAttribute("schools", schools);
        model.addAttribute("school", (Object)school);
        model.addAttribute("student", (Object)student);
    }

    @GetMapping(value={"/users"})
    public List<User> systemUsers(Model model, Principal principal) {
        ArrayList<User> systemUsers = new ArrayList<User>();
        User user = new User();
        List<User> users = this.userService.findAllUsers();
        this.AllUsers(principal, model);
        for (int i = 0; i < users.size(); ++i) {
            if (!users.get(i).getRole().getName().contains("ADMIN") && !users.get(i).getRole().getName().contains("FIELDOFFICER") && !users.get(i).getRole().getName().contains("OFFICEASSISTANT") && !users.get(i).getRole().getName().contains("C.E.O")) continue;
            systemUsers.add(users.get(i));
        }
        model.addAttribute("user", (Object)user);
        model.addAttribute("users", systemUsers);
        return users;
    }

    @GetMapping(value={"/schoolUsers"})
    public String schoolUsers(Model model, Principal principal) {
        ArrayList<User> schoolUsers = new ArrayList<User>();
        SchoolUser user = new SchoolUser();
        List<User> users = this.userService.findAllUsers();
        this.AllUsers(principal, model);
        for (int i = 0; i < users.size(); ++i) {
            if (!users.get(i).getRole().getName().contains("PRINCIPAL") && !users.get(i).getRole().getName().contains("DEPUTYPRINCIPAL") && !users.get(i).getRole().getName().contains("D.O.S") && !users.get(i).getRole().getName().contains("TEACHER") && !users.get(i).getRole().getName().contains("BURSAR") && !users.get(i).getRole().getName().contains("ACCOUNTSCLERK")) continue;
            schoolUsers.add(users.get(i));
        }
        model.addAttribute("user", (Object)user);
        model.addAttribute("users", schoolUsers);
        return "schoolUsers";
    }

    @PostMapping(value={"/users"})
    public RedirectView addSystemUsers(RedirectAttributes redit, @RequestParam String role, @ModelAttribute User user) {
        if (this.userService.userExists(user.getUsername()).booleanValue()) {
            redit.addFlashAttribute("fail", (Object)("User with username:" + user.getUsername() + " already exists"));
        } else {
            Role roleObj = new Role();
            switch (role) {
                case "admin": {
                    roleObj.setName("ADMIN");
                    break;
                }
                case "ceo": {
                    roleObj.setName("C.E.O");
                    break;
                }
                case "officeAssistant": {
                    roleObj.setName("OFFICEASSISTANT");
                    break;
                }
                case "fieldAssistant": {
                    roleObj.setName("FIELDOFFICER");
                    break;
                }
            }
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            user.setPassword(encoder.encode((CharSequence)user.getUsername()));
            redit.addFlashAttribute("success", (Object)(user.getUsername() + " saved successfully"));
            user.setRole(roleObj);
            this.roleService.addRole(roleObj);
            this.userService.addUser(user);
            redit.addFlashAttribute("success", (Object)(user.getUsername() + " successfully added"));
        }
        RedirectView redirectView = new RedirectView("/users", true);
        return redirectView;
    }

    @PostMapping(value={"/schoolUsers"})
    public RedirectView addSchoolUsers(RedirectAttributes redit, @RequestParam String role, @ModelAttribute SchoolUser user, @RequestParam int code, Principal principal, HttpServletRequest request) {
        RedirectView redirectView;
        Role roleObj = new Role();
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        if (this.userService.userExists(user.getUsername()).booleanValue()) {
            redit.addFlashAttribute("fail", (Object)("A user with username " + user.getUsername() + " already exists"));
        } else {
            user.setSchool(new School("", code));
            Teacher teacher = new Teacher();
            switch (role) {
                case "Principal": {
                    roleObj.setName("PRINCIPAL");
                    break;
                }
                case "deputyPrincipal": {
                    roleObj.setName("DEPUTYPRINCIPAL");
                    break;
                }
                case "d.o.s": {
                    roleObj.setName("D.O.S");
                    break;
                }
                case "bursar": {
                    roleObj.setName("BURSAR");
                    break;
                }
                case "accountsClerk": {
                    roleObj.setName("ACCOUNTSCLERK");
                    break;
                }
                case "teacher": {
                    teacher = new Teacher(user.getUsername(), user.getFirstname(), user.getSecondname(), user.getThirdname(), user.getPassword(), user.getEmail(), user.getPhoneNumber(), user.getAddress());
                    teacher.setSchool(user.getSchool());
                    teacher.setTeacherNumber(request.getParameter("teacherNumber"));
                    teacher.setTscNumber(request.getParameter("tscNumber"));
                    teacher.setInitials(user.getFirstname().charAt(0) + "." + user.getSecondname().charAt(0) + "." + user.getThirdname().charAt(0));
                    roleObj.setName("TEACHER");
                    teacher.setRole(roleObj);
                    break;
                }
            }
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            user.setRole(roleObj);
            this.roleService.addRole(roleObj);
            if (user.getRole().getName() == "TEACHER") {
                teacher.setPassword(encoder.encode((CharSequence)teacher.getUsername()));
                this.userService.addUser(teacher);
            } else {
                user.setPassword(encoder.encode((CharSequence)user.getUsername()));
                this.userService.addUser(user);
            }
            redit.addFlashAttribute("success", (Object)(user.getUsername() + " saved successfully"));
        }
        if (activeUser.getRole().getName().equals("PRINCIPAL")) {
            redirectView = new RedirectView("/schools/principal", true);
            return redirectView;
        }
        if (activeUser.getRole().getName().contains("DEPUTYPRINCIPAL")) {
            redirectView = new RedirectView("/schools/deputyPrincipal", true);
            return redirectView;
        }
        redirectView = new RedirectView("schoolUsers", true);
        return redirectView;
    }

    @GetMapping(value={"profile/{username}"})
    public String getSingleUser(@PathVariable String username, Model model, Principal principal) {
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        School school = new School();
        if (this.userService.userExists(username).booleanValue()) {
            if (activeUser.getRole().getName().equals("PRINCIPAL") || activeUser.getRole().getName().equals("DEPUTYPRINCIPAL") || activeUser.getRole().getName().equals("D.O.S") || activeUser.getRole().getName().equals("BURSAR") || activeUser.getRole().getName().equals("TEACHER") || activeUser.getRole().getName().equals("ACCOUNTSCLERK")) {
                SchoolUser schoolUser = (SchoolUser)activeUser;
                school = schoolUser.getSchool();
                List<Stream> streams = this.streamService.getStreamsInSchool(school.getCode());
                model.addAttribute("streams", streams);
            } else {
                school = new School();
            }
            User user = this.userService.getByUsername(username).get();
            if (user.getRole().getName().contains("TEACHER")) {
                Teacher teacher = this.userService.gettingTeacherByUsername(username);
                List<Subject> subjects = this.subjectService.getAllSubjectInSchool(teacher.getSchool().getCode());
                for (int i = 0; i < teacher.getSubjects().size(); ++i) {
                    if (!subjects.contains(teacher.getSubjects().get(i))) continue;
                    subjects.remove(teacher.getSubjects().get(i));
                }
                model.addAttribute("subjects", subjects);
                model.addAttribute("teacher", (Object)teacher);
            }
            Student student = new Student();
            model.addAttribute("school", (Object)school);
            model.addAttribute("activeUser", (Object)activeUser);
            model.addAttribute("student", (Object)student);
            model.addAttribute("user", (Object)user);
            return "userHome";
        }
        Student student = new Student();
        User user = new User();
        List<User> users = this.userService.findAllUsers();
        List<School> schools = this.schoolService.getAllSchools();
        model.addAttribute("fail", (Object)("User with username:" + username + " does not exist"));
        model.addAttribute("activeUser", (Object)activeUser);
        model.addAttribute("schools", schools);
        model.addAttribute("user", (Object)user);
        model.addAttribute("student", (Object)student);
        model.addAttribute("school", (Object)school);
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping(value={"/user/{username}"})
    public RedirectView deleteUser(@PathVariable String username, RedirectAttributes redit, Principal principal) {
        if (this.userService.userExists(username).booleanValue()) {
            this.userService.deleteUser(username);
            redit.addFlashAttribute("success", (Object)(username + " successfully deleted"));
        } else {
            redit.addFlashAttribute("fail", (Object)("A user with username:" + username + " does not exist"));
        }
        RedirectView redirectView = new RedirectView("/users", true);
        return redirectView;
    }

    @GetMapping(value={"/schoolUser/{username}"})
    public RedirectView deleteSchoolUser1(@PathVariable String username, RedirectAttributes redit, Principal principal) {
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        if (this.userService.userExists(username).booleanValue()) {
            this.userService.deleteUser(username);
            redit.addFlashAttribute("success", (Object)(username + " successfully deleted"));
        } else {
            redit.addFlashAttribute("fail", (Object)("A user with username:" + username + " does not exist"));
        }
        if (activeUser.getRole().getName().contains("PRINCIPAL")) {
            RedirectView redirectView = new RedirectView("/schools/principal", true);
            return redirectView;
        }
        RedirectView redirectView = new RedirectView("/schoolUsers", true);
        return redirectView;
    }
}