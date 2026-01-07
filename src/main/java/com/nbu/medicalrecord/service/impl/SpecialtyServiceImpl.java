package com.nbu.medicalrecord.service.impl;

import com.nbu.medicalrecord.dto.SpecialtyDto;
import com.nbu.medicalrecord.entity.Specialty;
import com.nbu.medicalrecord.exception.DuplicateResourceException;
import com.nbu.medicalrecord.exception.ResourceNotFoundException;
import com.nbu.medicalrecord.repository.SpecialtyRepository;
import com.nbu.medicalrecord.service.SpecialtyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;

    @Override
    public SpecialtyDto.Response create(SpecialtyDto.Request request) {
        if (specialtyRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Специалност с име '" + request.getName() + "' вече съществува");
        }

        Specialty specialty = Specialty.builder()
                .name(request.getName())
                .build();

        return toResponse(specialtyRepository.save(specialty));
    }

    @Override
    @Transactional(readOnly = true)
    public SpecialtyDto.Response getById(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecialtyDto.Response> getAll() {
        return specialtyRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SpecialtyDto.Response update(Long id, SpecialtyDto.Request request) {
        Specialty specialty = findById(id);

        if (!specialty.getName().equals(request.getName()) &&
                specialtyRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Специалност с име '" + request.getName() + "' вече съществува");
        }

        specialty.setName(request.getName());
        return toResponse(specialtyRepository.save(specialty));
    }

    @Override
    public void delete(Long id) {
        Specialty specialty = findById(id);
        specialtyRepository.delete(specialty);
    }

    private Specialty findById(Long id) {
        return specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Специалност с ID " + id + " не е намерена"));
    }

    private SpecialtyDto.Response toResponse(Specialty specialty) {
        return SpecialtyDto.Response.builder()
                .id(specialty.getId())
                .name(specialty.getName())
                .build();
    }
}
