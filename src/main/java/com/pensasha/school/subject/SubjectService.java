package com.pensasha.school.subject;

import com.pensasha.school.subject.Subject;
import com.pensasha.school.subject.SubjectRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubjectService {
    @Autowired
    private SubjectRepository subjectRepository;

    public Subject addSubject(Subject subject) {
        return (Subject)this.subjectRepository.save(subject);
    }

    public Boolean doesSubjectExistsInSchool(String initials, int code) {
        return this.subjectRepository.existsByInitialsAndSchoolsCode(initials, code);
    }

    public Subject updateSubject(Subject subject) {
        return (Subject)this.subjectRepository.save(subject);
    }

    public List<Subject> getAllSubjects() {
        return this.subjectRepository.findAll();
    }

    public Subject getSubjectByName(String name) {
        return this.subjectRepository.findByName(name);
    }

    public List<Subject> getAllSubjectInSchool(int code) {
        return this.subjectRepository.findBySchoolsCode(code);
    }

    public List<Subject> getAllSubjectsByFormAndAdmNo(int form, String admNo) {
        return this.subjectRepository.findByFormsFormAndStudentsAdmNo(form, admNo);
    }

    public List<Subject> getAllSubjectsByYearFormAndAdmNo(int year, int form, String admNo) {
        return this.subjectRepository.findByYearsYearAndFormsFormAndStudentsAdmNo(year, form, admNo);
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
        return this.subjectRepository.findByStudentsAdmNo(admNo);
    }

    public Subject getSubjectByStudent(String initials, String admNo) {
        return this.subjectRepository.findByInitialsAndStudentsAdmNo(initials, admNo);
    }
}