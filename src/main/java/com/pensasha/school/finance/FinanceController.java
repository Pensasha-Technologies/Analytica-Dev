package com.pensasha.school.finance;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pensasha.school.form.Form;
import com.pensasha.school.form.FormService;
import com.pensasha.school.school.School;
import com.pensasha.school.school.SchoolService;
import com.pensasha.school.stream.Stream;
import com.pensasha.school.stream.StreamService;
import com.pensasha.school.student.Student;
import com.pensasha.school.student.StudentService;
import com.pensasha.school.term.Term;
import com.pensasha.school.term.TermService;
import com.pensasha.school.user.SchoolUser;
import com.pensasha.school.user.User;
import com.pensasha.school.user.UserService;
import com.pensasha.school.year.Year;
import com.pensasha.school.year.YearService;

@Controller
public class FinanceController {
    private UserService userService;
    private SchoolService schoolService;
    private FeeStructureService feeStructureService;
    private FormService formService;
    private FeeRecordService feeRecordService;
    private StudentService studentService;
    private YearService yearService;
    private TermService termService;
    private StreamService streamService;

    public FinanceController(UserService userService, SchoolService schoolService, FeeStructureService feeStructureService, FormService formService, FeeRecordService feeRecordService, StudentService studentService, YearService yearService, TermService termService, StreamService streamService) {
        this.userService = userService;
        this.schoolService = schoolService;
        this.feeStructureService = feeStructureService;
        this.formService = formService;
        this.feeRecordService = feeRecordService;
        this.studentService = studentService;
        this.yearService = yearService;
        this.termService = termService;
        this.streamService = streamService;
    }

    @PostMapping(value={"/schools/{code}/viewFeeStructure"})
    public String postViewFeeStructure(@PathVariable int code, @RequestParam int feeYear, @RequestParam int feeTerm, @RequestParam int feeForm) {
        return "redirect:/schools/" + code + "/years/" + feeYear + "/forms/" + feeForm + "/terms/" + feeTerm + "/viewStructure";
    }

    @GetMapping(value={"/schools/{code}/years/{year}/forms/{form}/terms/{term}/viewStructure"})
    public String viewFeeStructure(Model model, Principal principal, @PathVariable int form, @PathVariable int code, @PathVariable int term, @PathVariable int year) {
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(code).get();
        Student student = new Student();
        User user = new User();
        List<SchoolUser> schoolUsers = this.userService.getUsersBySchoolCode(code);

        if(school.getScholar() == "Both"){
                List<FeeStructure> dayFeeStructures = this.feeStructureService.allFeeItemInSchoolYearFormScholarTerm(code, year, form, "Day", term);
                model.addAttribute("dayFeeStructures", dayFeeStructures);
                List<FeeStructure> boardingFeeStructures = this.feeStructureService.allFeeItemInSchoolYearFormScholarTerm(code, year, form, "Boarding", term);
                model.addAttribute("boardingFeeStructures", boardingFeeStructures);
        }else{
            List<FeeStructure> feeStructures = this.feeStructureService.allFeeItemInSchoolYearFormScholarTerm(code, year, form, school.getScholar(), term);
            model.addAttribute("feeStructures", feeStructures);
        }

        List<FeeStructure> feeStructures = this.feeStructureService.allFeeItemInSchoolYearFormScholarTerm(code, year, form, school.getScholar(), term);
        List<Stream> streams = this.streamService.getStreamsInSchool(code);
        model.addAttribute("streams", streams);
        model.addAttribute("feeStructures", feeStructures);
        model.addAttribute("schoolUsers", schoolUsers);
        model.addAttribute("user", (Object)user);
        model.addAttribute("activeUser", (Object)activeUser);
        model.addAttribute("student", (Object)student);
        model.addAttribute("school", (Object)school);
        model.addAttribute("form", (Object)form);
        model.addAttribute("year", (Object)year);
        model.addAttribute("term", (Object)term);
        return "viewFeeStructure";
    }

    @PostMapping(value={"/schools/{code}/createFeeStructure"})
    public String postCreateFeeStructure(@PathVariable int code, @RequestParam int feeYear, @RequestParam int feeTerm, @RequestParam int feeForm, @RequestParam String scholar) {
        return "redirect:/schools/" + code + "/years/" + feeYear + "/forms/" + feeForm + "/scholar/" + scholar + "/terms/" + feeTerm + "/createStructure";
    }

    @GetMapping(value={"/schools/{code}/years/{year}/forms/{form}/scholar/{scholar}/terms/{term}/createStructure"})
    public String createFeeStructure(Model model, Principal principal, @PathVariable int code, @PathVariable int year, @PathVariable int form, @PathVariable int term, @PathVariable String scholar) {
        User activeUser = this.userService.getByUsername(principal.getName()).get();
        School school = this.schoolService.getSchool(code).get();
        Student student = new Student();
        User user = new User();
        List<SchoolUser> schoolUsers = this.userService.getUsersBySchoolCode(code);
        List<FeeStructure> feeStructures = this.feeStructureService.allFeeItemInSchoolYearFormScholarTerm(code,year,form,scholar,term);
        model.addAttribute("feeStructures", feeStructures);
        model.addAttribute("schoolUsers", schoolUsers);
        model.addAttribute("user", (Object)user);
        model.addAttribute("activeUser", (Object)activeUser);
        model.addAttribute("student", (Object)student);
        model.addAttribute("school", (Object)school);
        model.addAttribute("form", (Object)form);
        model.addAttribute("year", (Object)year);
        model.addAttribute("term", (Object)term);
        return "createFeeStructure";
    }

    @PostMapping(value={"/schools/{code}/years/{year}/forms/{form}/scholar/{scholar}/terms/{term}/createStructure/items"})
    public String addItemToFeeStructure(RedirectAttributes redit, Principal principal, @PathVariable int code, @PathVariable int year, @PathVariable int form, @PathVariable String scholar, @PathVariable int term, @RequestParam String name, @RequestParam int cost) {
        School school = this.schoolService.getSchool(code).get();
        Form formObj = this.formService.getFormByForm(form);
        Term termObj = this.termService.getTerm(term, form, year, code);
        if (termObj == null) {
            termObj = new Term(term);
            ArrayList<Term> terms = new ArrayList<Term>();
            terms.add(termObj);
            formObj.setTerms(terms);
        }
        if (formObj == null) {
            formObj = new Form(form);
            this.formService.addForm(formObj);
        }
        Year yearObj = new Year();
        if (this.yearService.doesYearExistInSchool(year, code).booleanValue()) {
            yearObj = this.yearService.getYearFromSchool(year, code).get();
        } else {
            yearObj = new Year(year);
            ArrayList<School> schools = new ArrayList<School>();
            schools.add(school);
            yearObj.setSchools(schools);
            this.yearService.addYear(yearObj);
        }
        FeeStructure feeStructure = new FeeStructure(name, cost, scholar,school, yearObj, formObj, termObj);
        this.feeStructureService.addItem(feeStructure);

        return "redirect:/schools/" + code + "/years/" + year + "/forms/" + form + "/scholar/" + scholar +"/terms/" + term + "/createStructure";
    }

    @GetMapping(value={"/schools/{code}/years/{year}/forms/{form}/scholar/{scholar}/terms/{term}/createStructure/items/{id}"})
    public String deleteFeeStructureItem(Principal principal, Model model, @PathVariable int code, @PathVariable int year, @PathVariable int form, @PathVariable String scholar, @PathVariable int term, @PathVariable int id) {
        this.feeStructureService.deleteFeeStructureItem(id);
        return "redirect:/schools/" + code + "/years/" + year + "/forms/" + form + "/scholar/" + scholar + "/terms/" + term + "/createStructure";
    }

    @GetMapping(value={"/school/{code}/feeRecords"})
    public String getAllFeeRecords(@PathVariable int code, Model model, Principal principal, @RequestParam int academicYear) {

        SchoolUser activeUser = this.userService.getSchoolUserByUsername(principal.getName());
        School school = this.schoolService.getSchool(code).get();
        List<FeeRecord> feeRecords = this.feeRecordService.getAllFeeRecordByAcademicYear(code, academicYear);
        List<Stream> streams = this.streamService.getStreamsInSchool(school.getCode());

        Student student = new Student();
        ArrayList<FeeRecord> newRecords = new ArrayList<FeeRecord>();
        for (int i = 0; i < feeRecords.size(); ++i) {
            newRecords.add(feeRecords.get(i));
        }
        model.addAttribute("streams", streams);
        model.addAttribute("student", (Object)student);
        model.addAttribute("feeRecords", newRecords);
        model.addAttribute("school", (Object)school);
        model.addAttribute("activeUser", (Object)activeUser);

        return "feeStatements";
    }

    @GetMapping(value={"/schools/{code}/students/{admNo}/statements/{id}"})
    public String getStatementById(@PathVariable int code, @PathVariable String admNo, @PathVariable int id, Model model, Principal principal) {
        SchoolUser activeUser = this.userService.getSchoolUserByUsername(principal.getName());
        School school = this.schoolService.getSchool(code).get();
        Student student = this.studentService.getStudentInSchool(admNo, code);
        FeeRecord feeRecord = this.feeRecordService.getFeeRecord(id).get();
        model.addAttribute("feeRecord", (Object)feeRecord);
        model.addAttribute("student", (Object)student);
        model.addAttribute("school", (Object)school);
        model.addAttribute("activeUser", (Object)activeUser);
        return "feeStatement";
    }

    @GetMapping(value={"/schools/{code}/students/{admNo}/statement/{id}"})
    public String deleteStatementById(@PathVariable int code, @PathVariable String admNo, @PathVariable int id) {
        this.feeRecordService.deleteFeeRecord(id);
        return "redirect:/schools/" + code + "/students/" + admNo + "/statements";
    }

    @GetMapping(value={"/schools/{code}/students/{admNo}/statements"})
    public String AllFeeStructureByStudent(Principal principal, Model model, @PathVariable int code, @PathVariable String admNo) {
        SchoolUser activeUser = this.userService.getSchoolUserByUsername(principal.getName());
        School school = this.schoolService.getSchool(code).get();
        Student student = this.studentService.getStudentInSchool(admNo, code);
        List<FeeStructure> feeStructure = this.feeStructureService.allFeeItemInSchoolYear(code, student.getYearAdmitted());
        List<FeeRecord> form1FeeRecords = this.feeRecordService.getAllFeeRecordForStudentByForm(admNo, 1);
        List<FeeRecord> form2FeeRecords = this.feeRecordService.getAllFeeRecordForStudentByForm(admNo, 2);
        List<FeeRecord> form3FeeRecords = this.feeRecordService.getAllFeeRecordForStudentByForm(admNo, 3);
        List<FeeRecord> form4FeeRecords = this.feeRecordService.getAllFeeRecordForStudentByForm(admNo, 4);
        model.addAttribute("feeStructure", feeStructure);
        model.addAttribute("form1FeeRecords", form1FeeRecords);
        model.addAttribute("form2FeeRecords", form2FeeRecords);
        model.addAttribute("form3FeeRecords", form3FeeRecords);
        model.addAttribute("form4FeeRecords", form4FeeRecords);
        model.addAttribute("student", (Object)student);
        model.addAttribute("school", (Object)school);
        model.addAttribute("activeUser", (Object)activeUser);
        return "studentFeeStatement";
    }

    @PostMapping(value={"/school/{code}/feeRecord"})
    public String addFeeRecord(RedirectAttributes redit, @PathVariable int code, @RequestParam String receiptNo, @RequestParam String admNo, @RequestParam int amount) {
        if (this.studentService.ifStudentExists(code + "_" + admNo).booleanValue()) {
            Student student = this.studentService.getStudentInSchool(code + "_" + admNo, code);
            LocalDateTime today = LocalDateTime.now();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String datePaid = today.format(dtf);
            Form form = this.formService.getFormByForm(student.getCurrentForm());
            FeeRecord feeRecord = new FeeRecord(receiptNo, amount, datePaid, student, form);
            this.feeRecordService.saveFeeRecord(feeRecord);
            redit.addFlashAttribute("success", (Object)"Student finance record saved successfully");
            return "redirect:/schools/bursarHome";
        }
        redit.addFlashAttribute("fail", (Object)("Student with admission number " + admNo + " does not exist"));
        return "redirect:/schools/bursarHome";
    }

    @PostMapping(value={"/school/{code}/feeRalance"})
    public String feeBalance(@PathVariable int code, @RequestParam int academicYear, @RequestParam int form, @RequestParam int term, @RequestParam String stream) {
        return "redirect:/schools/" + code + "/years/" + academicYear + "/forms/" + form + "/terms/" + term + "/streams/" + stream + "/feeBalance";
    }

    @GetMapping(value={"/schools/{code}/years/{year}/forms/{form}/terms/{term}/streams/{stream}/feeBalance"})
    public String getFeeBalance(@PathVariable int code, @PathVariable int year, @PathVariable int form, @PathVariable int term, @PathVariable String stream, Principal principal, Model model) {
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
        List<Stream> streams = this.streamService.getStreamsInSchool(code);
        model.addAttribute("streams", streams);
        model.addAttribute("activeUser", (Object)user);
        model.addAttribute("school", (Object)school);
        model.addAttribute("student", (Object)student);
        model.addAttribute("feeBalances", feeBalances);
        model.addAttribute("form", (Object)form);
        model.addAttribute("year", (Object)year);
        model.addAttribute("term", (Object)term);
        model.addAttribute("stream", (Object)stream);
        return "feeBalance";
    }
}