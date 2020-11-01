package com.pensasha.school.discipline;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DisciplineService {

	@Autowired
	private DisciplineRepository disciplineRepository;

	// Get all discipline report by student admNo
	public List<Discipline> allDisciplineReportForStudent(String admNo) {

		return disciplineRepository.findByStudentAdmNo(admNo);
	}

	// Get all discipline report by stream
	public List<Discipline> allDisciplineReportByStream(String stream) {

		return disciplineRepository.findByStudentStreamStream(stream);
	}

	// Get all discipline report by class
	public List<Discipline> allDisciplineReportByClass(String form) {

		return disciplineRepository.findByStudentFormsForm(form);
	}

	// Get all discipline report by school
	public List<Discipline> allDisciplineReportBySchoolCode(int code) {

		return disciplineRepository.findByStudentSchoolCode(code);
	}

	// Get a single discipline report
	public Optional<Discipline> getDisciplineReportById(int id) {

		return disciplineRepository.findById(id);
	}

	// Add discipline report
	public Discipline saveDisciplineReport(Discipline discipline) {

		return disciplineRepository.save(discipline);
	}

	// update discipline report
	public Discipline updateDisciplineReport(Discipline discipline) {

		return disciplineRepository.save(discipline);
	}

	// Delete discipline report
	public void deleteDisciplineReport(int id) {

		disciplineRepository.deleteById(id);
	}

}
