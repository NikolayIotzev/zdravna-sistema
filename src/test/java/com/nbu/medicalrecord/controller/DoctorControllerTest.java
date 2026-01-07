package com.nbu.medicalrecord.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbu.medicalrecord.dto.DoctorDto;
import com.nbu.medicalrecord.service.DoctorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DoctorService doctorService;

    private DoctorDto.Response doctorResponse;
    private DoctorDto.Request doctorRequest;

    @BeforeEach
    void setUp() {
        doctorResponse = DoctorDto.Response.builder()
                .id(1L)
                .uin("1234567890")
                .name("Д-р Иван Петров")
                .isGp(true)
                .specialties(new HashSet<>())
                .patientCount(5)
                .build();

        doctorRequest = DoctorDto.Request.builder()
                .uin("1234567890")
                .name("Д-р Иван Петров")
                .isGp(true)
                .build();
    }

    @Test
    void getAll_ShouldReturnDoctorsList() throws Exception {
        when(doctorService.getAll()).thenReturn(Collections.singletonList(doctorResponse));

        mockMvc.perform(get("/api/doctors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Д-р Иван Петров"));
    }

    @Test
    void getById_ShouldReturnDoctor() throws Exception {
        when(doctorService.getById(1L)).thenReturn(doctorResponse);

        mockMvc.perform(get("/api/doctors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.uin").value("1234567890"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_WithAdminRole_ShouldCreateDoctor() throws Exception {
        when(doctorService.create(any(DoctorDto.Request.class))).thenReturn(doctorResponse);

        mockMvc.perform(post("/api/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doctorRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void create_WithPatientRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doctorRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_WithAdminRole_ShouldDeleteDoctor() throws Exception {
        mockMvc.perform(delete("/api/doctors/1"))
                .andExpect(status().isNoContent());
    }
}
