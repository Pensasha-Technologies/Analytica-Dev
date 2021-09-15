package com.pensasha.school.exam;

import com.pensasha.school.exam.Mark;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarkRepository extends JpaRepository<Mark, Integer> {
    
    public List<Mark> findByStudentAdmNoAndYearYearAndFormFormAndTermTermAndSubjectInitials(String var1, int var2, int var3, int var4, String var5);

    public Mark findByStudentAdmNoAndYearYearAndFormFormAndTermTerm(String var1, int var2, int var3, int var4);

    public Boolean existsByStudentAdmNo(String var1);

    public List<Mark> findByStudentAdmNo(String var1);

    public List<Mark> findByYearYearAndFormFormAndTermTermAndSubjectInitials(int var1, int var2, int var3, String var4);

    public List<Mark> findByStudentAdmNoAndFormFormAndTermTerm(String var1, int var2, int var3);

    public List<Mark> findByStudentSchoolCodeAndYearYearAndFormFormAndTermTerm(int var1, int var2, int var3, int var4);

    public Mark findByStudentAdmNoAndYearYearAndFormFormAndTermTermAndSubjectInitialsAndExamNameId(String var1, int var2, int var3, int var4, String var5, int var6);

    public List<Mark> findByStudentSchoolCodeAndYearYearAndFormFormAndTermTermAndExamNameName(int var1, int var2, int var3, int var4, String var5);

}