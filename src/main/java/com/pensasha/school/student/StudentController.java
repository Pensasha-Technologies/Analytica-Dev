package com.pensasha.school.student;

import com.pensasha.school.exam.Mark;
import com.pensasha.school.exam.MarkService;
import com.pensasha.school.form.Form;
import com.pensasha.school.form.FormService;
import com.pensasha.school.school.School;
import com.pensasha.school.school.SchoolService;
import com.pensasha.school.stream.Stream;
import com.pensasha.school.stream.StreamService;
import com.pensasha.school.subject.Subject;
import com.pensasha.school.subject.SubjectService;
import com.pensasha.school.term.Term;
import com.pensasha.school.term.TermService;
import com.pensasha.school.user.User;
import com.pensasha.school.user.UserService;
import com.pensasha.school.year.Year;
import com.pensasha.school.year.YearService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.*;
import java.security.Principal;
import java.util.*;

@Controller
public class StudentController {
    private final StudentService studentService;
    private final SchoolService schoolService;
    private final UserService userService;
    private final StreamService streamService;
    private final YearService yearService;
    private final SubjectService subjectService;
    private final FormService formService;
    private final TermService termService;
    private final MarkService markService;
    private final StudentFormYearService studentFormYearService;

    public StudentController(StudentService studentService, SchoolService schoolService, UserService userService,
			StreamService streamService, YearService yearService, SubjectService subjectService,
			FormService formService, TermService termService, MarkService markService,
			StudentFormYearService studentFormYearService) {
		super();
		this.studentService = studentService;
		this.schoolService = schoolService;
		this.userService = userService;
		this.streamService = streamService;
		this.yearService = yearService;
		this.subjectService = subjectService;
		this.formService = formService;
		this.termService = termService;
		this.markService = markService;
		this.studentFormYearService = studentFormYearService;
	}

	@GetMapping(value={"/schools/{code}/students"})
	public String allStudents(@PathVariable int code, Model model, Principal principal) {
        
		List<Student> students = this.studentService.getAllStudentsInSchool(code);
      /*
		for(Student student : students) {
			
			Set<School> schools = new HashSet<School>();
            School school = this.schoolService.getSchool(code).get();
            schools.add(school);
			
			ArrayList<Term> terms = new ArrayList<Term>();
            terms.add(new Term(1));
            terms.add(new Term(2));
            terms.add(new Term(3));
            
            Form form;
        	
        	ArrayList<Year> years;
        	ArrayList<Form> forms;
            
        	List<Year> testYears = this.yearService.getAllYears();
      
            switch (student.getCurrentForm()) {
                case 1: {
                     
                	Year year = new Year(student.getYearAdmitted());
                	years = new ArrayList<>();
                	
                	forms = new ArrayList<Form>();
                	form = new Form(1, terms);
                	forms.add(form);
                	if(!formService.ifFormExists(form.getForm(), year.getYear(), code)) {
                		 this.formService.addForm(form);
                	}
                   
                	year.setForms(forms);
                    years.add(year);
                	student.setYears(years);

                	List<Integer> intYears = new ArrayList<>();
                	
                	for(Year tempYear : testYears) {
                		intYears.add(tempYear.getYear());
                	}
                	
                	if(intYears.contains(year.getYear())) {
                		 List<School> newSchools = this.schoolService.getSchoolsByYear(year.getYear());
                         if (newSchools.contains(school)) {
                             newSchools.remove(school);
                         }
                         schools.addAll(newSchools);
                         year.setSchools(schools);
                         
                         List<Form> newForms = this.formService.getFormsWithYear(year.getYear());
                         List<Integer> intForm = new ArrayList<>();
                         for(Form tempForm : newForms) {
                         	intForm.add(tempForm.getForm());
                         }
                         if(!intForm.contains(form.getForm())) {
                         	newForms.add(form);
                         }
                        
                         year.setForms(newForms);
                	}else {
                		year.setSchools(schools);
                	}
                	
                    this.yearService.addYear(year);
                	student.setForms(forms);
                    
                	this.studentService.addStudent(student);
                	this.studentFormYearService.addStudentFormYearRecord(new StudentFormYear(student, this.yearService.getYearFromSchool(student.getYearAdmitted(), code).get(), form));
                    
                	break;
                }
                case 2: {
                	
                	List<Integer> intYears;
                	
                	//Form Two 
                   	Year year = new Year(student.getYearAdmitted());
                   	years = new ArrayList<>();
                   	
                   	forms = new ArrayList<Form>();
                   	form = new Form(2, terms);
                   	forms.add(form);
                   	if(!formService.ifFormExists(form.getForm(), year.getYear(), code)) {
                   		 this.formService.addForm(form);
                   	}
                      
                   	year.setForms(forms);
                    years.add(year);
                   	student.setYears(years);

                   	intYears = new ArrayList<>();
                   	
                   	for(Year tempYear : testYears) {
                   		intYears.add(tempYear.getYear());
                   	}
                   	
                   	if(intYears.contains(year.getYear())) {
                   		 List<School> newSchools = this.schoolService.getSchoolsByYear(year.getYear());
                            if (newSchools.contains(school)) {
                                newSchools.remove(school);
                            }
                            schools.addAll(newSchools);
                            year.setSchools(schools);
                            
                            List<Form> newForms = this.formService.getFormsWithYear(year.getYear());
                            List<Integer> intForm = new ArrayList<>();
                            for(Form tempForm : newForms) {
                            	intForm.add(tempForm.getForm());
                            }
                            if(!intForm.contains(form.getForm())) {
                            	newForms.add(form);
                            }
                           
                            year.setForms(newForms);
                   	}else {
                   		year.setSchools(schools);
                   	}
                   	
                    this.yearService.addYear(year);
                   	student.setForms(forms);
                       
                   	this.studentService.addStudent(student);
                   	this.studentFormYearService.addStudentFormYearRecord(new StudentFormYear(student, this.yearService.getYearFromSchool(student.getYearAdmitted(), code).get(), form));
                    
                   	//Form One
                   	Year year1 = new Year(student.getYearAdmitted() - 1);
                   	years = new ArrayList<>();
                   	
                   	forms = new ArrayList<Form>();
                   	form = new Form(1, terms);
                   	forms.add(form);
                   	if(!formService.ifFormExists(form.getForm(), year1.getYear(), code)) {
                   		 this.formService.addForm(form);
                   	}
                      
                   	year1.setForms(forms);
                    years.add(year1);
                   	student.setYears(years);

                   	intYears = new ArrayList<>();
                   	
                   	for(Year tempYear : testYears) {
                   		intYears.add(tempYear.getYear());
                   	}
                   	
                   	if(intYears.contains(year1.getYear())) {
                   		 List<School> newSchools = this.schoolService.getSchoolsByYear(year1.getYear());
                            if (newSchools.contains(school)) {
                                newSchools.remove(school);
                            }
                            schools.addAll(newSchools);
                            year1.setSchools(schools);
                            
                            List<Form> newForms = this.formService.getFormsWithYear(year1.getYear());
                            List<Integer> intForm = new ArrayList<>();
                            for(Form tempForm : newForms) {
                            	intForm.add(tempForm.getForm());
                            }
                            if(!intForm.contains(form.getForm())) {
                            	newForms.add(form);
                            }
                           
                            year1.setForms(newForms);
                   	}else {
                   		year1.setSchools(schools);
                   	}
                   	
                    this.yearService.addYear(year1);
                   	student.setForms(forms);
                       
                   	this.studentService.addStudent(student);
                   	this.studentFormYearService.addStudentFormYearRecord(new StudentFormYear(student, this.yearService.getYearFromSchool(student.getYearAdmitted() - 1, code).get(), form));
                       
                	
                    break;
                }
                case 3: {
                	
                	List<Integer> intYears;
                	
                	//Form Three 
                   	Year year = new Year(student.getYearAdmitted());
                   	years = new ArrayList<>();
                   	
                   	forms = new ArrayList<Form>();
                   	form = new Form(3, terms);
                   	forms.add(form);
                   	if(!formService.ifFormExists(form.getForm(), year.getYear(), code)) {
                   		 this.formService.addForm(form);
                   	}
                      
                   	year.setForms(forms);
                    years.add(year);
                   	student.setYears(years);

                   	intYears = new ArrayList<>();
                   	
                   	for(Year tempYear : testYears) {
                   		intYears.add(tempYear.getYear());
                   	}
                   	
                   	if(intYears.contains(year.getYear())) {
                   		 List<School> newSchools = this.schoolService.getSchoolsByYear(year.getYear());
                            if (newSchools.contains(school)) {
                                newSchools.remove(school);
                            }
                            schools.addAll(newSchools);
                            year.setSchools(schools);
                            
                            List<Form> newForms = this.formService.getFormsWithYear(year.getYear());
                            List<Integer> intForm = new ArrayList<>();
                            for(Form tempForm : newForms) {
                            	intForm.add(tempForm.getForm());
                            }
                            if(!intForm.contains(form.getForm())) {
                            	newForms.add(form);
                            }
                           
                            year.setForms(newForms);
                   	}else {
                   		year.setSchools(schools);
                   	}
                   	
                    this.yearService.addYear(year);
                   	student.setForms(forms);
                       
                   	this.studentService.addStudent(student);
                   	this.studentFormYearService.addStudentFormYearRecord(new StudentFormYear(student, this.yearService.getYearFromSchool(student.getYearAdmitted(), code).get(), form));
                    
                   	//Form Two              
                   	Year year1 = new Year(student.getYearAdmitted() - 1);
                   	years = new ArrayList<>();
                   	
                   	forms = new ArrayList<Form>();
                   	form = new Form(2, terms);
                   	forms.add(form);
                   	if(!formService.ifFormExists(form.getForm(), year1.getYear(), code)) {
                   		 this.formService.addForm(form);
                   	}
                      
                   	year1.setForms(forms);
                    years.add(year1);
                   	student.setYears(years);

                   	intYears = new ArrayList<>();
                   	
                   	for(Year tempYear : testYears) {
                   		intYears.add(tempYear.getYear());
                   	}
                   	
                   	if(intYears.contains(year1.getYear())) {
                   		 List<School> newSchools = this.schoolService.getSchoolsByYear(year1.getYear());
                            if (newSchools.contains(school)) {
                                newSchools.remove(school);
                            }
                            schools.addAll(newSchools);
                            year1.setSchools(schools);
                            
                            List<Form> newForms = this.formService.getFormsWithYear(year1.getYear());
                            List<Integer> intForm = new ArrayList<>();
                            for(Form tempForm : newForms) {
                            	intForm.add(tempForm.getForm());
                            }
                            if(!intForm.contains(form.getForm())) {
                            	newForms.add(form);
                            }
                           
                            year1.setForms(newForms);
                   	}else {
                   		year1.setSchools(schools);
                   	}
                   	
                    this.yearService.addYear(year1);
                   	student.setForms(forms);
                       
                   	this.studentService.addStudent(student);
                   	this.studentFormYearService.addStudentFormYearRecord(new StudentFormYear(student, this.yearService.getYearFromSchool(student.getYearAdmitted() - 1, code).get(), form));
                       
                   	//Form One 
                   	Year year2 = new Year(student.getYearAdmitted() - 2);
                   	years = new ArrayList<>();
                   	
                   	forms = new ArrayList<Form>();
                   	form = new Form(1, terms);
                   	forms.add(form);
                   	if(!formService.ifFormExists(form.getForm(), year2.getYear(), code)) {
                   		 this.formService.addForm(form);
                   	}
                      
                   	year2.setForms(forms);
                    years.add(year2);
                   	student.setYears(years);

                   	intYears = new ArrayList<>();
                   	
                   	for(Year tempYear : testYears) {
                   		intYears.add(tempYear.getYear());
                   	}
                   	
                   	if(intYears.contains(year2.getYear())) {
                   		 List<School> newSchools = this.schoolService.getSchoolsByYear(year2.getYear());
                            if (newSchools.contains(school)) {
                                newSchools.remove(school);
                            }
                            schools.addAll(newSchools);
                            year2.setSchools(schools);
                            
                            List<Form> newForms = this.formService.getFormsWithYear(year2.getYear());
                            List<Integer> intForm = new ArrayList<>();
                            for(Form tempForm : newForms) {
                            	intForm.add(tempForm.getForm());
                            }
                            if(!intForm.contains(form.getForm())) {
                            	newForms.add(form);
                            }
                           
                            year2.setForms(newForms);
                   	}else {
                   		year2.setSchools(schools);
                   	}
                   	
                    this.yearService.addYear(year2);
                   	student.setForms(forms);
                       
                   	this.studentService.addStudent(student);
                   	this.studentFormYearService.addStudentFormYearRecord(new StudentFormYear(student, this.yearService.getYearFromSchool(student.getYearAdmitted() - 2, code).get(), form));
                    
                    break;
                }
                case 4: {
                	
                	List<Integer> intYears;
                	
                	//Form Four 
 
                   	Year year = new Year(student.getYearAdmitted());
                   	years = new ArrayList<>();
                   	
                   	forms = new ArrayList<Form>();
                   	form = new Form(4, terms);
                   	forms.add(form);
                   	if(!formService.ifFormExists(form.getForm(), year.getYear(), code)) {
                   		 this.formService.addForm(form);
                   	}
                      
                   	year.setForms(forms);
                    years.add(year);
                   	student.setYears(years);

                   	intYears = new ArrayList<>();
                   	
                   	for(Year tempYear : testYears) {
                   		intYears.add(tempYear.getYear());
                   	}
                   	
                   	if(intYears.contains(year.getYear())) {
                   		 List<School> newSchools = this.schoolService.getSchoolsByYear(year.getYear());
                            if (newSchools.contains(school)) {
                                newSchools.remove(school);
                            }
                            schools.addAll(newSchools);
                            year.setSchools(schools);
                            
                            List<Form> newForms = this.formService.getFormsWithYear(year.getYear());
                            List<Integer> intForm = new ArrayList<>();
                            for(Form tempForm : newForms) {
                            	intForm.add(tempForm.getForm());
                            }
                            if(!intForm.contains(form.getForm())) {
                            	newForms.add(form);
                            }
                           
                            year.setForms(newForms);
                   	}else {
                   		year.setSchools(schools);
                   	}
                   	
                    this.yearService.addYear(year);
                   	student.setForms(forms);
                       
                   	this.studentService.addStudent(student);
                   	this.studentFormYearService.addStudentFormYearRecord(new StudentFormYear(student, this.yearService.getYearFromSchool(student.getYearAdmitted(), code).get(), form));
                    
                   	//Form Three
                   	Year year1 = new Year(student.getYearAdmitted() - 1);
                   	years = new ArrayList<>();
                   	
                   	forms = new ArrayList<Form>();
                   	form = new Form(3, terms);
                   	forms.add(form);
                   	if(!formService.ifFormExists(form.getForm(), year1.getYear(), code)) {
                   		 this.formService.addForm(form);
                   	}
                      
                   	year1.setForms(forms);
                    years.add(year1);
                   	student.setYears(years);

                   	intYears = new ArrayList<>();
                   	
                   	for(Year tempYear : testYears) {
                   		intYears.add(tempYear.getYear());
                   	}
                   	
                   	if(intYears.contains(year1.getYear())) {
                   		 List<School> newSchools = this.schoolService.getSchoolsByYear(year1.getYear());
                            if (newSchools.contains(school)) {
                                newSchools.remove(school);
                            }
                            schools.addAll(newSchools);
                            year1.setSchools(schools);
                            
                            List<Form> newForms = this.formService.getFormsWithYear(year1.getYear());
                            List<Integer> intForm = new ArrayList<>();
                            for(Form tempForm : newForms) {
                            	intForm.add(tempForm.getForm());
                            }
                            if(!intForm.contains(form.getForm())) {
                            	newForms.add(form);
                            }
                           
                            year1.setForms(newForms);
                   	}else {
                   		year1.setSchools(schools);
                   	}
                   	
                    this.yearService.addYear(year1);
                   	student.setForms(forms);
                       
                   	this.studentService.addStudent(student);
                   	this.studentFormYearService.addStudentFormYearRecord(new StudentFormYear(student, this.yearService.getYearFromSchool(student.getYearAdmitted() - 1, code).get(), form));
                       
                   	//Form Two    
                   	Year year2 = new Year(student.getYearAdmitted() - 2);
                   	years = new ArrayList<>();
                   	
                   	forms = new ArrayList<Form>();
                   	form = new Form(2, terms);
                   	forms.add(form);
                   	if(!formService.ifFormExists(form.getForm(), year2.getYear(), code)) {
                   		 this.formService.addForm(form);
                   	}
                      
                   	year2.setForms(forms);
                    years.add(year2);
                   	student.setYears(years);

                   	intYears = new ArrayList<>();
                   	
                   	for(Year tempYear : testYears) {
                   		intYears.add(tempYear.getYear());
                   	}
                   	
                   	if(intYears.contains(year2.getYear())) {
                   		 List<School> newSchools = this.schoolService.getSchoolsByYear(year2.getYear());
                            if (newSchools.contains(school)) {
                                newSchools.remove(school);
                            }
                            schools.addAll(newSchools);
                            year2.setSchools(schools);
                            
                            List<Form> newForms = this.formService.getFormsWithYear(year2.getYear());
                            List<Integer> intForm = new ArrayList<>();
                            for(Form tempForm : newForms) {
                            	intForm.add(tempForm.getForm());
                            }
                            if(!intForm.contains(form.getForm())) {
                            	newForms.add(form);
                            }
                           
                            year2.setForms(newForms);
                   	}else {
                   		year2.setSchools(schools);
                   	}
                   	
                    this.yearService.addYear(year2);
                   	student.setForms(forms);
                       
                   	this.studentService.addStudent(student);
                   	this.studentFormYearService.addStudentFormYearRecord(new StudentFormYear(student, this.yearService.getYearFromSchool(student.getYearAdmitted() - 2, code).get(), form));
                    
                  //Form One                         
                   	Year year3 = new Year(student.getYearAdmitted() - 3);
                   	years = new ArrayList<>();
                   	
                   	forms = new ArrayList<Form>();
                   	form = new Form(1, terms);
                   	forms.add(form);
                   	if(!formService.ifFormExists(form.getForm(), year3.getYear(), code)) {
                   		 this.formService.addForm(form);
                   	}
                      
                   	year3.setForms(forms);
                    years.add(year3);
                   	student.setYears(years);

                   	intYears = new ArrayList<>();
                   	
                   	for(Year tempYear : testYears) {
                   		intYears.add(tempYear.getYear());
                   	}
                   	
                   	if(intYears.contains(year3.getYear())) {
                   		 List<School> newSchools = this.schoolService.getSchoolsByYear(year3.getYear());
                            if (newSchools.contains(school)) {
                                newSchools.remove(school);
                            }
                            schools.addAll(newSchools);
                            year3.setSchools(schools);
                            
                            List<Form> newForms = this.formService.getFormsWithYear(year3.getYear());
                            List<Integer> intForm = new ArrayList<>();
                            for(Form tempForm : newForms) {
                            	intForm.add(tempForm.getForm());
                            }
                            if(!intForm.contains(form.getForm())) {
                            	newForms.add(form);
                            }
                           
                            year3.setForms(newForms);
                   	}else {
                   		year3.setSchools(schools);
                   	}
                   	
                    this.yearService.addYear(year3);
                   	student.setForms(forms);
                       
                   	this.studentService.addStudent(student);
                   	this.studentFormYearService.addStudentFormYearRecord(new StudentFormYear(student, this.yearService.getYearFromSchool(student.getYearAdmitted() - 3, code).get(), form));
                    
                	
                	
                    break;
                }
            }
			
		}
		*/
		
        School school = this.schoolService.getSchool(code).get();
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        List<Stream> streams = this.streamService.getStreamsInSchool(code);
        Student student = new Student();
        List<Subject> subjects = this.subjectService.getAllSubjectInSchool(code);
        List<Year> years = this.yearService.getAllYearsInSchool(code);
        
        model.addAttribute("years", years);
        model.addAttribute("subjects", subjects);
        model.addAttribute("streams", streams);
        model.addAttribute("activeUser", activeUser);
        model.addAttribute("student", student);
        model.addAttribute("school", school);
        model.addAttribute("students", students);
        
        return "students";
   
	}

    @GetMapping(value={"/schools/{code}/addStudent"})
    public String addStudent(Model model, Principal principal, @PathVariable int code) {
        School school = this.schoolService.getSchool(code).get();
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        List<Stream> streams = this.streamService.getStreamsInSchool(code);
        if (!model.containsAttribute("student")) {
            model.addAttribute("student", new Student());
        }
        model.addAttribute("streams", streams);
        model.addAttribute("school", school);
        model.addAttribute("activeUser", activeUser);
        return "addStudent";
    }

    @GetMapping(value={"/schools/{code}/editStudent/{admNo}"})
    public String editStudent(Model model, Principal principal, @PathVariable int code, @PathVariable String admNo) {
        School school = this.schoolService.getSchool(code).get();
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        List<Stream> streams = this.streamService.getStreamsInSchool(code);
        if (!model.containsAttribute("student")) {
            model.addAttribute("student", this.studentService.getStudentInSchool(admNo, code));
        }
        model.addAttribute("streams", streams);
        model.addAttribute("school", school);
        model.addAttribute("activeUser", activeUser);

        return "editStudent";

    }

    @PostMapping(value={"/schools/{code}/editStudent/{admNo}"})
    public String updateStudent(@PathVariable int code, @ModelAttribute @Valid Student student01, @PathVariable String admNo, BindingResult bindingResult, RedirectAttributes redit, Principal principal, @RequestParam int stream) throws IOException {
        String view;
        if (bindingResult.hasErrors()) {
            redit.addFlashAttribute("org.springframework.validation.BindingResult.student", bindingResult);
            redit.addFlashAttribute("student", student01);
            view = "redirect:/schools/" + code + "/addStudent";
        } else {
            Student studentObj = this.studentService.getStudentInSchool(admNo, code);
            studentObj.setFirstname(student01.getFirstname());
            studentObj.setSecondname(student01.getSecondname());
            studentObj.setThirdname(student01.getThirdname());
            studentObj.setUpiNo(student01.getUpiNo());
            studentObj.setHudumaNo(student01.getHudumaNo());
            studentObj.setBirthNo(student01.getBirthNo());
            studentObj.setDob(student01.getDob());
            studentObj.setF_firstname(student01.getF_firstname());
            studentObj.setF_secondname(student01.getF_secondname());
            studentObj.setF_thirdname(student01.getF_thirdname());
            studentObj.setF_phoneNumber(student01.getF_phoneNumber());
            studentObj.setF_email(student01.getF_email());
            studentObj.setM_firstname(student01.getM_firstname());
            studentObj.setM_secondname(student01.getM_secondname());
            studentObj.setM_thirdname(student01.getM_thirdname());
            studentObj.setM_phoneNumber(student01.getM_phoneNumber());
            studentObj.setM_email(student01.getM_email());
            studentObj.setG_firstname(student01.getG_firstname());
            studentObj.setG_secondname(student01.getG_secondname());
            studentObj.setG_thirdname(student01.getG_thirdname());
            studentObj.setG_phoneNumber(student01.getG_phoneNumber());
            studentObj.setG_email(student01.getG_email());
            studentObj.setStream(this.streamService.getStream(stream));
            studentObj.setGender(student01.getGender());
            studentObj.setSponsor(student01.getSponsor());
            studentObj.setKcpeMarks(student01.getKcpeMarks());
            studentObj.setScholar(student01.getScholar());
            
            if(student01.getCurrentForm() != studentObj.getCurrentForm() || student01.getYearAdmitted() != studentObj.getYearAdmitted()) {
            
            	studentObj.setYearAdmitted(student01.getYearAdmitted());
            	studentObj.setCurrentForm(student01.getCurrentForm());
            	
            	List<Mark> marks = this.markService.allMarks(admNo);
                for (int i = 0; i < marks.size(); ++i) {
                    this.markService.deleteMark(marks.get(i).getId());
                }
                
                List<StudentFormYear> studentsFormYear = this.studentFormYearService.getAllStudentFormYearByStudentAdmNo(admNo);
            	
                for(StudentFormYear studentFormYear : studentsFormYear) {
                	if(studentFormYear.getStudent().getAdmNo().equals(admNo)) {
                		studentFormYearService.deleteStudentById(studentFormYear.getId());
                	}
                }
                
                this.studentService.deleteStudent(admNo);
            	
            	Iterator<Subject> iterator;
            	
            	Set<School> schools = new HashSet<School>();
            	School school = this.schoolService.getSchool(code).get();
            	schools.add(school);
         
                Stream streamObj = this.streamService.getStream(stream);
                studentObj.setStream(streamObj);
                
                ArrayList<Term> terms = new ArrayList<Term>();
                terms.add(new Term(1));
                terms.add(new Term(2));
                terms.add(new Term(3));
                
                for (int i = 0; i < terms.size(); ++i) {
                    this.termService.addTerm(terms.get(i));
                }
                
                Form form;
                ArrayList<Form> forms1 = new ArrayList<Form>();
            	ArrayList<Form> forms2 = new ArrayList<Form>();
            	ArrayList<Form> forms3 = new ArrayList<Form>();
            	ArrayList<Form> allForms = new ArrayList<Form>();
            	
            	ArrayList<Year> years;
            	ArrayList<Form> forms;

                ArrayList<Subject> subjects = new ArrayList<Subject>();
                
                List<Year> testYears = this.yearService.getAllYears();
            	
                switch (studentObj.getCurrentForm()) {
                    case 1: {
                    	
                    	iterator = school.getCompSubjectF1F2().iterator();
                         while (iterator.hasNext()) {
                             subjects.add(iterator.next());
                         }
                         
                    	Year year = new Year(studentObj.getYearAdmitted());
                    	years = new ArrayList<>();
                    	
                    	forms = new ArrayList<Form>();
                    	form = new Form(1, terms);
                    	forms.add(form);
                    	if(!formService.ifFormExists(form.getForm(), year.getYear(), code)) {
                    		 this.formService.addForm(form);
                    	}
                       
                    	year.setForms(forms);
                        years.add(year);
                    	studentObj.setYears(years);

                    	List<Integer> intYears = new ArrayList<>();
                    	
                    	for(Year tempYear : testYears) {
                    		intYears.add(tempYear.getYear());
                    	}
                    	
                    	if(intYears.contains(year.getYear())) {
                    		 List<School> newSchools = this.schoolService.getSchoolsByYear(year.getYear());
                            newSchools.remove(school);
                             schools.addAll(newSchools);
                             year.setSchools(schools);
                             
                             List<Form> newForms = this.formService.getFormsWithYear(year.getYear());
                             List<Integer> intForm = new ArrayList<>();
                             for(Form tempForm : newForms) {
                             	intForm.add(tempForm.getForm());
                             }
                             if(!intForm.contains(form.getForm())) {
                             	newForms.add(form);
                             }
                            
                             year.setForms(newForms);
                    	}else {
                    		year.setSchools(schools);
                    	}
                    	
                        this.yearService.addYear(year);
                    	studentObj.setForms(forms);
                    	studentObj.setSubjects(subjects);
                        
                    	this.studentService.addStudent(studentObj);
                    	this.studentFormYearService.addStudentFormYearRecord(new StudentFormYear(studentObj, this.yearService.getYearFromSchool(studentObj.getYearAdmitted(), code).get(), form));
                        
                    	break;
                    }
                    case 2: {

                    	List<Integer> intYears;
                    	
                    	//Form Two 
                    	iterator = school.getCompSubjectF1F2().iterator();
                        while (iterator.hasNext()) {
                            subjects.add(iterator.next());
                        }
                        
	                   	Year year = new Year(studentObj.getYearAdmitted());
	                   	years = new ArrayList<>();
	                   	
	                   	forms = new ArrayList<Form>();
	                   	form = new Form(2, terms);
	                   	forms.add(form);
	                   	if(!formService.ifFormExists(form.getForm(), year.getYear(), code)) {
	                   		 this.formService.addForm(form);
	                   	}
	                      
	                   	year.setForms(forms);
	                    years.add(year);
	                   	studentObj.setYears(years);

	                   	intYears = new ArrayList<>();
	                   	
	                   	for(Year tempYear : testYears) {
	                   		intYears.add(tempYear.getYear());
	                   	}
	                   	
	                   	if(intYears.contains(year.getYear())) {
	                   		 List<School> newSchools = this.schoolService.getSchoolsByYear(year.getYear());
                            newSchools.remove(school);
	                            schools.addAll(newSchools);
	                            year.setSchools(schools);
	                            
	                            List<Form> newForms = this.formService.getFormsWithYear(year.getYear());
	                            List<Integer> intForm = new ArrayList<>();
	                            for(Form tempForm : newForms) {
	                            	intForm.add(tempForm.getForm());
	                            }
	                            if(!intForm.contains(form.getForm())) {
	                            	newForms.add(form);
	                            }
	                           
	                            year.setForms(newForms);
	                   	}else {
	                   		year.setSchools(schools);
	                   	}
	                   	
	                    this.yearService.addYear(year);
	                   	studentObj.setForms(forms);
	                       
	                   	this.studentService.addStudent(studentObj);
	                   	this.studentFormYearService.addStudentFormYearRecord(new StudentFormYear(studentObj, this.yearService.getYearFromSchool(studentObj.getYearAdmitted(), code).get(), form));
	                    
	                   	//Form One
	                   	Year year1 = new Year(studentObj.getYearAdmitted() - 1);
	                   	years = new ArrayList<>();
	                   	
	                   	forms = new ArrayList<Form>();
	                   	form = new Form(1, terms);
	                   	forms.add(form);
	                   	if(!formService.ifFormExists(form.getForm(), year1.getYear(), code)) {
	                   		 this.formService.addForm(form);
	                   	}
	                      
	                   	year1.setForms(forms);
	                    years.add(year1);
	                   	studentObj.setYears(years);

	                   	intYears = new ArrayList<>();
	                   	
	                   	for(Year tempYear : testYears) {
	                   		intYears.add(tempYear.getYear());
	                   	}
	                   	
	                   	if(intYears.contains(year1.getYear())) {
	                   		 List<School> newSchools = this.schoolService.getSchoolsByYear(year1.getYear());
                            newSchools.remove(school);
	                            schools.addAll(newSchools);
	                            year1.setSchools(schools);
	                            
	                            List<Form> newForms = this.formService.getFormsWithYear(year1.getYear());
	                            List<Integer> intForm = new ArrayList<>();
	                            for(Form tempForm : newForms) {
	                            	intForm.add(tempForm.getForm());
	                            }
	                            if(!intForm.contains(form.getForm())) {
	                            	newForms.add(form);
	                            }
	                           
	                            year1.setForms(newForms);
	                   	}else {
	                   		year1.setSchools(schools);
	                   	}
	                   	
	                    this.yearService.addYear(year1);
	                   	studentObj.setForms(forms);
	                   	studentObj.setSubjects(subjects);
	                       
	                   	this.studentService.addStudent(studentObj);
	                   	this.studentFormYearService.addStudentFormYearRecord(new StudentFormYear(studentObj, this.yearService.getYearFromSchool(studentObj.getYearAdmitted() - 1, code).get(), form));
	                       
                    	
                        break;
                    }
                    case 3: {

                    	List<Integer> intYears;
                    	
                    	//Form Three 
                    	iterator = school.getCompSubjectF3F4().iterator();
                        while (iterator.hasNext()) {
                            subjects.add(iterator.next());
                        }
                        
	                   	Year year = new Year(studentObj.getYearAdmitted());
	                   	years = new ArrayList<>();
	                   	
	                   	forms = new ArrayList<Form>();
	                   	form = new Form(3, terms);
	                   	forms.add(form);
	                   	if(!formService.ifFormExists(form.getForm(), year.getYear(), code)) {
	                   		 this.formService.addForm(form);
	                   	}
	                      
	                   	year.setForms(forms);
	                    years.add(year);
	                   	studentObj.setYears(years);

	                   	intYears = new ArrayList<>();
	                   	
	                   	for(Year tempYear : testYears) {
	                   		intYears.add(tempYear.getYear());
	                   	}
	                   	
	                   	if(intYears.contains(year.getYear())) {
	                   		 List<School> newSchools = this.schoolService.getSchoolsByYear(year.getYear());
                            newSchools.remove(school);
	                            schools.addAll(newSchools);
	                            year.setSchools(schools);
	                            
	                            List<Form> newForms = this.formService.getFormsWithYear(year.getYear());
	                            List<Integer> intForm = new ArrayList<>();
	                            for(Form tempForm : newForms) {
	                            	intForm.add(tempForm.getForm());
	                            }
	                            if(!intForm.contains(form.getForm())) {
	                            	newForms.add(form);
	                            }
	                           
	                            year.setForms(newForms);
	                   	}else {
	                   		year.setSchools(schools);
	                   	}
	                   	
	                    this.yearService.addYear(year);
	                   	studentObj.setForms(forms);
	                       
	                   	this.studentService.addStudent(studentObj);
	                   	this.studentFormYearService.addStudentFormYearRecord(new StudentFormYear(studentObj, this.yearService.getYearFromSchool(studentObj.getYearAdmitted(), code).get(), form));
	                    
	                   	//Form Two              
	                   	Year year1 = new Year(studentObj.getYearAdmitted() - 1);
	                   	years = new ArrayList<>();
	                   	
	                   	forms = new ArrayList<Form>();
	                   	form = new Form(2, terms);
	                   	forms.add(form);
	                   	if(!formService.ifFormExists(form.getForm(), year1.getYear(), code)) {
	                   		 this.formService.addForm(form);
	                   	}
	                      
	                   	year1.setForms(forms);
	                    years.add(year1);
	                   	studentObj.setYears(years);

	                   	intYears = new ArrayList<>();
	                   	
	                   	for(Year tempYear : testYears) {
	                   		intYears.add(tempYear.getYear());
	                   	}
	                   	
	                   	if(intYears.contains(year1.getYear())) {
	                   		 List<School> newSchools = this.schoolService.getSchoolsByYear(year1.getYear());
                            newSchools.remove(school);
	                            schools.addAll(newSchools);
	                            year1.setSchools(schools);
	                            
	                            List<Form> newForms = this.formService.getFormsWithYear(year1.getYear());
	                            List<Integer> intForm = new ArrayList<>();
	                            for(Form tempForm : newForms) {
	                            	intForm.add(tempForm.getForm());
	                            }
	                            if(!intForm.contains(form.getForm())) {
	                            	newForms.add(form);
	                            }
	                           
	                            year1.setForms(newForms);
	                   	}else {
	                   		year1.setSchools(schools);
	                   	}
	                   	
	                    this.yearService.addYear(year1);
	                   	studentObj.setForms(forms);
	                       
	                   	this.studentService.addStudent(studentObj);
	                   	this.studentFormYearService.addStudentFormYearRecord(new StudentFormYear(studentObj, this.yearService.getYearFromSchool(studentObj.getYearAdmitted() - 1, code).get(), form));
	                       
	                   	//Form One 
	                   	Year year2 = new Year(studentObj.getYearAdmitted() - 2);
	                   	years = new ArrayList<>();
	                   	
	                   	forms = new ArrayList<Form>();
	                   	form = new Form(1, terms);
	                   	forms.add(form);
	                   	if(!formService.ifFormExists(form.getForm(), year2.getYear(), code)) {
	                   		 this.formService.addForm(form);
	                   	}
	                      
	                   	year2.setForms(forms);
	                    years.add(year2);
	                   	studentObj.setYears(years);

	                   	intYears = new ArrayList<>();
	                   	
	                   	for(Year tempYear : testYears) {
	                   		intYears.add(tempYear.getYear());
	                   	}
	                   	
	                   	if(intYears.contains(year2.getYear())) {
	                   		 List<School> newSchools = this.schoolService.getSchoolsByYear(year2.getYear());
                            newSchools.remove(school);
	                            schools.addAll(newSchools);
	                            year2.setSchools(schools);
	                            
	                            List<Form> newForms = this.formService.getFormsWithYear(year2.getYear());
	                            List<Integer> intForm = new ArrayList<>();
	                            for(Form tempForm : newForms) {
	                            	intForm.add(tempForm.getForm());
	                            }
	                            if(!intForm.contains(form.getForm())) {
	                            	newForms.add(form);
	                            }
	                           
	                            year2.setForms(newForms);
	                   	}else {
	                   		year2.setSchools(schools);
	                   	}
	                   	
	                    this.yearService.addYear(year2);
	                   	studentObj.setForms(forms);
	                   	studentObj.setSubjects(subjects);
	                       
	                   	this.studentService.addStudent(studentObj);
	                   	this.studentFormYearService.addStudentFormYearRecord(new StudentFormYear(studentObj, this.yearService.getYearFromSchool(studentObj.getYearAdmitted() - 2, code).get(), form));
	                    
                        break;
                    }
                    case 4: {

                    	List<Integer> intYears;
                    	
                    	//Form Four 
                    	iterator = school.getCompSubjectF3F4().iterator();
                        while (iterator.hasNext()) {
                            subjects.add(iterator.next());
                        }
                        
	                   	Year year = new Year(studentObj.getYearAdmitted());
	                   	years = new ArrayList<>();
	                   	
	                   	forms = new ArrayList<Form>();
	                   	form = new Form(4, terms);
	                   	forms.add(form);
	                   	if(!formService.ifFormExists(form.getForm(), year.getYear(), code)) {
	                   		 this.formService.addForm(form);
	                   	}
	                      
	                   	year.setForms(forms);
	                    years.add(year);
	                   	studentObj.setYears(years);

	                   	intYears = new ArrayList<>();
	                   	
	                   	for(Year tempYear : testYears) {
	                   		intYears.add(tempYear.getYear());
	                   	}
	                   	
	                   	if(intYears.contains(year.getYear())) {
	                   		 List<School> newSchools = this.schoolService.getSchoolsByYear(year.getYear());
                            newSchools.remove(school);
	                            schools.addAll(newSchools);
	                            year.setSchools(schools);
	                            
	                            List<Form> newForms = this.formService.getFormsWithYear(year.getYear());
	                            List<Integer> intForm = new ArrayList<>();
	                            for(Form tempForm : newForms) {
	                            	intForm.add(tempForm.getForm());
	                            }
	                            if(!intForm.contains(form.getForm())) {
	                            	newForms.add(form);
	                            }
	                           
	                            year.setForms(newForms);
	                   	}else {
	                   		year.setSchools(schools);
	                   	}
	                   	
	                    this.yearService.addYear(year);
	                   	studentObj.setForms(forms);
	                       
	                   	this.studentService.addStudent(studentObj);
	                   	this.studentFormYearService.addStudentFormYearRecord(new StudentFormYear(studentObj, this.yearService.getYearFromSchool(studentObj.getYearAdmitted(), code).get(), form));
	                    
	                   	//Form Three
	                   	Year year1 = new Year(studentObj.getYearAdmitted() - 1);
	                   	years = new ArrayList<>();
	                   	
	                   	forms = new ArrayList<Form>();
	                   	form = new Form(3, terms);
	                   	forms.add(form);
	                   	if(!formService.ifFormExists(form.getForm(), year1.getYear(), code)) {
	                   		 this.formService.addForm(form);
	                   	}
	                      
	                   	year1.setForms(forms);
	                    years.add(year1);
	                   	studentObj.setYears(years);

	                   	intYears = new ArrayList<>();
	                   	
	                   	for(Year tempYear : testYears) {
	                   		intYears.add(tempYear.getYear());
	                   	}
	                   	
	                   	if(intYears.contains(year1.getYear())) {
	                   		 List<School> newSchools = this.schoolService.getSchoolsByYear(year1.getYear());
                            newSchools.remove(school);
	                            schools.addAll(newSchools);
	                            year1.setSchools(schools);
	                            
	                            List<Form> newForms = this.formService.getFormsWithYear(year1.getYear());
	                            List<Integer> intForm = new ArrayList<>();
	                            for(Form tempForm : newForms) {
	                            	intForm.add(tempForm.getForm());
	                            }
	                            if(!intForm.contains(form.getForm())) {
	                            	newForms.add(form);
	                            }
	                           
	                            year1.setForms(newForms);
	                   	}else {
	                   		year1.setSchools(schools);
	                   	}
	                   	
	                    this.yearService.addYear(year1);
	                   	studentObj.setForms(forms);
	                       
	                   	this.studentService.addStudent(studentObj);
	                   	this.studentFormYearService.addStudentFormYearRecord(new StudentFormYear(studentObj, this.yearService.getYearFromSchool(studentObj.getYearAdmitted() - 1, code).get(), form));
	                       
	                   	//Form Two    
	                   	Year year2 = new Year(studentObj.getYearAdmitted() - 2);
	                   	years = new ArrayList<>();
	                   	
	                   	forms = new ArrayList<Form>();
	                   	form = new Form(2, terms);
	                   	forms.add(form);
	                   	if(!formService.ifFormExists(form.getForm(), year2.getYear(), code)) {
	                   		 this.formService.addForm(form);
	                   	}
	                      
	                   	year2.setForms(forms);
	                    years.add(year2);
	                   	studentObj.setYears(years);

	                   	intYears = new ArrayList<>();
	                   	
	                   	for(Year tempYear : testYears) {
	                   		intYears.add(tempYear.getYear());
	                   	}
	                   	
	                   	if(intYears.contains(year2.getYear())) {
	                   		 List<School> newSchools = this.schoolService.getSchoolsByYear(year2.getYear());
                            newSchools.remove(school);
	                            schools.addAll(newSchools);
	                            year2.setSchools(schools);
	                            
	                            List<Form> newForms = this.formService.getFormsWithYear(year2.getYear());
	                            List<Integer> intForm = new ArrayList<>();
	                            for(Form tempForm : newForms) {
	                            	intForm.add(tempForm.getForm());
	                            }
	                            if(!intForm.contains(form.getForm())) {
	                            	newForms.add(form);
	                            }
	                           
	                            year2.setForms(newForms);
	                   	}else {
	                   		year2.setSchools(schools);
	                   	}
	                   	
	                    this.yearService.addYear(year2);
	                   	studentObj.setForms(forms);
	                       
	                   	this.studentService.addStudent(studentObj);
	                   	this.studentFormYearService.addStudentFormYearRecord(new StudentFormYear(studentObj, this.yearService.getYearFromSchool(studentObj.getYearAdmitted() - 2, code).get(), form));
	                    
	                  //Form One                         
	                   	Year year3 = new Year(studentObj.getYearAdmitted() - 3);
	                   	years = new ArrayList<>();
	                   	
	                   	forms = new ArrayList<Form>();
	                   	form = new Form(1, terms);
	                   	forms.add(form);
	                   	if(!formService.ifFormExists(form.getForm(), year3.getYear(), code)) {
	                   		 this.formService.addForm(form);
	                   	}
	                      
	                   	year3.setForms(forms);
	                    years.add(year3);
	                   	studentObj.setYears(years);

	                   	intYears = new ArrayList<>();
	                   	
	                   	for(Year tempYear : testYears) {
	                   		intYears.add(tempYear.getYear());
	                   	}
	                   	
	                   	if(intYears.contains(year3.getYear())) {
	                   		 List<School> newSchools = this.schoolService.getSchoolsByYear(year3.getYear());
                            newSchools.remove(school);
	                            schools.addAll(newSchools);
	                            year3.setSchools(schools);
	                            
	                            List<Form> newForms = this.formService.getFormsWithYear(year3.getYear());
	                            List<Integer> intForm = new ArrayList<>();
	                            for(Form tempForm : newForms) {
	                            	intForm.add(tempForm.getForm());
	                            }
	                            if(!intForm.contains(form.getForm())) {
	                            	newForms.add(form);
	                            }
	                           
	                            year3.setForms(newForms);
	                   	}else {
	                   		year3.setSchools(schools);
	                   	}
	                   	
	                    this.yearService.addYear(year3);
	                   	studentObj.setForms(forms);
	                   	studentObj.setSubjects(subjects);
	                       
	                   	this.studentService.addStudent(studentObj);
	                   	this.studentFormYearService.addStudentFormYearRecord(new StudentFormYear(studentObj, this.yearService.getYearFromSchool(studentObj.getYearAdmitted() - 3, code).get(), form));
	                    
                    	
                    	
                        break;
                    }
                }
            }
                
           
            String[] admString = studentObj.getAdmNo().split("_");
            redit.addFlashAttribute("success", "Student with admision number: " + admString[0] + " updated successfully");
            view = "redirect:/schools/" + code + "/students";
        }
        return view;
    }

    @PostMapping(value={"/schools/{code}/students"})
    public String addStudent(@RequestParam(value="file") MultipartFile file, @PathVariable int code, @ModelAttribute @Valid Student student, BindingResult bindingResult, RedirectAttributes redit, Principal principal, @RequestParam int stream) throws IOException {
       	
    	String view;
        String[] admString = student.getAdmNo().split("_");
        if (bindingResult.hasErrors()) {
            redit.addFlashAttribute("org.springframework.validation.BindingResult.student", bindingResult);
            redit.addFlashAttribute("student", student);
            view = "redirect:/schools/" + code + "/addStudent";
        } else {
        	Set<School> schools = new HashSet<School>();
            School school = this.schoolService.getSchool(code).get();
            schools.add(school);
            if (this.studentService.ifStudentExistsInSchool(code + "_" + student.getAdmNo(), code).booleanValue()) {
                redit.addFlashAttribute("fail", "Student with admision number: " + admString[0] + " already exists");
            } else if (student.getAdmNo() == "" || Integer.parseInt(student.getAdmNo()) < 1) {
                redit.addFlashAttribute("fail", "Admission number cannot be less than 1");
            } else {
                Iterator<Subject> iterator;
                student.setSchool(this.schoolService.getSchool(code).get());
                if (file.isEmpty()) {
                    student.setPhoto("noStudent");
                } else {

                    String path = new File("src/main/resources/static/studImg").getAbsolutePath();
                    String fileName = school.getCode() + "_" + student.getAdmNo();

                    OutputStream out = null;
                    InputStream filecontent = null;
                    try {
                        out = new FileOutputStream(new File(path + File.separator + fileName));
                        filecontent = file.getInputStream();
                        int read = 0;
                        byte[] bytes = new byte[1024];
                        while ((read = filecontent.read(bytes)) != -1) {
                            out.write(bytes, 0, read);
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally {
                        if (out != null) {
                            out.close();
                        }
                        if (filecontent != null) {
                            filecontent.close();
                        }
                    }
                    student.setPhoto(student.getAdmNo());
                }
                
                Stream streamObj = this.streamService.getStream(stream);
                student.setStream(streamObj);
                
                ArrayList<Term> terms = new ArrayList<Term>();
                terms.add(new Term(1));
                terms.add(new Term(2));
                terms.add(new Term(3));
                
                for (int i = 0; i < terms.size(); ++i) {
                    this.termService.addTerm(terms.get(i));
                }
                
                Form form;
                ArrayList<Form> forms1 = new ArrayList<Form>();
            	ArrayList<Form> forms2 = new ArrayList<Form>();
            	ArrayList<Form> forms3 = new ArrayList<Form>();
            	ArrayList<Form> allForms = new ArrayList<Form>();
            	
            	ArrayList<Year> years;
            	ArrayList<Form> forms;
                
            	student.setAdmNo(school.getCode() + "_" + student.getAdmNo());
                ArrayList<Subject> subjects = new ArrayList<Subject>();
                
                List<Year> testYears = this.yearService.getAllYears();
            	
                switch (student.getCurrentForm()) {
                    case 1: {
                    	
                    	iterator = school.getCompSubjectF1F2().iterator();
                         while (iterator.hasNext()) {
                             subjects.add(iterator.next());
                         }
                         
                    	Year year = new Year(student.getYearAdmitted());
                    	years = new ArrayList<>();
                    	
                    	forms = new ArrayList<Form>();
                    	form = new Form(1, terms);
                    	forms.add(form);
                    	if(!formService.ifFormExists(form.getForm(), year.getYear(), code)) {
                    		 this.formService.addForm(form);
                    	}
                       
                    	year.setForms(forms);
                        years.add(year);
                    	student.setYears(years);

                    	List<Integer> intYears = new ArrayList<>();
                    	
                    	for(Year tempYear : testYears) {
                    		intYears.add(tempYear.getYear());
                    	}
                    	
                    	if(intYears.contains(year.getYear())) {
                    		 List<School> newSchools = this.schoolService.getSchoolsByYear(year.getYear());
                            newSchools.remove(school);
                             schools.addAll(newSchools);
                             year.setSchools(schools);
                             
                             List<Form> newForms = this.formService.getFormsWithYear(year.getYear());
                             List<Integer> intForm = new ArrayList<>();
                             for(Form tempForm : newForms) {
                             	intForm.add(tempForm.getForm());
                             }
                             if(!intForm.contains(form.getForm())) {
                             	newForms.add(form);
                             }
                            
                             year.setForms(newForms);
                    	}else {
                    		year.setSchools(schools);
                    	}
                    	
                        this.yearService.addYear(year);
                    	student.setForms(forms);
                    	student.setSubjects(subjects);
                        
                    	this.studentService.addStudent(student);
                    	this.studentFormYearService.addStudentFormYearRecord(new StudentFormYear(student, this.yearService.getYearFromSchool(student.getYearAdmitted(), code).get(), form));
                        
                    	break;
                    }
                    case 2: {

                    	List<Integer> intYears;
                    	
                    	//Form Two 
                    	iterator = school.getCompSubjectF1F2().iterator();
                        while (iterator.hasNext()) {
                            subjects.add(iterator.next());
                        }
                        
	                   	Year year = new Year(student.getYearAdmitted());
	                   	years = new ArrayList<>();
	                   	
	                   	forms = new ArrayList<Form>();
	                   	form = new Form(2, terms);
	                   	forms.add(form);
	                   	if(!formService.ifFormExists(form.getForm(), year.getYear(), code)) {
	                   		 this.formService.addForm(form);
	                   	}
	                      
	                   	year.setForms(forms);
	                    years.add(year);
	                   	student.setYears(years);

	                   	intYears = new ArrayList<>();
	                   	
	                   	for(Year tempYear : testYears) {
	                   		intYears.add(tempYear.getYear());
	                   	}
	                   	
	                   	if(intYears.contains(year.getYear())) {
	                   		 List<School> newSchools = this.schoolService.getSchoolsByYear(year.getYear());
                            newSchools.remove(school);
	                            schools.addAll(newSchools);
	                            year.setSchools(schools);
	                            
	                            List<Form> newForms = this.formService.getFormsWithYear(year.getYear());
	                            List<Integer> intForm = new ArrayList<>();
	                            for(Form tempForm : newForms) {
	                            	intForm.add(tempForm.getForm());
	                            }
	                            if(!intForm.contains(form.getForm())) {
	                            	newForms.add(form);
	                            }
	                           
	                            year.setForms(newForms);
	                   	}else {
	                   		year.setSchools(schools);
	                   	}
	                   	
	                    this.yearService.addYear(year);
	                   	student.setForms(forms);
	                       
	                   	this.studentService.addStudent(student);
	                   	this.studentFormYearService.addStudentFormYearRecord(new StudentFormYear(student, this.yearService.getYearFromSchool(student.getYearAdmitted(), code).get(), form));
	                    
	                   	//Form One
	                   	Year year1 = new Year(student.getYearAdmitted() - 1);
	                   	years = new ArrayList<>();
	                   	
	                   	forms = new ArrayList<Form>();
	                   	form = new Form(1, terms);
	                   	forms.add(form);
	                   	if(!formService.ifFormExists(form.getForm(), year1.getYear(), code)) {
	                   		 this.formService.addForm(form);
	                   	}
	                      
	                   	year1.setForms(forms);
	                    years.add(year1);
	                   	student.setYears(years);

	                   	intYears = new ArrayList<>();
	                   	
	                   	for(Year tempYear : testYears) {
	                   		intYears.add(tempYear.getYear());
	                   	}
	                   	
	                   	if(intYears.contains(year1.getYear())) {
	                   		 List<School> newSchools = this.schoolService.getSchoolsByYear(year1.getYear());
                            newSchools.remove(school);
	                            schools.addAll(newSchools);
	                            year1.setSchools(schools);
	                            
	                            List<Form> newForms = this.formService.getFormsWithYear(year1.getYear());
	                            List<Integer> intForm = new ArrayList<>();
	                            for(Form tempForm : newForms) {
	                            	intForm.add(tempForm.getForm());
	                            }
	                            if(!intForm.contains(form.getForm())) {
	                            	newForms.add(form);
	                            }
	                           
	                            year1.setForms(newForms);
	                   	}else {
	                   		year1.setSchools(schools);
	                   	}
	                   	
	                    this.yearService.addYear(year1);
	                   	student.setForms(forms);
	                   	student.setSubjects(subjects);
	                       
	                   	this.studentService.addStudent(student);
	                   	this.studentFormYearService.addStudentFormYearRecord(new StudentFormYear(student, this.yearService.getYearFromSchool(student.getYearAdmitted() - 1, code).get(), form));
	                       
                    	
                        break;
                    }
                    case 3: {

                    	List<Integer> intYears;
                    	
                    	//Form Three 
                    	iterator = school.getCompSubjectF3F4().iterator();
                        while (iterator.hasNext()) {
                            subjects.add(iterator.next());
                        }
                        
	                   	Year year = new Year(student.getYearAdmitted());
	                   	years = new ArrayList<>();
	                   	
	                   	forms = new ArrayList<Form>();
	                   	form = new Form(3, terms);
	                   	forms.add(form);
	                   	if(!formService.ifFormExists(form.getForm(), year.getYear(), code)) {
	                   		 this.formService.addForm(form);
	                   	}
	                      
	                   	year.setForms(forms);
	                    years.add(year);
	                   	student.setYears(years);

	                   	intYears = new ArrayList<>();
	                   	
	                   	for(Year tempYear : testYears) {
	                   		intYears.add(tempYear.getYear());
	                   	}
	                   	
	                   	if(intYears.contains(year.getYear())) {
	                   		 List<School> newSchools = this.schoolService.getSchoolsByYear(year.getYear());
                            newSchools.remove(school);
	                            schools.addAll(newSchools);
	                            year.setSchools(schools);
	                            
	                            List<Form> newForms = this.formService.getFormsWithYear(year.getYear());
	                            List<Integer> intForm = new ArrayList<>();
	                            for(Form tempForm : newForms) {
	                            	intForm.add(tempForm.getForm());
	                            }
	                            if(!intForm.contains(form.getForm())) {
	                            	newForms.add(form);
	                            }
	                           
	                            year.setForms(newForms);
	                   	}else {
	                   		year.setSchools(schools);
	                   	}
	                   	
	                    this.yearService.addYear(year);
	                   	student.setForms(forms);
	                       
	                   	this.studentService.addStudent(student);
	                   	this.studentFormYearService.addStudentFormYearRecord(new StudentFormYear(student, this.yearService.getYearFromSchool(student.getYearAdmitted(), code).get(), form));
	                    
	                   	//Form Two              
	                   	Year year1 = new Year(student.getYearAdmitted() - 1);
	                   	years = new ArrayList<>();
	                   	
	                   	forms = new ArrayList<Form>();
	                   	form = new Form(2, terms);
	                   	forms.add(form);
	                   	if(!formService.ifFormExists(form.getForm(), year1.getYear(), code)) {
	                   		 this.formService.addForm(form);
	                   	}
	                      
	                   	year1.setForms(forms);
	                    years.add(year1);
	                   	student.setYears(years);

	                   	intYears = new ArrayList<>();
	                   	
	                   	for(Year tempYear : testYears) {
	                   		intYears.add(tempYear.getYear());
	                   	}
	                   	
	                   	if(intYears.contains(year1.getYear())) {
	                   		 List<School> newSchools = this.schoolService.getSchoolsByYear(year1.getYear());
                            newSchools.remove(school);
	                            schools.addAll(newSchools);
	                            year1.setSchools(schools);
	                            
	                            List<Form> newForms = this.formService.getFormsWithYear(year1.getYear());
	                            List<Integer> intForm = new ArrayList<>();
	                            for(Form tempForm : newForms) {
	                            	intForm.add(tempForm.getForm());
	                            }
	                            if(!intForm.contains(form.getForm())) {
	                            	newForms.add(form);
	                            }
	                           
	                            year1.setForms(newForms);
	                   	}else {
	                   		year1.setSchools(schools);
	                   	}
	                   	
	                    this.yearService.addYear(year1);
	                   	student.setForms(forms);
	                       
	                   	this.studentService.addStudent(student);
	                   	this.studentFormYearService.addStudentFormYearRecord(new StudentFormYear(student, this.yearService.getYearFromSchool(student.getYearAdmitted() - 1, code).get(), form));
	                       
	                   	//Form One 
	                   	Year year2 = new Year(student.getYearAdmitted() - 2);
	                   	years = new ArrayList<>();
	                   	
	                   	forms = new ArrayList<Form>();
	                   	form = new Form(1, terms);
	                   	forms.add(form);
	                   	if(!formService.ifFormExists(form.getForm(), year2.getYear(), code)) {
	                   		 this.formService.addForm(form);
	                   	}
	                      
	                   	year2.setForms(forms);
	                    years.add(year2);
	                   	student.setYears(years);

	                   	intYears = new ArrayList<>();
	                   	
	                   	for(Year tempYear : testYears) {
	                   		intYears.add(tempYear.getYear());
	                   	}
	                   	
	                   	if(intYears.contains(year2.getYear())) {
	                   		 List<School> newSchools = this.schoolService.getSchoolsByYear(year2.getYear());
                            newSchools.remove(school);
	                            schools.addAll(newSchools);
	                            year2.setSchools(schools);
	                            
	                            List<Form> newForms = this.formService.getFormsWithYear(year2.getYear());
	                            List<Integer> intForm = new ArrayList<>();
	                            for(Form tempForm : newForms) {
	                            	intForm.add(tempForm.getForm());
	                            }
	                            if(!intForm.contains(form.getForm())) {
	                            	newForms.add(form);
	                            }
	                           
	                            year2.setForms(newForms);
	                   	}else {
	                   		year2.setSchools(schools);
	                   	}
	                   	
	                    this.yearService.addYear(year2);
	                   	student.setForms(forms);
	                   	student.setSubjects(subjects);
	                       
	                   	this.studentService.addStudent(student);
	                   	this.studentFormYearService.addStudentFormYearRecord(new StudentFormYear(student, this.yearService.getYearFromSchool(student.getYearAdmitted() - 2, code).get(), form));
	                    
                        break;
                    }
                    case 4: {

                    	List<Integer> intYears;
                    	
                    	//Form Four 
                    	iterator = school.getCompSubjectF3F4().iterator();
                        while (iterator.hasNext()) {
                            subjects.add(iterator.next());
                        }
                        
	                   	Year year = new Year(student.getYearAdmitted());
	                   	years = new ArrayList<>();
	                   	
	                   	forms = new ArrayList<Form>();
	                   	form = new Form(4, terms);
	                   	forms.add(form);
	                   	if(!formService.ifFormExists(form.getForm(), year.getYear(), code)) {
	                   		 this.formService.addForm(form);
	                   	}
	                      
	                   	year.setForms(forms);
	                    years.add(year);
	                   	student.setYears(years);

	                   	intYears = new ArrayList<>();
	                   	
	                   	for(Year tempYear : testYears) {
	                   		intYears.add(tempYear.getYear());
	                   	}
	                   	
	                   	if(intYears.contains(year.getYear())) {
	                   		 List<School> newSchools = this.schoolService.getSchoolsByYear(year.getYear());
                            newSchools.remove(school);
	                            schools.addAll(newSchools);
	                            year.setSchools(schools);
	                            
	                            List<Form> newForms = this.formService.getFormsWithYear(year.getYear());
	                            List<Integer> intForm = new ArrayList<>();
	                            for(Form tempForm : newForms) {
	                            	intForm.add(tempForm.getForm());
	                            }
	                            if(!intForm.contains(form.getForm())) {
	                            	newForms.add(form);
	                            }
	                           
	                            year.setForms(newForms);
	                   	}else {
	                   		year.setSchools(schools);
	                   	}
	                   	
	                    this.yearService.addYear(year);
	                   	student.setForms(forms);
	                       
	                   	this.studentService.addStudent(student);
	                   	this.studentFormYearService.addStudentFormYearRecord(new StudentFormYear(student, this.yearService.getYearFromSchool(student.getYearAdmitted(), code).get(), form));
	                    
	                   	//Form Three
	                   	Year year1 = new Year(student.getYearAdmitted() - 1);
	                   	years = new ArrayList<>();
	                   	
	                   	forms = new ArrayList<Form>();
	                   	form = new Form(3, terms);
	                   	forms.add(form);
	                   	if(!formService.ifFormExists(form.getForm(), year1.getYear(), code)) {
	                   		 this.formService.addForm(form);
	                   	}
	                      
	                   	year1.setForms(forms);
	                    years.add(year1);
	                   	student.setYears(years);

	                   	intYears = new ArrayList<>();
	                   	
	                   	for(Year tempYear : testYears) {
	                   		intYears.add(tempYear.getYear());
	                   	}
	                   	
	                   	if(intYears.contains(year1.getYear())) {
	                   		 List<School> newSchools = this.schoolService.getSchoolsByYear(year1.getYear());
                            newSchools.remove(school);
	                            schools.addAll(newSchools);
	                            year1.setSchools(schools);
	                            
	                            List<Form> newForms = this.formService.getFormsWithYear(year1.getYear());
	                            List<Integer> intForm = new ArrayList<>();
	                            for(Form tempForm : newForms) {
	                            	intForm.add(tempForm.getForm());
	                            }
	                            if(!intForm.contains(form.getForm())) {
	                            	newForms.add(form);
	                            }
	                           
	                            year1.setForms(newForms);
	                   	}else {
	                   		year1.setSchools(schools);
	                   	}
	                   	
	                    this.yearService.addYear(year1);
	                   	student.setForms(forms);
	                       
	                   	this.studentService.addStudent(student);
	                   	this.studentFormYearService.addStudentFormYearRecord(new StudentFormYear(student, this.yearService.getYearFromSchool(student.getYearAdmitted() - 1, code).get(), form));
	                       
	                   	//Form Two    
	                   	Year year2 = new Year(student.getYearAdmitted() - 2);
	                   	years = new ArrayList<>();
	                   	
	                   	forms = new ArrayList<Form>();
	                   	form = new Form(2, terms);
	                   	forms.add(form);
	                   	if(!formService.ifFormExists(form.getForm(), year2.getYear(), code)) {
	                   		 this.formService.addForm(form);
	                   	}
	                      
	                   	year2.setForms(forms);
	                    years.add(year2);
	                   	student.setYears(years);

	                   	intYears = new ArrayList<>();
	                   	
	                   	for(Year tempYear : testYears) {
	                   		intYears.add(tempYear.getYear());
	                   	}
	                   	
	                   	if(intYears.contains(year2.getYear())) {
	                   		 List<School> newSchools = this.schoolService.getSchoolsByYear(year2.getYear());
                            newSchools.remove(school);
	                            schools.addAll(newSchools);
	                            year2.setSchools(schools);
	                            
	                            List<Form> newForms = this.formService.getFormsWithYear(year2.getYear());
	                            List<Integer> intForm = new ArrayList<>();
	                            for(Form tempForm : newForms) {
	                            	intForm.add(tempForm.getForm());
	                            }
	                            if(!intForm.contains(form.getForm())) {
	                            	newForms.add(form);
	                            }
	                           
	                            year2.setForms(newForms);
	                   	}else {
	                   		year2.setSchools(schools);
	                   	}
	                   	
	                    this.yearService.addYear(year2);
	                   	student.setForms(forms);
	                       
	                   	this.studentService.addStudent(student);
	                   	this.studentFormYearService.addStudentFormYearRecord(new StudentFormYear(student, this.yearService.getYearFromSchool(student.getYearAdmitted() - 2, code).get(), form));
	                    
	                  //Form One                         
	                   	Year year3 = new Year(student.getYearAdmitted() - 3);
	                   	years = new ArrayList<>();
	                   	
	                   	forms = new ArrayList<Form>();
	                   	form = new Form(1, terms);
	                   	forms.add(form);
	                   	if(!formService.ifFormExists(form.getForm(), year3.getYear(), code)) {
	                   		 this.formService.addForm(form);
	                   	}
	                      
	                   	year3.setForms(forms);
	                    years.add(year3);
	                   	student.setYears(years);

	                   	intYears = new ArrayList<>();
	                   	
	                   	for(Year tempYear : testYears) {
	                   		intYears.add(tempYear.getYear());
	                   	}
	                   	
	                   	if(intYears.contains(year3.getYear())) {
	                   		 List<School> newSchools = this.schoolService.getSchoolsByYear(year3.getYear());
                            newSchools.remove(school);
	                            schools.addAll(newSchools);
	                            year3.setSchools(schools);
	                            
	                            List<Form> newForms = this.formService.getFormsWithYear(year3.getYear());
	                            List<Integer> intForm = new ArrayList<>();
	                            for(Form tempForm : newForms) {
	                            	intForm.add(tempForm.getForm());
	                            }
	                            if(!intForm.contains(form.getForm())) {
	                            	newForms.add(form);
	                            }
	                           
	                            year3.setForms(newForms);
	                   	}else {
	                   		year3.setSchools(schools);
	                   	}
	                   	
	                    this.yearService.addYear(year3);
	                   	student.setForms(forms);
	                   	student.setSubjects(subjects);
	                       
	                   	this.studentService.addStudent(student);
	                   	this.studentFormYearService.addStudentFormYearRecord(new StudentFormYear(student, this.yearService.getYearFromSchool(student.getYearAdmitted() - 3, code).get(), form));
	                    
                    	
                    	
                        break;
                    }
                }
                
                redit.addFlashAttribute("success", "Student with admision number: " + admString[0] + " saved successfully");
            }
            view = "redirect:/schools/" + code + "/students";
        }
        return view;
      
    }

    @GetMapping(value={"/schools/{code}/students/{admNo}"})
    public String deleteStudent(@PathVariable int code, @PathVariable String admNo, Model model, Principal principal) {
    	
        if (this.studentService.ifStudentExistsInSchool(admNo, code).booleanValue()) {
            List<Mark> marks = this.markService.allMarks(admNo);
            for (int i = 0; i < marks.size(); ++i) {
                this.markService.deleteMark(marks.get(i).getId());
            }
            
            List<StudentFormYear> studentsFormYear = this.studentFormYearService.getAllStudentFormYearByStudentAdmNo(admNo);
        	
            for(StudentFormYear studentFormYear : studentsFormYear) {
            	if(studentFormYear.getStudent().getAdmNo().equals(admNo)) {
            		studentFormYearService.deleteStudentById(studentFormYear.getId());
            	}
            }
            
            this.studentService.deleteStudent(admNo);
            model.addAttribute("success", "Student with admision number:" + admNo + " successfully deleted");
        } else {
            model.addAttribute("fail", "Student with admision number:" + admNo + " does not exist");
        }
        List<Student> students = this.studentService.getAllStudentsInSchool(code);
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(code).get();
        List<Stream> streams = this.streamService.getStreamsInSchool(code);
        Student student = new Student();
        List<Subject> subjects = this.subjectService.getAllSubjectInSchool(code);
        List<Year> years = this.yearService.getAllYearsInSchool(code);
        model.addAttribute("years", years);
        model.addAttribute("subjects", subjects);
        model.addAttribute("streams", streams);
        model.addAttribute("activeUser", activeUser);
        model.addAttribute("student", student);
        model.addAttribute("school", school);
        model.addAttribute("students", students);
        if (activeUser.getRole().getName().contains("TEACHER")) {
            return "teacherHome";
        }
        return "students";
	    	
    }

    @GetMapping(value={"/schools/{code}/student/{admNo}"})
    public String getStudent(@PathVariable int code, @PathVariable String admNo, Model model, Principal principal) {
        Student student = this.studentService.getStudentInSchool(admNo, code);
        School school = this.schoolService.getSchool(code).get();
        List<Subject> subjects = this.subjectService.getSubjectDoneByStudent(admNo);
        List<Subject> schoolSubjects = this.subjectService.getAllSubjectInSchool(code);
        for (int i = 0; i < subjects.size(); ++i) {
            schoolSubjects.remove(subjects.get(i));
        }
        List<Form> forms = this.formService.studentForms(admNo);
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        ArrayList<Subject> group1 = new ArrayList<Subject>();
        ArrayList<Subject> group2 = new ArrayList<Subject>();
        ArrayList<Subject> group3 = new ArrayList<Subject>();
        ArrayList<Subject> group4 = new ArrayList<Subject>();
        ArrayList<Subject> group5 = new ArrayList<Subject>();
        for (int i = 0; i < subjects.size(); ++i) {
            if (subjects.get(i).getName().contains("Mathematics") || subjects.get(i).getName().contains("English") || subjects.get(i).getName().contains("Kiswahili")) {
                group1.add(subjects.get(i));
                continue;
            }
            if (subjects.get(i).getName().contains("Biology") || subjects.get(i).getName().contains("Physics") || subjects.get(i).getName().contains("Chemistry")) {
                group2.add(subjects.get(i));
                continue;
            }
            if (subjects.get(i).getInitials().contains("Hist") || subjects.get(i).getInitials().contains("Geo") || subjects.get(i).getInitials().contains("C.R.E") || subjects.get(i).getInitials().contains("I.R.E") || subjects.get(i).getInitials().contains("H.R.E")) {
                group3.add(subjects.get(i));
                continue;
            }
            if (subjects.get(i).getInitials().contains("Hsci") || subjects.get(i).getInitials().contains("AnD") || subjects.get(i).getInitials().contains("Agric") || subjects.get(i).getInitials().contains("Comp") || subjects.get(i).getInitials().contains("Avi") || subjects.get(i).getInitials().contains("Elec") || subjects.get(i).getInitials().contains("Pwr") || subjects.get(i).getInitials().contains("Wood") || subjects.get(i).getInitials().contains("Metal") || subjects.get(i).getInitials().contains("Bc") || subjects.get(i).getInitials().contains("Dnd")) {
                group4.add(subjects.get(i));
                continue;
            }
            group5.add(subjects.get(i));
        }
        List<Stream> streams = this.streamService.getStreamsInSchool(code);
        List<Year> years = this.yearService.getAllYearsInSchool(code);
        model.addAttribute("streams", streams);
        model.addAttribute("group1", group1);
        model.addAttribute("group2", group2);
        model.addAttribute("group3", group3);
        model.addAttribute("group4", group4);
        model.addAttribute("group5", group5);
        model.addAttribute("activeUser", activeUser);
        model.addAttribute("years", years);
        model.addAttribute("forms", forms);
        model.addAttribute("school", school);
        model.addAttribute("subjects", subjects);
        model.addAttribute("schoolSubjects", schoolSubjects);
        model.addAttribute("student", student);
        return "student";
    }
}