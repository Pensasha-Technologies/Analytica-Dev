package com.pensasha.school.discipline;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DisciplineRepository extends JpaRepository<Discipline, Integer>{

	List<Discipline> findByStudentAdmNo(String admNo);

	List<Discipline> findByStudentStreamStream(String stream);

	List<Discipline> findByStudentFormsForm(String form);

	List<Discipline> findByStudentSchoolCode(int code);

}
