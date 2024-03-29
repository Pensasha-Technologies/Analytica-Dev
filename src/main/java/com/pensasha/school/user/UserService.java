package com.pensasha.school.user;

import com.pensasha.school.role.Role;
import com.pensasha.school.role.RoleRepository;
import com.pensasha.school.school.School;
import com.pensasha.school.school.SchoolRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final SchoolRepository schoolRepository;
    private final SchoolUserRepository schoolUserRepository;
    private final TeacherRepository teacherRepository;

    public UserService(RoleRepository roleRepository, UserRepository userRepository, SchoolRepository schoolRepository, SchoolUserRepository schoolUserRepository, TeacherRepository teacherRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.schoolRepository = schoolRepository;
        this.schoolUserRepository = schoolUserRepository;
        this.teacherRepository = teacherRepository;
    }

    public Optional<User> getByUsername(String username) {
        return this.userRepository.findById(username);
    }

    public List<Teacher> getAllTeachersBySubjectInitials(int code, String initials) {
        return this.teacherRepository.findBySchoolCodeAndSubjectsInitials(code, initials);
    }

    public List<Teacher> getAllTeachersByAcademicYearAndSchool(int code, int year) {
        return this.teacherRepository.findBySchoolCodeAndYearsYear(code, year);
    }

    public List<Teacher> getAllTeachersByAcademicYearAndSchoolFormStream(int code, int form, int stream, int year) {
        return this.teacherRepository.findBySchoolCodeAndYearsYearAndFormsFormAndStreamsId(code, year, form, stream);
    }

    public List<Teacher> getTeachersInSchool(int code) {
        return this.teacherRepository.findBySchoolCode(code);
    }
    
    public List<Teacher> getAllTeachersByForm(int form){
    	return this.teacherRepository.findByFormsForm(form);
    }

    public SchoolUser getSchoolUserByUsername(String username) {
        return this.schoolUserRepository.findByUsername(username);
    }

    public List<User> findAllUsers() {
        ArrayList<User> users = new ArrayList<User>();
        this.userRepository.findAll().forEach(users::add);
        return users;
    }

    public List<User> findUserByRole(String role) {
        ArrayList<User> users = new ArrayList<User>();
        this.userRepository.findByRoleName(role).forEach(users::add);
        return users;
    }

    public Optional<User> findOneUser(String username) {
        return this.userRepository.findById(username);
    }

    public Boolean userExists(String username) {
        return this.userRepository.existsById(username);
    }

    public List<SchoolUser> getUsersBySchoolCode(int code) {
        return this.schoolUserRepository.findBySchoolCode(code);
    }

    public Teacher gettingTeacherByUsername(String username) {
        return this.teacherRepository.findByUsername(username);
    }

    public User addAdmin(User user) {
        this.roleRepository.save(new Role("ADMIN"));
        user.setRole(new Role("ADMIN"));
        return this.userRepository.save(user);
    }

    public User addManager(User user) {
        this.roleRepository.save(new Role("MANAGER"));
        user.setRole(new Role("MANAGER"));
        return this.userRepository.save(user);
    }

    public User addOfficeAssistant(User user) {
        this.roleRepository.save(new Role("OFFICE_ASSISTANT"));
        user.setRole(new Role("OFFICE_ASSISTANT"));
        return this.userRepository.save(user);
    }

    public User addFieldOfficer(User user) {
        this.roleRepository.save(new Role("FIELD_OFFICER"));
        user.setRole(new Role("FIELD_OFFICER"));
        return this.userRepository.save(user);
    }

    public User addUser(User user) {
        return this.userRepository.save(user);
    }

    public Teacher addUser(Teacher teacher) {
        return this.userRepository.save(teacher);
    }

    public SchoolUser addPrincipal(int code, SchoolUser user) {
        this.roleRepository.save(new Role("PRINCIPAL"));
        School school = this.schoolRepository.findById(code).get();
        ArrayList<SchoolUser> users = new ArrayList<SchoolUser>();
        users.add(user);
        school.setUsers(users);
        user.setRole(new Role("PRINCIPAL"));
        user.setSchool(new School("", code));
        this.userRepository.save(user);
        this.schoolRepository.save(school);
        return user;
    }

    public SchoolUser addDeputyPricipal(int code, SchoolUser user) {
        this.roleRepository.save(new Role("DEPUTY_PRINCIPAL"));
        School school = this.schoolRepository.findById(code).get();
        ArrayList<SchoolUser> users = new ArrayList<SchoolUser>();
        users.add(user);
        school.setUsers(users);
        user.setRole(new Role("DEPUTY_PRINCIPAL"));
        user.setSchool(new School("", code));
        this.userRepository.save(user);
        this.schoolRepository.save(school);
        return user;
    }

    public SchoolUser addDirectorAcademic(int code, SchoolUser user) {
        this.roleRepository.save(new Role("DIRECTOR_ACADEMIC"));
        School school = this.schoolRepository.findById(code).get();
        ArrayList<SchoolUser> users = new ArrayList<SchoolUser>();
        users.add(user);
        school.setUsers(users);
        user.setRole(new Role("DIRECTOR_ACADEMIC"));
        user.setSchool(new School("", code));
        this.userRepository.save(user);
        this.schoolRepository.save(school);
        return user;
    }

    public Teacher addTeacher(int code, Teacher user) {
        this.roleRepository.save(new Role("TEACHER"));
        School school = this.schoolRepository.findById(code).get();
        ArrayList<SchoolUser> users = new ArrayList<SchoolUser>();
        users.add(user);
        school.setUsers(users);
        user.setRole(new Role("TEACHER"));
        user.setSchool(new School("", code));
        this.userRepository.save(user);
        this.schoolRepository.save(school);
        return user;
    }

    public SchoolUser addBursar(int code, SchoolUser user) {
        this.roleRepository.save(new Role("BURSAR"));
        School school = this.schoolRepository.findById(code).get();
        ArrayList<SchoolUser> users = new ArrayList<SchoolUser>();
        users.add(user);
        school.setUsers(users);
        user.setRole(new Role("BURSAR"));
        user.setSchool(new School("", code));
        this.userRepository.save(user);
        this.schoolRepository.save(school);
        return user;
    }

    public SchoolUser addAccountsClerk(int code, SchoolUser user) {
        this.roleRepository.save(new Role("ACCOUNTS_CLERK"));
        School school = this.schoolRepository.findById(code).get();
        ArrayList<SchoolUser> users = new ArrayList<SchoolUser>();
        users.add(user);
        school.setUsers(users);
        user.setRole(new Role("ACCOUNTS_CLERK"));
        user.setSchool(new School("", code));
        this.userRepository.save(user);
        this.schoolRepository.save(school);
        return user;
    }

    public User updateUser(String username, User user) {
        return this.userRepository.save(user);
    }

    public void deleteUser(String username) {
        this.userRepository.deleteById(username);
    }
}