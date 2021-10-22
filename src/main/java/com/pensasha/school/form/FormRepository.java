package com.pensasha.school.form;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FormRepository extends JpaRepository<Form, Integer> {
    
    List<Form> findByYearsYearAndYearsSchoolsCode(int var1, int var2);

    Optional<Form> findByFormAndYearsYearAndYearsSchoolsCode(int var1, int var2, int var3);

    List<Form> findByStudentsAdmNo(String var1);

    List<Form> findBySubjectsInitials(String var1);

    Boolean existsByFormAndStudentsAdmNo(int var1, String var2);

    Form findByFormAndStudentsAdmNo(int var1, String var2);

    Form findByForm(int var1);

    List<Form> findByExamNamesName(String var1);

    Boolean existsByFormAndYearsYearAndYearsSchoolsCode(int var1, int var2, int var3);

    List<Form> findByYearsYear(int var1);

	List<Form> findByTeachersUsername(String username);
}