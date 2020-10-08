package com.pensasha.school.school;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolRepository extends JpaRepository<School, Integer> {

	public List<School> findByYearsYear(int year);

	public List<School> findBySubjectsInitials(String initials);

}
