package com.pensasha.school.finance;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeeRecordService {

	@Autowired
	private FeeRecordRepository feeRecordRepository;

	// Get a single fee record.
	public Optional<FeeRecord> getFeeRecord(int id) {

		return feeRecordRepository.findById(id);
	}
	
	//Get all financial record by year
	public List<FeeRecord> getAllFeeRecordByAcademicYear(int code,int academicYear){
		
		return feeRecordRepository.findByStudentSchoolYearsYearAndStudentSchoolCode(academicYear, code);
	}
	
	//Get all fee record for student
	public List<FeeRecord> getAllFeeRecordForStudent(String admNo){
		
		return feeRecordRepository.findByStudentAdmNo(admNo);
	}
	
	//Get all fee record for a student by form
	public List<FeeRecord> getAllFeeRecordForStudentByForm(String admNo, int form){
		
		return feeRecordRepository.findByStudentAdmNoAndFormForm(admNo,form);
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
