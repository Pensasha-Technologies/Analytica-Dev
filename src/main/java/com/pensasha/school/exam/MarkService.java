package com.pensasha.school.exam;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MarkService {
   
    @Autowired
    private MarkRepository markRepository;

    public List<Mark> getMarkByStudentOnAsubject(String admNo, int year, int form, int term, String subject) {
        return this.markRepository.findByStudentAdmNoAndYearYearAndFormFormAndTermTermAndSubjectInitials(admNo, year, form, term, subject);
    }

    public Mark getMarksByStudentOnSubjectByExamId(String admNo, int year, int form, int term, String subject, int id) {
        return this.markRepository.findByStudentAdmNoAndYearYearAndFormFormAndTermTermAndSubjectInitialsAndExamNameId(admNo, year, form, term, subject, id);
    }

    public List<Mark> getMarksBySchoolOnSubjectByExamName(int code, int year, int form, int term, String name) {
        return this.markRepository.findByStudentSchoolCodeAndYearYearAndFormFormAndTermTermAndExamNameName(code, year, form, term, name);
    }

    public Mark getAllSubjectMarks(String admNo, int year, int form, int term) {
        return this.markRepository.findByStudentAdmNoAndYearYearAndFormFormAndTermTerm(admNo, year, form, term);
    }

    public List<Mark> getTermlySubjectMark(String admNo, int form, int term) {
        return this.markRepository.findByStudentAdmNoAndFormFormAndTermTerm(admNo, form, term);
    }

    public List<Mark> getAllStudentsMarksBySchoolYearFormAndTerm(int code, int form, int term, int year) {
        return this.markRepository.findByStudentSchoolCodeAndYearYearAndFormFormAndTermTerm(code, year, form, term);
    }

    public List<Mark> allMarks(String admNo) {
        return this.markRepository.findByStudentAdmNo(admNo);
    }

    public Boolean getMarkByAdm(String admNo) {
        return this.markRepository.existsByStudentAdmNo(admNo);
    }

    public Mark addMarksToSubject(Mark mark) {
        return this.markRepository.save(mark);
    }

    public Mark updateMark(Mark mark) {
        return this.markRepository.save(mark);
    }

    public void deleteMark(int id) {
        this.markRepository.deleteById(id);
    }
}