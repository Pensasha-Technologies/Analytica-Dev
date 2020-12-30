package com.pensasha.school.term;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pensasha.school.exam.ExamName;
import com.pensasha.school.exam.ExamNameService;
import com.pensasha.school.exam.Mark;
import com.pensasha.school.exam.MarkService;
import com.pensasha.school.exam.MeritList;
import com.pensasha.school.form.Form;
import com.pensasha.school.form.FormService;
import com.pensasha.school.school.School;
import com.pensasha.school.school.SchoolService;
import com.pensasha.school.stream.StreamService;
import com.pensasha.school.student.Student;
import com.pensasha.school.student.StudentService;
import com.pensasha.school.subject.Subject;
import com.pensasha.school.subject.SubjectService;
import com.pensasha.school.user.Teacher;
import com.pensasha.school.user.User;
import com.pensasha.school.user.UserService;
import com.pensasha.school.year.Year;
import com.pensasha.school.year.YearService;

@Controller
public class TermController {

	private SubjectService subjectService;
	private SchoolService schoolService;
	private StudentService studentService;
	private StreamService streamService;
	private YearService yearService;
	private FormService formService;
	private UserService userService;
	private MarkService markService;
	private ExamNameService examNameService;

	public TermController(SubjectService subjectService, SchoolService schoolService, StudentService studentService,
			StreamService streamService, YearService yearService, FormService formService, UserService userService,
			MarkService markService, ExamNameService examNameService) {
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
	}

	@PostMapping("/schools/{code}/studentsReport")
	public String postStudentReport(@PathVariable int code, @RequestParam int year, @RequestParam int form,
			@RequestParam int term, @RequestParam String stream) {

		return "redirect:/schools/" + code + "/years/" + year + "/forms/" + form + "/terms/" + term + "/streams/"
				+ stream + "/studentsReport";
	}

	@GetMapping("/schools/{code}/years/{year}/forms/{form}/terms/{term}/streams/{stream}/studentsReport")
	public String getStudentReport(@PathVariable int code, @PathVariable int year, @PathVariable int form,
			@PathVariable int term, @PathVariable String stream, Model model, Principal principal) {

		User activeUser = userService.getByUsername(principal.getName()).get();
		School school = schoolService.getSchool(code).get();
		Student student = new Student();
		List<Student> students = studentService.getAllStudentsInSchoolByYearFormTerm(code, year, form, term);
		List<Student> streamStudents = studentService.getAllStudentinSchoolYearFormTermStream(code, year, form, term,
				stream);
		List<ExamName> examNames = examNameService.getExamBySchoolYearFormTerm(code, year, form, term);
		List<Subject> subjects = subjectService.getAllSubjectInSchool(code);
		int cnt = 0;

		for (int i = 0; i < students.size(); i++) {
			model.addAttribute("subjects" + students.get(i).getAdmNo(),
					subjectService.getSubjectDoneByStudent(students.get(i).getAdmNo()));
			List<Mark> marks = markService.getTermlySubjectMark(students.get(i).getAdmNo(), form, term);

			for (int j = 0; j < subjects.size(); j++) {

				int sum = 0;

				for (int k = 0; k < marks.size(); k++) {
					if (marks.get(k).getSubject().equals(subjects.get(j))) {
						sum = sum + marks.get(k).getMark();
					}

				}

				if (sum > 0) {
					cnt++;
				}
			}

			model.addAttribute("marks" + students.get(i).getAdmNo(), marks);

			List<Teacher> teachers = userService.getAllTeachersByAcademicYearAndSchoolFormStream(code, form,
					students.get(i).getStream().getId(), year);

			model.addAttribute("teachers" + students.get(i).getAdmNo(), teachers);

		}

		getMeritList get = new getMeritList();

		model.addAttribute("activeUser", activeUser);
		model.addAttribute("school", school);
		model.addAttribute("streams", streamService.getStreamsInSchool(code));
		model.addAttribute("student", student);
		model.addAttribute("students", students);
		model.addAttribute("year", year);
		model.addAttribute("form", form);
		model.addAttribute("term", term);
		model.addAttribute("examNames", examNames);
		model.addAttribute("meritLists", get.getList(students, subjects, markService, year, form, term));
		model.addAttribute("studentMeritList", get.getList(streamStudents, subjects, markService, year, form, term));
		model.addAttribute("count", cnt);

		return "studentReport";

	}

	@PostMapping("/schools/{code}/student/{admNo}/termlyReport")
	public String getTermlyReport(@PathVariable int code, @PathVariable String admNo, @RequestParam int form,
			@RequestParam int year, @RequestParam int term, Model model, Principal principal) {

		return "redirect:/schools/" + code + "/years/" + year + "/forms/" + form + "/terms/" + term + "/students/"
				+ admNo + "/termlyReport";
	}

	@GetMapping("/schools/{code}/years/{year}/forms/{form}/terms/{term}/students/{admNo}/termlyReport")
	public String getTermlyReports(@PathVariable int code, @PathVariable int year, @PathVariable int form,
			@PathVariable int term, @PathVariable String admNo, Model model, Principal principal) {

		School school = schoolService.getSchool(code).get();
		Student student = studentService.getStudentInSchool(admNo, code);
		List<Subject> subjects = subjectService.getSubjectDoneByStudent(admNo);
		List<Year> years = yearService.allYearsForStudent(admNo);
		List<Form> forms = formService.studentForms(admNo);
		User activeUser = userService.getByUsername(principal.getName()).get();
		List<ExamName> examNames = examNameService.getExamBySchoolYearFormTerm(code, year, form, term);

		List<Mark> marks = markService.getTermlySubjectMark(admNo, form, term);

		List<Teacher> teachers = userService.getAllTeachersByAcademicYearAndSchoolFormStream(code, form,
				student.getStream().getId(), year);

		model.addAttribute("streams", streamService.getStreamsInSchool(code));
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

}

class SortByTotal implements Comparator<MeritList> {

	public int compare(MeritList a, MeritList b) {
		return a.getTotal() - b.getTotal();
	}
}

class getMeritList {

	public List<MeritList> getList(List<Student> students, List<Subject> subjects, MarkService markService, int year,
			int form, int term) {

		List<Student> studentsWithoutMarks = new ArrayList<>();
		List<Student> studentsWithMarks = new ArrayList<>();

		for (int i = 0; i < students.size(); i++) {
			if (!markService.getMarkByAdm(students.get(i).getAdmNo())) {
				studentsWithoutMarks.add(students.get(i));
			} else {
				studentsWithMarks.add(students.get(i));
			}

		}

		MeritList meritList = new MeritList();
		List<MeritList> meritLists = new ArrayList<>();

		int mathsCount = 0, engCount = 0, kisCount = 0, bioCount = 0, chemCount = 0, phyCount = 0, histCount = 0,
				creCount = 0, geoCount = 0, ireCount = 0, hreCount = 0, hsciCount = 0, andCount = 0, agricCount = 0,
				compCount = 0, aviCount = 0, elecCount = 0, pwrCount = 0, woodCount = 0, metalCount = 0, bcCount = 0,
				frenCount = 0, germCount = 0, arabCount = 0, mscCount = 0, bsCount = 0, dndCount = 0;

		for (int i = 0; i < studentsWithMarks.size(); i++) {

			int count = 0;

			for (int j = 0; j < subjects.size(); j++) {

				meritList.setFirstname(students.get(i).getFirstname());
				meritList.setSecondname(students.get(i).getThirdname());
				meritList.setAdmNo(students.get(i).getAdmNo());
				meritList.setKcpe(students.get(i).getKcpeMarks());
				meritList.setStream(students.get(i).getStream().getStream());

				List<Mark> marks = new ArrayList<>();

				int sum = 0;

				switch (subjects.get(j).getInitials()) {
				case "Maths":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						mathsCount++;
					}
					meritList.setMaths(sum);
					break;
				case "Eng":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						engCount++;
					}
					meritList.setEng(sum);

					break;
				case "Kis":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						kisCount++;
					}
					meritList.setKis(sum);

					break;
				case "Bio":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						bioCount++;
					}
					meritList.setBio(sum);

					break;
				case "Chem":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						chemCount++;
					}
					meritList.setChem(sum);

					break;
				case "Phy":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						phyCount++;
					}
					meritList.setPhy(sum);

					break;
				case "Hist":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						histCount++;
					}
					meritList.setHist(sum);

					break;
				case "C.R.E":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						creCount++;
					}
					meritList.setCre(sum);

					break;
				case "Geo":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						geoCount++;
					}
					meritList.setGeo(sum);

					break;
				case "I.R.E":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						ireCount++;
					}
					meritList.setIre(sum);

					break;
				case "H.R.E":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						hreCount++;
					}
					meritList.setHre(sum);

					break;
				case "Hsci":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						hsciCount++;
					}
					meritList.setHsci(sum);

					break;
				case "AnD":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						andCount++;
					}
					meritList.setAnd(sum);

					break;
				case "Agric":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						agricCount++;
					}
					meritList.setAgric(sum);

					break;
				case "Comp":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						compCount++;
					}
					meritList.setComp(sum);

					break;
				case "Avi":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						aviCount++;
					}
					meritList.setAvi(sum);

					break;
				case "Elec":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						elecCount++;
					}
					meritList.setElec(sum);

					break;
				case "Pwr":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						pwrCount++;
					}
					meritList.setPwr(sum);

					break;
				case "Wood":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						woodCount++;
					}
					meritList.setWood(sum);

					break;
				case "Metal":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						metalCount++;
					}
					meritList.setMetal(sum);

					break;
				case "Bc":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						bcCount++;
					}
					meritList.setBc(sum);

					break;
				case "Fren":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						frenCount++;
					}
					meritList.setFren(sum);

					break;
				case "Germ":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						germCount++;
					}
					meritList.setGerm(sum);

					break;
				case "Arab":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						arabCount++;
					}
					meritList.setArab(sum);

					break;
				case "Msc":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						mscCount++;
					}
					meritList.setMsc(sum);

					break;
				case "Bs":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						bsCount++;
					}
					meritList.setBs(sum);

					break;
				case "DnD":

					marks = markService.getMarkByStudentOnAsubject(students.get(i).getAdmNo(), year, form, term,
							subjects.get(j).getInitials());

					for (int k = 0; k < marks.size(); k++) {
						sum = sum + marks.get(k).getMark();
					}

					if (sum > 0) {
						count++;
						dndCount++;
					}
					meritList.setDnd(sum);

					break;
				}

			}

			meritList.setTotal(meritList.getMaths() + meritList.getEng() + meritList.getKis() + meritList.getBio()
					+ meritList.getChem() + meritList.getPhy() + meritList.getHist() + meritList.getCre()
					+ meritList.getGeo() + meritList.getIre() + meritList.getHre() + meritList.getHsci()
					+ meritList.getAnd() + meritList.getAgric() + meritList.getComp() + meritList.getAvi()
					+ meritList.getElec() + meritList.getPwr() + meritList.getWood() + meritList.getMetal()
					+ meritList.getBc() + meritList.getFren() + meritList.getGerm() + meritList.getArab()
					+ meritList.getMsc() + meritList.getBs() + meritList.getDnd());

			meritList.setAverage(meritList.getTotal() / count);
			meritList.setDeviation(meritList.getAverage() - (students.get(i).getKcpeMarks()) / 5);
			meritLists.add(meritList);

		}

		Collections.sort(meritLists, new SortByTotal());

		for (int i = 0; i < studentsWithoutMarks.size(); i++) {

			meritList = new MeritList();
			meritList.setFirstname(studentsWithoutMarks.get(i).getFirstname());
			meritList.setSecondname(studentsWithoutMarks.get(i).getSecondname());
			meritList.setAdmNo(studentsWithoutMarks.get(i).getAdmNo());
			meritList.setKcpe(studentsWithoutMarks.get(i).getKcpeMarks());
			meritList.setStream(studentsWithoutMarks.get(i).getStream().getStream());
			meritList.setTotal(0);

			meritLists.add(meritList);

		}

		for (int i = 0; i < meritLists.size(); i++) {
			meritLists.get(i).setRank(i + 1);
		}

		return meritLists;
	}
}
