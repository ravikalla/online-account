package com.synechron.onlineacc.dao;

import org.springframework.data.repository.CrudRepository;

import com.synechron.onlineacc.domain.security.Role;

public interface RoleDao extends CrudRepository<Role, Integer> {
    
    Role findByName(String name);
}
