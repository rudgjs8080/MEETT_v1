package com.team.meett.service;

import com.team.meett.dto.UserRequestDto;
import com.team.meett.model.Users;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface UserService extends UserDetailsService {

    public Users findById(String username);
    public boolean existsById(String username);

    public void insert(UserRequestDto user);
    public void update(Users user);
    public void delete(String username);

}
