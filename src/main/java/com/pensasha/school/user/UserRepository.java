package com.pensasha.school.user;

import com.pensasha.school.user.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    public List<User> findByRoleName(String var1);
}