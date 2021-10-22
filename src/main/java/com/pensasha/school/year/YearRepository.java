package com.pensasha.school.year;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface YearRepository extends JpaRepository<Year, Integer> {
    List<Year> findBySchoolsCode(int var1);

    Optional<Year> findByYearAndSchoolsCode(int var1, int var2);

    List<Year> findByFormsForm(int var1);

    List<Year> findByStudentsAdmNo(String var1);

    List<Year> findBySubjectsInitials(String var1);

    List<Year> findByExamNamesName(String var1);

    Boolean existsByYearAndSchoolsCode(int var1, int var2);

	List<Year> findByTeachersUsername(String username);
}