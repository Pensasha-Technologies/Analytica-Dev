package com.pensasha.school.timetable;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TimetableService {

	@Autowired
	private TimetableRepository timetableRepository;
	
	public List<Timetable> getTimetableBySchoolYearFormStream(int code, int year, int form, int term, String stream){
		
		return timetableRepository.findBySchoolCodeAndYearYearAndFormFormAndTermTermAndStreamStream(code, year, form, term, stream);
	}
	
	
}
