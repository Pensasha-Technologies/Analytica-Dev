package com.pensasha.school.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.pensasha.school.role.Role;
import com.pensasha.school.role.RoleRepository;
import com.pensasha.school.school.School;
import com.pensasha.school.school.SchoolRepository;

import org.springframework.stereotype.Service;

@Service
public class UserService {

	private RoleRepository roleRepository;
	private UserRepository userRepository;
	private SchoolRepository schoolRepository;
	private SchoolUserRepository schoolUserRepository;
	private TeacherRepository teacherRepository;

	public UserService(RoleRepository roleRepository, UserRepository userRepository, SchoolRepository schoolRepository,
			SchoolUserRepository schoolUserRepository, TeacherRepository teacherRepository) {
		super();
		this.roleRepository = roleRepository;
		this.userRepository = userRepository;
		this.schoolRepository = schoolRepository;
		this.schoolUserRepository = schoolUserRepository;
		this.teacherRepository = teacherRepository;
	}

	// Get user by username
	public Optional<User> getByUsername(String username) {
		return userRepository.findById(username);
	}

	public List<Teacher> getAllTeachersBySubjectInitials(String initials) {

		return teacherRepository.findBySubjectsInitials(initials);
	}

	public List<Teacher> getAllTeachersByAcademicYearAndSchool(int code, int year){
		return teacherRepository.findBySchoolCodeAndYearsYear(code, year);
	}
	
	public List<Teacher> getAllTeachersByAcademicYearAndSchoolFormStream(int code,int form, int stream, int year){
	
		return teacherRepository.findBySchoolCodeAndYearsYearAndFormsFormAndStreamsId(code, year,form,stream);
	}

	public List<Teacher> getTeachersInSchool(int code){
		
		return teacherRepository.findBySchoolCode(code);
	}
	
	// Get school user by username
	public SchoolUser getSchoolUserByUsername(String username) {
		return schoolUserRepository.findByUsername(username);
	}

	// Getting all users in the system
	public List<User> findAllUsers() {

		List<User> users = new ArrayList<>();
		userRepository.findAll().forEach(users::add);

		return users;
	}

	// Getting all users by role
	public List<User> findUserByRole(String role) {
		List<User> users = new ArrayList<>();
		userRepository.findByRoleName(role).forEach(users::add);

		return users;
	}

	// Getting one user by username
	public Optional<User> findOneUser(String username) {
		return userRepository.findById(username);
	}

	// does user with username exits
	public Boolean userExists(String username) {
		return userRepository.existsById(username);
	}

	// Getting users in a school
	public List<SchoolUser> getUsersBySchoolCode(int code) {
		return schoolUserRepository.findBySchoolCode(code);
	}

	// Getting a teacher in school
	public Teacher gettingTeacherByUsername(String username) {
		return teacherRepository.findByUsername(username);
	}

	//// Adding System Users ////

	// Adding system admin
	public User addAdmin(User user) {
		roleRepository.save(new Role("ADMIN"));
		user.setRole(new Role("ADMIN"));
		return userRepository.save(user);
	}

	// Adding manager or CEO
	public User addManager(User user) {
		roleRepository.save(new Role("MANAGER"));
		user.setRole(new Role("MANAGER"));

		return userRepository.save(user);
	}

	// Adding Office Assistant
	public User addOfficeAssistant(User user) {
		roleRepository.save(new Role("OFFICE_ASSISTANT"));
		user.setRole(new Role("OFFICE_ASSISTANT"));

		return userRepository.save(user);
	}

	// Adding Field Officer
	public User addFieldOfficer(User user) {
		roleRepository.save(new Role("FIELD_OFFICER"));
		user.setRole(new Role("FIELD_OFFICER"));

		return userRepository.save(user);
	}

	//// Adding School users ////

	public User addUser(User user) {
		return userRepository.save(user);
	}

	public Teacher addUser(Teacher teacher) {
		return userRepository.save(teacher);
	}

	// Adding school principal
	public SchoolUser addPrincipal(int code, SchoolUser user) {
		roleRepository.save(new Role("PRINCIPAL"));
		School school = schoolRepository.findById(code).get();

		List<SchoolUser> users = new ArrayList<>();
		users.add(user);
		school.setUsers(users);
		user.setRole(new Role("PRINCIPAL"));
		user.setSchool(new School("", code));
		userRepository.save(user);
		schoolRepository.save(school);

		return user;
	}

	// Adding a Deputy Principal
	public SchoolUser addDeputyPricipal(int code, SchoolUser user) {
		roleRepository.save(new Role("DEPUTY_PRINCIPAL"));
		School school = schoolRepository.findById(code).get();

		List<SchoolUser> users = new ArrayList<>();
		users.add(user);
		school.setUsers(users);
		user.setRole(new Role("DEPUTY_PRINCIPAL"));
		user.setSchool(new School("", code));
		userRepository.save(user);
		schoolRepository.save(school);

		return user;
	}

	// Adding Director Academic Affairs
	public SchoolUser addDirectorAcademic(int code, SchoolUser user) {
		roleRepository.save(new Role("DIRECTOR_ACADEMIC"));
		School school = schoolRepository.findById(code).get();

		List<SchoolUser> users = new ArrayList<>();
		users.add(user);
		school.setUsers(users);
		user.setRole(new Role("DIRECTOR_ACADEMIC"));
		user.setSchool(new School("", code));
		userRepository.save(user);
		schoolRepository.save(school);

		return user;
	}

	// Adding a teacher
	public Teacher addTeacher(int code, Teacher user) {
		roleRepository.save(new Role("TEACHER"));
		School school = schoolRepository.findById(code).get();

		List<SchoolUser> users = new ArrayList<>();
		users.add(user);
		school.setUsers(users);
		user.setRole(new Role("TEACHER"));
		user.setSchool(new School("", code));
		userRepository.save(user);
		schoolRepository.save(school);

		return user;
	}

	// Adding bursar
	public SchoolUser addBursar(int code, SchoolUser user) {
		roleRepository.save(new Role("BURSAR"));
		School school = schoolRepository.findById(code).get();

		List<SchoolUser> users = new ArrayList<>();
		users.add(user);
		school.setUsers(users);
		user.setRole(new Role("BURSAR"));
		user.setSchool(new School("", code));
		userRepository.save(user);
		schoolRepository.save(school);

		return user;
	}

	// Adding Accounts Clerk
	public SchoolUser addAccountsClerk(int code, SchoolUser user) {
		roleRepository.save(new Role("ACCOUNTS_CLERK"));
		School school = schoolRepository.findById(code).get();

		List<SchoolUser> users = new ArrayList<>();
		users.add(user);
		school.setUsers(users);
		user.setRole(new Role("ACCOUNTS_CLERK"));
		user.setSchool(new School("", code));
		userRepository.save(user);
		schoolRepository.save(school);

		return user;
	}

	// Updating user details
	public User updateUser(String username, User user) {
		return userRepository.save(user);
	}

	// Deleting user details
	public void deleteUser(String username) {
		userRepository.deleteById(username);
	}
}
