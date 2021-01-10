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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pensasha.school.form.Form;
import com.pensasha.school.form.FormService;
import com.pensasha.school.school.School;
import com.pensasha.school.school.SchoolService;
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

	public FinanceController(UserService userService, SchoolService schoolService,
			FeeStructureService feeStructureService, FormService formService, FeeRecordService feeRecordService,
			StudentService studentService, YearService yearService, TermService termService) {
		super();
		this.userService = userService;
		this.schoolService = schoolService;
		this.feeStructureService = feeStructureService;
		this.formService = formService;
		this.feeRecordService = feeRecordService;
		this.studentService = studentService;
		this.yearService = yearService;
		this.termService = termService;
	}

	@PostMapping("/schools/{code}/viewFeeStructure")
	public String postViewFeeStructure(@PathVariable int code, @RequestParam int feeYear, @RequestParam int feeTerm,
			@RequestParam int feeForm) {

		return "redirect:/schools/" + code + "/years/" + feeYear + "/forms/" + feeForm + "/terms/" + feeTerm
				+ "/viewStructure";

	}

	@GetMapping("/schools/{code}/years/{year}/forms/{form}/terms/{term}/viewStructure")
	public String viewFeeStructure(Model model, Principal principal, @PathVariable int form, @PathVariable int code,
			@PathVariable int term, @PathVariable int year) {

		User activeUser = userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(code).get();
		Student student = new Student();
		User user = new User();
		List<SchoolUser> schoolUsers = userService.getUsersBySchoolCode(code);
		List<FeeStructure> feeStructures = feeStructureService.allFeeItemInSchoolYearFormTerm(code, year, form, term);

		model.addAttribute("feeStructures", feeStructures);
		model.addAttribute("schoolUsers", schoolUsers);
		model.addAttribute("user", user);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("school", school);
		model.addAttribute("form", form);
		model.addAttribute("year", year);
		model.addAttribute("term", term);

		return "viewFeeStructure";
	}

	@PostMapping("/schools/{code}/createFeeStructure")
	public String postCreateFeeStructure(@PathVariable int code, @RequestParam int feeYear, @RequestParam int feeTerm,
			@RequestParam int feeForm) {

		return "redirect:/schools/" + code + "/years/" + feeYear + "/forms/" + feeForm + "/terms/" + feeTerm
				+ "/createStructure";

	}

	@GetMapping("/schools/{code}/years/{year}/forms/{form}/terms/{term}/createStructure")
	public String createFeeStructure(Model model, Principal principal, @PathVariable int code, @PathVariable int year,
			@PathVariable int form, @PathVariable int term) {

		User activeUser = userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(code).get();

		Student student = new Student();
		User user = new User();
		List<SchoolUser> schoolUsers = userService.getUsersBySchoolCode(code);
		List<FeeStructure> feeStructures = feeStructureService.allFeeItemInSchoolYearFormTerm(code, year, form, term);

		model.addAttribute("feeStructures", feeStructures);
		model.addAttribute("schoolUsers", schoolUsers);
		model.addAttribute("user", user);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("school", school);
		model.addAttribute("form", form);
		model.addAttribute("year", year);
		model.addAttribute("term", term);

		return "createFeeStructure";

	}

	@PostMapping("/schools/{code}/years/{year}/forms/{form}/terms/{term}/createStructure/items")
	public String addItemToFeeStructure(RedirectAttributes redit, Principal principal, @PathVariable int code,
			@PathVariable int year, @PathVariable int form, @PathVariable int term, @RequestParam String name,
			@RequestParam int cost) {

		School school = schoolService.getSchool(code).get();

		Form formObj = formService.getFormByForm(form);

		Term termObj = termService.getTerm(term, form, year, code);
		if (termObj == null) {
			termObj = new Term(term);
			List<Term> terms = new ArrayList<>();
			terms.add(termObj);

			formObj.setTerms(terms);
		}

		if (formObj == null) {
			formObj = new Form(form);
			formService.addForm(formObj);
		}

		Year yearObj = new Year();

		if (yearService.doesYearExistInSchool(year, code) == true) {
			yearObj = yearService.getYearFromSchool(year, code).get();
		} else {
			yearObj = new Year(year);
			List<School> schools = new ArrayList<>();
			schools.add(school);
			yearObj.setSchools(schools);
			yearService.addYear(yearObj);
		}

		FeeStructure feeStructure = new FeeStructure(name, cost, school, yearObj, formObj, termObj);

		feeStructureService.addItem(feeStructure);

		return "redirect:/schools/" + code + "/years/" + year + "/forms/" + form + "/terms/" + term
				+ "/createStructure";

	}

	@GetMapping("/schools/{code}/years/{year}/forms/{form}/terms/{term}/createStructure/items/{id}")
	public String deleteFeeStructureItem(Principal principal, Model model, @PathVariable int code,
			@PathVariable int year, @PathVariable int form, @PathVariable int term, @PathVariable int id) {

		feeStructureService.deleteFeeStructureItem(id);

		return "redirect:/schools/" + code + "/years/" + year + "/forms/" + form + "/terms/" + term
				+ "/createStructure";
	}

	@GetMapping("/school/{code}/feeRecords")
	public String getAllFeeRecords(@PathVariable int code, Model model, Principal principal,
			@RequestParam int academicYear) {

		SchoolUser activeUser = userService.getSchoolUserByUsername(principal.getName());
		School school = schoolService.getSchool(code).get();
		List<FeeRecord> feeRecords = feeRecordService.getAllFeeRecordByAcademicYear(code, academicYear);
		Student student = new Student();

		List<FeeRecord> newRecords = new ArrayList<>();

		for (int i = 0; i < feeRecords.size(); i++) {

			if (newRecords.isEmpty()) {

				newRecords.add(feeRecords.get(i));
			} else {
				for (int j = 0; j < newRecords.size(); j++) {
					if (newRecords.get(j).getStudent() == feeRecords.get(i).getStudent()) {
						newRecords.get(j).setAmount(newRecords.get(j).getAmount() + feeRecords.get(i).getAmount());
					} else {
						newRecords.add(feeRecords.get(i));
					}
				}
			}
		}

		model.addAttribute("student", student);
		model.addAttribute("feeRecords", newRecords);
		model.addAttribute("school", school);
		model.addAttribute("activeUser", activeUser);

		return "feeStatements";
	}

	@GetMapping("/schools/{code}/students/{admNo}/statements/{id}")
	public String getStatementById(@PathVariable int code, @PathVariable String admNo, @PathVariable int id,
			Model model, Principal principal) {

		SchoolUser activeUser = userService.getSchoolUserByUsername(principal.getName());
		School school = schoolService.getSchool(code).get();
		Student student = studentService.getStudentInSchool(admNo, code);
		FeeRecord feeRecord = feeRecordService.getFeeRecord(id).get();

		model.addAttribute("feeRecord", feeRecord);
		model.addAttribute("student", student);
		model.addAttribute("school", school);
		model.addAttribute("activeUser", activeUser);

		return "feeStatement";
	}

	@GetMapping("/schools/{code}/students/{admNo}/statement/{id}")
	public String deleteStatementById(@PathVariable int code, @PathVariable String admNo, @PathVariable int id) {

		feeRecordService.deleteFeeRecord(id);

		return "redirect:/schools/" + code + "/students/" + admNo + "/statements";
	}

	@GetMapping("/schools/{code}/students/{admNo}/statements")
	@ResponseBody
	public List<FeeStructure> AllFeeStructureByStudent(Principal principal, Model model, @PathVariable int code,
			@PathVariable String admNo) {

		SchoolUser activeUser = userService.getSchoolUserByUsername(principal.getName());
		School school = schoolService.getSchool(code).get();
		Student student = studentService.getStudentInSchool(admNo, code);

		List<FeeStructure> feeStructure = feeStructureService.allFeeItemInSchool(code);

		return feeStructure;
		
		/*
		List<FeeRecord> form1FeeRecords = feeRecordService.getAllFeeRecordForStudentByForm(admNo, 1);
		List<FeeRecord> form2FeeRecords = feeRecordService.getAllFeeRecordForStudentByForm(admNo, 2);
		List<FeeRecord> form3FeeRecords = feeRecordService.getAllFeeRecordForStudentByForm(admNo, 3);
		List<FeeRecord> form4FeeRecords = feeRecordService.getAllFeeRecordForStudentByForm(admNo, 4);

		model.addAttribute("feeStructure", feeStructure);
		model.addAttribute("form1FeeRecords", form1FeeRecords);
		model.addAttribute("form2FeeRecords", form2FeeRecords);
		model.addAttribute("form3FeeRecords", form3FeeRecords);
		model.addAttribute("form4FeeRecords", form4FeeRecords);
		model.addAttribute("student", student);
		model.addAttribute("school", school);
		model.addAttribute("activeUser", activeUser);

		return "studentFeeStatement";
		
		*/
	}

	@PostMapping("/school/{code}/feeRecord")
	public String addFeeRecord(RedirectAttributes redit, @PathVariable int code, @RequestParam String receiptNo,
			@RequestParam String admNo, @RequestParam int amount) {

		if (studentService.ifStudentExists(code + "_" + admNo) == true) {

			Student student = studentService.getStudentInSchool(code + "_" + admNo, code);

			LocalDateTime today = LocalDateTime.now();
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");

			String datePaid = today.format(dtf);
			Form form = formService.getFormByForm(student.getCurrentForm());

			FeeRecord feeRecord = new FeeRecord(receiptNo, amount, datePaid, student, form);

			feeRecordService.saveFeeRecord(feeRecord);

			redit.addFlashAttribute("success", "Student finance record saved successfully");

			return "redirect:/schools/bursarHome";
		} else {

			redit.addFlashAttribute("fail", "Student with admission number " + admNo + " does not exist");

			return "redirect:/schools/bursarHome";
		}

	}

}
