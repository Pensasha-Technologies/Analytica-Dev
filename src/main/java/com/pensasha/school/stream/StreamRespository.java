package com.pensasha.school.stream;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StreamRespository extends JpaRepository<Stream, Integer> {
    List<Stream> findBySchoolCode(int var1);

    boolean existsByIdAndSchoolCode(int var1, int var2);

    Stream findByStream(String var1);

	List<Stream> findByTeachersUsername(String username);
}