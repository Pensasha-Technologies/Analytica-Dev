package com.pensasha.school.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeacherRepository extends JpaRepository<Teacher, String> {
    Teacher findByUsername(String var1);

    List<Teacher> findBySchoolCodeAndSubjectsInitials(int code, String var1);

    List<Teacher> findBySchoolCodeAndYearsYear(int var1, int var2);

    List<Teacher> findBySchoolCodeAndYearsYearAndFormsFormAndStreamsId(int var1, int var2, int var3, int var4);

    List<Teacher> findBySchoolCode(int var1);

	List<Teacher> findByFormsForm(int form);
}