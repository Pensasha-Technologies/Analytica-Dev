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

	@Override
	public void run(String... args) throws Exception {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		User admin = new User("sobunge", "Samuel", encoder.encode("samuel1995"));
		User admin1 = new User("vmalala", "Victor", encoder.encode("victor2020"));

		userService.addAdmin(admin);
		userService.addAdmin(admin1);

		List<Subject> subjects = new ArrayList<>();
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

		List<School> schools = new ArrayList<>();
		List<Student> students = new ArrayList<>();
		List<Form> forms = new ArrayList<>();
		List<Year> years = new ArrayList<>();

		for (int i = 0; i < subjects.size(); i++) {

			schools = schoolService.getAllSchoolsWithSubject(subjects.get(i).getInitials());
			students = studentService.getAllStudentsDoing(subjects.get(i).getInitials());
			forms = formService.getAllFormsBySubject(subjects.get(i).getInitials());
			years = yearService.allYearsForSubject(subjects.get(i).getInitials());

			subjects.get(i).setSchools(schools);
			subjects.get(i).setStudents(students);
			subjects.get(i).setForms(forms);
			subjects.get(i).setYears(years);

			subjectService.addSubject(subjects.get(i));
		}

		/*
		 * server.port=5000 spring.jpa.hibernate.ddl-auto=update
		 * spring.datasource.url=jdbc:mysql://${RDS_HOSTNAME}:${RDS_PORT}/${RDS_DB_NAME}
		 * spring.datasource.username=${RDS_USERNAME}
		 * spring.datasource.password=${RDS_PASSWORD}
		 */

	
	}
}
