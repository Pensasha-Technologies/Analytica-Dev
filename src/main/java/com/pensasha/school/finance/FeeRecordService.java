package com.pensasha.school.finance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeeRecordService {
    @Autowired
    private FeeRecordRepository feeRecordRepository;

    public Optional<FeeRecord> getFeeRecord(int id) {
        return this.feeRecordRepository.findById(id);
    }

    public List<FeeRecord> getAllFeeRecordByAcademicYear(int code, int academicYear) {
        return this.feeRecordRepository.findByStudentSchoolYearsYearAndStudentSchoolCode(academicYear, code);
    }

    public List<FeeRecord> getAllFeeRecordForStudent(String admNo) {
        return this.feeRecordRepository.findByStudentAdmNo(admNo);
    }

    public List<FeeRecord> getAllFeeRecordForStudentByForm(String admNo, int form) {
        return this.feeRecordRepository.findByStudentAdmNoAndFormForm(admNo, form);
    }

    public FeeRecord saveFeeRecord(FeeRecord feeRecord) {
        return this.feeRecordRepository.save(feeRecord);
    }

    public FeeRecord updateFeeRecord(FeeRecord feeRecord) {
        return this.feeRecordRepository.save(feeRecord);
    }

    public void deleteFeeRecord(int id) {
        this.feeRecordRepository.deleteById(id);
    }
}