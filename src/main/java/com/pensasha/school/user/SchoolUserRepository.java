package com.pensasha.school.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchoolUserRepository extends JpaRepository<SchoolUser, String> {
    List<SchoolUser> findBySchoolCode(int var1);

    SchoolUser findByUsername(String var1);
}