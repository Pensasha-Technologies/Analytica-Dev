package com.pensasha.school.user;

import com.pensasha.school.user.SchoolUser;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolUserRepository extends JpaRepository<SchoolUser, String> {
    public List<SchoolUser> findBySchoolCode(int var1);

    public SchoolUser findByUsername(String var1);
}