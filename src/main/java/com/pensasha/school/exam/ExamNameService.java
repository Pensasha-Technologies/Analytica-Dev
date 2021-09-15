package com.pensasha.school.exam;

import com.pensasha.school.exam.ExamName;
import com.pensasha.school.exam.ExamNameRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExamNameService {
   
    @Autowired
    ExamNameRepository examNameRepository;

    public ExamName addExam(ExamName examName) {
        return (ExamName)this.examNameRepository.save(examName);
    }

    public ExamName updateExam(ExamName examName) {
        return (ExamName)this.examNameRepository.save(examName);
    }

    public Boolean nameExists(int id) {
        return this.examNameRepository.existsById(id);
    }

    public List<ExamName> allExamNames(int code) {
        return this.examNameRepository.findBySchoolsCode(code);
    }

    public ExamName getExamByIdSchoolCodeYearFormTerm(int id, int code, int year, int form, int term) {
        return this.examNameRepository.findByIdAndSchoolsCodeAndYearsYearAndFormsFormAndTermsTerm(id, code, year, form, term);
    }

    public List<ExamName> getExamByCodeNameYearFormTermAndExamName(String name, int code, int year, int form, int term) {
        return this.examNameRepository.findBySchoolsCodeAndYearsYearAndFormsFormAndTermsTermAndName(code, year, form, term, name);
    }

    public List<ExamName> getExamBySchoolYearFormTerm(int code, int year, int form, int term) {
        return this.examNameRepository.findBySchoolsCodeAndYearsYearAndFormsFormAndTermsTerm(code, year, form, term);
    }

    public List<ExamName> getExamBySchoolYearForm(int code, int year, int form) {
        return this.examNameRepository.findBySchoolsCodeAndYearsYearAndFormsForm(code, year, form);
    }

    public ExamName getExamBySchoolCodeYearFormTermSubject(int code, int year, int form, int term, String initials) {
        return this.examNameRepository.findBySchoolsCodeAndYearsYearAndFormsFormAndTermsTermAndSubjectInitials(code, year, form, term, initials);
    }

    public List<ExamName> getExamBySchoolYear(int code, int year) {
        return this.examNameRepository.findBySchoolsCodeAndYearsYear(code, year);
    }

    public ExamName getExam(int id) {
        return (ExamName)this.examNameRepository.findById(id).get();
    }

    public void deleteExam(int id) {
        this.examNameRepository.deleteById(id);
    }
}