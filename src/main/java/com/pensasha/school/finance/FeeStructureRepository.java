package com.pensasha.school.finance;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FeeStructureRepository extends JpaRepository<FeeStructure, Integer> {
    public List<FeeStructure> findBySchoolCode(int var1);

    public List<FeeStructure> findBySchoolCodeAndYearYearAndFormFormAndScholarAndTermTerm(int var1, int var2, int var3, String var4, int var5);

    public List<FeeStructure> findBySchoolCodeAndYearYear(int var1, int var2);
}