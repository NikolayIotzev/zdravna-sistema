package com.nbu.medicalrecord.service;

import com.nbu.medicalrecord.dto.SpecialtyDto;

import java.util.List;

public interface SpecialtyService {

    SpecialtyDto.Response create(SpecialtyDto.Request request);

    SpecialtyDto.Response getById(Long id);

    List<SpecialtyDto.Response> getAll();

    SpecialtyDto.Response update(Long id, SpecialtyDto.Request request);

    void delete(Long id);
}
