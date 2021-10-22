package com.pensasha.school.timetable;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimetableRepository extends JpaRepository<Timetable, Integer> {
    List<Timetable> findBySchoolCodeAndYearYearAndFormFormAndTermTermAndStreamId(int var1, int var2, int var3, int var4, int var5);

    List<Timetable> findBySchoolCode(int var1);
}