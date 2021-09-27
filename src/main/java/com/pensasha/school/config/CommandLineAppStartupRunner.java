package com.pensasha.school.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.pensasha.school.form.Form;
import com.pensasha.school.form.FormService;
import com.pensasha.school.school.School;
import com.pensasha.school.school.SchoolService;
import com.pensasha.school.student.Student;
import com.pensasha.school.student.StudentService;
import com.pensasha.school.subject.Subject;
import com.pensasha.school.subject.SubjectService;
import com.pensasha.school.user.User;
import com.pensasha.school.user.UserService;
import com.pensasha.school.year.Year;
import com.pensasha.school.year.YearService;

@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {
   
    @Autowired
    private UserService userService;

    @Autowired
    private SubjectService subjectService;
    
    @Autowired
    private SchoolService schoolService;
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private FormService formService;
    
    @Autowired
    private YearService yearService;

    public void run(String ... args) throws Exception {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        User admin = new User("sobunge", "Samuel", "Odhiambo", "Obunge", encoder.encode((CharSequence)"samuel1995"), 707335375);
        User admin1 = new User("vmalala", "Victor", "", "Malala", encoder.encode((CharSequence)"victor2020"), 717858286);
        
        this.userService.addAdmin(admin);
        this.userService.addAdmin(admin1);
        
        ArrayList<Subject> subjects = new ArrayList<Subject>();
        
        subjects.add(new Subject("Maths", "Mathematics"));
        subjects.add(new Subject("Eng", "English"));
        subjects.add(new Subject("Kis", "Kiswahili"));
        subjects.add(new Subject("Bio", "Biology"));
        subjects.add(new Subject("Chem", "Chemistry"));
        subjects.add(new Subject("Phy", "Physics"));
        subjects.add(new Subject("Hist", "History and Government"));
        subjects.add(new Subject("C.R.E", "Christian Religious Education"));
        subjects.add(new Subject("Geo", "Geography"));
        subjects.add(new Subject("I.R.E", "Islamic Religious Education"));
        subjects.add(new Subject("H.R.E", "Hindu Religious Education"));
        subjects.add(new Subject("Hsci", "Home Science"));
        subjects.add(new Subject("AnD", "Art and Design"));
        subjects.add(new Subject("Agric", "Agriculture"));
        subjects.add(new Subject("Comp", "Computer Studies"));
        subjects.add(new Subject("Avi", "Aviation"));
        subjects.add(new Subject("Elec", "Electicity"));
        subjects.add(new Subject("Pwr", "Power mechanics"));
        subjects.add(new Subject("Wood", "Woodwork"));
        subjects.add(new Subject("Metal", "Metalwork"));
        subjects.add(new Subject("Bc", "Building Construction"));
        subjects.add(new Subject("Fren", "French"));
        subjects.add(new Subject("Germ", "German"));
        subjects.add(new Subject("Arab", "Arabic"));
        subjects.add(new Subject("Msc", "Music"));
        subjects.add(new Subject("Bs", "Business Studies"));
        subjects.add(new Subject("Dnd", "Drawing and design"));
       
        List<School> schools = new ArrayList<School>();
        List<Student> students = new ArrayList<Student>();
        List<Form> forms = new ArrayList<Form>();
        List<Year> years = new ArrayList<Year>();
       
        for (int i = 0; i < subjects.size(); ++i) {
            schools = this.schoolService.getAllSchoolsWithSubject(subjects.get(i).getInitials());
            students = this.studentService.getAllStudentsDoing(subjects.get(i).getInitials());
            forms = this.formService.getAllFormsBySubject(subjects.get(i).getInitials());
            years = this.yearService.allYearsForSubject(subjects.get(i).getInitials());
            subjects.get(i).setSchools(schools);
            subjects.get(i).setStudents(students);
            subjects.get(i).setForms(forms);
            subjects.get(i).setYears(years);
            this.subjectService.addSubject(subjects.get(i));
        }
    }
}