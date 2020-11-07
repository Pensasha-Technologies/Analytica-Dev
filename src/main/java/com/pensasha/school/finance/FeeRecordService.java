package com.pensasha.school.finance;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class FeeRecordService {

	@Autowired
	private FeeRecordRepository feeRecordRepository;

	//Get all fee record in school by year
	public Page<FeeRecord> getAllFeeRecordInSchoolByYear(int pageNumber, int code, int year){
		
		Sort sort = Sort.by("amount").descending();
		
		Pageable pageable = PageRequest.of(pageNumber -1, 6, sort); 
		
		return feeRecordRepository.findByStudentSchoolCodeAndStudentSchoolYearsYear(code, year, pageable);
	}
	
	public List<FeeRecord>  getFeeRecordsInSchool(int code){
		return feeRecordRepository.findByStudentSchoolCode(code);
	}
	
	//Get all fee records in school pages
	public Page<FeeRecord> getAllFeeRecordInSchool(int pageNumber, int code){
		
		Sort sort = Sort.by("amount").descending();
			
		Pageable pageable = PageRequest.of(pageNumber -1, 6, sort); 
		
		return feeRecordRepository.findByStudentSchoolCode(code, pageable);
	}
	
	// Get all fee records for student.
	public List<FeeRecord> getAllFeeRecordForStudent(String admNo) {

		return feeRecordRepository.findByStudentAdmNo(admNo);
	}

	// Get all fee records for student by form.
	public List<FeeRecord> getAllFeeRecordForStudentByForm(String admNo, int form) {

		return feeRecordRepository.findByStudentAdmNoAndFormForm(admNo, form);
	}

	// Get all fee records for student by term.
	public List<FeeRecord> getAllFeeRecordForStudentByTerm(String admNo, int term) {

		return feeRecordRepository.findByStudentAdmNoAndTermTerm(admNo, term);
	}

	// Get all fee records by form.
	public List<FeeRecord> getAllFeeRecordByForm(int form) {

		return feeRecordRepository.findByFormForm(form);
	}

	// Get all fee records by term.
	public List<FeeRecord> getAllFeeRecordByTerm(int term) {

		return feeRecordRepository.findByTermTerm(term);
	}

	// Get a single fee record.
	public Optional<FeeRecord> getFeeRecord(int id) {

		return feeRecordRepository.findById(id);
	}

	// Save a fee record.
	public FeeRecord saveFeeRecord(FeeRecord feeRecord) {

		return feeRecordRepository.save(feeRecord);
	}

	// Update fee record.
	public FeeRecord updateFeeRecord(FeeRecord feeRecord) {

		return feeRecordRepository.save(feeRecord);
	}

	// delete a student record.
	public void deleteFeeRecord(int id) {

		feeRecordRepository.deleteById(id);
	}

}
