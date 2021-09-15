package com.pensasha.school.user;

import com.pensasha.school.user.Teacher;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, String> {
    public Teacher findByUsername(String var1);

    public List<Teacher> findBySubjectsInitials(String var1);

    public List<Teacher> findBySchoolCodeAndYearsYear(int var1, int var2);

    public List<Teacher> findBySchoolCodeAndYearsYearAndFormsFormAndStreamsId(int var1, int var2, int var3, int var4);

    public List<Teacher> findBySchoolCode(int var1);
}