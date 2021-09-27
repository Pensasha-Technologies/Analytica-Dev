package com.pensasha.school.student;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;

    public Student addStudent(Student student) {
        return this.studentRepository.save(student);
    }

    public List<Student> getAllStudentsInSchool(int code) {
        return this.studentRepository.findBySchoolCode(code);
    }

    public List<Student> getAllStudentsInSchoolByYearFormandStream(int code, int year, int form, String stream) {
        return this.studentRepository.findBySchoolCodeAndFormsFormAndYearsYearAndStreamStream(code, form, year, stream);
    }

    public List<Student> getAllStudentinSchoolYearFormTermStream(int code, int year, int form, int term, String stream) {
        return this.studentRepository.findBySchoolCodeAndYearsYearAndFormsFormAndFormsTermsTermAndStreamStream(code, year, form, term, stream);
    }

    public List<Student> getAllStudentsInSchoolByYearFormTerm(int code, int year, int form, int term) {
        return this.studentRepository.findBySchoolCodeAndYearsYearAndFormsFormAndFormsTermsTerm(code, year, form, term);
    }

    public Boolean ifStudentExists(String admNo) {
        return this.studentRepository.existsById(admNo);
    }

    public Boolean ifStudentExistsInSchool(String admNo, int code) {
        return this.studentRepository.existsByAdmNoAndSchoolCode(admNo, code);
    }

    public Student getStudentInSchool(String admNo, int code) {
        return this.studentRepository.findByAdmNoAndSchoolCode(admNo, code);
    }

    public List<Student> getStudentsByFormAndYear(int code, int form, int year) {
        return this.studentRepository.findBySchoolCodeAndFormsFormAndYearsYear(code, form, year);
    }

    public List<Student> getAllStudentsDoing(String initials) {
        return this.studentRepository.findBySubjectsInitials(initials);
    }

    public Student getStudentByFormAndYear(String admNo, int code, int form, int year) {
        return this.studentRepository.findByAdmNoAndSchoolCodeAndFormsFormAndYearsYear(admNo, code, form, year);
    }

    public Student updateStudentDetails(String admNo, Student student) {
        return this.studentRepository.save(student);
    }

    public void deleteStudent(String admNo) {
        this.studentRepository.deleteById(admNo);
    }

    public List<Student> findAllStudentDoingSubject(int code, int year, int form, int term, String initials) {
        return this.studentRepository.findBySchoolCodeAndYearsYearAndFormsFormAndFormsTermsTermAndSubjectsInitials(code, year, form, term, initials);
    }

    public List<Student> findAllStudentDoingSubjectInStream(int code, int year, int form, int term, String initials, int stream) {
        return this.studentRepository.findBySchoolCodeAndYearsYearAndFormsFormAndFormsTermsTermAndAndSubjectsInitialsAndStreamId(code, year, form, term, initials, stream);
    }
}