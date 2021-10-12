package com.pensasha.school.student;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;

    //Adding a student
    public Student addStudent(Student student) {
        return this.studentRepository.save(student);
    }

    // Getting all students in a school
    public List<Student> getAllStudentsInSchool(int code) {
        return this.studentRepository.findBySchoolCode(code);
    }

    // Getting students in school by year, form and stream
    public List<Student> getAllStudentsInSchoolByYearFormandStream(int code, int year, int form, String stream) {
        return this.studentRepository.findBySchoolCodeAndFormsYearsYearAndFormsFormAndStreamStream(code, year, form, stream);
    }
    
    // Getting students in school by year, form, term and stream
    public List<Student> getAllStudentinSchoolYearFormTermStream(int code, int year, int form, int term, String stream) {
        return this.studentRepository.findBySchoolCodeAndYearsYearAndYearsFormsFormAndFormsTermsTermAndStreamStream(code, year, form, term, stream);
    }

    // Getting students in school by year, form and term
    public List<Student> getAllStudentsInSchoolByYearFormTerm(int code, int year, int form, int term) {
        return this.studentRepository.findBySchoolCodeAndYearsYearAndYearsFormsFormAndYearsFormsTermsTerm(code, year, form, term);
    }

    // Checking if a student exists.
    public Boolean ifStudentExists(String admNo) {
        return this.studentRepository.existsById(admNo);
    }

    // Checking if a student exists in a school.
    public Boolean ifStudentExistsInSchool(String admNo, int code) {
        return this.studentRepository.existsByAdmNoAndSchoolCode(admNo, code);
    }

    // Finding a student in a school
    public Student getStudentInSchool(String admNo, int code) {
        return this.studentRepository.findByAdmNoAndSchoolCode(admNo, code);
    }

    // Getting a student in school by form and year
    public List<Student> getStudentsByFormAndYear(int code, int form, int year) {
        return this.studentRepository.findBySchoolCodeAndYearsFormsFormAndYearsYear(code, form, year);
    }

    // Getting all students doing a particular subject
    public List<Student> getAllStudentsDoing(String initials) {
        return this.studentRepository.findBySubjectsInitials(initials);
    }

    // Getting a student in school by admission number, form and year
    public Student getStudentByFormAndYear(String admNo, int code, int form, int year) {
        return this.studentRepository.findByAdmNoAndSchoolCodeAndYearsFormsFormAndYearsYear(admNo, code, form, year);
    }

    // Updating a student details
    public Student updateStudentDetails(String admNo, Student student) {
        return this.studentRepository.save(student);
    }

    // Deleting a student record
    public void deleteStudent(String admNo) {
        this.studentRepository.deleteById(admNo);
    }

    // Getting all students doing a subject in a school by year, form and term
    public List<Student> findAllStudentDoingSubject(int code, int year, int form, int term, String initials) {
        return this.studentRepository.findBySchoolCodeAndYearsYearAndYearsFormsFormAndFormsTermsTermAndSubjectsInitials(code, year, form, term, initials);
    }

    // Getting all students doing a subject in school by year, form, term and stream
    public List<Student> findAllStudentDoingSubjectInStream(int code, int year, int form, int term, String initials, int stream) {
        return this.studentRepository.findBySchoolCodeAndYearsYearAndYearsFormsFormAndFormsTermsTermAndAndSubjectsInitialsAndStreamId(code, year, form, term, initials, stream);
    }
}