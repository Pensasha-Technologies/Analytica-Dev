package com.pensasha.school.discipline;

import com.pensasha.school.discipline.Discipline;
import com.pensasha.school.discipline.DisciplineRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DisciplineService {
   
    @Autowired
    private DisciplineRepository disciplineRepository;

    public List<Discipline> allDisciplineReportForStudent(String admNo) {
        return this.disciplineRepository.findByStudentAdmNo(admNo);
    }

    public List<Discipline> allDisciplineReportByStream(String stream) {
        return this.disciplineRepository.findByStudentStreamStream(stream);
    }

    public List<Discipline> allDisciplineReportByClass(String form) {
        return this.disciplineRepository.findByStudentFormsForm(form);
    }

    public List<Discipline> allDisciplineReportBySchoolCode(int code) {
        return this.disciplineRepository.findByStudentSchoolCode(code);
    }

    public Optional<Discipline> getDisciplineReportById(int id) {
        return this.disciplineRepository.findById(id);
    }

    public Discipline saveDisciplineReport(Discipline discipline) {
        return (Discipline)this.disciplineRepository.save(discipline);
    }

    public Discipline updateDisciplineReport(Discipline discipline) {
        return (Discipline)this.disciplineRepository.save(discipline);
    }

    public void deleteDisciplineReport(int id) {
        this.disciplineRepository.deleteById(id);
    }
}