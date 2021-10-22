package com.pensasha.school.finance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeeRecordRepository
extends JpaRepository<FeeRecord, Integer> {
    List<FeeRecord> findByStudentSchoolYearsYearAndStudentSchoolCode(int var1, int var2);

    List<FeeRecord> findByStudentAdmNo(String var1);

    List<FeeRecord> findByStudentAdmNoAndFormForm(String var1, int var2);
}