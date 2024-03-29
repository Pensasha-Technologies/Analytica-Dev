package com.pensasha.school.year;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class YearService {
    @Autowired
    YearRepository yearRepository;
    
    public List<Year> getAllYears(){
    	return yearRepository.findAll();
    }
    
    public List<Year> getAllYearsInSchool(int code) {
        ArrayList<Year> years = new ArrayList<Year>();
        this.yearRepository.findBySchoolsCode(code).forEach(years::add);
        return years;
    }

    public List<Year> getAllYearsByForm(int form) {
        return this.yearRepository.findByFormsForm(form);
    }

    public List<Year> getAllYearsWithExamName(String name) {
        return this.yearRepository.findByExamNamesName(name);
    }

    public Optional<Year> getYearFromSchool(int year, int code) {
        return this.yearRepository.findByYearAndSchoolsCode(year, code);
    }

    public Optional<Year> getYear(int year) {
        return this.yearRepository.findById(year);
    }

    public Boolean doesYearExistInSchool(int year, int code) {
        return this.yearRepository.existsByYearAndSchoolsCode(year, code);
    }

    public List<Year> allYearsForStudent(String admNo) {
        return this.yearRepository.findByStudentsAdmNo(admNo);
    }

    public List<Year> allYearsForSubject(String initials) {
        return this.yearRepository.findBySubjectsInitials(initials);
    }

    public Year addYear(Year year) {
        return this.yearRepository.save(year);
    }

    public Year updateYear(Year year) {
        return this.yearRepository.save(year);
    }

    public void deleteYearById(int year) {
        Year yearObj = this.yearRepository.findById(year).get();
        yearObj.getSchools().forEach(school -> school.getYears().remove(yearObj));
        yearObj.getForms().forEach(form -> form.getYears().remove(yearObj));
        this.yearRepository.deleteById(year);
    }

    public boolean doesYearExist(int year) {
        return this.yearRepository.existsById(year);
    }
    
    public List<Year> getAllYearsByTeacher(String username){
    	return this.yearRepository.findByTeachersUsername(username);
    }
}