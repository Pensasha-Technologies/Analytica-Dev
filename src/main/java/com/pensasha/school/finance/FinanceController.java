package com.pensasha.school.finance;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

		AmountPayable term1 = amountPayableService.getAmountPayableBySchoolIdFormTerm(school.getCode(), form,
				termService.getOneTerm(1, form, school.getCode()).getTerm());
		AmountPayable term2 = amountPayableService.getAmountPayableBySchoolIdFormTerm(school.getCode(), form,
				termService.getOneTerm(2, form, school.getCode()).getTerm());
		AmountPayable term3 = amountPayableService.getAmountPayableBySchoolIdFormTerm(school.getCode(), form,
				termService.getOneTerm(3, form, school.getCode()).getTerm());

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

	@PostMapping("/school/forms/{form}/terms/{term}/amountPayable")
	public String addTermAmountPayable(Principal principal, RedirectAttributes redit, @PathVariable int form,
			@PathVariable int term, @RequestParam int amount) {

		User activeUser = userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(((SchoolUser) activeUser).getSchool().getCode()).get();

		AmountPayable amountPayable = new AmountPayable();
		amountPayable.setAmount(amount);

		Term termObj = termService.getOneTerm(term, form, school.getCode());

		amountPayable.setTerm(termObj);
		amountPayableService.addAmountPayable(amountPayable);

		return "redirect:/schools/bursarHome/createStructure/" + form;
	}

	@GetMapping("/school/forms/{form}/terms/{term}/amountPayable/{id}")
	public String deleteAmountPayable(Principal principal, RedirectAttributes redit, @PathVariable int form,
			@PathVariable int term, @PathVariable int id) {

		amountPayableService.deleteAmountPayable(id);

		return "redirect:/schools/bursarHome/createStructure/" + form;
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
	public String getStatementById(@PathVariable int code, @PathVariable String admNo, @PathVariable int id, Model model,
			Principal principal) {

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
	public String deleteStatementById(@PathVariable int code, @PathVariable String admNo, @PathVariable int id ) {

		feeRecordService.deleteFeeRecord(id);

		return "redirect:/schools/" + code + "/students/" + admNo +"/statements";
	}

	@GetMapping("/schools/{code}/students/{admNo}/statements")
	public String AllFeeStructureByStudent(Principal principal, Model model, @PathVariable int code,
			@PathVariable String admNo) {

		SchoolUser activeUser = userService.getSchoolUserByUsername(principal.getName());
		School school = schoolService.getSchool(code).get();
		Student student = studentService.getStudentInSchool(admNo, code);

		List<FeeStructure> feeStructure = feeStructureService.allFeeItemInSchool(code);

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
