package com.pensasha.school.year;

import com.pensasha.school.form.Form;
import com.pensasha.school.form.FormService;
import com.pensasha.school.school.School;
import com.pensasha.school.school.SchoolService;
import com.pensasha.school.term.Term;
import com.pensasha.school.term.TermService;
import com.pensasha.school.year.Year;
import com.pensasha.school.year.YearService;
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

@RestController
public class YearController {
    SchoolService schoolService;
    YearService yearService;
    FormService formService;
    TermService termService;

    public YearController(SchoolService schoolService, YearService yearService, FormService formService, TermService termService) {
        this.schoolService = schoolService;
        this.yearService = yearService;
        this.formService = formService;
        this.termService = termService;
    }

    @GetMapping(value={"/api/schools/{school_code}/years"})
    public List<Year> getAllYears(@PathVariable int school_code) {
        return this.yearService.getAllYearsInSchool(school_code);
    }

    @GetMapping(value={"/api/schools/{school}/years/{year}"})
    public Optional<Year> getYear(@PathVariable int year, @PathVariable int school) {
        return this.yearService.getYearFromSchool(year, school);
    }

    @PostMapping(value={"/api/schools/{school_code}/years"})
    public String saveYear(@RequestBody Year year, @PathVariable int school_code) {
        List<Year> years = this.getAllYears(school_code);
        for (int i = 0; i < years.size(); ++i) {
            if (years.get(i).getYear() != year.getYear()) continue;
            return "Year " + year.getYear() + " already exist";
        }
        School school = this.schoolService.getSchool(school_code).get();
        ArrayList<School> schools = new ArrayList<School>();
        this.schoolService.getSchoolsByYear(year.getYear()).forEach(schools::add);
        schools.add(school);
        ArrayList<Term> terms = new ArrayList<Term>();
        terms.add(new Term(1));
        terms.add(new Term(2));
        terms.add(new Term(3));
        for (int i = 0; i < terms.size(); ++i) {
            this.termService.addTerm((Term)terms.get(i));
        }
        ArrayList<Form> forms = new ArrayList<Form>();
        forms.add(new Form(1, terms));
        forms.add(new Form(2, terms));
        forms.add(new Form(3, terms));
        forms.add(new Form(4, terms));
        for (int i = 0; i < forms.size(); ++i) {
            this.formService.addForm((Form)forms.get(i));
        }
        year.setSchools(schools);
        year.setForms(forms);
        this.yearService.addYear(year);
        return "Year successfully saved";
    }

    @PutMapping(value={"/api/schools/{school}/years/{year}"})
    public String updateYear(@RequestBody Year yearObj, @PathVariable int school, @PathVariable int year) {
        ArrayList years = new ArrayList();
        this.yearService.getAllYearsInSchool(school).forEach(years::add);
        for (int i = 0; i < years.size(); ++i) {
            if (((Year)years.get(i)).getYear() != year) continue;
            this.yearService.updateYear(yearObj);
            return "Year found and updated successfully";
        }
        return "Year not found";
    }

    @DeleteMapping(value={"/api/schools/{code}/years/{year}"})
    public String deleteYear(@PathVariable int year, @PathVariable int code) {
        ArrayList years = new ArrayList();
        this.yearService.getAllYearsInSchool(code).forEach(years::add);
        for (int i = 0; i < years.size(); ++i) {
            if (((Year)years.get(i)).getYear() != year) continue;
            ArrayList<School> schools = new ArrayList<School>();
            this.schoolService.getSchoolsByYear(((Year)years.get(i)).getYear()).forEach(schools::add);
            schools.remove(this.schoolService.getSchool(code).get());
            ((Year)years.get(i)).setSchools(schools);
            this.yearService.addYear((Year)years.get(i));
            return "Year found and deleted successfully";
        }
        return "Year not found";
    }
}