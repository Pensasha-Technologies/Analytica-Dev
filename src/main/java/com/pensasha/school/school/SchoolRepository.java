package com.pensasha.school.school;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchoolRepository extends JpaRepository<School, Integer> {
    
    List<School> findByYearsYear(int var1);

    List<School> findBySubjectsInitials(String var1);

    List<School> findByExamNamesName(String var1);
}