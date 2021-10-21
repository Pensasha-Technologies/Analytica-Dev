package com.pensasha.school.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherYearFormStreamRepository extends JpaRepository<TeacherYearFormStream, Long>{

	List<TeacherYearFormStream> findByTeacherSchoolCodeAndYearYearAndFormFormAndStreamId(int code, int year, int form,
			int stream);

}
