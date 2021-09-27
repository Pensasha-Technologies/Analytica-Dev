package com.pensasha.school.form;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FormService {
    @Autowired
    FormRepository formRepository;

    public List<Form> getAllForms(int year, int code) {
        return this.formRepository.findByYearsYearAndYearsSchoolsCode(year, code);
    }

    public List<Form> studentForms(String admNo) {
        return this.formRepository.findByStudentsAdmNo(admNo);
    }

    public List<Form> getAllFormsBySubject(String initials) {
        return this.formRepository.findBySubjectsInitials(initials);
    }

    public List<Form> getAllFormsByExamName(String name) {
        return this.formRepository.findByExamNamesName(name);
    }

    public Optional<Form> getForm(int form, int year, int code) {
        return this.formRepository.findByFormAndYearsYearAndYearsSchoolsCode(form, year, code);
    }

    public Boolean ifFormExists(int form, int year, int code) {
        return this.formRepository.existsByFormAndYearsYearAndYearsSchoolsCode(form, year, code);
    }

    public Boolean doesFormExists(int form) {
        return this.formRepository.existsById(form);
    }

    public Form getStudentForm(int form, String admNo) {
        return this.formRepository.findByFormAndStudentsAdmNo(form, admNo);
    }

    public Form getFormByForm(int form) {
        return this.formRepository.findByForm(form);
    }

    public Boolean hasStudent(String admNo, int form) {
        return this.formRepository.existsByFormAndStudentsAdmNo(form, admNo);
    }

    public Form addForm(Form form) {
        return this.formRepository.save(form);
    }

    public void deleteForm(int form) {
        this.formRepository.deleteById(form);
    }

    public List<Form> getFormsWithYear(int year) {
        return this.formRepository.findByYearsYear(year);
    }
}