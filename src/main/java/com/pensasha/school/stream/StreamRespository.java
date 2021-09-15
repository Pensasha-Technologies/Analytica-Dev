package com.pensasha.school.stream;

import com.pensasha.school.stream.Stream;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StreamRespository extends JpaRepository<Stream, Integer> {
    public List<Stream> findBySchoolCode(int var1);

    public boolean existsByIdAndSchoolCode(int var1, int var2);

    public Stream findByStream(String var1);
}