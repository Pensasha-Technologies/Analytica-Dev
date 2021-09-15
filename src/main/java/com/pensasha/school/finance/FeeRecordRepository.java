package com.pensasha.school.finance;

import com.pensasha.school.finance.FeeRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeeRecordRepository
extends JpaRepository<FeeRecord, Integer> {
    public List<FeeRecord> findByStudentSchoolYearsYearAndStudentSchoolCode(int var1, int var2);

    public List<FeeRecord> findByStudentAdmNo(String var1);

    public List<FeeRecord> findByStudentAdmNoAndFormForm(String var1, int var2);
}