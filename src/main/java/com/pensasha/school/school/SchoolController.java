package com.pensasha.school.school;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SchoolController {

	@Autowired
	private SchoolService schoolService;

	// Getting all schools
	@GetMapping("/api/schools")
	public List<School> getAllSchools() {
		return schoolService.getAllSchools();
	}

	// getting school with school code
	@GetMapping("/api/school/{code}")
	public Optional<School> getSchool(@PathVariable int code) {

		return schoolService.getSchool(code);
	}

	// saving a school if not present
	@PostMapping("/api/schools")
	public String addSchool(@RequestBody School school) {

		if (school.getCode() < 1) {
			return "School code entry is incorrect";
		} else {
			List<School> schools = new ArrayList<>();
			schoolService.getAllSchools().forEach(schools::add);

			for (int i = 0; i < schools.size(); i++) {
				if (schools.get(i).getCode() == school.getCode()) {
					return "school with code:" + school.getCode() + " already exists";
				}
			}

			schoolService.addSchool(school);
			return "School with code:" + school.getCode() + " successfully saved";
		}
	}

	@PutMapping("/api/schools/{code}")
	public String updateSchool(@PathVariable int code, @RequestBody School school) {

		List<School> schools = new ArrayList<>();
		schoolService.getAllSchools().forEach(schools::add);

		for (int i = 0; i < schools.size(); i++) {

			if ((schools.get(i).getCode()) == code) {
				school.setYears(schools.get(i).getYears());
				school.setUsers(schools.get(i).getUsers());

				schoolService.updateSchool(school);
				return "School updated successfully";
			}

		}

		return "School of code:" + school.getCode() + " not found";

	}

	@DeleteMapping("/api/schools/{code}")
	public String deleteSchool(@PathVariable int code) {

		List<School> schools = new ArrayList<>();
		schoolService.getAllSchools().forEach(schools::add);

		for (int i = 0; i < schools.size(); i++) {
			if ((schools.get(i).getCode()) == code) {

				schoolService.deleteSchool(code);

				return "Year successfully removed from school";
			}
		}

		return "Year not found";
	}

}
