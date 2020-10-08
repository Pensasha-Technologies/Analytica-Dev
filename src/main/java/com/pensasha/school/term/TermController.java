package com.pensasha.school.term;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TermController {

	@Autowired
	private TermService termService;

	// Getting all terms in form, year and school
	@GetMapping("/api/schools/{code}/years/{year}/forms/{form}/terms")
	public List<Term> getAllTerms(@PathVariable int form, @PathVariable int year, @PathVariable int code) {
		return termService.getAllTerms(form, year, code);
	}

	// Get term in form, year and school
	@GetMapping("/api/schools/{code}/years/{year}/forms/{form}/terms/{term}")
	public Term getTerm(@PathVariable int term, @PathVariable int form, @PathVariable int year,
			@PathVariable int code) {
		return termService.getTerm(term, form, year, code);
	}
}
