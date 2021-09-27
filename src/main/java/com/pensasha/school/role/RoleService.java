package com.pensasha.school.role;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public List<Role> getAllRoles() {
        ArrayList<Role> roles = new ArrayList<Role>();
        this.roleRepository.findAll().forEach(roles::add);
        return roles;
    }

    public Role getRole(String role) {
        return (Role)this.roleRepository.findById(role).get();
    }

    public Role addRole(Role role) {
        return this.roleRepository.save(role);
    }

    public Role updateRole(Role role) {
        return this.roleRepository.save(role);
    }

    public void deleteRole(String role) {
        this.roleRepository.deleteById(role);
    }
}