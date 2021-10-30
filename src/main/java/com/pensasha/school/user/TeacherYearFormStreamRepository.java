package com.pensasha.school.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherYearFormStreamRepository extends JpaRepository<TeacherYearFormStream, Long>{

	List<TeacherYearFormStream> findByTeacherSchoolCodeAndYearYearAndFormFormAndStreamId(int code, int year, int form,
			int stream);

    Boolean existsByYearYearAndFormFormAndStreamIdAndSubjectInitials(int year, int form, int stream, String subject);
}
