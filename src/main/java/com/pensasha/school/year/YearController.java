package com.pensasha.school.year;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.pensasha.school.form.Form;
import com.pensasha.school.form.FormService;
import com.pensasha.school.school.School;
import com.pensasha.school.school.SchoolService;
import com.pensasha.school.term.Term;
import com.pensasha.school.term.TermService;

@RestController
public class YearController {

	SchoolService schoolService;
	YearService yearService;
	FormService formService;
	TermService termService;

	public YearController(SchoolService schoolService, YearService yearService, FormService formService,
			TermService termService) {
		super();
		this.schoolService = schoolService;
		this.yearService = yearService;
		this.formService = formService;
		this.termService = termService;
	}

	// Getting all years
	@GetMapping("/api/schools/{school_code}/years")
	public List<Year> getAllYears(@PathVariable int school_code) {
		return yearService.getAllYearsInSchool(school_code);
	}

	// Getting one year
	@GetMapping("/api/schools/{school}/years/{year}")
	public Optional<Year> getYear(@PathVariable int year, @PathVariable int school) {
		return yearService.getYearFromSchool(year, school);
	}

	// Saving year
	@PostMapping("/api/schools/{school_code}/years")
	public String saveYear(@RequestBody Year year, @PathVariable int school_code) {

		List<Year> years = getAllYears(school_code);

		for (int i = 0; i < years.size(); i++) {
			if (years.get(i).getYear() == year.getYear()) {
				return "Year " + year.getYear() + " already exist";
			}
		}

		School school = schoolService.getSchool(school_code).get();

		List<School> schools = new ArrayList<>();
		schoolService.getSchoolsByYear(year.getYear()).forEach(schools::add);
		schools.add(school);

		List<Term> terms = new ArrayList<>();
		terms.add(new Term(1));
		terms.add(new Term(2));
		terms.add(new Term(3));

		for (int i = 0; i < terms.size(); i++) {
			termService.addTerm(terms.get(i));
		}

		List<Form> forms = new ArrayList<>();
		forms.add(new Form(1, terms));
		forms.add(new Form(2, terms));
		forms.add(new Form(3, terms));
		forms.add(new Form(4, terms));

		for (int i = 0; i < forms.size(); i++) {
			formService.addForm(forms.get(i));
		}

		year.setSchools(schools);
		year.setForms(forms);

		yearService.addYear(year);

		return "Year successfully saved";

	}

	// Update year
	@PutMapping("/api/schools/{school}/years/{year}")
	public String updateYear(@RequestBody Year yearObj, @PathVariable int school, @PathVariable int year) {

		List<Year> years = new ArrayList<>();
		yearService.getAllYearsInSchool(school).forEach(years::add);

		for (int i = 0; i < years.size(); i++) {
			if (years.get(i).getYear() == year) {
				yearService.updateYear(yearObj);
				return "Year found and updated successfully";
			}
		}

		return "Year not found";
	}

	// Deleting year
	@DeleteMapping("/api/schools/{code}/years/{year}")
	public String deleteYear(@PathVariable int year, @PathVariable int code) {

		List<Year> years = new ArrayList<>();
		yearService.getAllYearsInSchool(code).forEach(years::add);

		for (int i = 0; i < years.size(); i++) {
			if (years.get(i).getYear() == year) {

				List<School> schools = new ArrayList<>();
				schoolService.getSchoolsByYear(years.get(i).getYear()).forEach(schools::add);

				schools.remove(schoolService.getSchool(code).get());

				years.get(i).setSchools(schools);
				yearService.addYear(years.get(i));

				return "Year found and deleted successfully";

			}
		}

		return "Year not found";

	}
}
