package com.pensasha.school.stream;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StreamRespository extends JpaRepository<Stream, Integer>{

	public List<Stream> findBySchoolCode(int code);
	
	public boolean existsByIdAndSchoolCode(int id, int code);
}
