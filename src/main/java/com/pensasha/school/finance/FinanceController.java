package com.pensasha.school.finance;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

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

@Controller
public class FinanceController {

	private UserService userService;
	private SchoolService schoolService;
	private FeeStructureService feeStructureService;
	private FormService formService;
	private FeeRecordService feeRecordService;
	private AmountPayableService amountPayableService;
	private StudentService studentService;
	private TermService termService;

	public FinanceController(UserService userService, SchoolService schoolService,
			FeeStructureService feeStructureService, FormService formService, FeeRecordService feeRecordService,
			AmountPayableService amountPayableService, StudentService studentService, TermService termService) {
		super();
		this.userService = userService;
		this.schoolService = schoolService;
		this.feeStructureService = feeStructureService;
		this.formService = formService;
		this.feeRecordService = feeRecordService;
		this.amountPayableService = amountPayableService;
		this.studentService = studentService;
		this.termService = termService;
	}

	@GetMapping("schools/bursarHome/viewFeeStructure/{form}")
	public String viewFeeStructure(Model model, Principal principal, @PathVariable int form) {

		switch (form) {
		case 1:
			model.addAttribute("form", "1");
			break;
		case 2:
			model.addAttribute("form", "2");
			break;
		case 3:
			model.addAttribute("form", "3");
			break;
		case 4:
			model.addAttribute("form", "4");
			break;
		default:
			break;
		}

		User activeUser = userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(((SchoolUser) activeUser).getSchool().getCode()).get();
		Student student = new Student();
		User user = new User();
		List<SchoolUser> schoolUsers = userService.getUsersBySchoolCode(school.getCode());
		List<FeeStructure> feeStructures = feeStructureService.allFeeItemInSchoolAndForm(school.getCode(), form);

		model.addAttribute("feeStructures", feeStructures);
		model.addAttribute("schoolUsers", schoolUsers);
		model.addAttribute("user", user);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("school", school);

		return "viewFeeStructure";
	}

	@GetMapping("schools/bursarHome/createStructure/{form}")
	public String createFeeStructure(Model model, Principal principal, @PathVariable int form) {
		
		switch (form) {
		case 1:
			model.addAttribute("form", "1");
			break;
		case 2:
			model.addAttribute("form", "2");
			break;
		case 3:
			model.addAttribute("form", "3");
			break;
		case 4:
			model.addAttribute("form", "4");
			break;
		default:
			break;
		}
		
		User activeUser = userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(((SchoolUser) activeUser).getSchool().getCode()).get();
		
		AmountPayable term1 = amountPayableService.getAmountPayableBySchoolIdFormTerm(school.getCode(), form, termService.getOneTerm(1, form, school.getCode()).getTerm());
		AmountPayable term2 = amountPayableService.getAmountPayableBySchoolIdFormTerm(school.getCode(), form, termService.getOneTerm(2, form, school.getCode()).getTerm());
		AmountPayable term3 = amountPayableService.getAmountPayableBySchoolIdFormTerm(school.getCode(), form, termService.getOneTerm(3, form, school.getCode()).getTerm());		
		
		model.addAttribute("term1", term1);
		model.addAttribute("term2", term2);
		model.addAttribute("term3", term3);
		
		Student student = new Student();
		User user = new User();
		List<SchoolUser> schoolUsers = userService.getUsersBySchoolCode(school.getCode());
		List<FeeStructure> feeStructures = feeStructureService.allFeeItemInSchoolAndForm(school.getCode(), form);

		model.addAttribute("feeStructures", feeStructures);
		model.addAttribute("schoolUsers", schoolUsers);
		model.addAttribute("user", user);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("school", school);

		return "createFeeStructure";
		
		
	}

	@PostMapping("school/term/amountPayable")
	public String addTermAmountPayable(RedirectAttributes redit){
		
	}
	
	@PostMapping("/schools/bursarHome/createStructure/{classes}/items")
	public String addItemToFeeStructure(RedirectAttributes redit, Principal principal, @PathVariable int classes,
			@ModelAttribute FeeStructure feeStructure) {

		User activeUser = userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(((SchoolUser) activeUser).getSchool().getCode()).get();

		Form formObj = formService.getFormByForm(classes);

		if (formObj == null) {
			formObj = new Form(classes);
			formService.addForm(formObj);
		}

		feeStructure.setSchool(school);
		feeStructure.setForm(formObj);

		feeStructureService.addItem(feeStructure);

		return "redirect:/schools/bursarHome/createStructure/" + classes;

	}

	@GetMapping("/schools/bursarHome/createStructure/{classes}/items/{id}")
	public String deleteFeeStructureItem(Principal principal, Model model, @PathVariable int classes,
			@PathVariable int id) {

		feeStructureService.deleteFeeStructureItem(id);

		return "redirect:/schools/bursarHome/createStructure/" + classes;
	}
	
	@GetMapping("/schools/{code}/statements")
	public String getAllSchoolStatement(@PathVariable int code, Principal principal, Model model,
			@RequestParam int academicYear) {

		return listByPage(model, principal, 1, code, academicYear);
	}

	@GetMapping("/schools/{code}/statements/years/{year}/page/{pageNumber}")
	public String listByPage(Model model, Principal principal, @PathVariable int pageNumber, @PathVariable int code,
			@PathVariable int year) {

		SchoolUser activeUser = (SchoolUser) userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(activeUser.getSchool().getCode()).get();
		Student student = new Student();
		User user = new User();

		Page<FeeRecord> page = feeRecordService.getAllFeeRecordInSchoolByYear(pageNumber, code, year);
		long totalRecords = page.getTotalElements();
		int totalPages = page.getTotalPages();

		List<FeeRecord> feeRecords = page.getContent();

		model.addAttribute("currentPage", pageNumber);
		model.addAttribute("totalRecords", totalRecords);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("feeRecords", feeRecords);
		model.addAttribute("user", user);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("school", school);

		return "feeStatements";
	}

	@GetMapping("/schools/{code}/statements/{id}")
	public String getFeeStatement(Model model, Principal principal, @PathVariable int code, @PathVariable int id) {

		SchoolUser activeUser = (SchoolUser) userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(activeUser.getSchool().getCode()).get();
		Student student = new Student();
		User user = new User();

		FeeRecord feeRecord = feeRecordService.getFeeRecord(id).get();

		model.addAttribute("feeRecord", feeRecord);
		model.addAttribute("user", user);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("school", school);

		return "feeStatement";
	}

	@GetMapping("/schools/{code}/students/{admNo}/statements")
	public String getStudentFeeStatements(Model model, Principal principal, @PathVariable int code,
			@PathVariable String admNo) {

		SchoolUser activeUser = (SchoolUser) userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(activeUser.getSchool().getCode()).get();
		Student student = studentService.getStudentInSchool(admNo, code);
		User user = new User();

		List<FeeRecord> feeRecords = feeRecordService.getAllFeeRecordForStudent(admNo);
		List<FeeStructure> feeStructure = feeStructureService.allFeeItemInSchool(code);

		model.addAttribute("feeStructure", feeStructure);
		model.addAttribute("feeRecords", feeRecords);
		model.addAttribute("user", user);
		model.addAttribute("activeUser", activeUser);
		model.addAttribute("student", student);
		model.addAttribute("school", school);

		return "studentFeeStatement";
	}

	@PostMapping("/schools/{code}/statements")
	public RedirectView addFeeStatement(@PathVariable int code, RedirectAttributes redit, Model model,
			Principal principal, @RequestParam String receiptNo, @RequestParam int admNo, @RequestParam int amount,
			@RequestParam int form, @RequestParam int term) {

		if (studentService.ifStudentExistsInSchool(code + "_" + admNo, code) == true) {
			Student student = studentService.getStudentInSchool(code + "_" + admNo, code);
			Form formObj = new Form(form);
			Term termObj = new Term(term);

			LocalDate datePaid = LocalDate.now();
			DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd/MM/yyyy");

			FeeRecord feeRecord = new FeeRecord(receiptNo, amount, datePaid.format(myFormatObj), student, formObj,
					termObj);

			feeRecordService.saveFeeRecord(feeRecord);

			redit.addFlashAttribute("success", admNo + " Fee successfully added");

		} else {

			redit.addFlashAttribute("fail", "Student with admNo: " + admNo + " could not be found");
		}

		RedirectView redirectView = new RedirectView("/schools/" + code + "/statements", true);

		return redirectView;
	}

	@GetMapping("/schools/{code}/statement/{id}")
	public RedirectView deleteSchoolStatement(@PathVariable int code, @PathVariable int id, RedirectAttributes redit,
			Principal principal, Model model) {

		feeRecordService.deleteFeeRecord(id);

		redit.addFlashAttribute("success", "Fee Record successfully added");

		RedirectView redirectView = new RedirectView("/schools/" + code + "/statements", true);

		return redirectView;

	}

	@GetMapping("/schools/{code}/students/{admNo}/statements/{id}")
	public RedirectView deleteStudentStatement(@PathVariable int code, @PathVariable int id, @PathVariable String admNo,
			RedirectAttributes redit, Principal principal, Model model) {

		feeRecordService.deleteFeeRecord(id);

		redit.addFlashAttribute("success", "Fee Record successfully added");

		RedirectView redirectView = new RedirectView("/schools/" + code + "/students/" + admNo + "/statements", true);

		return redirectView;

	}
	
	
}
