package com.pensasha.school.school;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SchoolService {

	@Autowired
	private SchoolRepository schoolRepository;

	public List<School> getAllSchools() {

		List<School> schools = new ArrayList<>();
		schoolRepository.findAll().forEach(schools::add);

		return schools;
	}

	// getting all schools with subject
	public List<School> getAllSchoolsWithSubject(String initials) {
		return schoolRepository.findBySubjectsInitials(initials);
	}

	public List<School> getSchoolsByYear(int year) {
		return schoolRepository.findByYearsYear(year);
	}

	public Optional<School> getSchool(int code) {
		return schoolRepository.findById(code);
	}
	
	public Boolean doesSchoolExists(int code) {
		return schoolRepository.existsById(code);
	}

	public School addSchool(School school) {
		return schoolRepository.save(school);
	}

	public School updateSchool(School school) {
		return schoolRepository.save(school);
	}

	public void deleteSchool(int code) {

		School school = schoolRepository.findById(code).get();

		school.getYears().forEach(year -> {
			year.getSchools().remove(school);
		});
		
		schoolRepository.delete(school);
	}
}
