package com.pensasha.school.timetable;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TimetableRepository extends JpaRepository<Timetable, Integer>{

	public List<Timetable> findBySchoolCodeAndYearYearAndFormFormAndTermTermAndStreamStream(int code, int year, int form, int term, String stream);
}
