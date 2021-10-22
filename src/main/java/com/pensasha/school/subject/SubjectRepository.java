package com.pensasha.school.subject;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, String> {
    List<Subject> findBySchoolsCode(int var1);

    Subject findByInitialsAndSchoolsCode(String var1, int var2);

    List<Subject> findByStudentsAdmNo(String var1);

    Subject findByInitialsAndStudentsAdmNo(String var1, String var2);

    List<Subject> findByFormsFormAndStudentsAdmNo(int var1, String var2);

    List<Subject> findByYearsYearAndFormsFormAndStudentsAdmNo(int var1, int var2, String var3);

    Boolean existsByInitialsAndSchoolsCode(String var1, int var2);

    Subject findByName(String var1);
}