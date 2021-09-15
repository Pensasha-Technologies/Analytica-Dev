package com.pensasha.school.form;

import com.pensasha.school.form.Form;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormRepository extends JpaRepository<Form, Integer> {
    
    public List<Form> findByYearsYearAndYearsSchoolsCode(int var1, int var2);

    public Optional<Form> findByFormAndYearsYearAndYearsSchoolsCode(int var1, int var2, int var3);

    public List<Form> findByStudentsAdmNo(String var1);

    public List<Form> findBySubjectsInitials(String var1);

    public Boolean existsByFormAndStudentsAdmNo(int var1, String var2);

    public Form findByFormAndStudentsAdmNo(int var1, String var2);

    public Form findByForm(int var1);

    public List<Form> findByExamNamesName(String var1);

    public Boolean existsByFormAndYearsYearAndYearsSchoolsCode(int var1, int var2, int var3);

    public List<Form> findByYearsYear(int var1);
}