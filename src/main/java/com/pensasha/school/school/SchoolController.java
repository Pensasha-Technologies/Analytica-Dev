package com.pensasha.school.school;

import com.pensasha.school.discipline.Discipline;
import com.pensasha.school.discipline.DisciplineService;
import com.pensasha.school.exam.ExamName;
import com.pensasha.school.exam.ExamNameService;
import com.pensasha.school.exam.Mark;
import com.pensasha.school.exam.MarkService;
import com.pensasha.school.finance.FeeRecord;
import com.pensasha.school.finance.FeeRecordService;
import com.pensasha.school.finance.FeeStructure;
import com.pensasha.school.finance.FeeStructureService;
import com.pensasha.school.school.School;
import com.pensasha.school.school.SchoolService;
import com.pensasha.school.stream.Stream;
import com.pensasha.school.stream.StreamService;
import com.pensasha.school.student.Student;
import com.pensasha.school.student.StudentService;
import com.pensasha.school.subject.Subject;
import com.pensasha.school.subject.SubjectService;
import com.pensasha.school.timetable.Timetable;
import com.pensasha.school.timetable.TimetableService;
import com.pensasha.school.user.User;
import com.pensasha.school.user.UserService;
import com.pensasha.school.year.Year;
import com.pensasha.school.year.YearService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class SchoolController {
    
    private SchoolService schoolService;
    private UserService userService;
    private SubjectService subjectService;
    private StudentService studentService;
    private MarkService markService;
    private TimetableService timetableService;
    private StreamService streamService;
    private YearService yearService;
    private FeeStructureService feeStructureService;
    private DisciplineService disciplineService;
    private FeeRecordService feeRecordService;
    private ExamNameService examNameService;

    public SchoolController(SchoolService schoolService, UserService userService, SubjectService subjectService, StudentService studentService, MarkService markService, TimetableService timetableService, StreamService streamService, YearService yearService, FeeStructureService feeStructureService, DisciplineService disciplineService, FeeRecordService feeRecordService, ExamNameService examNameService) {
        this.schoolService = schoolService;
        this.userService = userService;
        this.subjectService = subjectService;
        this.studentService = studentService;
        this.markService = markService;
        this.timetableService = timetableService;
        this.streamService = streamService;
        this.yearService = yearService;
        this.feeStructureService = feeStructureService;
        this.disciplineService = disciplineService;
        this.feeRecordService = feeRecordService;
        this.examNameService = examNameService;
    }

    @GetMapping(value={"/addSchool"})
    public String addSchoolGet(Model model, Principal principal) {
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        School school = new School();
        Student student = new Student();
        model.addAttribute("student", (Object)student);
        model.addAttribute("school", (Object)school);
        model.addAttribute("activeUser", (Object)activeUser);
        return "addSchool";
    }

    @GetMapping(value={"/editSchool/{code}"})
    public String editSchool(@PathVariable int code, Model model, Principal principal) {
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(code).get();
        Student student = new Student();
        model.addAttribute("student", (Object)student);
        model.addAttribute("school", (Object)school);
        model.addAttribute("activeUser", (Object)activeUser);
        return "editSchool";
    }

    @PostMapping(value={"/editSchool/{s_code}"})
    public RedirectView saveSchoolEdits(@Valid School school, BindingResult bindingResult, RedirectAttributes redit, @PathVariable int s_code) {
        if (bindingResult.hasErrors()) {
            RedirectView redirectView = new RedirectView("/editSchool/" + s_code, true);
            return redirectView;
        }
        this.schoolService.addSchool(school);
        redit.addFlashAttribute("success", (Object)"School successfully updated");
        RedirectView redirectView = new RedirectView("/editSchool/" + s_code, true);
        return redirectView;
    }

    @PostMapping(value={"/schools"})
    public RedirectView addSchool(@RequestParam(value="file") MultipartFile file, @Valid School school, BindingResult bindingResult, RedirectAttributes redit, Principal principal) throws IOException {
        User user = this.userService.getByUsername(principal.getName()).get();
        RedirectView redirectView = new RedirectView();
        if (bindingResult.hasErrors()) {
            redit.addFlashAttribute("org.springframework.validation.BindingResult.student", (Object)bindingResult);
            redit.addFlashAttribute("school", (Object)school);
            return new RedirectView("/addSchool", true);
        }
        if (this.schoolService.doesSchoolExists(school.getCode()).booleanValue()) {
            redit.addFlashAttribute("fail", (Object)("School with code:" + school.getCode() + " already exists"));
            if (user.getRole().getName().equals("ADMIN")) {
                redirectView = new RedirectView("/adminHome", true);
            } else if (user.getRole().getName().equals("C.E.O")) {
                redirectView = new RedirectView("/ceoHome", true);
            } else if (user.getRole().getName().equals("OFFICEASSISTANT")) {
                redirectView = new RedirectView("/officeAssistantHome", true);
            } else if (user.getRole().getName().equals("FIELDOFFICER")) {
                redirectView = new RedirectView("/fieldOfficerHome", true);
            }
            return redirectView;
        }

        String path = new File("src/main/resources/static/schImg").getAbsolutePath();
        String fileName = file.getOriginalFilename();
        if (fileName.isEmpty()) {
            school.setLogo("noSchool");
        } else {
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
            school.setLogo(fileName);
        }
        this.schoolService.addSchool(school);
        redit.addFlashAttribute("success", (Object)("School with code:" + school.getCode() + " saved successfully"));
        if (user.getRole().getName().equals("ADMIN")) {
            redirectView = new RedirectView("/adminHome", true);
        } else if (user.getRole().getName().equals("C.E.O")) {
            redirectView = new RedirectView("/ceoHome", true);
        } else if (user.getRole().getName().equals("OFFICEASSISTANT")) {
            redirectView = new RedirectView("/officeAssistantHome", true);
        } else if (user.getRole().getName().equals("FIELDOFFICER")) {
            redirectView = new RedirectView("/fieldOfficerHome", true);
        }
        return redirectView;
    }

    @GetMapping(value={"/schools/{code}"})
    public RedirectView deleteSchool(@PathVariable int code, RedirectAttributes redit, Principal principal) {
        RedirectView redirectView = new RedirectView();
        User user = this.userService.getByUsername(principal.getName()).get();
        if (this.schoolService.doesSchoolExists(code).booleanValue()) {
            List<FeeStructure> feeStructures = this.feeStructureService.allFeeItemInSchool(code);
            for (int j = 0; j < feeStructures.size(); ++j) {
                this.feeStructureService.deleteFeeStructureItem(feeStructures.get(j).getId());
            }
            List<Subject> subjects = this.subjectService.getAllSubjectInSchool(code);
            for (int i = 0; i < subjects.size(); ++i) {
                List<School> schools = this.schoolService.getAllSchoolsWithSubject(subjects.get(i).getInitials());
                schools.remove(this.schoolService.getSchool(code).get());
                subjects.get(i).setSchools(schools);
                this.subjectService.addSubject(subjects.get(i));
            }
            List<Student> students = this.studentService.getAllStudentsInSchool(code);
            for (int i = 0; i < students.size(); ++i) {
                List<Mark> marks = this.markService.allMarks(students.get(i).getAdmNo());
                for (int j = 0; j < marks.size(); ++j) {
                    this.markService.deleteMark(marks.get(j).getId());
                }
                List<FeeRecord> feeRecords = this.feeRecordService.getAllFeeRecordForStudent(students.get(i).getAdmNo());
                for (int x = 0; x < feeRecords.size(); ++x) {
                    this.feeRecordService.deleteFeeRecord(feeRecords.get(x).getId());
                }
                List<Discipline> disciplines = this.disciplineService.allDisciplineReportForStudent(students.get(i).getAdmNo());
                for (int k = 0; k < disciplines.size(); ++k) {
                    this.disciplineService.deleteDisciplineReport(disciplines.get(k).getId());
                }
                this.studentService.deleteStudent(students.get(i).getAdmNo());
            }
            List<Timetable> timetableItems = this.timetableService.getALlTimetableItemsInSchoolByCode(code);
            for (int i = 0; i < timetableItems.size(); ++i) {
                this.timetableService.deleteTimetableItem(timetableItems.get(i).getId());
            }
            List<Stream> streams = this.streamService.getStreamsInSchool(code);
            for (int i = 0; i < streams.size(); ++i) {
                this.streamService.deleteStream(streams.get(i).getId());
            }
            List<ExamName> examNames = this.examNameService.allExamNames(code);
            for (int i = 0; i < examNames.size(); ++i) {
                this.examNameService.deleteExam(examNames.get(i).getId());
            }
            this.schoolService.deleteSchool(code);
            redit.addFlashAttribute("success", (Object)("School of code:" + code + " successfully deleted"));
        } else {
            redit.addFlashAttribute("fail", (Object)("School of code:" + code + " does not exist"));
        }
        if (user.getRole().getName().equals("ADMIN")) {
            redirectView = new RedirectView("/adminHome", true);
        } else if (user.getRole().getName().equals("C.E.O")) {
            redirectView = new RedirectView("/ceoHome", true);
        } else if (user.getRole().getName().equals("OFFICEASSISTANT")) {
            redirectView = new RedirectView("/officeAssistantHome", true);
        } else if (user.getRole().getName().equals("FIELDOFFICER")) {
            redirectView = new RedirectView("/fieldOfficerHome", true);
        }
        return redirectView;
    }

    @GetMapping(value={"/school/{code}"})
    public String school(@PathVariable int code, Model model, Principal principal) {
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        if (this.schoolService.doesSchoolExists(code).booleanValue()) {
            School school = this.schoolService.getSchool(code).get();
            List<Subject> subjects = this.subjectService.getAllSubjectInSchool(code);
            List<Subject> allSubjects = this.subjectService.getAllSubjects();
            for (int i = 0; i < subjects.size(); ++i) {
                allSubjects.remove(subjects.get(i));
            }
            Student student = new Student();
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
            Stream stream = new Stream();
            List<Stream> streams = this.streamService.getStreamsInSchool(code);
            List<Year> years = this.yearService.getAllYearsInSchool(code);
            ArrayList<Subject> allCompF1F2Subjects = new ArrayList<Subject>();
            Collection<Subject> compF1F2Subjects = school.getCompSubjectF1F2();
            ArrayList<Subject> allCompF3F4Subjects = new ArrayList<Subject>();
            Collection<Subject> compF3F4Subjects = school.getCompSubjectF3F4();
            for (Subject subject : subjects) {
                if (!compF1F2Subjects.contains(subject)) {
                    allCompF1F2Subjects.add(subject);
                }
                if (compF3F4Subjects.contains(subject)) continue;
                allCompF3F4Subjects.add(subject);
            }
            model.addAttribute("allCompF1F2Subjects", allCompF1F2Subjects);
            model.addAttribute("allCompF3F4Subjects", allCompF3F4Subjects);
            model.addAttribute("years", years);
            model.addAttribute("streams", streams);
            model.addAttribute("stream", (Object)stream);
            model.addAttribute("group1", group1);
            model.addAttribute("group2", group2);
            model.addAttribute("group3", group3);
            model.addAttribute("group4", group4);
            model.addAttribute("group5", group5);
            model.addAttribute("activeUser", (Object)activeUser);
            model.addAttribute("student", (Object)student);
            model.addAttribute("school", (Object)school);
            model.addAttribute("subjects", allSubjects);
            return "school";
        }
        School school = new School();
        Student student = new Student();
        List<School> schools = this.schoolService.getAllSchools();
        Stream stream = new Stream();
        model.addAttribute("stream", (Object)stream);
        model.addAttribute("school", (Object)school);
        model.addAttribute("activeUser", (Object)activeUser);
        model.addAttribute("student", (Object)student);
        model.addAttribute("schools", schools);
        model.addAttribute("fail", (Object)("School with code:" + code + " does not exist"));
        if (activeUser.getRole().getName() == "ADMIN") {
            return "/adminHome";
        }
        if (activeUser.getRole().getName() == "C.E.O") {
            return "/ceoHome";
        }
        if (activeUser.getRole().getName() == "OFFICEASSISTANT") {
            return "/officeAssistantHome";
        }
        return "/fieldOfficerHome";
    }
}