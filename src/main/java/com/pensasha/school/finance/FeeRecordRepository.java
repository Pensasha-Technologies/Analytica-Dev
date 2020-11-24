package com.pensasha.school.finance;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FeeRecordRepository extends JpaRepository<FeeRecord, Integer>{

	List<FeeRecord> findByStudentSchoolYearsYearAndStudentSchoolCode(int academicYear, int code);

	List<FeeRecord> findByStudentAdmNo(String admNo);

	List<FeeRecord> findByStudentAdmNoAndFormForm(String admNo, int form);
	
}
