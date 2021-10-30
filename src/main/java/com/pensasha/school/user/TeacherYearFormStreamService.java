package com.pensasha.school.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherYearFormStreamService {

	@Autowired 
	private TeacherYearFormStreamRepository teacherYearFormStreamRepository; 
	
	// Saving Teacher in year, form, stream for a particular subject
	public TeacherYearFormStream addTeacherTeachingSubjectToStream(TeacherYearFormStream teacher) {
		return this.teacherYearFormStreamRepository.save(teacher);
	}

	public void deleteTeacherTeachingSubject(TeacherYearFormStream teacher){
		this.teacherYearFormStreamRepository.deleteById(teacher.getId());
	}
	
	//Getting all teacher teaching a subject in a year, form and stream
	public List<TeacherYearFormStream> getAllTeachersTeachingInYearFormAndStream(int code, int year, int form, int stream){
		return this.teacherYearFormStreamRepository.findByTeacherSchoolCodeAndYearYearAndFormFormAndStreamId(code, year, form, stream);
	}

	//Checkking is there is a teacher teaching in a particular stream
	public Boolean isThereATeacherInStream(int year, int form, int stream, String subject){
		return this.teacherYearFormStreamRepository.existsByYearYearAndFormFormAndStreamIdAndSubjectInitials(year, form, stream, subject);
	}
}
