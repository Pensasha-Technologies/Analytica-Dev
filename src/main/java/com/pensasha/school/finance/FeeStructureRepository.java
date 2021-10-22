package com.pensasha.school.finance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeeStructureRepository extends JpaRepository<FeeStructure, Integer> {
    List<FeeStructure> findBySchoolCode(int var1);

    List<FeeStructure> findBySchoolCodeAndYearYearAndFormFormAndScholarAndTermTerm(int var1, int var2, int var3, String var4, int var5);

    List<FeeStructure> findBySchoolCodeAndYearYear(int var1, int var2);
}