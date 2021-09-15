package com.pensasha.school.timetable;

import com.pensasha.school.timetable.Timetable;
import com.pensasha.school.timetable.TimetableRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TimetableService {
    @Autowired
    private TimetableRepository timetableRepository;

    public List<Timetable> getTimetableBySchoolYearFormStream(int code, int year, int form, int term, int stream) {
        return this.timetableRepository.findBySchoolCodeAndYearYearAndFormFormAndTermTermAndStreamId(code, year, form, term, stream);
    }

    public List<Timetable> getALlTimetableItemsInSchoolByCode(int code) {
        return this.timetableRepository.findBySchoolCode(code);
    }

    public Timetable saveTimetableItem(Timetable timetable) {
        return (Timetable)this.timetableRepository.save(timetable);
    }

    public void deleteTimetableItem(int id) {
        this.timetableRepository.deleteById(id);
    }
}