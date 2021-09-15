package com.pensasha.school.subject;

import com.pensasha.school.subject.Subject;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, String> {
    public List<Subject> findBySchoolsCode(int var1);

    public Subject findByInitialsAndSchoolsCode(String var1, int var2);

    public List<Subject> findByStudentsAdmNo(String var1);

    public Subject findByInitialsAndStudentsAdmNo(String var1, String var2);

    public List<Subject> findByFormsFormAndStudentsAdmNo(int var1, String var2);

    public List<Subject> findByYearsYearAndFormsFormAndStudentsAdmNo(int var1, int var2, String var3);

    public Boolean existsByInitialsAndSchoolsCode(String var1, int var2);

    public Subject findByName(String var1);
}