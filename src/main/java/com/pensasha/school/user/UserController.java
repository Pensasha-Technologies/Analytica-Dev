package com.pensasha.school.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

	@Autowired
	private UserService userService;

	// Displaying all users
	@GetMapping("/api/users")
	public List<User> getAllUsers() {
		return userService.findAllUsers();
	}

	// Getting user by username
	@GetMapping("/api/users/{username}")
	public User getUser(@PathVariable String username) {
		return userService.findOneUser(username);
	}

	// Getting users by role
	@GetMapping("/api/users/role/{role}")
	public List<User> getUserByRole(@PathVariable String role) {
		return userService.findUserByRole(role);
	}

	// Adding admin
	@PostMapping("/api/users/admin")
	public User addAdmin(@RequestBody User user) {
		return userService.addAdmin(user);
	}

	// Adding manager
	@PostMapping("/api/users/manager")
	public User addManager(@RequestBody User user) {
		return userService.addManager(user);
	}

	// Adding Office Assistant
	@PostMapping("/api/users/assistant")
	public User addOfficeAssistant(@RequestBody User user) {
		return userService.addOfficeAssistant(user);
	}

	// Adding Field Officer
	@PostMapping("/api/users/field")
	public User addFieldOfficer(@RequestBody User user) {
		return userService.addFieldOfficer(user);
	}

	// Updating user details
	@PutMapping("/api/users/{username}")
	public User updateUser(@PathVariable String username, @RequestBody User user) {
		return userService.updateUser(username, user);
	}

	// Deleting user details
	@DeleteMapping("/api/users/{username}")
	public void deleteUser(@PathVariable String username) {
		userService.deleteUser(username);
	}

	// Getting all users from school
	@GetMapping("/api/schools/{code}/users")
	public List<User> getUserBySchoolCode(@PathVariable int code) {
		return userService.getUsersBySchoolCode(code);
	}

	// Getting a user by username from school
	@GetMapping("/api/schools/{code}/users/{username}")
	public User getOneUserBySchoolCode(@PathVariable String username) {
		return userService.findOneUser(username);
	}

	// Adding school principal
	@PostMapping("/api/schools/{code}/users/principal")
	public User addPrincipalBySchoolCode(@PathVariable int code, @RequestBody User user) {
		return userService.addPrincipal(code, user);
	}

	// Adding deputy principal
	@PostMapping("/api/schools/{code}/users/deputy")
	public User addDeputyBySchoolCode(@PathVariable int code, @RequestBody User user) {
		return userService.addDeputyPricipal(code, user);
	}

	// Addding Director Academics
	@PostMapping("/api/schools/{code}/users/academics")
	public User addDirectorAcademicsBySchoolCode(@PathVariable int code, @RequestBody User user) {
		return userService.addDirectorAcademic(code, user);
	}

	// Adding school bursar
	@PostMapping("/api/schools/{code}/users/bursar")
	public User addBursarBySchoolCode(@PathVariable int code, @RequestBody User user) {
		return userService.addBursar(code, user);
	}

	// Adding school accounts clerk
	@PostMapping("/api/schools/{code}/users/clerk")
	public User addAccountClerkBySchoolCode(@PathVariable int code, @RequestBody User user) {
		return userService.addAccountsClerk(code, user);
	}

	// Adding school teachers
	@PostMapping("/api/schools/{code}/users/teacher")
	public User addTeacherBySchoolCode(@PathVariable int code, @RequestBody User user) {
		return userService.addTeacher(code, user);
	}

	// Updating school user details
	@PutMapping("/api/schools/{code}/users/{username}")
	public User updateOneUserBySchoolCode(@PathVariable String username, @PathVariable User user) {
		return userService.updateUser(username, user);
	}

	// Deleting school user details
	@DeleteMapping("/api/schools/{code}/users/{username}")
	public void deleteOneUserBySchoolCode(@PathVariable String username) {
		userService.deleteUser(username);
	}

}
