package com.pensasha.school.year;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface YearRepository extends JpaRepository<Year, Integer> {

	public List<Year> findBySchoolsCode(int code);

	public Optional<Year> findByYearAndSchoolsCode(int year, int code);

	public List<Year> findByFormsForm(int form);

	public List<Year> findByStudentsAdmNo(String admNo);

	public List<Year> findBySubjectsInitials(String initials);

	public List<Year> findByExamNamesName(String name);
}
