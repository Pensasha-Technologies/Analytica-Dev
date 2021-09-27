package com.pensasha.school.school;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolRepository extends JpaRepository<School, Integer> {
    
    public List<School> findByYearsYear(int var1);

    public List<School> findBySubjectsInitials(String var1);

    public List<School> findByExamNamesName(String var1);
}