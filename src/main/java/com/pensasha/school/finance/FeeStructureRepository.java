package com.pensasha.school.finance;

import com.pensasha.school.finance.FeeStructure;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeeStructureRepository extends JpaRepository<FeeStructure, Integer> {
    public List<FeeStructure> findBySchoolCode(int var1);

    public List<FeeStructure> findBySchoolCodeAndYearYearAndFormFormAndTermTerm(int var1, int var2, int var3, int var4);

    public List<FeeStructure> findBySchoolCodeAndYearYear(int var1, int var2);
}