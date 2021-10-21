package com.pensasha.school.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Principal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.WebContext;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.styledxmlparser.css.media.MediaDeviceDescription;
import com.itextpdf.styledxmlparser.css.media.MediaType;
import com.pensasha.school.discipline.Discipline;
import com.pensasha.school.discipline.DisciplineService;
import com.pensasha.school.exam.ExamName;
import com.pensasha.school.exam.ExamNameService;
import com.pensasha.school.exam.GradeCount;
import com.pensasha.school.exam.Mark;
import com.pensasha.school.exam.MarkService;
import com.pensasha.school.exam.MeritList;
import com.pensasha.school.finance.FeeBalance;
import com.pensasha.school.finance.FeeRecord;
import com.pensasha.school.finance.FeeRecordService;
import com.pensasha.school.finance.FeeStructure;
import com.pensasha.school.finance.FeeStructureService;
import com.pensasha.school.form.Form;
import com.pensasha.school.form.FormService;
import com.pensasha.school.school.School;
import com.pensasha.school.school.SchoolService;
import com.pensasha.school.stream.Stream;
import com.pensasha.school.stream.StreamService;
import com.pensasha.school.student.Student;
import com.pensasha.school.student.StudentFormYear;
import com.pensasha.school.student.StudentFormYearService;
import com.pensasha.school.student.StudentService;
import com.pensasha.school.subject.Subject;
import com.pensasha.school.subject.SubjectService;
import com.pensasha.school.term.Term;
import com.pensasha.school.term.TermService;
import com.pensasha.school.timetable.Timetable;
import com.pensasha.school.timetable.TimetableService;
import com.pensasha.school.user.SchoolUser;
import com.pensasha.school.user.Teacher;
import com.pensasha.school.user.TeacherYearFormStream;
import com.pensasha.school.user.TeacherYearFormStreamService;
import com.pensasha.school.user.User;
import com.pensasha.school.user.UserService;
import com.pensasha.school.year.Year;
import com.pensasha.school.year.YearService;

@Controller
public class ReportController {

    private UserService userService;
    private SubjectService subjectService;
    private TermService termService;
    private YearService yearService;
    private FormService formService;
    private SchoolService schoolService;
    private StreamService streamService;
    private ExamNameService examNameService;
    private StudentService studentService;
    private MarkService markService;
    private TimetableService timetableService;
    private final TemplateEngine templateEngine;
    private FeeRecordService feeRecordService;
    private FeeStructureService feeStructureService;
    private DisciplineService disciplineService;
    private StudentFormYearService studentFormYearService;
    private TeacherYearFormStreamService teacherYearFormStreamService;

    @Autowired
    ServletContext servletContext;
    
    private final String baseUrl = "http://localhost:8080/";
    //private final String baseUrl = "http://analytica-env.eba-iigws4mq.us-east-2.elasticbeanstalk.com/";

    public ReportController(UserService userService, SubjectService subjectService, TermService termService,
			YearService yearService, FormService formService, SchoolService schoolService, StreamService streamService,
			ExamNameService examNameService, StudentService studentService, MarkService markService,
			TimetableService timetableService, TemplateEngine templateEngine, FeeRecordService feeRecordService,
			FeeStructureService feeStructureService, DisciplineService disciplineService,
			StudentFormYearService studentFormYearService, TeacherYearFormStreamService teacherYearFormStreamService,
			ServletContext servletContext) {
		super();
		this.userService = userService;
		this.subjectService = subjectService;
		this.termService = termService;
		this.yearService = yearService;
		this.formService = formService;
		this.schoolService = schoolService;
		this.streamService = streamService;
		this.examNameService = examNameService;
		this.studentService = studentService;
		this.markService = markService;
		this.timetableService = timetableService;
		this.templateEngine = templateEngine;
		this.feeRecordService = feeRecordService;
		this.feeStructureService = feeStructureService;
		this.disciplineService = disciplineService;
		this.studentFormYearService = studentFormYearService;
		this.teacherYearFormStreamService = teacherYearFormStreamService;
		this.servletContext = servletContext;
	}
    
    @GetMapping(value={"/schools/{code}/discipline/pdf"})
    public ResponseEntity<?> getDisciplineReportsPdf(@PathVariable int code, Principal principal, Model model, HttpServletRequest request, HttpServletResponse response) {
        SchoolUser activeUser = (SchoolUser)this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(activeUser.getSchool().getCode()).get();
        Student student = new Student();
        List<SchoolUser> schoolUsers = this.userService.getUsersBySchoolCode(school.getCode());
        User user = new User();
        List<Year> years = this.yearService.getAllYearsInSchool(school.getCode());
        List<Stream> streams = this.streamService.getStreamsInSchool(school.getCode());
        List<Subject> subjects = this.subjectService.getAllSubjectInSchool(school.getCode());
        List<Discipline> allDisciplineReport = this.disciplineService.allDisciplineReportBySchoolCode(code);
        WebContext context = new WebContext(request, response, this.servletContext);
        context.setVariable("disciplines", allDisciplineReport);
        context.setVariable("subjects", subjects);
        context.setVariable("streams", streams);
        context.setVariable("years", years);
        context.setVariable("schoolUsers", schoolUsers);
        context.setVariable("user", (Object)user);
        context.setVariable("activeUser", (Object)activeUser);
        context.setVariable("student", (Object)student);
        context.setVariable("school", (Object)school);
        String disciplineHtml = this.templateEngine.process("disciplinePdf", (IContext)context);
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setBaseUri("http://analytica-env.eba-iigws4mq.us-east-2.elasticbeanstalk.com/");
        HtmlConverter.convertToPdf((String)disciplineHtml, (OutputStream)target, (ConverterProperties)converterProperties);
        byte[] bytes = target.toByteArray();
        return ResponseEntity.ok().contentType(org.springframework.http.MediaType.APPLICATION_PDF).body((Object)bytes);
    }


	@GetMapping(value={"/schools/{code}/discipline/{id}/pdf"})
    public ResponseEntity<?> getDisciplineReportPdf(@PathVariable int code, @PathVariable int id, Principal principal, Model model, HttpServletRequest request, HttpServletResponse response) {
        SchoolUser activeUser = (SchoolUser)this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(activeUser.getSchool().getCode()).get();
        Discipline discipline = this.disciplineService.getDisciplineReportById(id).get();
        Student student = discipline.getStudent();
        User user = new User();
        List<Year> years = this.yearService.getAllYearsInSchool(school.getCode());
        List<Stream> streams = this.streamService.getStreamsInSchool(school.getCode());
        List<Subject> subjects = this.subjectService.getAllSubjectInSchool(school.getCode());
        WebContext context = new WebContext(request, response, this.servletContext);
        context.setVariable("discipline", (Object)discipline);
        context.setVariable("subjects", subjects);
        context.setVariable("streams", streams);
        context.setVariable("years", years);
        context.setVariable("user", (Object)user);
        context.setVariable("activeUser", (Object)activeUser);
        context.setVariable("student", (Object)student);
        context.setVariable("school", (Object)school);
        String viewDisciplineHtml = this.templateEngine.process("viewDisciplinePdf", (IContext)context);
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setBaseUri("http://analytica-env.eba-iigws4mq.us-east-2.elasticbeanstalk.com/");
        HtmlConverter.convertToPdf((String)viewDisciplineHtml, (OutputStream)target, (ConverterProperties)converterProperties);
        byte[] bytes = target.toByteArray();
        return ResponseEntity.ok().contentType(org.springframework.http.MediaType.APPLICATION_PDF).body((Object)bytes);
    }

    @GetMapping(value={"/schools/{code}/years/{year}/forms/{form}/terms/{term}/subjects/{subject}/streams/{stream}/exams/{exam}/pdf"})
    public ResponseEntity<?> getPDF(@PathVariable int code, @PathVariable int year, @PathVariable int form, @PathVariable int term, @PathVariable String subject, @PathVariable int stream, @PathVariable int exam, HttpServletRequest request, HttpServletResponse response, Principal principal) throws IOException {
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        Subject subjectObj = this.subjectService.getSubject(subject);
        School school = this.schoolService.getSchool(code).get();
        Student student = new Student();
        Term termObj = this.termService.getTerm(term, form, year, code);
        Year yearObj = this.yearService.getYearFromSchool(year, code).get();
        Form formObj = this.formService.getFormByForm(form);
        Stream streamObj = this.streamService.getStream(stream);
        ExamName examName = this.examNameService.getExam(exam);
        List<Student> students = new ArrayList<>();
        List<StudentFormYear> studentsFormYear = this.studentFormYearService.findAllStudentDoingSubjectInStream(code, year, form, term, subject, stream);
        
        for(StudentFormYear studentFormYear : studentsFormYear) {
            if(!students.contains(studentFormYear.getStudent())) {
             	students.add(studentFormYear.getStudent());
            } 
        }
        
        List<Stream> streams = this.streamService.getStreamsInSchool(school.getCode());
        List<Year> years = this.yearService.getAllYearsInSchool(school.getCode());
        List<Subject> subjects = this.subjectService.getAllSubjectInSchool(school.getCode());
        ArrayList<Mark> marks = new ArrayList<Mark>();
        for (int i = 0; i < students.size(); ++i) {
            Mark mark = new Mark();
            if (this.markService.getMarksByStudentOnSubjectByExamId(students.get(i).getAdmNo(), year, form, term, subject, exam) != null) {
                marks.add(this.markService.getMarksByStudentOnSubjectByExamId(students.get(i).getAdmNo(), year, form, term, subject, exam));
                continue;
            }
            mark.setStudent(students.get(i));
            mark.setSubject(subjectObj);
            mark.setTerm(termObj);
            mark.setYear(yearObj);
            mark.setForm(formObj);
            mark.setExamName(examName);
            marks.add(mark);
            this.markService.addMarksToSubject(mark);
        }
        WebContext context = new WebContext(request, response, this.servletContext);
        context.setVariable("marks", marks);
        context.setVariable("subjects", subjects);
        context.setVariable("streams", streams);
        context.setVariable("years", years);
        context.setVariable("students", students);
        context.setVariable("subject", (Object)subjectObj);
        context.setVariable("year", (Object)year);
        context.setVariable("form", (Object)form);
        context.setVariable("term", (Object)term);
        context.setVariable("stream", (Object)streamObj);
        context.setVariable("examName", (Object)examName);
        context.setVariable("student", (Object)student);
        context.setVariable("school", (Object)school);
        context.setVariable("activeUser", (Object)activeUser);
        String marksSheetHtml = this.templateEngine.process("markEntryPdf", (IContext)context);
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setBaseUri("http://analytica-env.eba-iigws4mq.us-east-2.elasticbeanstalk.com/");
        HtmlConverter.convertToPdf((String)marksSheetHtml, (OutputStream)target, (ConverterProperties)converterProperties);
        byte[] bytes = target.toByteArray();
        return ResponseEntity.ok().contentType(org.springframework.http.MediaType.APPLICATION_PDF).body((Object)bytes);
    }

    @GetMapping(value={"/schools/{code}/years/{year}/forms/{form}/terms/{term}/meritList/pdf"})
    public ResponseEntity<?> getMeritListPDF(@PathVariable int code, @PathVariable int year, @PathVariable int form, @PathVariable int term, HttpServletRequest request, HttpServletResponse response, Principal principal) throws IOException {
    	 int i;
         GradeCount gradeCount;
         int i2;
         WebContext context = new WebContext(request, response, this.servletContext);
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
         
         List<Subject> subjects = this.subjectService.getAllSubjectInSchool(code);
         List<Mark> allMarks = this.markService.getAllStudentsMarksBySchoolYearFormAndTerm(code, form, term, year);
         ArrayList<Student> studentsWithoutMarks = new ArrayList<Student>();
         ArrayList<Student> studentsWithMarks = new ArrayList<Student>();
         for (int i3 = 0; i3 < students.size(); ++i3) {
             if (!this.markService.getMarkByAdm(students.get(i3).getAdmNo()).booleanValue()) {
                 studentsWithoutMarks.add(students.get(i3));
                 continue;
             }
             studentsWithMarks.add(students.get(i3));
         }
         ArrayList<MeritList> meritLists = new ArrayList<MeritList>();
         ArrayList<GradeCount> gradeCounts = new ArrayList<GradeCount>();
         
        
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

         for (i2 = 0; i2 < studentsWithMarks.size(); ++i2) {
             gradeCount = new GradeCount();
             MeritList meritList = new MeritList();
             int count = 0;
             block118: for (int j = 0; j < subjects.size(); ++j) {
                 meritList.setFirstname(students.get(i2).getFirstname());
                 meritList.setSecondname(students.get(i2).getThirdname());
                 meritList.setAdmNo(students.get(i2).getAdmNo());
                 meritList.setKcpe(students.get(i2).getKcpeMarks());
                 meritList.setStream(students.get(i2).getStream().getStream());
                 meritList.setGender(students.get(i2).getGender());
                 gradeCount.setFirstname(students.get(i2).getFirstname());
                 gradeCount.setSecondname(students.get(i2).getThirdname());
                 gradeCount.setAdmNo(students.get(i2).getAdmNo());
                 List<Mark> marks = new ArrayList<Mark>();
                 int sum = 0;
                 int totalOutOf = 0;
                 switch (subjects.get(j).getInitials()) {
                     case "Maths": {
                         int k;
                         marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                         for (k = 0; k < marks.size(); ++k) {
                             sum += marks.get(k).getMark();
                             totalOutOf += marks.get(k).getExamName().getOutOf();
                         }
                         if (sum > 0) {
                             ++count;
                             ++mathsCount;
                         }
                         if (totalOutOf > 0) {
                             meritList.setMaths(sum * 100 / totalOutOf);
                             gradeCount.setMaths(this.getGrade(meritList.getMaths()));
                             continue block118;
                         }
                         if(sum < 0){
                             meritList.setMaths(-1);
                         }else{
                             meritList.setMaths(sum);
                         }

                         gradeCount.setMaths(this.getGrade(meritList.getMaths()));
                         continue block118;
                     }
                     case "Eng": {
                         int k;
                         marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                         for (k = 0; k < marks.size(); ++k) {
                             sum += marks.get(k).getMark();
                             totalOutOf += marks.get(k).getExamName().getOutOf();
                         }
                         if (sum > 0) {
                             ++count;
                             ++engCount;
                         }
                         if (totalOutOf > 0) {
                             meritList.setEng(sum * 100 / totalOutOf);
                             gradeCount.setEng(this.getGrade(meritList.getEng()));
                             continue block118;
                         }

                         if(sum < 0){
                             meritList.setEng(-1);
                         }else {
                             meritList.setEng(sum);
                         }

                         gradeCount.setEng(this.getGrade(meritList.getEng()));
                         continue block118;
                     }
                     case "Kis": {
                         int k;
                         marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                         for (k = 0; k < marks.size(); ++k) {
                             sum += marks.get(k).getMark();
                             totalOutOf += marks.get(k).getExamName().getOutOf();
                         }
                         if (sum > 0) {
                             ++count;
                             ++kisCount;
                         }
                         if (totalOutOf > 0) {
                             meritList.setKis(sum * 100 / totalOutOf);
                             gradeCount.setKis(this.getGrade(meritList.getKis()));
                             continue block118;
                         }

                         if(sum < 0){
                             meritList.setKis(-1);
                         }else{
                             meritList.setKis(sum);
                         }

                         gradeCount.setKis(this.getGrade(meritList.getKis()));
                         continue block118;
                     }
                     case "Bio": {
                         int k;
                         marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                         for (k = 0; k < marks.size(); ++k) {
                             sum += marks.get(k).getMark();
                             totalOutOf += marks.get(k).getExamName().getOutOf();
                         }
                         if (sum > 0) {
                             ++count;
                             ++bioCount;
                         }
                         if (totalOutOf > 0) {
                             meritList.setBio(sum * 100 / totalOutOf);
                             gradeCount.setBio(this.getGrade(meritList.getBio()));
                             continue block118;
                         }

                         if(sum < 0){
                             meritList.setBio(-1);
                         }else{
                             meritList.setBio(sum);
                         }

                         gradeCount.setBio(this.getGrade(meritList.getBio()));
                         continue block118;
                     }
                     case "Chem": {
                         int k;
                         marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                         for (k = 0; k < marks.size(); ++k) {
                             sum += marks.get(k).getMark();
                             totalOutOf += marks.get(k).getExamName().getOutOf();
                         }
                         if (sum > 0) {
                             ++count;
                             ++chemCount;
                         }
                         if (totalOutOf > 0) {
                             meritList.setChem(sum * 100 / totalOutOf);
                             gradeCount.setChem(this.getGrade(meritList.getChem()));
                             continue block118;
                         }

                         if(sum < 0){
                             meritList.setChem(-1);
                         }else{
                             meritList.setChem(sum);
                         }

                         gradeCount.setChem(this.getGrade(meritList.getChem()));
                         continue block118;
                     }
                     case "Phy": {
                         int k;
                         marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                         for (k = 0; k < marks.size(); ++k) {
                             sum += marks.get(k).getMark();
                             totalOutOf += marks.get(k).getExamName().getOutOf();
                         }
                         if (sum > 0) {
                             ++count;
                             ++phyCount;
                         }
                         if (totalOutOf > 0) {
                             meritList.setPhy(sum * 100 / totalOutOf);
                             gradeCount.setPhy(this.getGrade(meritList.getPhy()));
                             continue block118;
                         }

                         if(sum < 0){
                             meritList.setChem(-1);
                         }else {
                             meritList.setChem(sum);
                         }

                         gradeCount.setPhy(this.getGrade(meritList.getPhy()));
                         continue block118;
                     }
                     case "Hist": {
                         int k;
                         marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                         for (k = 0; k < marks.size(); ++k) {
                             sum += marks.get(k).getMark();
                             totalOutOf += marks.get(k).getExamName().getOutOf();
                         }
                         if (sum > 0) {
                             ++count;
                             ++histCount;
                         }
                         if (totalOutOf > 0) {
                             meritList.setHist(sum * 100 / totalOutOf);
                             gradeCount.setHist(this.getGrade(meritList.getHist()));
                             continue block118;
                         }

                         if(sum < 0){
                             meritList.setHist(-1);
                         }else{
                             meritList.setHist(sum);
                         }

                         gradeCount.setHist(this.getGrade(meritList.getHist()));
                         continue block118;
                     }
                     case "C.R.E": {
                         int k;
                         marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                         for (k = 0; k < marks.size(); ++k) {
                             sum += marks.get(k).getMark();
                             totalOutOf += marks.get(k).getExamName().getOutOf();
                         }
                         if (sum > 0) {
                             ++count;
                             ++creCount;
                         }
                         if (totalOutOf > 0) {
                             meritList.setCre(sum * 100 / totalOutOf);
                             gradeCount.setCre(this.getGrade(meritList.getCre()));
                             continue block118;
                         }

                         if(sum < 0){
                             meritList.setCre(-1);
                         }else{
                             meritList.setCre(sum);
                         }

                         gradeCount.setCre(this.getGrade(meritList.getCre()));
                         continue block118;
                     }
                     case "Geo": {
                         int k;
                         marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                         for (k = 0; k < marks.size(); ++k) {
                             sum += marks.get(k).getMark();
                             totalOutOf += marks.get(k).getExamName().getOutOf();
                         }
                         if (sum > 0) {
                             ++count;
                             ++geoCount;
                         }
                         if (totalOutOf > 0) {
                             meritList.setGeo(sum * 100 / totalOutOf);
                             gradeCount.setGeo(this.getGrade(meritList.getGeo()));
                             continue block118;
                         }

                         if(sum < 0){
                             meritList.setGeo(-1);
                         }else{
                             meritList.setGeo(sum);
                         }

                         gradeCount.setGeo(this.getGrade(meritList.getGeo()));
                         continue block118;
                     }
                     case "I.R.E": {
                         int k;
                         marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                         for (k = 0; k < marks.size(); ++k) {
                             sum += marks.get(k).getMark();
                             totalOutOf += marks.get(k).getExamName().getOutOf();
                         }
                         if (sum > 0) {
                             ++count;
                             ++ireCount;
                         }
                         if (totalOutOf > 0) {
                             meritList.setIre(sum * 100 / totalOutOf);
                             gradeCount.setIre(this.getGrade(meritList.getIre()));
                             continue block118;
                         }

                         if(sum < 0){
                             meritList.setIre(-1);
                         }else{
                             meritList.setIre(sum);
                         }

                         gradeCount.setIre(this.getGrade(meritList.getIre()));
                         continue block118;
                     }
                     case "H.R.E": {
                         int k;
                         marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                         for (k = 0; k < marks.size(); ++k) {
                             sum += marks.get(k).getMark();
                             totalOutOf += marks.get(k).getExamName().getOutOf();
                         }
                         if (sum > 0) {
                             ++count;
                             ++hreCount;
                         }
                         if (totalOutOf > 0) {
                             meritList.setHre(sum * 100 / totalOutOf);
                             gradeCount.setHre(this.getGrade(meritList.getHre()));
                             continue block118;
                         }

                         if(sum < 0){
                             meritList.setHre(-1);
                         }else{
                             meritList.setHre(sum);
                         }

                         gradeCount.setHre(this.getGrade(meritList.getHre()));
                         continue block118;
                     }
                     case "Hsci": {
                         int k;
                         marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                         for (k = 0; k < marks.size(); ++k) {
                             sum += marks.get(k).getMark();
                             totalOutOf += marks.get(k).getExamName().getOutOf();
                         }
                         if (sum > 0) {
                             ++count;
                             ++hsciCount;
                         }
                         if (totalOutOf > 0) {
                             meritList.setHsci(sum * 100 / totalOutOf);
                             gradeCount.setHsci(this.getGrade(meritList.getHsci()));
                             continue block118;
                         }

                         if(sum < 0){
                             meritList.setHsci(-1);
                         }else{
                             meritList.setHsci(sum);
                         }

                         gradeCount.setHsci(this.getGrade(meritList.getHsci()));
                         continue block118;
                     }
                     case "AnD": {
                         int k;
                         marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                         for (k = 0; k < marks.size(); ++k) {
                             sum += marks.get(k).getMark();
                             totalOutOf += marks.get(k).getExamName().getOutOf();
                         }
                         if (sum > 0) {
                             ++count;
                             ++andCount;
                         }
                         if (totalOutOf > 0) {
                             meritList.setAnD(sum * 100 / totalOutOf);
                             gradeCount.setAnd(this.getGrade(meritList.getAnD()));
                             continue block118;
                         }

                         if(sum < 0){
                             meritList.setAnD(-1);
                         }else{
                             meritList.setAnD(sum);
                         }

                         gradeCount.setAnd(this.getGrade(meritList.getAnD()));
                         continue block118;
                     }
                     case "Agric": {
                         int k;
                         marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                         for (k = 0; k < marks.size(); ++k) {
                             sum += marks.get(k).getMark();
                             totalOutOf += marks.get(k).getExamName().getOutOf();
                         }
                         if (sum > 0) {
                             ++count;
                             ++agricCount;
                         }
                         if (totalOutOf > 0) {
                             meritList.setAgric(sum * 100 / totalOutOf);
                             gradeCount.setAgric(this.getGrade(meritList.getAgric()));
                             continue block118;
                         }

                         if(sum < 0){
                             meritList.setAgric(-1);
                         }else{
                             meritList.setAgric(sum);
                         }

                         gradeCount.setAgric(this.getGrade(meritList.getAgric()));
                         continue block118;
                     }
                     case "Comp": {
                         int k;
                         marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                         for (k = 0; k < marks.size(); ++k) {
                             sum += marks.get(k).getMark();
                             totalOutOf += marks.get(k).getExamName().getOutOf();
                         }
                         if (sum > 0) {
                             ++count;
                             ++compCount;
                         }
                         if (totalOutOf > 0) {
                             meritList.setComp(sum * 100 / totalOutOf);
                             gradeCount.setComp(this.getGrade(meritList.getComp()));
                             continue block118;
                         }

                         if(sum < 0){
                             meritList.setComp(-1);
                         }else{
                             meritList.setComp(sum);
                         }

                         gradeCount.setComp(this.getGrade(meritList.getComp()));
                         continue block118;
                     }
                     case "Avi": {
                         int k;
                         marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                         for (k = 0; k < marks.size(); ++k) {
                             sum += marks.get(k).getMark();
                             totalOutOf += marks.get(k).getExamName().getOutOf();
                         }
                         if (sum > 0) {
                             ++count;
                             ++aviCount;
                         }
                         if (totalOutOf > 0) {
                             meritList.setAvi(sum * 100 / totalOutOf);
                             gradeCount.setAvi(this.getGrade(meritList.getAvi()));
                             continue block118;
                         }

                         if(sum < 0){
                             meritList.setAvi(-1);
                         }else{
                             meritList.setAvi(sum);
                         }

                         gradeCount.setAvi(this.getGrade(meritList.getAvi()));
                         continue block118;
                     }
                     case "Elec": {
                         int k;
                         marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                         for (k = 0; k < marks.size(); ++k) {
                             sum += marks.get(k).getMark();
                             totalOutOf += marks.get(k).getExamName().getOutOf();
                         }
                         if (sum > 0) {
                             ++count;
                             ++elecCount;
                         }
                         if (totalOutOf > 0) {
                             meritList.setElec(sum * 100 / totalOutOf);
                             gradeCount.setElec(this.getGrade(meritList.getElec()));
                             continue block118;
                         }

                         if(sum < 0){
                             meritList.setElec(-1);
                         }else{
                             meritList.setElec(sum);
                         }

                         gradeCount.setElec(this.getGrade(meritList.getElec()));
                         continue block118;
                     }
                     case "Pwr": {
                         int k;
                         marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                         for (k = 0; k < marks.size(); ++k) {
                             sum += marks.get(k).getMark();
                             totalOutOf += marks.get(k).getExamName().getOutOf();
                         }
                         if (sum > 0) {
                             ++count;
                             ++pwrCount;
                         }
                         if (totalOutOf > 0) {
                             meritList.setPwr(sum * 100 / totalOutOf);
                             gradeCount.setPwr(this.getGrade(meritList.getPwr()));
                             continue block118;
                         }

                         if(sum < 0){
                             meritList.setPwr(-1);
                         }else{
                             meritList.setPwr(sum);
                         }

                         gradeCount.setPwr(this.getGrade(meritList.getPwr()));
                         continue block118;
                     }
                     case "Wood": {
                         int k;
                         marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                         for (k = 0; k < marks.size(); ++k) {
                             sum += marks.get(k).getMark();
                             totalOutOf += marks.get(k).getExamName().getOutOf();
                         }
                         if (sum > 0) {
                             ++count;
                             ++woodCount;
                         }
                         if (totalOutOf > 0) {
                             meritList.setWood(sum * 100 / totalOutOf);
                             gradeCount.setWood(this.getGrade(meritList.getWood()));
                             continue block118;
                         }

                         if(sum < 0){
                             meritList.setWood(-1);
                         }else{
                             meritList.setWood(sum);
                         }

                         gradeCount.setWood(this.getGrade(meritList.getWood()));
                         continue block118;
                     }
                     case "Metal": {
                         int k;
                         marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                         for (k = 0; k < marks.size(); ++k) {
                             sum += marks.get(k).getMark();
                             totalOutOf += marks.get(k).getExamName().getOutOf();
                         }
                         if (sum > 0) {
                             ++count;
                             ++metalCount;
                         }
                         if (totalOutOf > 0) {
                             meritList.setMetal(sum * 100 / totalOutOf);
                             gradeCount.setMetal(this.getGrade(meritList.getMetal()));
                             continue block118;
                         }

                         if(sum < 0){
                             meritList.setMetal(-1);
                         }else{
                             meritList.setMetal(sum);
                         }

                         gradeCount.setMetal(this.getGrade(meritList.getMetal()));
                         continue block118;
                     }
                     case "Bc": {
                         int k;
                         marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                         for (k = 0; k < marks.size(); ++k) {
                             sum += marks.get(k).getMark();
                             totalOutOf += marks.get(k).getExamName().getOutOf();
                         }
                         if (sum > 0) {
                             ++count;
                             ++bcCount;
                         }
                         if (totalOutOf > 0) {
                             meritList.setBc(sum * 100 / totalOutOf);
                             gradeCount.setBc(this.getGrade(meritList.getBc()));
                             continue block118;
                         }

                         if(sum < 0){
                             meritList.setBc(-1);
                         }else {
                             meritList.setBc(sum);
                         }

                         gradeCount.setBc(this.getGrade(meritList.getBc()));
                         continue block118;
                     }
                     case "Fren": {
                         int k;
                         marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                         for (k = 0; k < marks.size(); ++k) {
                             sum += marks.get(k).getMark();
                             totalOutOf += marks.get(k).getExamName().getOutOf();
                         }
                         if (sum > 0) {
                             ++count;
                             ++frenCount;
                         }
                         if (totalOutOf > 0) {
                             meritList.setFren(sum * 100 / totalOutOf);
                             gradeCount.setFren(this.getGrade(meritList.getFren()));
                             continue block118;
                         }

                         if(sum < 0){
                             meritList.setFren(-1);
                         }else{
                             meritList.setFren(sum);
                         }

                         gradeCount.setFren(this.getGrade(meritList.getFren()));
                         continue block118;
                     }
                     case "Germ": {
                         int k;
                         marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                         for (k = 0; k < marks.size(); ++k) {
                             sum += marks.get(k).getMark();
                             totalOutOf += marks.get(k).getExamName().getOutOf();
                         }
                         if (sum > 0) {
                             ++count;
                             ++germCount;
                         }
                         if (totalOutOf > 0) {
                             meritList.setGerm(sum * 100 / totalOutOf);
                             gradeCount.setGerm(this.getGrade(meritList.getGerm()));
                             continue block118;
                         }

                         if(sum < 0){
                             meritList.setGerm(-1);
                         }else{
                             meritList.setGerm(sum);
                         }

                         gradeCount.setGerm(this.getGrade(meritList.getGerm()));
                         continue block118;
                     }
                     case "Arab": {
                         int k;
                         marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                         for (k = 0; k < marks.size(); ++k) {
                             sum += marks.get(k).getMark();
                             totalOutOf += marks.get(k).getExamName().getOutOf();
                         }
                         if (sum > 0) {
                             ++count;
                             ++arabCount;
                         }
                         if (totalOutOf > 0) {
                             meritList.setArab(sum * 100 / totalOutOf);
                             gradeCount.setArab(this.getGrade(meritList.getArab()));
                             continue block118;
                         }

                         if(sum < 0){
                             meritList.setArab(-1);
                         }else{
                             meritList.setArab(sum);
                         }

                         gradeCount.setArab(this.getGrade(meritList.getArab()));
                         continue block118;
                     }
                     case "Msc": {
                         int k;
                         marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                         for (k = 0; k < marks.size(); ++k) {
                             sum += marks.get(k).getMark();
                             totalOutOf += marks.get(k).getExamName().getOutOf();
                         }
                         if (sum > 0) {
                             ++count;
                             ++mscCount;
                         }
                         if (totalOutOf > 0) {
                             meritList.setMsc(sum * 100 / totalOutOf);
                             gradeCount.setMsc(this.getGrade(meritList.getMsc()));
                             continue block118;
                         }

                         if(sum < 0){
                             meritList.setMsc(-1);
                         }else {
                             meritList.setMsc(sum);
                         }

                         gradeCount.setMsc(this.getGrade(meritList.getMsc()));
                         continue block118;
                     }
                     case "Bs": {
                         int k;
                         marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                         for (k = 0; k < marks.size(); ++k) {
                             sum += marks.get(k).getMark();
                             totalOutOf += marks.get(k).getExamName().getOutOf();
                         }
                         if (sum > 0) {
                             ++count;
                             ++bsCount;
                         }
                         if (totalOutOf > 0) {
                             meritList.setBs(sum * 100 / totalOutOf);
                             gradeCount.setBs(this.getGrade(meritList.getBs()));
                             continue block118;
                         }

                         if(sum < 0){
                             meritList.setBs(-1);
                         }else {
                             meritList.setBs(sum);
                         }

                         gradeCount.setBs(this.getGrade(meritList.getBs()));
                         continue block118;
                     }
                     case "Dnd": {
                         int k;
                         marks = this.markService.getMarkByStudentOnAsubject(students.get(i2).getAdmNo(), year, form, term, subjects.get(j).getInitials());
                         for (k = 0; k < marks.size(); ++k) {
                             sum += marks.get(k).getMark();
                             totalOutOf += marks.get(k).getExamName().getOutOf();
                         }
                         if (sum > 0) {
                             ++count;
                             ++dndCount;
                         }
                         if (totalOutOf > 0) {
                             meritList.setDnd(sum * 100 / totalOutOf);
                             gradeCount.setDnd(this.getGrade(meritList.getDnd()));
                             continue block118;
                         }

                         if(sum < 0){
                             meritList.setDnd(-1);
                         }else{
                             meritList.setDnd(sum);
                         }

                         gradeCount.setDnd(this.getGrade(meritList.getDnd()));
                     }
                 }
             }

             int maths01 = 0;
             byte mathsPoints = 0;
             if(meritList.getMaths() >= 0){
                 maths01 = meritList.getMaths();
                 mathsPoints = this.getPoints(meritList.getMaths());
             }

             int eng01 = 0;
             byte engPoints = 0;
             if(meritList.getEng() >= 0){
                 eng01 = meritList.getEng();
                 engPoints = this.getPoints(meritList.getEng());
             }

             int kis01 = 0;
             byte kisPoints = 0;
             if(meritList.getKis() >= 0){
                 kis01 = meritList.getKis();
                 kisPoints = this.getPoints(meritList.getKis());
             }

             int bio01 = 0;
             byte bioPoints = 0;
             if(meritList.getBio() >= 0){
                 bio01 = meritList.getBio();
                 bioPoints = this.getPoints(meritList.getBio());
             }

             int chem01 = 0;
             byte chemPoints = 0;
             if(meritList.getChem() >= 0){
                 chem01 = meritList.getChem();
                 chemPoints = this.getPoints(meritList.getChem());
             }

             int phy01 = 0;
             byte phyPoints = 0;
             if(meritList.getPhy() >= 0 ){
                 phy01 = meritList.getPhy();
                 phyPoints = this.getPoints(meritList.getPhy());
             }

             int hist01 = 0;
             byte histPoints = 0;
             if(meritList.getHist() >= 0){
                 hist01 = meritList.getHist();
                 histPoints = this.getPoints(meritList.getHist());
             }

             int cre01 = 0;
             byte crePoints = 0;
             if(meritList.getCre() >= 0) {
                 cre01 = meritList.getCre();
                 crePoints = this.getPoints(meritList.getCre());
             }

             int geo01 = 0;
             byte geoPoints = 0;
             if(meritList.getGeo() >= 0 ){
                 geo01 = meritList.getGeo();
                 geoPoints = this.getPoints(meritList.getGeo());
             }

             int ire01 = 0;
             byte irePoints = 0;
             if(meritList.getIre() >=0){
                 ire01 = meritList.getIre();
                 irePoints = this.getPoints(meritList.getIre());
             }

             int hre01 = 0;
             byte hrePoints = 0;
             if(meritList.getHre() >= 0){
                 hre01 = meritList.getHre();
                 hrePoints = this.getPoints(meritList.getHre());
             }

             int hsci01 = 0;
             byte hsciPoints = 0;
             if(meritList.getHsci() >= 0){
                 hsci01 = meritList.getHsci();
                 hsciPoints = this.getPoints(meritList.getHsci());
             }

             int and01 = 0;
             byte andPoints = 0;
             if(meritList.getAnD() >= 0){
                 and01 = meritList.getAnD();
                 andPoints = this.getPoints(meritList.getAnD());
             }

             int agric01 = 0;
             byte agricPoints = 0;
             if(meritList.getAgric() >= 0){
                 agric01 = meritList.getAgric();
                 agricPoints = this.getPoints(meritList.getAgric());
             }

             int comp01 = 0;
             byte compPoints = 0;
             if(meritList.getComp() >= 0 ){
                 comp01 = meritList.getComp();
                 compPoints = this.getPoints(meritList.getComp());
             }

             int avi01 = 0;
             byte aviPoints = 0;
             if(meritList.getAvi() >= 0){
                 avi01 = meritList.getAvi();
                 aviPoints = this.getPoints(meritList.getAvi());
             }

             int elec01 = 0;
             byte elecPoints = 0;
             if(meritList.getElec() >= 0){
                 elec01 = meritList.getElec();
                 elecPoints = this.getPoints(meritList.getElec());
             }

             int pwr01 = 0;
             byte pwrPoints = 0;
             if(meritList.getPwr() >= 0 ){
                 pwr01 = meritList.getPwr();
                 pwrPoints = this.getPoints(meritList.getPwr());
             }

             int wood01 = 0;
             byte woodPoints = 0;
             if(meritList.getWood() >= 0){
                 wood01 = meritList.getWood();
                 woodPoints = this.getPoints(meritList.getWood());
             }

             int metal01 = 0;
             byte metalPoints = 0;
             if(meritList.getMetal() >= 0){
                 metal01 = meritList.getMetal();
                 metalPoints = this.getPoints(meritList.getMetal());
             }

             int bc01 = 0;
             byte bcPoints = 0;
             if(meritList.getBc() >= 0){
                 bc01 = meritList.getBc();
                 bcPoints = this.getPoints(meritList.getBc());
             }

             int fren01 = 0;
             byte frenPoints = 0;
             if(meritList.getFren() >= 0 ){
                 fren01 = meritList.getFren();
                 frenPoints = this.getPoints(meritList.getFren());
             }

             int germ01 = 0;
             byte germPoints = 0;
             if(meritList.getGerm() >= 0){
                 germ01 = meritList.getGerm();
                 germPoints = this.getPoints(meritList.getGerm());
             }

             int arab01 = 0;
             byte arabPoints = 0;
             if(meritList.getArab() >= 0 ){
                 arab01 = meritList.getArab();
                 arabPoints = this.getPoints(meritList.getArab());
             }

             int msc01 = 0;
             byte mscPoints = 0;
             if(meritList.getMsc() >= 0){
                 msc01 = meritList.getMsc();
                 mscPoints = this.getPoints(meritList.getMsc());
             }

             int bs01 = 0;
             byte bsPoints = 0;
             if(meritList.getBs() >= 0){
                 bs01 = meritList.getBs();
                 bsPoints = this.getPoints(meritList.getBs());
             }

             int dnd01 = 0;
             byte dndPoints= 0;
             if(meritList.getDnd() >= 0){
                 dnd01 = meritList.getDnd();
                 dndPoints = this.getPoints(meritList.getDnd());
             }

             DecimalFormat df = new DecimalFormat("#.####");

             meritList.setTotal(maths01 + eng01 + kis01 + bio01 + chem01 + phy01 + hist01 + cre01 + geo01 + ire01 + hre01 + hsci01 + and01 + agric01 + comp01 + avi01 + elec01 + pwr01 + wood01 + metal01 + bc01 + fren01 + germ01 + arab01 + msc01 + bs01 + dnd01);
             int totalPoints = mathsPoints + engPoints + kisPoints + bioPoints + chemPoints + phyPoints + histPoints + crePoints + geoPoints + irePoints + hrePoints + hsciPoints + andPoints + agricPoints + compPoints + aviPoints + elecPoints + pwrPoints + woodPoints + metalPoints + bcPoints + frenPoints + germPoints + arabPoints + mscPoints + bsPoints + dndPoints;
             float average = (float) totalPoints / studentService.getStudentInSchool(meritList.getAdmNo(), school.getCode()).getSubjects().size();
             meritList.setAverage(Float.valueOf(df.format(average)));
             meritList.setPoints(this.getPoints(meritList.getTotal() / studentService.getStudentInSchool(meritList.getAdmNo(), school.getCode()).getSubjects().size()));
             meritList.setGrade(this.getGrade(meritList.getTotal() / studentService.getStudentInSchool(meritList.getAdmNo(), school.getCode()).getSubjects().size()));
             meritList.setDeviation(meritList.getAverage() - students.get(i2).getKcpeMarks() / 5);

             meritLists.add(meritList);
             gradeCounts.add(gradeCount);
         }

         List<Stream> streams = this.streamService.getStreamsInSchool(code);
        
        
         	for(int j = 0; j < subjects.size(); j++){
             int totalPoints = 0,femalePoints = 0, malePoints = 0, count = 0,fcount = 0, mcount = 0;
             List<StreamPoints> streamsMeanPoints = new ArrayList<>();
             
             for(int k = 0; k<meritLists.size(); k++){
             	StreamPoints streamPoints = new StreamPoints();
                 switch (subjects.get(j).getInitials()) {
                     case "Maths": 
                         totalPoints += this.getPoints(meritLists.get(k).getMaths());
                         count++;
                         if(meritLists.get(k).getGender().equals("Female")){
                             femalePoints += this.getPoints(meritLists.get(k).getMaths());
                             fcount++;
                         }else if(meritLists.get(k).getGender().equals("Male")){
                             malePoints += this.getPoints(meritLists.get(k).getMaths());
                             mcount++;
                         }
                         
                         for(int l = 0; l<streams.size();l++){
                         	if(meritLists.get(k).getStream().equals(streams.get(l).getStream())) {
                         		
                                 streamPoints.setStream(streams.get(l).getStream());
                                 streamPoints.setPoints(this.getPoints(meritLists.get(k).getMaths()));
                                 streamPoints.setCount(1);

                                 streamsMeanPoints.add(streamPoints);
                         	}
                         }
                         
                         break;
                     
                     case "Eng": 
                         totalPoints += this.getPoints(meritLists.get(k).getEng());
                         count++;
                         if(meritLists.get(k).getGender().equals("Female")){
                             femalePoints += this.getPoints(meritLists.get(k).getEng());
                             fcount++;
                         }else if(meritLists.get(k).getGender().equals("Male")){
                             malePoints += this.getPoints(meritLists.get(k).getEng());
                             mcount++;
                         }
                         for(int l = 0; l<streams.size();l++){
                         	if(meritLists.get(k).getStream().equals(streams.get(l).getStream())) {
                         		
                                 streamPoints.setStream(streams.get(l).getStream());
                                 streamPoints.setPoints(this.getPoints(meritLists.get(k).getEng()));
                                 streamPoints.setCount(1);

                                 streamsMeanPoints.add(streamPoints);
                         	}
                         }

                         break;
                     
                     case "Kis": 
                         totalPoints += this.getPoints(meritLists.get(k).getKis());
                         count++;
                         if(meritLists.get(k).getGender().equals("Female")){
                             femalePoints += this.getPoints(meritLists.get(k).getKis());
                             fcount++;
                         }else if(meritLists.get(k).getGender().equals("Male")){
                             malePoints += this.getPoints(meritLists.get(k).getKis());
                             mcount++;
                         }
                         for(int l = 0; l<streams.size();l++){
                         	if(meritLists.get(k).getStream().equals(streams.get(l).getStream())) {
                         		
                                 streamPoints.setStream(streams.get(l).getStream());
                                 streamPoints.setPoints(this.getPoints(meritLists.get(k).getKis()));
                                 streamPoints.setCount(1);

                                 streamsMeanPoints.add(streamPoints);
                         	}
                         }
                        
                         break;
                    
                     case "Bio": 
                         totalPoints += this.getPoints(meritLists.get(k).getBio());
                         count++;
                         if(meritLists.get(k).getGender().equals("Female")){
                             femalePoints += this.getPoints(meritLists.get(k).getBio());
                             fcount++;
                         }else if(meritLists.get(k).getGender().equals("Male")){
                             malePoints += this.getPoints(meritLists.get(k).getBio());
                             mcount++;
                         }
                         for(int l = 0; l<streams.size();l++){
                         	if(meritLists.get(k).getStream().equals(streams.get(l).getStream())) {
                         		
                                 streamPoints.setStream(streams.get(l).getStream());
                                 streamPoints.setPoints(this.getPoints(meritLists.get(k).getBio()));
                                 streamPoints.setCount(1);

                                 streamsMeanPoints.add(streamPoints);
                         	}
                         }
                         
                         break;
                     
                     case "Chem": 
                         totalPoints += this.getPoints(meritLists.get(k).getChem());
                         count++;
                         if(meritLists.get(k).getGender().equals("Female")){
                             femalePoints += this.getPoints(meritLists.get(k).getChem());
                             fcount++;
                         }else if(meritLists.get(k).getGender().equals("Male")){
                             malePoints += this.getPoints(meritLists.get(k).getChem());
                             mcount++;
                         }
                         for(int l = 0; l<streams.size();l++){
                         	if(meritLists.get(k).getStream().equals(streams.get(l).getStream())) {
                         		
                                 streamPoints.setStream(streams.get(l).getStream());
                                 streamPoints.setPoints(this.getPoints(meritLists.get(k).getChem()));
                                 streamPoints.setCount(1);

                                 streamsMeanPoints.add(streamPoints);
                         	}
                         }

                         
                         break;
                     
                     case "Phy": 
                         totalPoints += this.getPoints(meritLists.get(k).getPhy());
                         count++;
                         if(meritLists.get(k).getGender().equals("Female")){
                             femalePoints += this.getPoints(meritLists.get(k).getPhy());
                             fcount++;
                         }else if(meritLists.get(k).getGender().equals("Male")){
                             malePoints += this.getPoints(meritLists.get(k).getPhy());
                             mcount++;
                         }
                         for(int l = 0; l<streams.size();l++){
                         	if(meritLists.get(k).getStream().equals(streams.get(l).getStream())) {
                         		
                                 streamPoints.setStream(streams.get(l).getStream());
                                 streamPoints.setPoints(this.getPoints(meritLists.get(k).getPhy()));
                                 streamPoints.setCount(1);

                                 streamsMeanPoints.add(streamPoints);
                         	}
                         }

                         
                         break;
                     
                     case "Hist": 
                         totalPoints += this.getPoints(meritLists.get(k).getHist());
                         count++;
                         if(meritLists.get(k).getGender().equals("Female")){
                             femalePoints += this.getPoints(meritLists.get(k).getHist());
                             fcount++;
                         }else if(meritLists.get(k).getGender().equals("Male")){
                             malePoints += this.getPoints(meritLists.get(k).getHist());
                             mcount++;
                         }
                         for(int l = 0; l<streams.size();l++){
                         	if(meritLists.get(k).getStream().equals(streams.get(l).getStream())) {
                         		
                                 streamPoints.setStream(streams.get(l).getStream());
                                 streamPoints.setPoints(this.getPoints(meritLists.get(k).getHist()));
                                 streamPoints.setCount(1);

                                 streamsMeanPoints.add(streamPoints);
                         	}
                         }

                         break;
                     
                     case "C.R.E": 
                         totalPoints += this.getPoints(meritLists.get(k).getCre());
                         count++;
                         if(meritLists.get(k).getGender().equals("Female")){
                             femalePoints += this.getPoints(meritLists.get(k).getCre());
                             fcount++;
                         }else if(meritLists.get(k).getGender().equals("Male")){
                             malePoints += this.getPoints(meritLists.get(k).getCre());
                             mcount++;
                         }
                         for(int l = 0; l<streams.size();l++){
                         	if(meritLists.get(k).getStream().equals(streams.get(l).getStream())) {
                         		
                                 streamPoints.setStream(streams.get(l).getStream());
                                 streamPoints.setPoints(this.getPoints(meritLists.get(k).getCre()));
                                 streamPoints.setCount(1);

                                 streamsMeanPoints.add(streamPoints);
                         	}
                         }

                         
                         break;
                     
                     case "Geo": 
                         totalPoints += this.getPoints(meritLists.get(k).getGeo());
                         count++;
                         if(meritLists.get(k).getGender() == "Female"){
                             femalePoints += this.getPoints(meritLists.get(k).getGeo());
                             fcount++;
                         }else if(meritLists.get(k).getGender() == "Male"){
                             malePoints += this.getPoints(meritLists.get(k).getGeo());
                             mcount++;
                         }
                         for(int l = 0; l<streams.size();l++){
                         	if(meritLists.get(k).getStream().equals(streams.get(l).getStream())) {
                         		
                                 streamPoints.setStream(streams.get(l).getStream());
                                 streamPoints.setPoints(this.getPoints(meritLists.get(k).getGeo()));
                                 streamPoints.setCount(1);

                                 streamsMeanPoints.add(streamPoints);
                         	}
                         }

                         
                         break;
                     
                     case "I.R.E": 
                         totalPoints += this.getPoints(meritLists.get(k).getIre());
                         count++;
                         if(meritLists.get(k).getGender() == "Female"){
                             femalePoints += this.getPoints(meritLists.get(k).getIre());
                             fcount++;
                         }else if(meritLists.get(k).getGender() == "Male"){
                             malePoints += this.getPoints(meritLists.get(k).getIre());
                             mcount++;
                         }
                         for(int l = 0; l<streams.size();l++){
                         	if(meritLists.get(k).getStream().equals(streams.get(l).getStream())) {
                         		
                                 streamPoints.setStream(streams.get(l).getStream());
                                 streamPoints.setPoints(this.getPoints(meritLists.get(k).getIre()));
                                 streamPoints.setCount(1);

                                 streamsMeanPoints.add(streamPoints);
                         	}
                         }

                         
                         break;
                     
                     case "H.R.E": 
                         totalPoints += this.getPoints(meritLists.get(k).getHre());
                         count++;
                         if(meritLists.get(k).getGender() == "Female"){
                             femalePoints += this.getPoints(meritLists.get(k).getHre());
                             fcount++;
                         }else if(meritLists.get(k).getGender() == "Male"){
                             malePoints += this.getPoints(meritLists.get(k).getHre());
                             mcount++;
                         }
                         for(int l = 0; l<streams.size();l++){
                         	if(meritLists.get(k).getStream().equals(streams.get(l).getStream())) {
                         		
                                 streamPoints.setStream(streams.get(l).getStream());
                                 streamPoints.setPoints(this.getPoints(meritLists.get(k).getHre()));
                                 streamPoints.setCount(1);

                                 streamsMeanPoints.add(streamPoints);
                         	}
                         }

                        
                         break;
                     
                     case "Hsci": 
                         totalPoints += this.getPoints(meritLists.get(k).getHsci());
                         count++;
                         if(meritLists.get(k).getGender() == "Female"){
                             femalePoints += this.getPoints(meritLists.get(k).getHsci());
                             fcount++;
                         }else if(meritLists.get(k).getGender() == "Male"){
                             malePoints += this.getPoints(meritLists.get(k).getHsci());
                             mcount++;
                         }
                         for(int l = 0; l<streams.size();l++){
                         	if(meritLists.get(k).getStream().equals(streams.get(l).getStream())) {
                         		
                                 streamPoints.setStream(streams.get(l).getStream());
                                 streamPoints.setPoints(this.getPoints(meritLists.get(k).getHsci()));
                                 streamPoints.setCount(1);

                                 streamsMeanPoints.add(streamPoints);
                         	}
                         }

                         
                         break;
                     
                     case "AnD": 
                         totalPoints += this.getPoints(meritLists.get(k).getAnD());
                         count++;
                         if(meritLists.get(k).getGender() == "Female"){
                             femalePoints += this.getPoints(meritLists.get(k).getAnD());
                             fcount++;
                         }else if(meritLists.get(k).getGender() == "Male"){
                             malePoints += this.getPoints(meritLists.get(k).getAnD());
                             mcount++;
                         }
                         for(int l = 0; l<streams.size();l++){
                         	if(meritLists.get(k).getStream().equals(streams.get(l).getStream())) {
                         		
                                 streamPoints.setStream(streams.get(l).getStream());
                                 streamPoints.setPoints(this.getPoints(meritLists.get(k).getAnD()));
                                 streamPoints.setCount(1);

                                 streamsMeanPoints.add(streamPoints);
                         	}
                         }

                         
                         break;
                     
                     case "Agric": 
                         totalPoints += this.getPoints(meritLists.get(k).getAgric());
                         count++;
                         if(meritLists.get(k).getGender() == "Female"){
                             femalePoints += this.getPoints(meritLists.get(k).getAgric());
                             fcount++;
                         }else if(meritLists.get(k).getGender() == "Male"){
                             malePoints += this.getPoints(meritLists.get(k).getAgric());
                             mcount++;
                         }
                         for(int l = 0; l<streams.size();l++){
                         	if(meritLists.get(k).getStream().equals(streams.get(l).getStream())) {
                         		
                                 streamPoints.setStream(streams.get(l).getStream());
                                 streamPoints.setPoints(this.getPoints(meritLists.get(k).getAgric()));
                                 streamPoints.setCount(1);

                                 streamsMeanPoints.add(streamPoints);
                         	}
                         }

                         
                         break;
                     
                     case "Comp": 
                         totalPoints += this.getPoints(meritLists.get(k).getComp());
                         count++;
                         if(meritLists.get(k).getGender() == "Female"){
                             femalePoints += this.getPoints(meritLists.get(k).getComp());
                             fcount++;
                         }else if(meritLists.get(k).getGender() == "Male"){
                             malePoints += this.getPoints(meritLists.get(k).getComp());
                             mcount++;
                         }
                         for(int l = 0; l<streams.size();l++){
                         	if(meritLists.get(k).getStream().equals(streams.get(l).getStream())) {
                         		
                                 streamPoints.setStream(streams.get(l).getStream());
                                 streamPoints.setPoints(this.getPoints(meritLists.get(k).getComp()));
                                 streamPoints.setCount(1);

                                 streamsMeanPoints.add(streamPoints);
                         	}
                         }

                        
                         break;
                     
                     case "Avi": 
                         totalPoints += this.getPoints(meritLists.get(k).getAvi());
                         count++;
                         if(meritLists.get(k).getGender().equals("Female")){
                             femalePoints += this.getPoints(meritLists.get(k).getAvi());
                             fcount++;
                         }else if(meritLists.get(k).getGender().equals("Male")){
                             malePoints += this.getPoints(meritLists.get(k).getAvi());
                             mcount++;
                         }
                         for(int l = 0; l<streams.size();l++){
                         	if(meritLists.get(k).getStream().equals(streams.get(l).getStream())) {
                         		
                                 streamPoints.setStream(streams.get(l).getStream());
                                 streamPoints.setPoints(this.getPoints(meritLists.get(k).getAvi()));
                                 streamPoints.setCount(1);

                                 streamsMeanPoints.add(streamPoints);
                         	}
                         }

                         
                         break;
                     
                     case "Elec": 
                         totalPoints += this.getPoints(meritLists.get(k).getElec());
                         count++;
                         if(meritLists.get(k).getGender().equals("Female")){
                             femalePoints += this.getPoints(meritLists.get(k).getElec());
                             fcount++;
                         }else if(meritLists.get(k).getGender().equals("Male")){
                             malePoints += this.getPoints(meritLists.get(k).getElec());
                             mcount++;
                         }
                         for(int l = 0; l<streams.size();l++){
                         	if(meritLists.get(k).getStream().equals(streams.get(l).getStream())) {
                         		
                                 streamPoints.setStream(streams.get(l).getStream());
                                 streamPoints.setPoints(this.getPoints(meritLists.get(k).getElec()));
                                 streamPoints.setCount(1);

                                 streamsMeanPoints.add(streamPoints);
                         	}
                         }

                         
                         break;
                     
                     case "Pwr": 
                         totalPoints += this.getPoints(meritLists.get(k).getPwr());
                         count++;
                         if(meritLists.get(k).getGender().equals("Female")){
                             femalePoints += this.getPoints(meritLists.get(k).getPwr());
                             fcount++;
                         }else if(meritLists.get(k).getGender().equals("Male")){
                             malePoints += this.getPoints(meritLists.get(k).getPwr());
                             mcount++;
                         }
                         for(int l = 0; l<streams.size();l++){
                         	if(meritLists.get(k).getStream().equals(streams.get(l).getStream())) {
                         		
                                 streamPoints.setStream(streams.get(l).getStream());
                                 streamPoints.setPoints(this.getPoints(meritLists.get(k).getPwr()));
                                 streamPoints.setCount(1);

                                 streamsMeanPoints.add(streamPoints);
                         	}
                         }

                        
                         break;
                     
                     case "Wood": 
                         totalPoints += this.getPoints(meritLists.get(k).getWood());
                         count++;
                         if(meritLists.get(k).getGender().equals("Female")){
                             femalePoints += this.getPoints(meritLists.get(k).getWood());
                             fcount++;
                         }else if(meritLists.get(k).getGender().equals("Male")){
                             malePoints += this.getPoints(meritLists.get(k).getWood());
                             mcount++;
                         }
                         for(int l = 0; l<streams.size();l++){
                         	if(meritLists.get(k).getStream().equals(streams.get(l).getStream())) {
                         		
                                 streamPoints.setStream(streams.get(l).getStream());
                                 streamPoints.setPoints(this.getPoints(meritLists.get(k).getWood()));
                                 streamPoints.setCount(1);

                                 streamsMeanPoints.add(streamPoints);
                         	}
                         }

                        
                         break;
                     
                     case "Metal": 
                         totalPoints += this.getPoints(meritLists.get(k).getMetal());
                         count++;
                         if(meritLists.get(k).getGender().equals("Female")){
                             femalePoints += this.getPoints(meritLists.get(k).getMetal());
                             fcount++;
                         }else if(meritLists.get(k).getGender().equals("Male")){
                             malePoints += this.getPoints(meritLists.get(k).getMetal());
                             mcount++;
                         }
                         for(int l = 0; l<streams.size();l++){
                         	if(meritLists.get(k).getStream().equals(streams.get(l).getStream())) {
                         		
                                 streamPoints.setStream(streams.get(l).getStream());
                                 streamPoints.setPoints(this.getPoints(meritLists.get(k).getMetal()));
                                 streamPoints.setCount(1);

                                 streamsMeanPoints.add(streamPoints);
                         	}
                         }

                        
                         break;
                     
                     case "Bc": 
                         totalPoints += this.getPoints(meritLists.get(k).getBc());
                         count++;
                         if(meritLists.get(k).getGender().equals("Female")){
                             femalePoints += this.getPoints(meritLists.get(k).getBc());
                             fcount++;
                         }else if(meritLists.get(k).getGender().equals("Male")){
                             malePoints += this.getPoints(meritLists.get(k).getBc());
                             mcount++;
                         }
                         for(int l = 0; l<streams.size();l++){
                         	if(meritLists.get(k).getStream().equals(streams.get(l).getStream())) {
                         		
                                 streamPoints.setStream(streams.get(l).getStream());
                                 streamPoints.setPoints(this.getPoints(meritLists.get(k).getBc()));
                                 streamPoints.setCount(1);

                                 streamsMeanPoints.add(streamPoints);
                         	}
                         }

                         
                         break;
                     
                     case "Fren": 
                         totalPoints += this.getPoints(meritLists.get(k).getFren());
                         count++;
                         if(meritLists.get(k).getGender().equals("Female")){
                             femalePoints += this.getPoints(meritLists.get(k).getFren());
                             fcount++;
                         }else if(meritLists.get(k).getGender().equals("Male")){
                             malePoints += this.getPoints(meritLists.get(k).getFren());
                             mcount++;
                         }
                         for(int l = 0; l<streams.size();l++){
                         	if(meritLists.get(k).getStream().equals(streams.get(l).getStream())) {
                         		
                                 streamPoints.setStream(streams.get(l).getStream());
                                 streamPoints.setPoints(this.getPoints(meritLists.get(k).getFren()));
                                 streamPoints.setCount(1);

                                 streamsMeanPoints.add(streamPoints);
                         	}
                         }

                         
                         break;
                     
                     case "Germ": 
                         totalPoints += this.getPoints(meritLists.get(k).getGerm());
                         count++;
                         if(meritLists.get(k).getGender().equals("Female")){
                             femalePoints += this.getPoints(meritLists.get(k).getGerm());
                             fcount++;
                         }else if(meritLists.get(k).getGender().equals("Male")){
                             malePoints += this.getPoints(meritLists.get(k).getGerm());
                             mcount++;
                         }
                         for(int l = 0; l<streams.size();l++){
                         	if(meritLists.get(k).getStream().equals(streams.get(l).getStream())) {
                         		
                                 streamPoints.setStream(streams.get(l).getStream());
                                 streamPoints.setPoints(this.getPoints(meritLists.get(k).getGerm()));
                                 streamPoints.setCount(1);

                                 streamsMeanPoints.add(streamPoints);
                         	}
                         }

                         
                         break;
                     
                     case "Arab": 
                         totalPoints += this.getPoints(meritLists.get(k).getArab());
                         count++;
                         if(meritLists.get(k).getGender().equals("Female")){
                             femalePoints += this.getPoints(meritLists.get(k).getArab());
                             fcount++;
                         }else if(meritLists.get(k).getGender().equals("Male")){
                             malePoints += this.getPoints(meritLists.get(k).getArab());
                             mcount++;
                         }
                         for(int l = 0; l<streams.size();l++){
                         	if(meritLists.get(k).getStream().equals(streams.get(l).getStream())) {
                         		
                                 streamPoints.setStream(streams.get(l).getStream());
                                 streamPoints.setPoints(this.getPoints(meritLists.get(k).getArab()));
                                 streamPoints.setCount(1);

                                 streamsMeanPoints.add(streamPoints);
                         	}
                         }

                        
                         break;
                     
                     case "Msc": 
                         totalPoints += this.getPoints(meritLists.get(k).getMsc());
                         count++;
                         if(meritLists.get(k).getGender().equals("Female")){
                             femalePoints += this.getPoints(meritLists.get(k).getMsc());
                             fcount++;
                         }else if(meritLists.get(k).getGender().equals("Male")){
                             malePoints += this.getPoints(meritLists.get(k).getMsc());
                             mcount++;
                         }
                         for(int l = 0; l<streams.size();l++){
                         	if(meritLists.get(k).getStream().equals(streams.get(l).getStream())) {
                         		
                                 streamPoints.setStream(streams.get(l).getStream());
                                 streamPoints.setPoints(this.getPoints(meritLists.get(k).getMsc()));
                                 streamPoints.setCount(1);

                                 streamsMeanPoints.add(streamPoints);
                         	}
                         }

                         
                         break;
                     
                     case "Bs": 
                         totalPoints += this.getPoints(meritLists.get(k).getBs());
                         count++;
                         if(meritLists.get(k).getGender().equals("Female")){
                             femalePoints += this.getPoints(meritLists.get(k).getBs());
                             fcount++;
                         }else if(meritLists.get(k).getGender().equals("Male")){
                             malePoints += this.getPoints(meritLists.get(k).getBs());
                             mcount++;
                         }
                         for(int l = 0; l<streams.size();l++){
                         	if(meritLists.get(k).getStream().equals(streams.get(l).getStream())) {
                         		
                                 streamPoints.setStream(streams.get(l).getStream());
                                 streamPoints.setPoints(this.getPoints(meritLists.get(k).getBs()));
                                 streamPoints.setCount(1);

                                 streamsMeanPoints.add(streamPoints);
                         	}
                         }
                        
                         break;
                     
                     case "Dnd": 
                         totalPoints += this.getPoints(meritLists.get(k).getDnd());
                         count++;
                         if(meritLists.get(k).getGender().equals("Female")){
                             femalePoints += this.getPoints(meritLists.get(k).getDnd());
                             fcount++;
                         }else if(meritLists.get(k).getGender().equals("Male")){
                             malePoints += this.getPoints(meritLists.get(k).getDnd());
                             mcount++;
                         }
                         for(int l = 0; l<streams.size();l++){
                         	if(meritLists.get(k).getStream().equals(streams.get(l).getStream())) {
                         		
                                 streamPoints.setStream(streams.get(l).getStream());
                                 streamPoints.setPoints(this.getPoints(meritLists.get(k).getDnd()));
                                 streamPoints.setCount(1);

                                 streamsMeanPoints.add(streamPoints);
                         	}
                         }

                         break;
                  
                 }
                 

             }
             
             DecimalFormat df = new DecimalFormat("#.####");
             float avg = (float) 0.000; 
             if(count != 0) {
         		avg = totalPoints/count;
         	}

             float favg = (float) 0.000;
             if(fcount != 0) {
         		favg = femalePoints/fcount;
         	}
             
             float mavg = (float) 0.000;
             if(mcount != 0) {
         		mavg = malePoints/mcount;
         	}
             
            
             for (int k = 0; k < streams.size(); k++){
                 int sPoints = 0;
                 int sCount = 0;
                 
                 for(int l = 0; l<streamsMeanPoints.size(); l++){
                     if(streams.get(k).getStream().equals(streamsMeanPoints.get(l).getStream())){
                         sPoints += streamsMeanPoints.get(l).getPoints();
                         sCount += streamsMeanPoints.get(l).getCount();
                     }
                 }
  
                 float savg = (float) 0.000;
                 if(sCount > 0) {
                 	savg = (float) sPoints/sCount;
                 }	
                 
                 if(subjects.get(j).getInitials().equals("C.R.E")){
                	 context.setVariable("cre" + streams.get(k).getStream() + "MeanPoints", Float.valueOf(df.format(savg)));
                 }
                 else if(subjects.get(j).getInitials().equals("H.R.E")){
                	 context.setVariable("hre" + streams.get(k).getStream() + "MeanPoints", Float.valueOf(df.format(savg)));
                 }
                 else if(subjects.get(j).getInitials().equals("I.R.E")){
                	 context.setVariable("ire" + streams.get(k).getStream() + "MeanPoints", Float.valueOf(df.format(savg)));
                 }
                 else if(subjects.get(j).getInitials() != "C.R.E" && subjects.get(j).getInitials() != "H.R.E" && subjects.get(j).getInitials() != "I.R.E") {
                	 context.setVariable(subjects.get(j).getInitials() + streams.get(k).getStream() + "MeanPoints", Float.valueOf(df.format(savg)));
                 }

             }

             if(subjects.get(j).getInitials().equals("C.R.E")){
            	 context.setVariable("creMeanPoints", Float.valueOf(df.format(avg)));
            	 context.setVariable("creFemaleMeanPoints", Float.valueOf(df.format(favg)));
            	 context.setVariable( "creMaleMeanPoints", Float.valueOf(df.format(mavg)));
             }else if(subjects.get(j).getInitials().equals("H.R.E")){
            	 context.setVariable("hreMeanPoints", Float.valueOf(df.format(avg)));
            	 context.setVariable("hreFemaleMeanPoints", Float.valueOf(df.format(favg)));
            	 context.setVariable( "hreMaleMeanPoints", Float.valueOf(df.format(mavg)));
             }else if(subjects.get(j).getInitials().equals("I.R.E")){
            	 context.setVariable("ireMeanPoints", Float.valueOf(df.format(avg)));
            	 context.setVariable("ireFemaleMeanPoints", Float.valueOf(df.format(favg)));
            	 context.setVariable( "ireMaleMeanPoints", Float.valueOf(df.format(mavg)));
             }
             else if(subjects.get(j).getInitials() != "C.R.E" && subjects.get(j).getInitials() != "H.R.E" && subjects.get(j).getInitials() != "I.R.E"){
            	 context.setVariable(subjects.get(j).getInitials() + "MeanPoints", Float.valueOf(df.format(avg)));
            	 context.setVariable(subjects.get(j).getInitials() + "FemaleMeanPoints", Float.valueOf(df.format(favg)));
            	 context.setVariable(subjects.get(j).getInitials() + "MaleMeanPoints", Float.valueOf(df.format(mavg)));
             }
            

         }

   
         	List<TeacherYearFormStream> teachersYearFormStream = new ArrayList<>();
            for(int k = 0; k < streams.size(); k++){
                List<TeacherYearFormStream> teachers = this.teacherYearFormStreamService.getAllTeachersTeachingInYearFormAndStream(code, year, form, streams.get(k).getId());
                teachersYearFormStream.addAll(teachers);
            }
            context.setVariable("teachers", teachersYearFormStream);

         for (i2 = 0; i2 < studentsWithoutMarks.size(); ++i2) {
             MeritList meritList = new MeritList();
             meritList.setFirstname(studentsWithoutMarks.get(i2).getFirstname());
             meritList.setSecondname(studentsWithoutMarks.get(i2).getThirdname());
             meritList.setAdmNo(studentsWithoutMarks.get(i2).getAdmNo());
             meritList.setKcpe(studentsWithoutMarks.get(i2).getKcpeMarks());
             meritList.setStream(studentsWithoutMarks.get(i2).getStream().getStream());
             meritList.setTotal(0);
             gradeCount = new GradeCount();
             gradeCount.setFirstname(studentsWithoutMarks.get(i2).getFirstname());
             gradeCount.setSecondname(studentsWithoutMarks.get(i2).getThirdname());
             gradeCount.setAdmNo(studentsWithoutMarks.get(i2).getAdmNo());
             meritLists.add(meritList);
             gradeCounts.add(gradeCount);
         }
         String[] grades = new String[]{"A", "Am", "Bp", "B", "Bm", "Cp", "C", "Cm", "Dp", "D", "Dm", "E"};
         String[] gds = new String[]{"A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "D-", "E"};
         context.setVariable("grades", (Object)grades);
         context.setVariable("gradeCounts", gradeCounts);
         Collections.sort(meritLists, new SortByDeviation().reversed());
         if (meritLists.size() > 0) {
        	 context.setVariable("mostImproved", meritLists.get(0));
         }
         List<MeritList> mathsMerits = this.getSubjectMeritList(code, year, form, term, "Maths", meritLists);
         Collections.sort(mathsMerits, new SortByMaths().reversed());
         if (mathsMerits.size() > 0) {

             List<MeritList> mathsGiant = new ArrayList<>();

             int mostMarks = mathsMerits.get(0).getMaths();
             for(int j=0;j<mathsMerits.size();j++){
                 if(mathsMerits.get(j).getMaths() == mostMarks){
                     mathsGiant.add(mathsMerits.get(j));
                 }
             }
             context.setVariable("MathsGiants", mathsGiant);
         }
         List<MeritList> engMerits = this.getSubjectMeritList(code, year, form, term, "Eng", meritLists);
         Collections.sort(engMerits, new SortByEng().reversed());
         if (engMerits.size() > 0) {

             List<MeritList> engGiant = new ArrayList<>();

             int mostMarks = engMerits.get(0).getEng();
             for(int j=0;j<engMerits.size();j++){
                 if(engMerits.get(j).getEng() == mostMarks){
                     engGiant.add(engMerits.get(j));
                 }
             }
             context.setVariable("EngGiants", engGiant);
         }
         List<MeritList> kisMerits = this.getSubjectMeritList(code, year, form, term, "Kis", meritLists);
         Collections.sort(kisMerits, new SortByKis().reversed());
         if (kisMerits.size() > 0) {

             List<MeritList> kisGiant = new ArrayList<>();

             int mostMarks = kisMerits.get(0).getKis();
             for(int j=0;j<kisMerits.size();j++){
                 if(kisMerits.get(j).getKis() == mostMarks){
                     kisGiant.add(kisMerits.get(j));
                 }
             }
             context.setVariable("KisGiants", kisGiant);

         }
         List<MeritList> bioMerits = this.getSubjectMeritList(code, year, form, term, "Bio", meritLists);
         Collections.sort(bioMerits, new SortByBio().reversed());
         if (bioMerits.size() > 0) {

             List<MeritList> bioGiant = new ArrayList<>();

             int mostMarks = bioMerits.get(0).getBio();
             for(int j=0;j<bioMerits.size();j++){
                 if(bioMerits.get(j).getBio() == mostMarks){
                     bioGiant.add(bioMerits.get(j));
                 }
             }
             context.setVariable("BioGiants", bioGiant);

         }
         List<MeritList> chemMerits = this.getSubjectMeritList(code, year, form, term, "Chem", meritLists);
         Collections.sort(chemMerits, new SortByChem().reversed());
         if (chemMerits.size() > 0) {

             List<MeritList> chemGiant = new ArrayList<>();

             int mostMarks = chemMerits.get(0).getChem();
             for(int j=0;j<chemMerits.size();j++){
                 if(chemMerits.get(j).getChem() == mostMarks){
                     chemGiant.add(chemMerits.get(j));
                 }
             }
             context.setVariable("ChemGiants", chemGiant);
         }
         List<MeritList> phyMerits = this.getSubjectMeritList(code, year, form, term, "Phy", meritLists);
         Collections.sort(phyMerits, new SortByPhy().reversed());
         if (phyMerits.size() > 0) {

             List<MeritList> phyGiant = new ArrayList<>();

             int mostMarks = phyMerits.get(0).getPhy();
             for(int j=0;j<phyMerits.size();j++){
                 if(phyMerits.get(j).getPhy() == mostMarks){
                     phyGiant.add(phyMerits.get(j));
                 }
             }
             context.setVariable("PhyGiants", phyGiant);
         }
         List<MeritList> histMerits = this.getSubjectMeritList(code, year, form, term, "Hist", meritLists);
         Collections.sort(histMerits, new SortByHist().reversed());
         if (histMerits.size() > 0) {

             List<MeritList> histGiant = new ArrayList<>();

             int mostMarks = histMerits.get(0).getHist();
             for(int j=0;j<histMerits.size();j++){
                 if(histMerits.get(j).getHist() == mostMarks){
                     histGiant.add(histMerits.get(j));
                 }
             }
             context.setVariable("HistGiants", histGiant);

         }
         
    

         List<MeritList> creMerits = this.getSubjectMeritList(code, year, form, term, "C.R.E", meritLists);
         Collections.sort(creMerits, new SortByCre().reversed());
         if (creMerits.size() > 0) {

             List<MeritList> creGiant = new ArrayList<>();

             int mostMarks = creMerits.get(0).getCre();

             for(int j=0;j<creMerits.size();j++){
                 if(creMerits.get(j).getCre() == mostMarks){
                     creGiant.add(creMerits.get(j));
                 }
             }
             context.setVariable("CreGiants", creGiant);

         }
         

         List<MeritList> geoMerits = this.getSubjectMeritList(code, year, form, term, "Geo", meritLists);
         Collections.sort(geoMerits, new SortByGeo().reversed());
         if (geoMerits.size() > 0) {

             List<MeritList> geoGiant = new ArrayList<>();

             int mostMarks = geoMerits.get(0).getGeo();
             for(int j=0;j<geoMerits.size();j++){
                 if(geoMerits.get(j).getGeo() == mostMarks){
                     geoGiant.add(geoMerits.get(j));
                 }
             }
             context.setVariable("GeoGiants", geoGiant);

         }

         List<MeritList> ireMerits = this.getSubjectMeritList(code, year, form, term, "I.R.E", meritLists);
         Collections.sort(ireMerits, new SortByIre().reversed());
         if (ireMerits.size() > 0) {

             List<MeritList> ireGiant = new ArrayList<>();

             int mostMarks = ireMerits.get(0).getIre();
             for(int j=0;j<ireMerits.size();j++){
                 if(ireMerits.get(j).getIre() == mostMarks){
                     ireGiant.add(ireMerits.get(j));
                 }
             }
             context.setVariable("IreGiants", ireGiant);

         }
         List<MeritList> hreMerits = this.getSubjectMeritList(code, year, form, term, "H.R.E", meritLists);
         Collections.sort(hreMerits, new SortByHre().reversed());
         if (hreMerits.size() > 0) {

             List<MeritList> hreGiant = new ArrayList<>();

             int mostMarks = hreMerits.get(0).getHre();
             for(int j=0;j<hreMerits.size();j++){
                 if(hreMerits.get(j).getHre() == mostMarks){
                     hreGiant.add(hreMerits.get(j));
                 }
             }
             context.setVariable("HreGiants", hreGiant);

         }
         List<MeritList> hsciMerits = this.getSubjectMeritList(code, year, form, term, "Hsci", meritLists);
         Collections.sort(hsciMerits, new SortByHsci().reversed());
         if (hsciMerits.size() > 0) {

             List<MeritList> hsciGiant = new ArrayList<>();

             int mostMarks = hsciMerits.get(0).getHsci();
             for(int j=0;j<hsciMerits.size();j++){
                 if(hsciMerits.get(j).getHsci() == mostMarks){
                     hsciGiant.add(hsciMerits.get(j));
                 }
             }
             context.setVariable("HsciGiants", hsciGiant);

         }
         List<MeritList> andMerits = this.getSubjectMeritList(code, year, form, term, "AnD", meritLists);
         Collections.sort(andMerits, new SortByAnd().reversed());
         if (andMerits.size() > 0) {

             List<MeritList> andGiant = new ArrayList<>();

             int mostMarks = andMerits.get(0).getAnD();
             for(int j=0;j<andMerits.size();j++){
                 if(andMerits.get(j).getAnD() == mostMarks){
                     andGiant.add(andMerits.get(j));
                 }
             }

             context.setVariable("AnDGiants", andGiant);
         }
         List<MeritList> agricMerits = this.getSubjectMeritList(code, year, form, term, "Agric", meritLists);
         Collections.sort(agricMerits, new SortByAgric().reversed());
         if (agricMerits.size() > 0) {

             List<MeritList> agricGiant = new ArrayList<>();

             int mostMarks = agricMerits.get(0).getAgric();
             for(int j=0;j<agricMerits.size();j++){
                 if(agricMerits.get(j).getAgric() == mostMarks){
                     agricGiant.add(agricMerits.get(j));
                 }
             }

             context.setVariable("AgricGiants", agricGiant);
         }
         List<MeritList> compMerits = this.getSubjectMeritList(code, year, form, term, "Comp", meritLists);
         Collections.sort(compMerits, new SortByComp().reversed());
         if (compMerits.size() > 0) {

             List<MeritList> compGiant = new ArrayList<>();

             int mostMarks = compMerits.get(0).getComp();
             for(int j=0;j<compMerits.size();j++){
                 if(compMerits.get(j).getComp() == mostMarks){
                     compGiant.add(compMerits.get(j));
                 }
             }
             context.setVariable("CompGiants", compGiant);
         }
         List<MeritList> aviMerits = this.getSubjectMeritList(code, year, form, term, "Avi", meritLists);
         Collections.sort(aviMerits, new SortByAvi().reversed());
         if (aviMerits.size() > 0) {

             List<MeritList> aviGiant = new ArrayList<>();

             int mostMarks = aviMerits.get(0).getAvi();
             for(int j=0;j<aviMerits.size();j++){
                 if(aviMerits.get(j).getAvi() == mostMarks){
                     aviGiant.add(aviMerits.get(j));
                 }
             }
             context.setVariable("AviGiants", aviGiant);
         }
         List<MeritList> elecMerits = this.getSubjectMeritList(code, year, form, term, "Elec", meritLists);
         Collections.sort(elecMerits, new SortByElec().reversed());
         if (elecMerits.size() > 0) {

             List<MeritList> elecGiant = new ArrayList<>();

             int mostMarks = elecMerits.get(0).getElec();
             for(int j=0;j<elecMerits.size();j++){
                 if(elecMerits.get(j).getElec() == mostMarks){
                     elecGiant.add(elecMerits.get(j));
                 }
             }
             context.setVariable("ElecGiants", elecGiant);
         }
         List<MeritList> pwrMerits = this.getSubjectMeritList(code, year, form, term, "Pwr", meritLists);
         Collections.sort(pwrMerits, new SortByPwr().reversed());
         if (pwrMerits.size() > 0) {

             List<MeritList> pwrGiant = new ArrayList<>();

             int mostMarks = pwrMerits.get(0).getPwr();
             for(int j=0;j<pwrMerits.size();j++){
                 if(pwrMerits.get(j).getPwr() == mostMarks){
                     pwrGiant.add(pwrMerits.get(j));
                 }
             }

             context.setVariable("PwrGiants", pwrGiant);

         }
         List<MeritList> woodMerits = this.getSubjectMeritList(code, year, form, term, "Wood", meritLists);
         Collections.sort(woodMerits, new SortByWood().reversed());
         if (woodMerits.size() > 0) {

             List<MeritList> woodGiant = new ArrayList<>();

             int mostMarks = woodMerits.get(0).getWood();
             for(int j=0;j<woodMerits.size();j++){
                 if(woodMerits.get(j).getWood() == mostMarks){
                     woodGiant.add(woodMerits.get(j));
                 }
             }

             context.setVariable("WoodGiants", woodGiant);
         }
         List<MeritList> metalMerits = this.getSubjectMeritList(code, year, form, term, "Metal", meritLists);
         Collections.sort(metalMerits, new SortByMetal().reversed());
         if (metalMerits.size() > 0) {

             List<MeritList> metalGiant = new ArrayList<>();

             int mostMarks =metalMerits.get(0).getMetal();
             for(int j=0;j<metalMerits.size();j++){
                 if(metalMerits.get(j).getMetal() == mostMarks){
                     metalGiant.add(metalMerits.get(j));
                 }
             }

             context.setVariable("MetalGiants", metalGiant);
         }
         List<MeritList> bcMerits = this.getSubjectMeritList(code, year, form, term, "Bc", meritLists);
         Collections.sort(bcMerits, new SortByBc().reversed());
         if (bcMerits.size() > 0) {

             List<MeritList> bcGiant = new ArrayList<>();

             int mostMarks = bcMerits.get(0).getBc();
             for(int j=0;j<bcMerits.size();j++){
                 if(bcMerits.get(j).getBc() == mostMarks){
                     bcGiant.add(bcMerits.get(j));
                 }
             }

             context.setVariable("BcGiants", bcGiant);
         }
         List<MeritList> frenMerits = this.getSubjectMeritList(code, year, form, term, "Fren", meritLists);
         Collections.sort(frenMerits, new SortByFren().reversed());
         if (frenMerits.size() > 0) {

             List<MeritList> frenGiant = new ArrayList<>();

             int mostMarks = frenMerits.get(0).getFren();
             for(int j=0;j<frenMerits.size();j++){
                 if(frenMerits.get(j).getFren() == mostMarks){
                     frenGiant.add(frenMerits.get(j));
                 }
             }

             context.setVariable("FrenGiants", frenGiant);
         }
         List<MeritList> germMerits = this.getSubjectMeritList(code, year, form, term, "Germ", meritLists);
         Collections.sort(germMerits, new SortByGerm().reversed());
         if (germMerits.size() > 0) {

             List<MeritList> germGiant = new ArrayList<>();

             int mostMarks = germMerits.get(0).getGerm();
             for(int j=0;j<germMerits.size();j++){
                 if(germMerits.get(j).getGerm() == mostMarks){
                     germGiant.add(germMerits.get(j));
                 }
             }

             context.setVariable("GermGiants", germGiant);
         }
         List<MeritList> arabMerits = this.getSubjectMeritList(code, year, form, term, "Arab", meritLists);
         Collections.sort(arabMerits, new SortByArab().reversed());
         if (arabMerits.size() > 0) {

             List<MeritList> arabGiant = new ArrayList<>();

             int mostMarks = arabMerits.get(0).getArab();
             for(int j=0;j<arabMerits.size();j++){
                 if(arabMerits.get(j).getArab() == mostMarks){
                     arabGiant.add(arabMerits.get(j));
                 }
             }

             context.setVariable("ArabGiants", arabGiant);
         }
         List<MeritList> mscMerits = this.getSubjectMeritList(code, year, form, term, "Msc", meritLists);
         Collections.sort(mscMerits, new SortByMsc().reversed());
         if (mscMerits.size() > 0) {

             List<MeritList> mscGiant = new ArrayList<>();

             int mostMarks = mscMerits.get(0).getMsc();
             for(int j=0;j<mscMerits.size();j++){
                 if(mscMerits.get(j).getMsc() == mostMarks){
                     mscGiant.add(mscMerits.get(j));
                 }
             }

             context.setVariable("MscGiants", mscGiant);
         }
         List<MeritList> bsMerits = this.getSubjectMeritList(code, year, form, term, "Bs", meritLists);
         Collections.sort(bsMerits, new SortByBs().reversed());
         if (bsMerits.size() > 0) {

             List<MeritList> bsGiant = new ArrayList<>();

             int mostMarks = bsMerits.get(0).getBs();
             for(int j=0;j<bsMerits.size();j++){
                 if(bsMerits.get(j).getBs() == mostMarks){
                     bsGiant.add(bsMerits.get(j));
                 }
             }

             context.setVariable("BsGiants", bsGiant);
         }
         List<MeritList> dndMerits = this.getSubjectMeritList(code, year, form, term, "Dnd", meritLists);
         Collections.sort(dndMerits, new SortByDnd().reversed());
         if (dndMerits.size() > 0) {

             List<MeritList> dndGiant = new ArrayList<>();

             int mostMarks = dndMerits.get(0).getDnd();
             for(int j=0;j<dndMerits.size();j++){
                 if(dndMerits.get(j).getDnd() == mostMarks){
                     dndGiant.add(dndMerits.get(j));
                 }
             }

             context.setVariable("DndGiants", dndGiant);
         }
         Collections.sort(meritLists, new SortByAverage().reversed());
         for (int j = 0; j < gds.length; ++j) {
             int totalS = 0;
             block148: for (int i4 = 0; i4 < subjects.size(); ++i4) {
                 int count = 0;
                 switch (subjects.get(i4).getInitials()) {
                     case "Maths": {
                         int k;
                         for (k = 0; k < gradeCounts.size(); ++k) {
                             if (gradeCounts.get(k).getMaths() != gds[j]) continue;
                             ++count;
                             ++totalS;
                         }
                         context.setVariable(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                         continue block148;
                     }
                     case "Eng": {
                         int k;
                         for (k = 0; k < gradeCounts.size(); ++k) {
                             if (gradeCounts.get(k).getEng() != gds[j]) continue;
                             ++count;
                             ++totalS;
                         }
                         context.setVariable(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                         continue block148;
                     }
                     case "Kis": {
                         int k;
                         for (k = 0; k < gradeCounts.size(); ++k) {
                             if (gradeCounts.get(k).getKis() != gds[j]) continue;
                             ++count;
                             ++totalS;
                         }
                         context.setVariable(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                         continue block148;
                     }
                     case "Bio": {
                         int k;
                         for (k = 0; k < gradeCounts.size(); ++k) {
                             if (gradeCounts.get(k).getBio() != gds[j]) continue;
                             ++count;
                             ++totalS;
                         }
                         context.setVariable(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                         continue block148;
                     }
                     case "Chem": {
                         int k;
                         for (k = 0; k < gradeCounts.size(); ++k) {
                             if (gradeCounts.get(k).getChem() != gds[j]) continue;
                             ++count;
                             ++totalS;
                         }
                         context.setVariable(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                         continue block148;
                     }
                     case "Phy": {
                         int k;
                         for (k = 0; k < gradeCounts.size(); ++k) {
                             if (gradeCounts.get(k).getPhy() != gds[j]) continue;
                             ++count;
                             ++totalS;
                         }
                         context.setVariable(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                         continue block148;
                     }
                     case "Hist": {
                         int k;
                         for (k = 0; k < gradeCounts.size(); ++k) {
                             if (gradeCounts.get(k).getHist() != gds[j]) continue;
                             ++count;
                             ++totalS;
                         }
                         context.setVariable(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                         continue block148;
                     }
                     case "C.R.E": {
                         int k;
                         for (k = 0; k < gradeCounts.size(); ++k) {
                             if (gradeCounts.get(k).getCre() != gds[j]) continue;
                             ++count;
                             ++totalS;
                         }
                         context.setVariable(grades[j] + "CreCount", (Object)count);
                         continue block148;
                     }
                     case "Geo": {
                         int k;
                         for (k = 0; k < gradeCounts.size(); ++k) {
                             if (gradeCounts.get(k).getGeo() != gds[j]) continue;
                             ++count;
                             ++totalS;
                         }
                         context.setVariable(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                         continue block148;
                     }
                     case "I.R.E": {
                         int k;
                         for (k = 0; k < gradeCounts.size(); ++k) {
                             if (gradeCounts.get(k).getIre() != gds[j]) continue;
                             ++count;
                             ++totalS;
                         }
                         context.setVariable(grades[j] + "IreCount", (Object)count);
                         continue block148;
                     }
                     case "H.R.E": {
                         int k;
                         for (k = 0; k < gradeCounts.size(); ++k) {
                             if (gradeCounts.get(k).getHre() != gds[j]) continue;
                             ++count;
                             ++totalS;
                         }
                         context.setVariable(grades[j] + "HreCount", (Object)count);
                         continue block148;
                     }
                     case "Hsci": {
                         int k;
                         for (k = 0; k < gradeCounts.size(); ++k) {
                             if (gradeCounts.get(k).getHsci() != gds[j]) continue;
                             ++count;
                             ++totalS;
                         }
                         context.setVariable(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                         continue block148;
                     }
                     case "AnD": {
                         int k;
                         for (k = 0; k < gradeCounts.size(); ++k) {
                             if (gradeCounts.get(k).getAnd() != gds[j]) continue;
                             ++count;
                             ++totalS;
                         }
                         context.setVariable(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                         continue block148;
                     }
                     case "Agric": {
                         int k;
                         for (k = 0; k < gradeCounts.size(); ++k) {
                             if (gradeCounts.get(k).getAgric() != gds[j]) continue;
                             ++count;
                             ++totalS;
                         }
                         context.setVariable(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                         continue block148;
                     }
                     case "Comp": {
                         int k;
                         for (k = 0; k < gradeCounts.size(); ++k) {
                             if (gradeCounts.get(k).getComp() != gds[j]) continue;
                             ++count;
                             ++totalS;
                         }
                         context.setVariable(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                         continue block148;
                     }
                     case "Avi": {
                         int k;
                         for (k = 0; k < gradeCounts.size(); ++k) {
                             if (gradeCounts.get(k).getAvi() != gds[j]) continue;
                             ++count;
                             ++totalS;
                         }
                         context.setVariable(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                         continue block148;
                     }
                     case "Elec": {
                         int k;
                         for (k = 0; k < gradeCounts.size(); ++k) {
                             if (gradeCounts.get(k).getElec() != gds[j]) continue;
                             ++count;
                             ++totalS;
                         }
                         context.setVariable(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                         continue block148;
                     }
                     case "Pwr": {
                         int k;
                         for (k = 0; k < gradeCounts.size(); ++k) {
                             if (gradeCounts.get(k).getPwr() != gds[j]) continue;
                             ++count;
                             ++totalS;
                         }
                         context.setVariable(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                         continue block148;
                     }
                     case "Wood": {
                         int k;
                         for (k = 0; k < gradeCounts.size(); ++k) {
                             if (gradeCounts.get(k).getWood() != gds[j]) continue;
                             ++count;
                             ++totalS;
                         }
                         context.setVariable(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                         continue block148;
                     }
                     case "Metal": {
                         int k;
                         for (k = 0; k < gradeCounts.size(); ++k) {
                             if (gradeCounts.get(k).getMetal() != gds[j]) continue;
                             ++count;
                             ++totalS;
                         }
                         context.setVariable(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                         continue block148;
                     }
                     case "Bc": {
                         int k;
                         for (k = 0; k < gradeCounts.size(); ++k) {
                             if (gradeCounts.get(k).getBc() != gds[j]) continue;
                             ++count;
                             ++totalS;
                         }
                         context.setVariable(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                         continue block148;
                     }
                     case "Fren": {
                         int k;
                         for (k = 0; k < gradeCounts.size(); ++k) {
                             if (gradeCounts.get(k).getFren() != gds[j]) continue;
                             ++count;
                             ++totalS;
                         }
                         context.setVariable(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                         continue block148;
                     }
                     case "Germ": {
                         int k;
                         for (k = 0; k < gradeCounts.size(); ++k) {
                             if (gradeCounts.get(k).getGerm() != gds[j]) continue;
                             ++count;
                             ++totalS;
                         }
                         context.setVariable(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                         continue block148;
                     }
                     case "Arab": {
                         int k;
                         for (k = 0; k < gradeCounts.size(); ++k) {
                             if (gradeCounts.get(k).getArab() != gds[j]) continue;
                             ++count;
                             ++totalS;
                         }
                         context.setVariable(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                         continue block148;
                     }
                     case "Msc": {
                         int k;
                         for (k = 0; k < gradeCounts.size(); ++k) {
                             if (gradeCounts.get(k).getMsc() != gds[j]) continue;
                             ++count;
                             ++totalS;
                         }
                         context.setVariable(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                         continue block148;
                     }
                     case "Bs": {
                         int k;
                         for (k = 0; k < gradeCounts.size(); ++k) {
                             if (gradeCounts.get(k).getBs() != gds[j]) continue;
                             ++count;
                             ++totalS;
                         }
                         context.setVariable(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                         continue block148;
                     }
                     case "Dnd": {
                         int k;
                         for (k = 0; k < gradeCounts.size(); ++k) {
                             if (gradeCounts.get(k).getDnd() != gds[j]) continue;
                             ++count;
                             ++totalS;
                         }
                         context.setVariable(grades[j] + subjects.get(i4).getInitials() + "Count", (Object)count);
                     }
                 }
             }
             context.setVariable(grades[j] + "Count", (Object)totalS);
         }
         for (i = 0; i < students.size(); ++i) {
        	 context.setVariable("Points" + students.get(i).getAdmNo(), (Object)this.getPoints(students.get(i).getKcpeMarks() / 5));
         }
         
         

         Collections.sort(meritLists, new SortByPoints().reversed());
         
         int count = 0;

         for(int j = 0; j<meritLists.size();j++){
             count++;
             if(count>1){
                 if(meritLists.get(j).getPoints() == meritLists.get(j-1).getPoints()																																										){
                     count--;
                 }
             }
             meritLists.get(j).setRank(count);
         }

         List<MeritList> topStudents = new ArrayList<>();
         List<MeritList> bottomStudents = new ArrayList<>();

         //Top Students
         if(meritLists.size() < 5){
             for(int j = 0; j<meritLists.size(); j++){
                 if(meritLists.get(j).getTotal() > 0) {
                     topStudents.add(meritLists.get(j));
                 }
             }
         }else if(meritLists.size() > 5){
             for(int j = 0; j<5; j++){
                 if(meritLists.get(j).getTotal() > 0) {
                     topStudents.add(meritLists.get(j));
                 }
             }
         }

         //Bottom Students
         if(meritLists.size() > 10 && meritLists.size() < 15 ){
             for(int j = meritLists.size() - 3; j < meritLists.size() - 1; j++){
                 if(meritLists.get(j).getTotal() > 0) {
                     bottomStudents.add(meritLists.get(j));
                 }
             }
         }else if(meritLists.size() > 15 && meritLists.size() < 40){
             for(int j = meritLists.size() - 5; j < meritLists.size() - 1; j++){
                 if(meritLists.get(j).getTotal() > 0) {
                     bottomStudents.add(meritLists.get(j));
                 }
             }
         }else if(meritLists.size() > 40){
             for(int j = meritLists.size() - 10; j < meritLists.size() - 1; j++){
                 if(meritLists.get(j).getTotal() > 0) {
                     bottomStudents.add(meritLists.get(j));
                 }
             }
         }

         //All student mean based on subjects

         context.setVariable("topStudents", topStudents);
         context.setVariable("bottomStudents", bottomStudents);
         context.setVariable("meritLists", meritLists);
         context.setVariable("activeUser", (Object)activeUser);
         context.setVariable("school", (Object)school);
         context.setVariable("student", (Object)student);
         context.setVariable("year", (Object)year);
         context.setVariable("form", (Object)form);
         context.setVariable("term", (Object)term);
         context.setVariable("marks", allMarks);
         context.setVariable("subjects", subjects);
         context.setVariable("streams", streams);
         context.setVariable("students", students);
         context.setVariable("studentsWithoutMarks", studentsWithoutMarks);
         context.setVariable("MathsCount", (Object)mathsCount);
         context.setVariable("EngCount", (Object)engCount);
         context.setVariable("KisCount", (Object)kisCount);
         context.setVariable("BioCount", (Object)bioCount);
         context.setVariable("ChemCount", (Object)chemCount);
         context.setVariable("PhyCount", (Object)phyCount);
         context.setVariable("HistCount", (Object)histCount);
         context.setVariable("creCount", (Object)creCount);
         context.setVariable("GeoCount", (Object)geoCount);
         context.setVariable("ireCount", (Object)ireCount);
         context.setVariable("hreCount", (Object)hreCount);
         context.setVariable("HsciCount", (Object)hsciCount);
         context.setVariable("AnDCount", (Object)andCount);
         context.setVariable("AgricCount", (Object)agricCount);
         context.setVariable("CompCount", (Object)compCount);
         context.setVariable("AviCount", (Object)aviCount);
         context.setVariable("ElecCount", (Object)elecCount);
         context.setVariable("PwrCount", (Object)pwrCount);
         context.setVariable("WoodCount", (Object)woodCount);
         context.setVariable("MetalCount", (Object)metalCount);
         context.setVariable("BcCount", (Object)bcCount);
         context.setVariable("FrenCount", (Object)frenCount);
         context.setVariable("GermCount", (Object)germCount);
         context.setVariable("ArabCount", (Object)arabCount);
         context.setVariable("MscCount", (Object)mscCount);
         context.setVariable("BsCount", (Object)bsCount);
         context.setVariable("DndCount", (Object)dndCount);

        String meritListHtml = this.templateEngine.process("meritListPdf", (IContext)context);
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter((OutputStream)target);
        PdfDocument pdfDocument = new PdfDocument(writer);
        PageSize pageSize = PageSize.A4.rotate();
        pdfDocument.setDefaultPageSize(pageSize);
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setBaseUri("http://analytica-env.eba-iigws4mq.us-east-2.elasticbeanstalk.com/");
        MediaDeviceDescription mediaDeviceDescription = new MediaDeviceDescription(MediaType.SCREEN);
        mediaDeviceDescription.setWidth(pageSize.getWidth());
        converterProperties.setMediaDeviceDescription(mediaDeviceDescription);
        HtmlConverter.convertToPdf((String)meritListHtml, (PdfDocument)pdfDocument, (ConverterProperties)converterProperties);
        byte[] bytes = target.toByteArray();
        return ResponseEntity.ok().contentType(org.springframework.http.MediaType.APPLICATION_PDF).body((Object)bytes);
    }

    @GetMapping(value={"/schools/{code}/years/{year}/forms/{form}/terms/{term}/students/{admNo}/termlyReport/pdf"})
    public ResponseEntity<?> getPDF(@PathVariable int code, @PathVariable int form, @PathVariable int year, @PathVariable int term, @PathVariable String admNo, HttpServletRequest request, HttpServletResponse response, Principal principal) throws IOException {
        School school = this.schoolService.getSchool(code).get();
        Student student = this.studentService.getStudentInSchool(admNo, code);
        List<Subject> subjects = this.subjectService.getSubjectDoneByStudent(admNo);
        List<Year> years = this.yearService.allYearsForStudent(admNo);
        List<Form> forms = this.formService.studentForms(admNo);
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        List<ExamName> examNames = this.examNameService.getExamBySchoolYearFormTerm(code, year, form, term);
        List<Mark> marks = this.markService.getTermlySubjectMark(admNo, form, term);
        List<Teacher> teachers = this.userService.getAllTeachersByAcademicYearAndSchoolFormStream(code, form, student.getStream().getId(), year);
        WebContext context = new WebContext(request, response, this.servletContext);
        context.setVariable("activeUser", (Object)activeUser);
        context.setVariable("marks", marks);
        context.setVariable("forms", forms);
        context.setVariable("years", years);
        context.setVariable("subjects", subjects);
        context.setVariable("student", (Object)student);
        context.setVariable("school", (Object)school);
        context.setVariable("examNames", examNames);
        context.setVariable("year", (Object)year);
        context.setVariable("form", (Object)form);
        context.setVariable("term", (Object)term);
        context.setVariable("teachers", teachers);
        String termlyReportHtml = this.templateEngine.process("termlyReportPdf", (IContext)context);
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setBaseUri("http://analytica-env.eba-iigws4mq.us-east-2.elasticbeanstalk.com/");
        HtmlConverter.convertToPdf((String)termlyReportHtml, (OutputStream)target, (ConverterProperties)converterProperties);
        byte[] bytes = target.toByteArray();
        return ResponseEntity.ok().contentType(org.springframework.http.MediaType.APPLICATION_PDF).body((Object)bytes);
    }

    @GetMapping(value={"/schools/{code}/years/{year}/forms/{form}/terms/{term}/streams/{stream}/studentsReport/pdf"})
    public ResponseEntity<?> getStudentReportPDF(@PathVariable int code, @PathVariable int form, @PathVariable int year, @PathVariable int term, @PathVariable String stream, HttpServletRequest request, HttpServletResponse response, Principal principal) throws IOException {
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
        WebContext context = new WebContext(request, response, this.servletContext);
        for (int i = 0; i < students.size(); ++i) {
            context.setVariable("subjects" + students.get(i).getAdmNo(), this.subjectService.getSubjectDoneByStudent(students.get(i).getAdmNo()));
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
                        context.setVariable(subjects.get(j).getInitials() + "sum" + students.get(i).getAdmNo(), (Object)(sum * 100 / totalOutOf));
                        overalTotal += sum * 100 / totalOutOf;
                    }
                    if (subjects.get(j).getInitials().equals("C.R.E")) {
                        context.setVariable("Cresum" + students.get(i).getAdmNo(), (Object)(sum * 100 / totalOutOf));
                        overalTotal += sum * 100 / totalOutOf;
                        continue;
                    }
                    if (subjects.get(j).getInitials().equals("H.R.E")) {
                        context.setVariable("Hresum" + students.get(i).getAdmNo(), (Object)(sum * 100 / totalOutOf));
                        overalTotal += sum * 100 / totalOutOf;
                        continue;
                    }
                    if (!subjects.get(j).getInitials().equals("I.R.E")) continue;
                    context.setVariable("Iresum" + students.get(i).getAdmNo(), (Object)(sum * 100 / totalOutOf));
                    overalTotal += sum * 100 / totalOutOf;
                    continue;
                }
                if (subjects.get(j).getInitials() != "C.R.E" && subjects.get(j).getInitials() != "I.R.E" && subjects.get(j).getInitials() != "H.R.E") {
                    context.setVariable(subjects.get(j).getInitials() + "sum" + students.get(i).getAdmNo(), (Object)sum);
                    overalTotal += sum;
                }
                if (subjects.get(j).getInitials().equals("C.R.E")) {
                    context.setVariable("Cresum" + students.get(i).getAdmNo(), (Object)sum);
                    overalTotal += sum;
                    continue;
                }
                if (subjects.get(j).getInitials().equals("H.R.E")) {
                    context.setVariable("Hresum" + students.get(i).getAdmNo(), (Object)sum);
                    overalTotal += sum;
                    continue;
                }
                if (!subjects.get(j).getInitials().equals("I.R.E")) continue;
                context.setVariable("Iresum" + students.get(i).getAdmNo(), (Object)sum);
                overalTotal += sum;
            }

            if(overalTotal >= 0){
                context.setVariable("total" + students.get(i).getAdmNo(), (Object)overalTotal);
            }else{
                context.setVariable("total" + students.get(i).getAdmNo(), 0);
            }

            context.setVariable("marks" + students.get(i).getAdmNo(), marks);
            List<TeacherYearFormStream> teachers = this.teacherYearFormStreamService.getAllTeachersTeachingInYearFormAndStream(code, year, form, students.get(i).getStream().getId());
            context.setVariable("teachers" + students.get(i).getAdmNo(), teachers);
        }
        
        context.setVariable("activeUser", (Object)activeUser);
        context.setVariable("school", (Object)school);
        context.setVariable("streams", this.streamService.getStreamsInSchool(code));
        context.setVariable("student", (Object)student);
        context.setVariable("students", students);
        context.setVariable("streamStudents", streamStudents);
        context.setVariable("year", (Object)year);
        context.setVariable("form", (Object)form);
        context.setVariable("term", (Object)term);
        context.setVariable("stream", (Object)stream);
        context.setVariable("examNames", examNames);

        getMeritList get = new getMeritList();

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

        context.setVariable("meritLists", allStudentsMeritList);

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

        context.setVariable("studentMeritList", streamStudentsMeritList);
        context.setVariable("count", (Object)cnt);
        String studentReportHtml = this.templateEngine.process("studentsReportPdf", (IContext)context);
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setBaseUri("http://analytica-env.eba-iigws4mq.us-east-2.elasticbeanstalk.com/");
        HtmlConverter.convertToPdf((String)studentReportHtml, (OutputStream)target, (ConverterProperties)converterProperties);
        byte[] bytes = target.toByteArray();
        return ResponseEntity.ok().contentType(org.springframework.http.MediaType.APPLICATION_PDF).body((Object)bytes);
    }

    @GetMapping(value={"/schools/{code}/years/{year}/forms/{form}/terms/{term}/streams/{stream}/timetable/pdf"})
    public ResponseEntity<?> getMeritListPDF(@PathVariable int code, @PathVariable int year, @PathVariable int form, @PathVariable int term, @PathVariable int stream, HttpServletRequest request, HttpServletResponse response, Principal principal) throws IOException {
        List<Timetable> finalTimetables;
        int i;
        School school = this.schoolService.getSchool(code).get();
        Year yearObj = this.yearService.getYearFromSchool(year, code).get();
        Form formObj = this.formService.getFormByForm(form);
        Term termObj = this.termService.getTerm(term, form, year, code);
        Stream streamObj = this.streamService.getStream(stream);
        Student student = new Student();
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        List<Subject> subjects = this.subjectService.getAllSubjectInSchool(code);
        Random rand = new Random();
        ArrayList<String> days = new ArrayList<String>();
        days.add("Mon");
        days.add("Tue");
        days.add("Wed");
        days.add("Thu");
        days.add("Fri");
        Timetable timetable = new Timetable();
        List<Timetable> timetables = this.timetableService.getTimetableBySchoolYearFormStream(code, year, form, term, stream);
        String[] breaks = new String[]{"B", "R", "E", "A", "K"};
        String[] lunch = new String[]{"L", "U", "N", "C", "H"};
        if (timetables.isEmpty()) {
            for (i = 0; i < days.size(); ++i) {
                timetable = new Timetable(days.get(i), subjects.get(rand.nextInt(subjects.size())).getInitials(), subjects.get(rand.nextInt(subjects.size())).getInitials(), subjects.get(rand.nextInt(subjects.size())).getInitials(), subjects.get(rand.nextInt(subjects.size())).getInitials(), subjects.get(rand.nextInt(subjects.size())).getInitials(), subjects.get(rand.nextInt(subjects.size())).getInitials(), subjects.get(rand.nextInt(subjects.size())).getInitials(), subjects.get(rand.nextInt(subjects.size())).getInitials(), subjects.get(rand.nextInt(subjects.size())).getInitials(), subjects.get(rand.nextInt(subjects.size())).getInitials(), subjects.get(rand.nextInt(subjects.size())).getInitials(), subjects.get(rand.nextInt(subjects.size())).getInitials(), subjects.get(rand.nextInt(subjects.size())).getInitials(), school, yearObj, formObj, termObj, streamObj);
                timetables.add(timetable);
            }
        }
        for (i = 0; i < 5; ++i) {
            for (int j = 0; j < 10; ++j) {
                if (j == 2) {
                    timetables.get(i).setTime3(breaks[i]);
                    continue;
                }
                if (j == 4) {
                    timetables.get(i).setTime6(breaks[i]);
                    continue;
                }
                if (j != 6) continue;
                timetables.get(i).setTime9(lunch[i]);
            }
        }
        if (this.formService.getFormByForm(form) != null) {
            for (i = 0; i < timetables.size(); ++i) {
                this.timetableService.saveTimetableItem(timetables.get(i));
            }
        }
        if ((finalTimetables = this.timetableService.getTimetableBySchoolYearFormStream(code, year, form, term, stream)) == null) {
            finalTimetables = new ArrayList<Timetable>();
        }
        List<Stream> streams = this.streamService.getStreamsInSchool(school.getCode());
        List<Year> years = this.yearService.getAllYearsInSchool(school.getCode());
        WebContext context = new WebContext(request, response, this.servletContext);
        context.setVariable("year", (Object)year);
        context.setVariable("form", (Object)form);
        context.setVariable("term", (Object)term);
        context.setVariable("stream", (Object)streamObj);
        context.setVariable("years", years);
        context.setVariable("streams", streams);
        context.setVariable("timetables", finalTimetables);
        context.setVariable("activeUser", (Object)activeUser);
        context.setVariable("student", (Object)student);
        context.setVariable("school", (Object)school);
        String timetableHtml = this.templateEngine.process("timetablePdf", (IContext)context);
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter((OutputStream)target);
        PdfDocument pdfDocument = new PdfDocument(writer);
        pdfDocument.setDefaultPageSize(PageSize.A4.rotate());
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setBaseUri("http://analytica-env.eba-iigws4mq.us-east-2.elasticbeanstalk.com/");
        HtmlConverter.convertToPdf((String)timetableHtml, (PdfDocument)pdfDocument, (ConverterProperties)converterProperties);
        byte[] bytes = target.toByteArray();
        return ResponseEntity.ok().contentType(org.springframework.http.MediaType.APPLICATION_PDF).body((Object)bytes);
    }

    @GetMapping(value={"/schools/{code}/years/{year}/forms/{form}/streams/{stream}/classList/pdf"})
    public ResponseEntity<?> getPDF(@PathVariable int code, @PathVariable int year, @PathVariable int form, @PathVariable String stream, HttpServletRequest request, HttpServletResponse response, Principal principal) throws IOException {
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(code).get();
        Student student = new Student();
        User user = new User();
        List<SchoolUser> schoolUsers = this.userService.getUsersBySchoolCode(school.getCode());
        List<Year> years = this.yearService.getAllYearsInSchool(school.getCode());
        List<Stream> streams = this.streamService.getStreamsInSchool(school.getCode());
        List<Student> students = new ArrayList<>();
        List<StudentFormYear> studentsFormYear = this.studentFormYearService.getAllStudentFormYearbyFormYearandStream(code, year, form, stream);
        for(StudentFormYear studentFormYear : studentsFormYear) {
        	students.add(studentFormYear.getStudent());
        }
        List<Subject> subjects = this.subjectService.getAllSubjectInSchool(code);
        WebContext context = new WebContext(request, response, this.servletContext);
        context.setVariable("subjects", subjects);
        context.setVariable("form", (Object)form);
        context.setVariable("stream", (Object)stream);
        context.setVariable("year", (Object)year);
        context.setVariable("students", students);
        context.setVariable("streams", streams);
        context.setVariable("years", years);
        context.setVariable("schoolUsers", schoolUsers);
        context.setVariable("user", (Object)user);
        context.setVariable("activeUser", (Object)activeUser);
        context.setVariable("student", (Object)student);
        context.setVariable("school", (Object)school);
        String classListHtml = this.templateEngine.process("classListPdf", (IContext)context);
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setBaseUri("http://analytica-env.eba-iigws4mq.us-east-2.elasticbeanstalk.com/");
        HtmlConverter.convertToPdf((String)classListHtml, (OutputStream)target, (ConverterProperties)converterProperties);
        byte[] bytes = target.toByteArray();
        return ResponseEntity.ok().contentType(org.springframework.http.MediaType.APPLICATION_PDF).body((Object)bytes);
    }

    @PostMapping(value={"/school/{code}/feeRalance/pdf"})
    public String feeBalance(@PathVariable int code, @RequestParam int academicYear, @RequestParam int form, @RequestParam int term, @RequestParam String stream) {
        return "redirect:/schools/" + code + "/years/" + academicYear + "/forms/" + form + "/terms/" + term + "/streams/" + stream + "/feeBalance/pdf";
    }

    @GetMapping(value={"/schools/{code}/years/{year}/forms/{form}/terms/{term}/streams/{stream}/feeBalance/pdf"})
    public ResponseEntity<?> getFeeBalancePdf(@PathVariable int code, @PathVariable int year, @PathVariable int form, @PathVariable int term, @PathVariable String stream, Principal principal, Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(code).get();
        Student student = new Student();
        int totalAmountExpected = 0;
        List<FeeStructure> feeStructures = new ArrayList<FeeStructure>();
        for (int i = 1; i <= form; ++i) {
            for (int j = 1; j <= term; ++j) {
                feeStructures = this.feeStructureService.allFeeItemInSchoolYearFormScholarTerm(code, year, i, school.getScholar(), j);
                for (int k = 0; k < feeStructures.size(); ++k) {
                    totalAmountExpected += feeStructures.get(k).getCost();
                }
            }
        }
        ArrayList<FeeBalance> feeBalances = new ArrayList<FeeBalance>();
        List<Student> students = this.studentService.getAllStudentinSchoolYearFormTermStream(code, year, form, term, stream);
        List<FeeRecord> feeRecords = new ArrayList<FeeRecord>();
        for (int i = 0; i < students.size(); ++i) {
            int totalFeePaid = 0;
            FeeBalance feeBalance = new FeeBalance(students.get(i).getFirstname(), students.get(i).getSecondname(), students.get(i).getThirdname(), students.get(i).getAdmNo());
            feeRecords = this.feeRecordService.getAllFeeRecordForStudent(students.get(i).getAdmNo());
            for (int j = 0; j < feeRecords.size(); ++j) {
                totalFeePaid += feeRecords.get(j).getAmount();
            }
            feeBalance.setBalance(totalAmountExpected - totalFeePaid);
            feeBalances.add(feeBalance);
        }
        WebContext context = new WebContext(request, response, this.servletContext);
        context.setVariable("activeUser", (Object)user);
        context.setVariable("school", (Object)school);
        context.setVariable("student", (Object)student);
        context.setVariable("feeBalances", feeBalances);
        context.setVariable("form", (Object)form);
        context.setVariable("year", (Object)year);
        context.setVariable("term", (Object)term);
        context.setVariable("stream", (Object)stream);
        String feeBalanceHtml = this.templateEngine.process("feeBalancePdf", (IContext)context);
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setBaseUri("http://analytica-env.eba-iigws4mq.us-east-2.elasticbeanstalk.com/");
        HtmlConverter.convertToPdf((String)feeBalanceHtml, (OutputStream)target, (ConverterProperties)converterProperties);
        byte[] bytes = target.toByteArray();
        return ResponseEntity.ok().contentType(org.springframework.http.MediaType.APPLICATION_PDF).body((Object)bytes);
    }

    public List<MeritList> getSubjectMeritList(int code, int year, int form, int term, String initials, List<MeritList> meritLists) {
        List<Student> subjectStudents = this.studentService.findAllStudentDoingSubject(code, year, form, term, initials);
        ArrayList<MeritList> subjectMerits = new ArrayList<MeritList>();
        for (int i = 0; i < meritLists.size(); ++i) {
            if (subjectStudents.size() == 0) {
                MeritList meritList = new MeritList("-", "-", "-", "-");
                subjectMerits.add(meritList);
                break;
            }
            for (int j = 0; j < subjectStudents.size(); ++j) {
                if (meritLists.get(i).getAdmNo() != subjectStudents.get(j).getAdmNo()) continue;
                subjectMerits.add(meritLists.get(i));
            }
        }
        return subjectMerits;
    }

    public String getGrade(int mark) {
        if (mark <= 100 && mark >= 80) {
            return "A";
        }
        if (mark < 80 && mark >= 75) {
            return "A-";
        }
        if (mark < 75 && mark >= 70) {
            return "B+";
        }
        if (mark < 70 && mark >= 65) {
            return "B";
        }
        if (mark < 65 && mark >= 60) {
            return "B-";
        }
        if (mark < 60 && mark >= 55) {
            return "C+";
        }
        if (mark < 55 && mark >= 50) {
            return "C";
        }
        if (mark < 50 && mark >= 45) {
            return "C-";
        }
        if (mark < 45 && mark >= 40) {
            return "D+";
        }
        if (mark < 40 && mark >= 35) {
            return "D";
        }
        if (mark < 35 && mark >= 30) {
            return "D-";
        }
        if (mark < 30 && mark > 0) {
            return "E";
        }
        return "-";
    }

    public byte getPoints(int mark) {
        if (mark <= 100 && mark >= 80) {
            return 12;
        }
        if (mark < 80 && mark >= 75) {
            return 11;
        }
        if (mark < 75 && mark >= 70) {
            return 10;
        }
        if (mark < 70 && mark >= 65) {
            return 9;
        }
        if (mark < 65 && mark >= 60) {
            return 8;
        }
        if (mark < 60 && mark >= 55) {
            return 7;
        }
        if (mark < 55 && mark >= 50) {
            return 6;
        }
        if (mark < 50 && mark >= 45) {
            return 5;
        }
        if (mark < 45 && mark >= 40) {
            return 4;
        }
        if (mark < 40 && mark >= 35) {
            return 3;
        }
        if (mark < 35 && mark >= 30) {
            return 2;
        }
        if (mark < 30 && mark > 0) {
            return 1;
        }
        return 0;
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

class SortByPoints implements Comparator<MeritList>{
	 
	public int compare(MeritList a, MeritList b) {
		return a.getPoints() - b.getPoints();
	}
}

class SortByMaths implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getMaths() - b.getMaths();
	}
}

class SortByEng implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getEng() - b.getEng();
	}
}

class SortByKis implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getKis() - b.getKis();
	}
}

class SortByBio implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getBio() - b.getBio();
	}
}

class SortByChem implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getChem() - b.getChem();
	}
}

class SortByPhy implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getPhy() - b.getPhy();
	}
}

class SortByHist implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getHist() - b.getHist();
	}
}

class SortByCre implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getCre() - b.getCre();
	}
}

class SortByGeo implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getGeo() - b.getGeo();
	}
}

class SortByIre implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getIre() - b.getIre();
	}
}

class SortByHre implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getHre() - b.getHre();
	}
}

class SortByHsci implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getHsci() - b.getHsci();
	}
}

class SortByAnd implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getAnD() - b.getAnD();
	}
}

class SortByAgric implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getAgric() - b.getAgric();
	}
}

class SortByComp implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getComp() - b.getComp();
	}
}

class SortByAvi implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getAvi() - b.getAvi();
	}
}

class SortByElec implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getElec() - b.getElec();
	}
}

class SortByPwr implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getPwr() - b.getPwr();
	}
}

class SortByWood implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getWood() - b.getWood();
	}
}

class SortByMetal implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getMetal() - b.getMetal();
	}
}

class SortByBc implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getBc() - b.getBc();
	}
}

class SortByFren implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getFren() - b.getFren();
	}
}

class SortByGerm implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getGerm() - b.getGerm();
	}
}

class SortByArab implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getArab() - b.getArab();
	}
}

class SortByMsc implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getMsc() - b.getMsc();
	}
}

class SortByBs implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getBs() - b.getBs();
	}
}

class SortByDnd implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getDnd() - b.getDnd();
	}
}

class SortByDeviation implements Comparator<MeritList> {
	public int compare(MeritList a, MeritList b) {
		return (int) (a.getDeviation() - b.getDeviation());
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

        Collections.sort(meritLists, new SortByPoints().reversed());
        
        int count = 0;

        for(int j = 0; j<meritLists.size();j++){
            count++;
            if(count>1){
                if(meritLists.get(j).getPoints() == meritLists.get(j-1).getPoints()){
                    count--;
                }
            }
            meritLists.get(j).setRank(count);
        }

        return meritLists;
    }
}

class StreamPoints {
    private String stream;
    private int points;
    private int count;

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
