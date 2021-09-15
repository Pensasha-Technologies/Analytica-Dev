package com.pensasha.school.discipline;

import com.pensasha.school.discipline.Discipline;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisciplineRepository extends JpaRepository<Discipline, Integer> {
    
    public List<Discipline> findByStudentAdmNo(String var1);

    public List<Discipline> findByStudentStreamStream(String var1);

    public List<Discipline> findByStudentFormsForm(String var1);

    public List<Discipline> findByStudentSchoolCode(int var1);

}