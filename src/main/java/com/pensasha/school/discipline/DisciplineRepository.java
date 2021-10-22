package com.pensasha.school.discipline;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisciplineRepository extends JpaRepository<Discipline, Integer> {
    
    List<Discipline> findByStudentAdmNo(String var1);

    List<Discipline> findByStudentStreamStream(String var1);

    List<Discipline> findByStudentFormsForm(String var1);

    List<Discipline> findByStudentSchoolCode(int var1);

}