package com.pensasha.school.subject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class SubjectService {
	
	List<Subject> subjects = new ArrayList<Subject>();
	
    @Autowired
    private SubjectRepository subjectRepository;

    public Subject addSubject(Subject subject) {
        return this.subjectRepository.save(subject);
    }

    public Boolean doesSubjectExistsInSchool(String initials, int code) {
        return this.subjectRepository.existsByInitialsAndSchoolsCode(initials, code);
    }

    public Subject updateSubject(Subject subject) {
        return this.subjectRepository.save(subject);
    }

    public List<Subject> getAllSubjects() {
    	subjects = this.subjectRepository.findAll();
    	Collections.sort(subjects, new SortByCode());
        return subjects;
    }

    public Subject getSubjectByName(String name) {
        return this.subjectRepository.findByName(name);
    }

    public List<Subject> getAllSubjectInSchool(int code) {
        subjects = this.subjectRepository.findBySchoolsCode(code);
        Collections.sort(subjects, new SortByCode());
        return subjects;
    }

    public List<Subject> getAllSubjectsByFormAndAdmNo(int form, String admNo) {
        subjects = this.subjectRepository.findByFormsFormAndStudentsAdmNo(form, admNo);
        Collections.sort(subjects, new SortByCode());
        return subjects;
    }

    public List<Subject> getAllSubjectsByYearFormAndAdmNo(int year, int form, String admNo) {
        subjects = this.subjectRepository.findByYearsYearAndFormsFormAndStudentsAdmNo(year, form, admNo);
        Collections.sort(subjects, new SortByCode());
        return subjects;
    }

    public Subject getSubjectInSchool(String initials, int code) {
        return this.subjectRepository.findByInitialsAndSchoolsCode(initials, code);
    }

    public Subject getSubject(String initials) {
        return (Subject)this.subjectRepository.findById(initials).get();
    }

    public void deleteSubjectInSchool(String initials) {
        this.subjectRepository.deleteById(initials);
    }

    public List<Subject> getSubjectDoneByStudent(String admNo) {
       	subjects =  this.subjectRepository.findByStudentsAdmNo(admNo);
        Collections.sort(subjects, new SortByCode());
        return subjects;
    }

    public Subject getSubjectByStudent(String initials, String admNo) {
        return this.subjectRepository.findByInitialsAndStudentsAdmNo(initials, admNo);
    }
}

class SortByCode implements Comparator<Subject> {

	public int compare(Subject a, Subject b) {
		return a.getCode() - b.getCode();
	}
}