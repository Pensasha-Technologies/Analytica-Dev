package com.pensasha.school.school;

import com.pensasha.school.school.School;
import com.pensasha.school.school.SchoolRepository;
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
        ArrayList<School> schools = new ArrayList<School>();
        this.schoolRepository.findAll().forEach(schools::add);
        return schools;
    }

    public List<School> getAllSchoolsWithSubject(String initials) {
        return this.schoolRepository.findBySubjectsInitials(initials);
    }

    public List<School> getSchoolsByYear(int year) {
        return this.schoolRepository.findByYearsYear(year);
    }

    public List<School> getAllSchoolsWithExamName(String name) {
        return this.schoolRepository.findByExamNamesName(name);
    }

    public Optional<School> getSchool(int code) {
        return this.schoolRepository.findById(code);
    }

    public Boolean doesSchoolExists(int code) {
        return this.schoolRepository.existsById(code);
    }

    public School addSchool(School school) {
        return (School)this.schoolRepository.save(school);
    }

    public School updateSchool(School school) {
        return (School)this.schoolRepository.save(school);
    }

    public void deleteSchool(int code) {
        School school = (School)this.schoolRepository.findById(code).get();
        school.getYears().forEach(year -> year.getSchools().remove(school));
        this.schoolRepository.delete(school);
    }
}