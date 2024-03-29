package com.pensasha.school.term;

import com.pensasha.school.exam.*;
import com.pensasha.school.form.Form;
import com.pensasha.school.form.FormService;
import com.pensasha.school.school.School;
import com.pensasha.school.school.SchoolService;
import com.pensasha.school.stream.StreamService;
import com.pensasha.school.student.Student;
import com.pensasha.school.student.StudentFormYear;
import com.pensasha.school.student.StudentFormYearService;
import com.pensasha.school.student.StudentService;
import com.pensasha.school.subject.Subject;
import com.pensasha.school.subject.SubjectService;
import com.pensasha.school.user.*;
import com.pensasha.school.year.Year;
import com.pensasha.school.year.YearService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
public class TermController {
    private final SubjectService subjectService;
    private final SchoolService schoolService;
    private final StudentService studentService;
    private final StreamService streamService;
    private final YearService yearService;
    private final FormService formService;
    private final UserService userService;
    private final MarkService markService;
    private final ExamNameService examNameService;
    private final StudentFormYearService studentFormYearService;
    private final TeacherYearFormStreamService teacherYearFormStreamService;
    
	public TermController(SubjectService subjectService, SchoolService schoolService, StudentService studentService,
			StreamService streamService, YearService yearService, FormService formService, UserService userService,
			MarkService markService, ExamNameService examNameService, StudentFormYearService studentFormYearService,
			TeacherYearFormStreamService teacherYearFormStreamService) {
		super();
		this.subjectService = subjectService;
		this.schoolService = schoolService;
		this.studentService = studentService;
		this.streamService = streamService;
		this.yearService = yearService;
		this.formService = formService;
		this.userService = userService;
		this.markService = markService;
		this.examNameService = examNameService;
		this.studentFormYearService = studentFormYearService;
		this.teacherYearFormStreamService = teacherYearFormStreamService;
	}

	@PostMapping(value={"/schools/{code}/studentsReport"})
    public String postStudentReport(@PathVariable int code, @RequestParam int year, @RequestParam int form, @RequestParam int term, @RequestParam String stream) {
        return "redirect:/schools/" + code + "/years/" + year + "/forms/" + form + "/terms/" + term + "/streams/" + stream + "/studentsReport";
    }

    @GetMapping(value={"/schools/{code}/years/{year}/forms/{form}/terms/{term}/streams/{stream}/studentsReport"})
    public String getStudentReport(@PathVariable int code, @PathVariable int year, @PathVariable int form, @PathVariable int term, @PathVariable String stream, Model model, Principal principal) {
    
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(code).get();
        Student student = new Student();
        
        List<Student> students = new ArrayList<>();
        List<StudentFormYear> studentsFormYear = this.studentFormYearService.getAllStudentsInSchoolByYearFormTerm(code, year, form, term);
        
        for(StudentFormYear studentFormYear : studentsFormYear) {
            if(!students.contains(studentFormYear.getStudent())) {
             	students.add(studentFormYear.getStudent());
            } 
        }
                
        List<Student> streamStudents = new ArrayList<>();
        List<StudentFormYear> studentsFormYear1 = this.studentFormYearService.getAllStudentFormYearByFormTermAndStream(code, year, form, term, stream);
        
        for(StudentFormYear studentFormYear : studentsFormYear1) {
            if(!streamStudents.contains(studentFormYear.getStudent())) {
            	streamStudents.add(studentFormYear.getStudent());
            } 
        }
        
      
        List<ExamName> examNames = new ArrayList<>();
		List<ExamName> eNs = this.examNameService.getExamBySchoolYearFormTerm(code, year, form, term);
		
		for (int i = 0; i < eNs.size(); ++i) {

			examNames.add(eNs.get(i));
            
            if (examNames.size() > 0) {
                for (int k = 0; k < examNames.size(); ++k) {
                   if(k>0)
                       {
                           if (examNames.get(k - 1).getName().equals(examNames.get(k).getName())) {
                        	   examNames.remove(examNames.get(k));
                               continue;
                           }
                       }
                }
			
            }
            
        }
        
        List<Subject> subjects = this.subjectService.getAllSubjectInSchool(code);

        int cnt = 0;
        for (int i = 0; i < students.size(); ++i) {
            model.addAttribute("subjects" + students.get(i).getAdmNo(), this.subjectService.getSubjectDoneByStudent(students.get(i).getAdmNo()));
            List<Mark> marks = this.markService.getTermlySubjectMark(students.get(i).getAdmNo(), form, term);
            int overalTotal = 0;
            for (int j = 0; j < subjects.size(); ++j) {
                int sum = 0;
                int totalOutOf = 0;
                for (int k = 0; k < marks.size(); ++k) {
                    if (!marks.get(k).getSubject().equals(subjects.get(j))) continue;
                    sum += marks.get(k).getMark();
                    totalOutOf += marks.get(k).getExamName().getOutOf();
                }
                if (sum > 0) {
                    ++cnt;
                }
                if (totalOutOf > 0) {
                    if (subjects.get(j).getInitials() != "C.R.E" && subjects.get(j).getInitials() != "I.R.E" && subjects.get(j).getInitials() != "H.R.E") {
                        model.addAttribute(subjects.get(j).getInitials() + "sum" + students.get(i).getAdmNo(), sum * 100 / totalOutOf);
                        overalTotal += sum * 100 / totalOutOf;
                    }
                    if (subjects.get(j).getInitials().equals("C.R.E")) {
                        model.addAttribute("Cresum" + students.get(i).getAdmNo(), sum * 100 / totalOutOf);
                        overalTotal += sum * 100 / totalOutOf;
                        continue;
                    }
                    if (subjects.get(j).getInitials().equals("H.R.E")) {
                        model.addAttribute("Hresum" + students.get(i).getAdmNo(), sum * 100 / totalOutOf);
                        overalTotal += sum * 100 / totalOutOf;
                        continue;
                    }
                    if (!subjects.get(j).getInitials().equals("I.R.E")) continue;
                    model.addAttribute("Iresum" + students.get(i).getAdmNo(), sum * 100 / totalOutOf);
                    overalTotal += sum * 100 / totalOutOf;
                    continue;
                }
                if (subjects.get(j).getInitials() != "C.R.E" && subjects.get(j).getInitials() != "I.R.E" && subjects.get(j).getInitials() != "H.R.E") {
                    model.addAttribute(subjects.get(j).getInitials() + "sum" + students.get(i).getAdmNo(), sum);
                    overalTotal += sum;
                }
                if (subjects.get(j).getInitials().equals("C.R.E")) {
                    model.addAttribute("Cresum" + students.get(i).getAdmNo(), sum);
                    overalTotal += sum;
                    continue;
                }
                if (subjects.get(j).getInitials().equals("H.R.E")) {
                    model.addAttribute("Hresum" + students.get(i).getAdmNo(), sum);
                    overalTotal += sum;
                    continue;
                }
                if (!subjects.get(j).getInitials().equals("I.R.E")) continue;
                model.addAttribute("Iresum" + students.get(i).getAdmNo(), sum);
                overalTotal += sum;
            }
            if(overalTotal >= 0){
                model.addAttribute("total" + students.get(i).getAdmNo(), overalTotal);
            }else{
                model.addAttribute("total" + students.get(i).getAdmNo(), 0);
            }

            model.addAttribute("marks" + students.get(i).getAdmNo(), marks);
            List<TeacherYearFormStream> teachers = this.teacherYearFormStreamService.getAllTeachersTeachingInYearFormAndStream(code, year, form, students.get(i).getStream().getId());
            model.addAttribute("teachers" + students.get(i).getAdmNo(), teachers);
        }
      
        getMeritList get = new getMeritList();

        model.addAttribute("activeUser", activeUser);
        model.addAttribute("school", school);
        model.addAttribute("streams", this.streamService.getStreamsInSchool(code));
        model.addAttribute("student", student);
        model.addAttribute("students", students);
        model.addAttribute("streamStudents", streamStudents);
        model.addAttribute("year", year);
        model.addAttribute("form", form);
        model.addAttribute("term", term);
        model.addAttribute("stream", stream);
        model.addAttribute("examNames", examNames);

        int count = 0;

        List<MeritList> allStudentsMeritList = get.getList(school, students, subjects, markService, year, form, term, studentService);
        Collections.sort(allStudentsMeritList, new SortByAverage().reversed());

        for(int i=0;i<allStudentsMeritList.size();i++){
            count++;
            if(count>1){
                if(allStudentsMeritList.get(i).getTotal() == allStudentsMeritList.get(i-1).getTotal()){
                    count--;
                }
            }
            allStudentsMeritList.get(i).setRank(count);
        }

        model.addAttribute("meritLists", allStudentsMeritList);

        int counter = 0;

        List<MeritList> streamStudentsMeritList = get.getList(school, streamStudents, subjects, markService, year, form, term, studentService);
        Collections.sort(streamStudentsMeritList, new SortByAverage().reversed());

        for(int i=0;i<streamStudentsMeritList.size();i++){
            counter++;
            if(counter>1){
                if(streamStudentsMeritList.get(i).getTotal() == streamStudentsMeritList.get(i-1).getTotal()){
                    counter--;
                }
            }
            streamStudentsMeritList.get(i).setRank(counter);
        }

        model.addAttribute("studentMeritList", streamStudentsMeritList);

        model.addAttribute("count", cnt);

       return "studentReport";

    }

    @PostMapping(value={"/schools/{code}/student/{admNo}/termlyReport"})
    public String getTermlyReport(@PathVariable int code, @PathVariable String admNo, @RequestParam int form, @RequestParam int year, @RequestParam int term, Model model, Principal principal) {
        return "redirect:/schools/" + code + "/years/" + year + "/forms/" + form + "/terms/" + term + "/students/" + admNo + "/termlyReport";
    }

    @GetMapping(value={"/schools/{code}/years/{year}/forms/{form}/terms/{term}/students/{admNo}/termlyReport"})
    public String getTermlyReports(@PathVariable int code, @PathVariable int year, @PathVariable int form, @PathVariable int term, @PathVariable String admNo, Model model, Principal principal) {
        School school = this.schoolService.getSchool(code).get();
        Student student = this.studentService.getStudentInSchool(admNo, code);
        List<Subject> subjects = this.subjectService.getSubjectDoneByStudent(admNo);
        List<Year> years = this.yearService.allYearsForStudent(admNo);
        List<Form> forms = this.formService.studentForms(admNo);
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        List<ExamName> examNames = this.examNameService.getExamBySchoolYearFormTerm(code, year, form, term);
        List<Mark> marks = this.markService.getTermlySubjectMark(admNo, form, term);
        List<Teacher> teachers = this.userService.getAllTeachersByAcademicYearAndSchoolFormStream(code, form, student.getStream().getId(), year);
        model.addAttribute("streams", this.streamService.getStreamsInSchool(code));
        model.addAttribute("activeUser", activeUser);
        model.addAttribute("marks", marks);
        model.addAttribute("forms", forms);
        model.addAttribute("years", years);
        model.addAttribute("subjects", subjects);
        model.addAttribute("student", student);
        model.addAttribute("school", school);
        model.addAttribute("examNames", examNames);
        model.addAttribute("year", year);
        model.addAttribute("form", form);
        model.addAttribute("term", term);
        model.addAttribute("teachers", teachers);
        return "termlyReport";
    }

    public List<MeritList> getMeritList(List<Student> students, List<Subject> subjects, int year, int form, int term) {
        int i;
        ArrayList<Student> studentsWithoutMarks = new ArrayList<Student>();
        ArrayList<Student> studentsWithMarks = new ArrayList<Student>();
        for (int i2 = 0; i2 < students.size(); ++i2) {
            if (!this.markService.getMarkByAdm(students.get(i2).getAdmNo()).booleanValue()) {
                studentsWithoutMarks.add(students.get(i2));
                continue;
            }
            studentsWithMarks.add(students.get(i2));
        }
        ArrayList<MeritList> meritLists = new ArrayList<MeritList>();
        int mathsCount = 0;
        int engCount = 0;
        int kisCount = 0;
        int bioCount = 0;
        int chemCount = 0;
        int phyCount = 0;
        int histCount = 0;
        int creCount = 0;
        int geoCount = 0;
        int ireCount = 0;
        int hreCount = 0;
        int hsciCount = 0;
        int andCount = 0;
        int agricCount = 0;
        int compCount = 0;
        int aviCount = 0;
        int elecCount = 0;
        int pwrCount = 0;
        int woodCount = 0;
        int metalCount = 0;
        int bcCount = 0;
        int frenCount = 0;
        int germCount = 0;
        int arabCount = 0;
        int mscCount = 0;
        int bsCount = 0;
        int dndCount = 0;
        for (i = 0; i < studentsWithMarks.size(); ++i) {
            int count = 0;
            MeritList meritList = new MeritList();
            block60: for (int j = 0; j < subjects.size(); ++j) {
                meritList.setFirstname(students.get(i).getFirstname());
                meritList.setSecondname(students.get(i).getThirdname());
                meritList.setAdmNo(students.get(i).getAdmNo());
                meritList.setKcpe(students.get(i).getKcpeMarks());
                meritList.setStream(students.get(i).getStream().getStream());
                List<Mark> marks = new ArrayList<Mark>();
                int sum = 0;
                switch (subjects.get(j).getInitials()) {
                    case "Maths": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                        }
                        if (sum > 0) {
                            ++count;
                            ++mathsCount;
                        }
                        meritList.setMaths(sum);
                        continue block60;
                    }
                    case "Eng": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                        }
                        if (sum > 0) {
                            ++count;
                            ++engCount;
                        }
                        meritList.setEng(sum);
                        continue block60;
                    }
                    case "Kis": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                        }
                        if (sum > 0) {
                            ++count;
                            ++kisCount;
                        }
                        meritList.setKis(sum);
                        continue block60;
                    }
                    case "Bio": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                        }
                        if (sum > 0) {
                            ++count;
                            ++bioCount;
                        }
                        meritList.setBio(sum);
                        continue block60;
                    }
                    case "Chem": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                        }
                        if (sum > 0) {
                            ++count;
                            ++chemCount;
                        }
                        meritList.setChem(sum);
                        continue block60;
                    }
                    case "Phy": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                        }
                        if (sum > 0) {
                            ++count;
                            ++phyCount;
                        }
                        meritList.setPhy(sum);
                        continue block60;
                    }
                    case "Hist": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                        }
                        if (sum > 0) {
                            ++count;
                            ++histCount;
                        }
                        meritList.setHist(sum);
                        continue block60;
                    }
                    case "C.R.E": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                        }
                        if (sum > 0) {
                            ++count;
                            ++creCount;
                        }
                        meritList.setCre(sum);
                        continue block60;
                    }
                    case "Geo": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                        }
                        if (sum > 0) {
                            ++count;
                            ++geoCount;
                        }
                        meritList.setGeo(sum);
                        continue block60;
                    }
                    case "I.R.E": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                        }
                        if (sum > 0) {
                            ++count;
                            ++ireCount;
                        }
                        meritList.setIre(sum);
                        continue block60;
                    }
                    case "H.R.E": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                        }
                        if (sum > 0) {
                            ++count;
                            ++hreCount;
                        }
                        meritList.setHre(sum);
                        continue block60;
                    }
                    case "Hsci": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                        }
                        if (sum > 0) {
                            ++count;
                            ++hsciCount;
                        }
                        meritList.setHsci(sum);
                        continue block60;
                    }
                    case "AnD": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                        }
                        if (sum > 0) {
                            ++count;
                            ++andCount;
                        }
                        meritList.setAnD(sum);
                        continue block60;
                    }
                    case "Agric": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                        }
                        if (sum > 0) {
                            ++count;
                            ++agricCount;
                        }
                        meritList.setAgric(sum);
                        continue block60;
                    }
                    case "Comp": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                        }
                        if (sum > 0) {
                            ++count;
                            ++compCount;
                        }
                        meritList.setComp(sum);
                        continue block60;
                    }
                    case "Avi": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                        }
                        if (sum > 0) {
                            ++count;
                            ++aviCount;
                        }
                        meritList.setAvi(sum);
                        continue block60;
                    }
                    case "Elec": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                        }
                        if (sum > 0) {
                            ++count;
                            ++elecCount;
                        }
                        meritList.setElec(sum);
                        continue block60;
                    }
                    case "Pwr": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                        }
                        if (sum > 0) {
                            ++count;
                            ++pwrCount;
                        }
                        meritList.setPwr(sum);
                        continue block60;
                    }
                    case "Wood": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                        }
                        if (sum > 0) {
                            ++count;
                            ++woodCount;
                        }
                        meritList.setWood(sum);
                        continue block60;
                    }
                    case "Metal": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                        }
                        if (sum > 0) {
                            ++count;
                            ++metalCount;
                        }
                        meritList.setMetal(sum);
                        continue block60;
                    }
                    case "Bc": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                        }
                        if (sum > 0) {
                            ++count;
                            ++bcCount;
                        }
                        meritList.setBc(sum);
                        continue block60;
                    }
                    case "Fren": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                        }
                        if (sum > 0) {
                            ++count;
                            ++frenCount;
                        }
                        meritList.setFren(sum);
                        continue block60;
                    }
                    case "Germ": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                        }
                        if (sum > 0) {
                            ++count;
                            ++germCount;
                        }
                        meritList.setGerm(sum);
                        continue block60;
                    }
                    case "Arab": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                        }
                        if (sum > 0) {
                            ++count;
                            ++arabCount;
                        }
                        meritList.setArab(sum);
                        continue block60;
                    }
                    case "Msc": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                        }
                        if (sum > 0) {
                            ++count;
                            ++mscCount;
                        }
                        meritList.setMsc(sum);
                        continue block60;
                    }
                    case "Bs": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                        }
                        if (sum > 0) {
                            ++count;
                            ++bsCount;
                        }
                        meritList.setBs(sum);
                        continue block60;
                    }
                    case "DnD": {
                        int k;
                        marks = this.markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                        for (k = 0; k < marks.size(); ++k) {
                            sum += marks.get(k).getMark();
                        }
                        if (sum > 0) {
                            ++count;
                            ++dndCount;
                        }
                        meritList.setDnd(sum);
                    }
                }
            }
            meritList.setTotal(meritList.getMaths() + meritList.getEng() + meritList.getKis() + meritList.getBio() + meritList.getChem() + meritList.getPhy() + meritList.getHist() + meritList.getCre() + meritList.getGeo() + meritList.getIre() + meritList.getHre() + meritList.getHsci() + meritList.getAnD() + meritList.getAgric() + meritList.getComp() + meritList.getAvi() + meritList.getElec() + meritList.getPwr() + meritList.getWood() + meritList.getMetal() + meritList.getBc() + meritList.getFren() + meritList.getGerm() + meritList.getArab() + meritList.getMsc() + meritList.getBs() + meritList.getDnd());
            if (count > 0) {
                meritList.setAverage(meritList.getTotal() / count);
                meritList.setDeviation(meritList.getAverage() - students.get(i).getKcpeMarks() / 5);
                meritLists.add(meritList);
                continue;
            }
            meritList.setAverage(0);
            meritList.setDeviation(meritList.getAverage() - students.get(i).getKcpeMarks() / 5);
            meritLists.add(meritList);
        }
        Collections.sort(meritLists, new SortByAverage());
        for (i = 0; i < studentsWithoutMarks.size(); ++i) {
            MeritList meritList = new MeritList();
            meritList.setFirstname(studentsWithoutMarks.get(i).getFirstname());
            meritList.setSecondname(studentsWithoutMarks.get(i).getSecondname());
            meritList.setAdmNo(studentsWithoutMarks.get(i).getAdmNo());
            meritList.setKcpe(studentsWithoutMarks.get(i).getKcpeMarks());
            meritList.setStream(studentsWithoutMarks.get(i).getStream().getStream());
            meritList.setTotal(0);
            meritLists.add(meritList);
        }
        for (i = 0; i < meritLists.size(); ++i) {
            meritLists.get(i).setRank(i + 1);
        }
        return meritLists;
    }
}

class SortByAverage implements Comparator<MeritList>{
	
	public int compare(MeritList a, MeritList b) {
		return Float.compare(a.getAverage(), b.getAverage());
	}
}

class getMeritList {

	public List<MeritList> getList(School school, List<Student> students, List<Subject> subjects, MarkService markService, int year,
			int form, int term, StudentService studentService) {

		List<Student> studentsWithoutMarks = new ArrayList<>();
		List<Student> studentsWithMarks = new ArrayList<>();

		for (int i = 0; i < students.size(); i++) {
			if (!markService.getMarkByAdm(students.get(i).getAdmNo())) {
				studentsWithoutMarks.add(students.get(i));
			} else {
				studentsWithMarks.add(students.get(i));
			}

		}

		List<MeritList> meritLists = new ArrayList<>();

		int mathsCount = 0, engCount = 0, kisCount = 0, bioCount = 0, chemCount = 0, phyCount = 0, histCount = 0,
				creCount = 0, geoCount = 0, ireCount = 0, hreCount = 0, hsciCount = 0, andCount = 0, agricCount = 0,
				compCount = 0, aviCount = 0, elecCount = 0, pwrCount = 0, woodCount = 0, metalCount = 0, bcCount = 0,
				frenCount = 0, germCount = 0, arabCount = 0, mscCount = 0, bsCount = 0, dndCount = 0;

		for (int i = 0; i < studentsWithMarks.size(); i++) {

			MeritList meritList = new MeritList();

			int count = 0;

			for (int j = 0; j < subjects.size(); j++) {

				meritList.setFirstname(students.get(i).getFirstname());
				meritList.setSecondname(students.get(i).getThirdname());
				meritList.setAdmNo(students.get(i).getAdmNo());
				meritList.setKcpe(students.get(i).getKcpeMarks());
				meritList.setStream(students.get(i).getStream().getStream());

				List<Mark> marks = new ArrayList<>();

				int sum = 0;
				int totalOutOf = 0;
				switch (subjects.get(j).getInitials()) {
				case "Maths":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
                        totalOutOf += marks.get(k).getExamName().getOutOf();
					}

					if (sum > 0) {
						count++;
						mathsCount++;
					}
                    if (totalOutOf > 0) {
                        meritList.setMaths(sum * 100 / totalOutOf);
                    }else{
                        meritList.setMaths(sum);
                    }

					break;
				case "Eng":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
                        totalOutOf += marks.get(k).getExamName().getOutOf();
					}

					if (sum > 0) {
						count++;
						engCount++;
					}

                    if (totalOutOf > 0) {
                        meritList.setEng(sum * 100 / totalOutOf);
                    }else{
                        meritList.setEng(sum);
                    }

					break;
				case "Kis":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());


                    for (int k = 0; k < marks.size(); k++) {
                        sum = sum + marks.get(k).getMark();
                        totalOutOf += marks.get(k).getExamName().getOutOf();
                    }

					if (sum > 0) {
						count++;
						kisCount++;
					}
                    if (totalOutOf > 0) {
                        meritList.setKis(sum * 100 / totalOutOf);
                    }else{
                        meritList.setKis(sum);
                    }

					break;
				case "Bio":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());


                    for (int k = 0; k < marks.size(); k++) {
                        sum = sum + marks.get(k).getMark();
                        totalOutOf += marks.get(k).getExamName().getOutOf();
                    }
					if (sum > 0) {
						count++;
						bioCount++;
					}
                    if (totalOutOf > 0) {
                        meritList.setBio(sum * 100 / totalOutOf);
                    }else{
                        meritList.setBio(sum);
                    }

					break;
				case "Chem":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

                    for (int k = 0; k < marks.size(); k++) {
                        sum = sum + marks.get(k).getMark();
                        totalOutOf += marks.get(k).getExamName().getOutOf();
                    }

					if (sum > 0) {
						count++;
						chemCount++;
					}
                    if (totalOutOf > 0) {
                        meritList.setChem(sum * 100 / totalOutOf);
                    }else{
                        meritList.setChem(sum);
                    }

					break;
				case "Phy":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

                    for (int k = 0; k < marks.size(); k++) {
                        sum = sum + marks.get(k).getMark();
                        totalOutOf += marks.get(k).getExamName().getOutOf();
                    }

					if (sum > 0) {
						count++;
						phyCount++;
					}
                    if (totalOutOf > 0) {
                        meritList.setPhy(sum * 100 / totalOutOf);
                    }else{
                        meritList.setPhy(sum);
                    }

					break;
				case "Hist":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

                    for (int k = 0; k < marks.size(); k++) {
                        sum = sum + marks.get(k).getMark();
                        totalOutOf += marks.get(k).getExamName().getOutOf();
                    }

					if (sum > 0) {
						count++;
						histCount++;
					}
                    if (totalOutOf > 0) {
                        meritList.setHist(sum * 100 / totalOutOf);
                    }else{
                        meritList.setHist(sum);
                    }

					break;
				case "C.R.E":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

                    for (int k = 0; k < marks.size(); k++) {
                        sum = sum + marks.get(k).getMark();
                        totalOutOf += marks.get(k).getExamName().getOutOf();
                    }

					if (sum > 0) {
						count++;
						creCount++;
					}
                    if (totalOutOf > 0) {
                        meritList.setCre(sum * 100 / totalOutOf);
                    }else{
                        meritList.setCre(sum);
                    }

					break;
				case "Geo":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

                    for (int k = 0; k < marks.size(); k++) {
                        sum = sum + marks.get(k).getMark();
                        totalOutOf += marks.get(k).getExamName().getOutOf();
                    }

					if (sum > 0) {
						count++;
						geoCount++;
					}
                    if (totalOutOf > 0) {
                        meritList.setGeo(sum * 100 / totalOutOf);
                    }else{
                        meritList.setGeo(sum);
                    }

					break;
				case "I.R.E":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

                    for (int k = 0; k < marks.size(); k++) {
                        sum = sum + marks.get(k).getMark();
                        totalOutOf += marks.get(k).getExamName().getOutOf();
                    }

					if (sum > 0) {
						count++;
						ireCount++;
					}
                    if (totalOutOf > 0) {
                        meritList.setIre(sum * 100 / totalOutOf);
                    }else{
                        meritList.setIre(sum);
                    }

					break;
				case "H.R.E":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

                    for (int k = 0; k < marks.size(); k++) {
                        sum = sum + marks.get(k).getMark();
                        totalOutOf += marks.get(k).getExamName().getOutOf();
                    }

					if (sum > 0) {
						count++;
						hreCount++;
					}
                    if (totalOutOf > 0) {
                        meritList.setHre(sum * 100 / totalOutOf);
                    }else{
                        meritList.setHre(sum);
                    }

					break;
				case "Hsci":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

                    for (int k = 0; k < marks.size(); k++) {
                        sum = sum + marks.get(k).getMark();
                        totalOutOf += marks.get(k).getExamName().getOutOf();
                    }

					if (sum > 0) {
						count++;
						hsciCount++;
					}
                    if (totalOutOf > 0) {
                        meritList.setHsci(sum * 100 / totalOutOf);
                    }else{
                        meritList.setHsci(sum);
                    }

					break;
				case "AnD":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

                    for (int k = 0; k < marks.size(); k++) {
                        sum = sum + marks.get(k).getMark();
                        totalOutOf += marks.get(k).getExamName().getOutOf();
                    }

					if (sum > 0) {
						count++;
						andCount++;
					}
                    if (totalOutOf > 0) {
                        meritList.setAnD(sum * 100 / totalOutOf);
                    }else{
                        meritList.setAnD(sum);
                    }

					break;
				case "Agric":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

                    for (int k = 0; k < marks.size(); k++) {
                        sum = sum + marks.get(k).getMark();
                        totalOutOf += marks.get(k).getExamName().getOutOf();
                    }

					if (sum > 0) {
						count++;
						agricCount++;
					}
                    if (totalOutOf > 0) {
                        meritList.setAgric(sum * 100 / totalOutOf);
                    }else{
                        meritList.setAgric(sum);
                    }

					break;
				case "Comp":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

                    for (int k = 0; k < marks.size(); k++) {
                        sum = sum + marks.get(k).getMark();
                        totalOutOf += marks.get(k).getExamName().getOutOf();
                    }

					if (sum > 0) {
						count++;
						compCount++;
					}
                    if (totalOutOf > 0) {
                        meritList.setComp(sum * 100 / totalOutOf);
                    }else{
                        meritList.setComp(sum);
                    }

					break;
				case "Avi":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

                    for (int k = 0; k < marks.size(); k++) {
                        sum = sum + marks.get(k).getMark();
                        totalOutOf += marks.get(k).getExamName().getOutOf();
                    }

					if (sum > 0) {
						count++;
						aviCount++;
					}
                    if (totalOutOf > 0) {
                        meritList.setAvi(sum * 100 / totalOutOf);
                    }else{
                        meritList.setAvi(sum);
                    }

					break;
				case "Elec":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

                    for (int k = 0; k < marks.size(); k++) {
                        sum = sum + marks.get(k).getMark();
                        totalOutOf += marks.get(k).getExamName().getOutOf();
                    }

					if (sum > 0) {
						count++;
						elecCount++;
					}
                    if (totalOutOf > 0) {
                        meritList.setElec(sum * 100 / totalOutOf);
                    }else{
                        meritList.setElec(sum);
                    }

					break;
				case "Pwr":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

                    for (int k = 0; k < marks.size(); k++) {
                        sum = sum + marks.get(k).getMark();
                        totalOutOf += marks.get(k).getExamName().getOutOf();
                    }

					if (sum > 0) {
						count++;
						pwrCount++;
					}
                    if (totalOutOf > 0) {
                        meritList.setPwr(sum * 100 / totalOutOf);
                    }else{
                        meritList.setPwr(sum);
                    }

					break;
				case "Wood":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

                    for (int k = 0; k < marks.size(); k++) {
                        sum = sum + marks.get(k).getMark();
                        totalOutOf += marks.get(k).getExamName().getOutOf();
                    }

					if (sum > 0) {
						count++;
						woodCount++;
					}

                    if (totalOutOf > 0) {
                        meritList.setWood(sum * 100 / totalOutOf);
                    }else{
                        meritList.setWood(sum);
                    }

					break;
				case "Metal":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

                    for (int k = 0; k < marks.size(); k++) {
                        sum = sum + marks.get(k).getMark();
                        totalOutOf += marks.get(k).getExamName().getOutOf();
                    }

					if (sum > 0) {
						count++;
						metalCount++;
					}
                    if (totalOutOf > 0) {
                        meritList.setMetal(sum * 100 / totalOutOf);
                    }else{
                        meritList.setMetal(sum);
                    }

					break;
				case "Bc":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

                    for (int k = 0; k < marks.size(); k++) {
                        sum = sum + marks.get(k).getMark();
                        totalOutOf += marks.get(k).getExamName().getOutOf();
                    }

					if (sum > 0) {
						count++;
						bcCount++;
					}
                    if (totalOutOf > 0) {
                        meritList.setBc(sum * 100 / totalOutOf);
                    }else{
                        meritList.setBc(sum);
                    }

					break;
				case "Fren":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

                    for (int k = 0; k < marks.size(); k++) {
                        sum = sum + marks.get(k).getMark();
                        totalOutOf += marks.get(k).getExamName().getOutOf();
                    }

					if (sum > 0) {
						count++;
						frenCount++;
					}

                    if (totalOutOf > 0) {
                        meritList.setFren(sum * 100 / totalOutOf);
                    }else{
                        meritList.setFren(sum);
                    }

					break;
				case "Germ":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

                    for (int k = 0; k < marks.size(); k++) {
                        sum = sum + marks.get(k).getMark();
                        totalOutOf += marks.get(k).getExamName().getOutOf();
                    }

					if (sum > 0) {
						count++;
						germCount++;
					}
                    if (totalOutOf > 0) {
                        meritList.setGerm(sum * 100 / totalOutOf);
                    }else{
                        meritList.setGerm(sum);
                    }

					break;
				case "Arab":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

                    for (int k = 0; k < marks.size(); k++) {
                        sum = sum + marks.get(k).getMark();
                        totalOutOf += marks.get(k).getExamName().getOutOf();
                    }

					if (sum > 0) {
						count++;
						arabCount++;
					}
                    if (totalOutOf > 0) {
                        meritList.setArab(sum * 100 / totalOutOf);
                    }else{
                        meritList.setArab(sum);
                    }

					break;
				case "Msc":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

                    for (int k = 0; k < marks.size(); k++) {
                        sum = sum + marks.get(k).getMark();
                        totalOutOf += marks.get(k).getExamName().getOutOf();
                    }

					if (sum > 0) {
						count++;
						mscCount++;
					}
                    if (totalOutOf > 0) {
                        meritList.setMsc(sum * 100 / totalOutOf);
                    }else{
                        meritList.setMsc(sum);
                    }

					break;
				case "Bs":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

                    for (int k = 0; k < marks.size(); k++) {
                        sum = sum + marks.get(k).getMark();
                        totalOutOf += marks.get(k).getExamName().getOutOf();
                    }

					if (sum > 0) {
						count++;
						bsCount++;
					}
                    if (totalOutOf > 0) {
                        meritList.setBs(sum * 100 / totalOutOf);
                    }else{
                        meritList.setBs(sum);
                    }

					break;
				case "DnD":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

                    for (int k = 0; k < marks.size(); k++) {
                        sum = sum + marks.get(k).getMark();
                        totalOutOf += marks.get(k).getExamName().getOutOf();
                    }
					if (sum > 0) {
						count++;
						dndCount++;
					}
                    if (totalOutOf > 0) {
                        meritList.setDnd(sum * 100 / totalOutOf);
                    }else{
                        meritList.setDnd(sum);
                    }

					break;
				}

			}

            int maths01 = 0;
            if(meritList.getMaths() >= 0){
                maths01 = meritList.getMaths();
            }

            int eng01 = 0;
            if(meritList.getEng() >= 0){
                eng01 = meritList.getEng();
            }

            int kis01 = 0;
            if(meritList.getKis() >= 0){
                kis01 = meritList.getKis();
            }

            int bio01 = 0;
            if(meritList.getBio() >= 0){
                bio01 = meritList.getBio();
            }

            int chem01 = 0;
            if(meritList.getChem() >= 0){
                chem01 = meritList.getChem();
            }

            int phy01 = 0;
            if(meritList.getPhy() >= 0 ){
                phy01 = meritList.getPhy();
            }

            int hist01 = 0;
            if(meritList.getHist() >= 0){
                hist01 = meritList.getHist();
            }

            int cre01 = 0;
            if(meritList.getCre() >= 0) {
                cre01 = meritList.getCre();
            }

            int geo01 = 0;
            if(meritList.getGeo() >= 0 ){
                geo01 = meritList.getGeo();
            }

            int ire01 = 0;
            if(meritList.getIre() >=0){
                ire01 = meritList.getIre();
            }

            int hre01 = 0;
            if(meritList.getHre() >= 0){
                hre01 = meritList.getHre();
            }

            int hsci01 = 0;
            if(meritList.getHsci() >= 0){
                hsci01 = meritList.getHsci();
            }

            int and01 = 0;
            if(meritList.getAnD() >= 0){
                and01 = meritList.getAnD();
            }

            int agric01 = 0;
            if(meritList.getAgric() >= 0){
                agric01 = meritList.getAgric();
            }

            int comp01 = 0;
            if(meritList.getComp() >= 0 ){
                comp01 = meritList.getComp();
            }

            int avi01 = 0;
            if(meritList.getAvi() >= 0){
                avi01 = meritList.getAvi();
            }

            int elec01 = 0;
            if(meritList.getElec() >= 0){
                elec01 = meritList.getElec();
            }

            int pwr01 = 0;
            if(meritList.getPwr() >= 0 ){
                pwr01 = meritList.getPwr();
            }

            int wood01 = 0;
            if(meritList.getWood() >= 0){
                wood01 = meritList.getWood();
            }

            int metal01 = 0;
            if(meritList.getMetal() >= 0){
                metal01 = meritList.getMetal();
            }

            int bc01 = 0;
            if(meritList.getBc() >= 0){
                bc01 = meritList.getBc();
            }

            int fren01 = 0;
            if(meritList.getFren() >= 0 ){
                fren01 = meritList.getFren();
            }

            int germ01 = 0;
            if(meritList.getGerm() >= 0){
                germ01 = meritList.getGerm();
            }

            int arab01 = 0;
            if(meritList.getArab() >= 0 ){
                arab01 = meritList.getArab();
            }

            int msc01 = 0;
            if(meritList.getMsc() >= 0){
                msc01 = meritList.getMsc();
            }

            int bs01 = 0;
            if(meritList.getBs() >= 0){
                bs01 = meritList.getBs();
            }

            int dnd01 = 0;
            if(meritList.getDnd() >= 0){
                dnd01 = meritList.getDnd();
            }

            meritList.setTotal(maths01 + eng01 + kis01 + bio01 + chem01 + phy01 + hist01 + cre01 + geo01 + ire01 + hre01 + hsci01 + and01 + agric01 + comp01 + avi01 + elec01 + pwr01 + wood01 + metal01 + bc01 + fren01 + germ01 + arab01 + msc01 + bs01 + dnd01);

			if(count > 0){
				meritList.setAverage(meritList.getTotal() / studentService.getStudentInSchool(meritList.getAdmNo(), school.getCode()).getSubjects().size());
			}else{
				meritList.setAverage(0);
			}
			meritList.setDeviation(meritList.getAverage() - (students.get(i).getKcpeMarks()) / 5);
			meritLists.add(meritList);

		}

		Collections.sort(meritLists, new SortByAverage());

		for (int i = 0; i < studentsWithoutMarks.size(); i++) {

			MeritList meritList = new MeritList();

			meritList = new MeritList();
			meritList.setFirstname(studentsWithoutMarks.get(i).getFirstname());
			meritList.setSecondname(studentsWithoutMarks.get(i).getSecondname());
			meritList.setAdmNo(studentsWithoutMarks.get(i).getAdmNo());
			meritList.setKcpe(studentsWithoutMarks.get(i).getKcpeMarks());
			meritList.setStream(studentsWithoutMarks.get(i).getStream().getStream());
			meritList.setTotal(0);

			meritLists.add(meritList);

		}

        int count = 0;

        for(int j = 0; j<meritLists.size();j++){
            count++;
            if(count>1){
                if(meritLists.get(j).getTotal() == meritLists.get(j-1).getTotal()){
                    count--;
                }
            }
            meritLists.get(j).setRank(count);
        }

		return meritLists;
	}
}
