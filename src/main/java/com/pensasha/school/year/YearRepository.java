package com.pensasha.school.year;

import com.pensasha.school.year.Year;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface YearRepository extends JpaRepository<Year, Integer> {
    public List<Year> findBySchoolsCode(int var1);

    public Optional<Year> findByYearAndSchoolsCode(int var1, int var2);

    public List<Year> findByFormsForm(int var1);

    public List<Year> findByStudentsAdmNo(String var1);

    public List<Year> findBySubjectsInitials(String var1);

    public List<Year> findByExamNamesName(String var1);

    public Boolean existsByYearAndSchoolsCode(int var1, int var2);
}