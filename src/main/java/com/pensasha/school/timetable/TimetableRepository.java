package com.pensasha.school.timetable;

import com.pensasha.school.timetable.Timetable;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimetableRepository extends JpaRepository<Timetable, Integer> {
    public List<Timetable> findBySchoolCodeAndYearYearAndFormFormAndTermTermAndStreamId(int var1, int var2, int var3, int var4, int var5);

    public List<Timetable> findBySchoolCode(int var1);
}