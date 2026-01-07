package com.nbu.medicalrecord.service;

import com.nbu.medicalrecord.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto.Response register(UserDto.RegisterRequest request);

    UserDto.Response getById(Long id);

    UserDto.Response getByUsername(String username);

    List<UserDto.Response> getAll();

    void delete(Long id);
}
