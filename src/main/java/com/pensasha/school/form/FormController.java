package com.pensasha.school.form;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FormController {
    @Autowired
    private FormService formService;

    @GetMapping(value={"/api/schools/{code}/years/{year}/forms"})
    public List<Form> getAllFormInYearAndSchool(@PathVariable int code, @PathVariable int year) {
        return this.formService.getAllForms(year, code);
    }

    @GetMapping(value={"/api/schools/{code}/years/{year}/forms/{form}"})
    public Form getForm(@PathVariable int form, @PathVariable int code, @PathVariable int year) {
        return this.formService.getForm(form, year, code).get();
    }

    @GetMapping(value={"/api/schools/{code}/students/{admNo}/forms/{form}"})
    public Form getStudentForm(@PathVariable int code, @PathVariable String admNo, @PathVariable int form) {
        return this.formService.getStudentForm(form, code + "_" + admNo);
    }

    @GetMapping(value={"/api/students/{admNo}/forms/{form}"})
    public Boolean ifStudentHasForm(@PathVariable int form, @PathVariable String admNo) {
        return this.formService.hasStudent(admNo, form);
    }
}