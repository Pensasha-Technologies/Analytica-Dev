package com.pensasha.school.finance;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeeRecordRepository extends JpaRepository<FeeRecord, Integer>{

	List<FeeRecord> findByStudentAdmNo(String admNo);

	List<FeeRecord> findByStudentAdmNoAndFormForm(String admNo, int form);

	List<FeeRecord> findByStudentAdmNoAndTermTerm(String admNo, int term);

	List<FeeRecord> findByFormForm(int form);

	List<FeeRecord> findByTermTerm(int term);

	Page<FeeRecord> findByStudentSchoolCode(int code, Pageable pageable);


	
}
