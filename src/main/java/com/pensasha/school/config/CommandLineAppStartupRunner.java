package com.pensasha.school.config;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
        
        User admin = new User("sobunge", "Samuel", "Odhiambo", "Obunge", encoder.encode("samuel1995"), 707335375);
        User admin1 = new User("vmalala", "Victor", "", "Malala", encoder.encode("victor2020"), 717858286);
        
        this.userService.addAdmin(admin);
        this.userService.addAdmin(admin1);
        
        ArrayList<Subject> subjects = new ArrayList<Subject>();
        
        subjects.add(new Subject("Maths", "Mathematics",121));
        subjects.add(new Subject("Eng", "English", 101));
        subjects.add(new Subject("Kis", "Kiswahili", 102));
        subjects.add(new Subject("Bio", "Biology",231));
        subjects.add(new Subject("Chem", "Chemistry",233));
        subjects.add(new Subject("Phy", "Physics",232));
        subjects.add(new Subject("Hist", "History and Government",311));
        subjects.add(new Subject("C.R.E", "Christian Religious Education",313));
        subjects.add(new Subject("Geo", "Geography",312));
        subjects.add(new Subject("I.R.E", "Islamic Religious Education",314));
        subjects.add(new Subject("H.R.E", "Hindu Religious Education",315));
        subjects.add(new Subject("Hsci", "Home Science",441));
        subjects.add(new Subject("AnD", "Art and Design",442));
        subjects.add(new Subject("Agric", "Agriculture",443));
        subjects.add(new Subject("Comp", "Computer Studies",451));
        subjects.add(new Subject("Avi", "Aviation",450));
        subjects.add(new Subject("Elec", "Electicity",448));
        subjects.add(new Subject("Pwr", "Power mechanics",447));
        subjects.add(new Subject("Wood", "Woodwork",444));
        subjects.add(new Subject("Metal", "Metalwork",445));
        subjects.add(new Subject("Bc", "Building Construction",446));
        subjects.add(new Subject("Fren", "French",501));
        subjects.add(new Subject("Germ", "German",502));
        subjects.add(new Subject("Arab", "Arabic",503));
        subjects.add(new Subject("Msc", "Music",511));
        subjects.add(new Subject("Bs", "Business Studies",565));
        subjects.add(new Subject("Dnd", "Drawing and design",449));
       
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