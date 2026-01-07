package com.nbu.medicalrecord.controller;

import com.nbu.medicalrecord.dto.*;
import com.nbu.medicalrecord.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;

import com.nbu.medicalrecord.entity.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final DoctorService doctorService;
    private final PatientService patientService;
    private final DiagnosisService diagnosisService;
    private final ExaminationService examinationService;
    private final SickLeaveService sickLeaveService;
    private final SpecialtyService specialtyService;
    private final UserService userService;

    // Home - redirect based on authentication and role
    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            boolean isAdminOrDoctor = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_DOCTOR"));
            if (isAdminOrDoctor) {
                return "redirect:/patients";
            }
            return "redirect:/examinations";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Doctors
    @GetMapping("/doctors")
    public String doctors(Model model) {
        model.addAttribute("doctors", doctorService.getAll());
        return "doctors/list";
    }

    @GetMapping("/doctors/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String newDoctorForm(Model model) {
        model.addAttribute("doctor", new DoctorForm());
        model.addAttribute("specialties", specialtyService.getAll());
        return "doctors/form";
    }

    @PostMapping("/doctors/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String createDoctor(@ModelAttribute DoctorForm form, RedirectAttributes redirectAttributes) {
        DoctorDto.Request doctorRequest = DoctorDto.Request.builder()
                .uin(form.getUin())
                .name(form.getName())
                .isGp(form.isGp())
                .specialtyIds(form.getSpecialtyIds())
                .build();

        UserDto.RegisterRequest userRequest = UserDto.RegisterRequest.builder()
                .username(form.getUsername())
                .password(form.getPassword())
                .role(User.Role.DOCTOR)
                .doctor(doctorRequest)
                .build();

        userService.register(userRequest);
        redirectAttributes.addFlashAttribute("success", "Лекарят е добавен успешно");
        return "redirect:/doctors";
    }

    @GetMapping("/doctors/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editDoctorForm(@PathVariable Long id, Model model) {
        DoctorDto.Response doctor = doctorService.getById(id);
        DoctorForm form = new DoctorForm();
        form.setId(doctor.getId());
        form.setUin(doctor.getUin());
        form.setName(doctor.getName());
        form.setGp(doctor.isGp());
        form.setSpecialtyIds(doctor.getSpecialties().stream()
                .map(SpecialtyDto.Response::getId)
                .collect(Collectors.toSet()));
        model.addAttribute("doctor", form);
        model.addAttribute("specialties", specialtyService.getAll());
        model.addAttribute("selectedSpecialties", form.getSpecialtyIds());
        return "doctors/form";
    }

    @PostMapping("/doctors/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateDoctor(@PathVariable Long id, @ModelAttribute DoctorForm form, RedirectAttributes redirectAttributes) {
        DoctorDto.Request request = DoctorDto.Request.builder()
                .uin(form.getUin())
                .name(form.getName())
                .isGp(form.isGp())
                .specialtyIds(form.getSpecialtyIds())
                .build();
        doctorService.update(id, request);
        redirectAttributes.addFlashAttribute("success", "Лекарят е обновен успешно");
        return "redirect:/doctors";
    }

    @GetMapping("/doctors/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteDoctor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        doctorService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Лекарят е изтрит успешно");
        return "redirect:/doctors";
    }

    // Patients
    @GetMapping("/patients")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String patients(Model model) {
        model.addAttribute("patients", patientService.getAll());
        return "patients/list";
    }

    @GetMapping("/patients/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String newPatientForm(Model model) {
        model.addAttribute("patient", new PatientForm());
        model.addAttribute("gps", doctorService.getAllGps());
        return "patients/form";
    }

    @PostMapping("/patients/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String createPatient(@ModelAttribute PatientForm form, RedirectAttributes redirectAttributes) {
        PatientDto.Request patientRequest = PatientDto.Request.builder()
                .name(form.getName())
                .egn(form.getEgn())
                .gpId(form.getGpId())
                .lastInsurancePayment(form.getLastInsurancePayment())
                .build();

        UserDto.RegisterRequest userRequest = UserDto.RegisterRequest.builder()
                .username(form.getUsername())
                .password(form.getPassword())
                .role(User.Role.PATIENT)
                .patient(patientRequest)
                .build();

        userService.register(userRequest);
        redirectAttributes.addFlashAttribute("success", "Пациентът е добавен успешно");
        return "redirect:/patients";
    }

    @GetMapping("/patients/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editPatientForm(@PathVariable Long id, Model model) {
        PatientDto.Response patient = patientService.getById(id);
        PatientForm form = new PatientForm();
        form.setId(patient.getId());
        form.setName(patient.getName());
        form.setEgn(patient.getEgn());
        form.setGpId(patient.getGp() != null ? patient.getGp().getId() : null);
        form.setLastInsurancePayment(patient.getLastInsurancePayment());
        model.addAttribute("patient", form);
        model.addAttribute("gps", doctorService.getAllGps());
        return "patients/form";
    }

    @PostMapping("/patients/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updatePatient(@PathVariable Long id, @ModelAttribute PatientForm form, RedirectAttributes redirectAttributes) {
        PatientDto.Request request = PatientDto.Request.builder()
                .name(form.getName())
                .egn(form.getEgn())
                .gpId(form.getGpId())
                .lastInsurancePayment(form.getLastInsurancePayment())
                .build();
        patientService.update(id, request);
        redirectAttributes.addFlashAttribute("success", "Пациентът е обновен успешно");
        return "redirect:/patients";
    }

    @GetMapping("/patients/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deletePatient(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        patientService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Пациентът е изтрит успешно");
        return "redirect:/patients";
    }

    @GetMapping("/patients/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String viewPatient(@PathVariable Long id, Model model) {
        model.addAttribute("patient", patientService.getById(id));
        model.addAttribute("examinations", examinationService.getByPatientId(id));
        return "patients/view";
    }

    // Diagnoses
    @GetMapping("/diagnoses")
    public String diagnoses(Model model) {
        model.addAttribute("diagnoses", diagnosisService.getAll());
        return "diagnoses/list";
    }

    @GetMapping("/diagnoses/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String newDiagnosisForm(Model model) {
        model.addAttribute("diagnosis", new DiagnosisForm());
        return "diagnoses/form";
    }

    @PostMapping("/diagnoses/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String createDiagnosis(@ModelAttribute DiagnosisForm form, RedirectAttributes redirectAttributes) {
        DiagnosisDto.Request request = DiagnosisDto.Request.builder()
                .code(form.getCode())
                .name(form.getName())
                .description(form.getDescription())
                .build();
        diagnosisService.create(request);
        redirectAttributes.addFlashAttribute("success", "Диагнозата е добавена успешно");
        return "redirect:/diagnoses";
    }

    @GetMapping("/diagnoses/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editDiagnosisForm(@PathVariable Long id, Model model) {
        DiagnosisDto.Response diagnosis = diagnosisService.getById(id);
        DiagnosisForm form = new DiagnosisForm();
        form.setId(diagnosis.getId());
        form.setCode(diagnosis.getCode());
        form.setName(diagnosis.getName());
        form.setDescription(diagnosis.getDescription());
        model.addAttribute("diagnosis", form);
        return "diagnoses/form";
    }

    @PostMapping("/diagnoses/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateDiagnosis(@PathVariable Long id, @ModelAttribute DiagnosisForm form, RedirectAttributes redirectAttributes) {
        DiagnosisDto.Request request = DiagnosisDto.Request.builder()
                .code(form.getCode())
                .name(form.getName())
                .description(form.getDescription())
                .build();
        diagnosisService.update(id, request);
        redirectAttributes.addFlashAttribute("success", "Диагнозата е обновена успешно");
        return "redirect:/diagnoses";
    }

    @GetMapping("/diagnoses/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteDiagnosis(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        diagnosisService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Диагнозата е изтрита успешно");
        return "redirect:/diagnoses";
    }

    // Examinations
    @GetMapping("/examinations")
    @PreAuthorize("isAuthenticated()")
    public String examinations(Model model, Authentication authentication) {
        boolean isPatient = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT"));

        if (isPatient) {
            UserDto.Response user = userService.getByUsername(authentication.getName());
            PatientDto.Response patient = patientService.getByUserId(user.getId());
            if (patient != null) {
                model.addAttribute("examinations", examinationService.getByPatientId(patient.getId()));
            } else {
                model.addAttribute("examinations", List.of());
            }
        } else {
            model.addAttribute("examinations", examinationService.getAll());
        }
        return "examinations/list";
    }

    @GetMapping("/examinations/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String newExaminationForm(Model model) {
        model.addAttribute("examination", new ExaminationForm());
        model.addAttribute("patients", patientService.getAll());
        model.addAttribute("doctors", doctorService.getAll());
        model.addAttribute("diagnoses", diagnosisService.getAll());
        return "examinations/form";
    }

    @PostMapping("/examinations/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String createExamination(@ModelAttribute ExaminationForm form,
                                     @RequestParam(required = false) LocalDate sickLeaveStartDate,
                                     @RequestParam(required = false) Integer sickLeaveNumberOfDays,
                                     RedirectAttributes redirectAttributes) {
        SickLeaveDto.Request sickLeave = null;
        if (sickLeaveStartDate != null && sickLeaveNumberOfDays != null && sickLeaveNumberOfDays > 0) {
            sickLeave = SickLeaveDto.Request.builder()
                    .startDate(sickLeaveStartDate)
                    .numberOfDays(sickLeaveNumberOfDays)
                    .build();
        }

        ExaminationDto.Request request = ExaminationDto.Request.builder()
                .examinationDate(form.getExaminationDate())
                .patientId(form.getPatientId())
                .doctorId(form.getDoctorId())
                .diagnosisId(form.getDiagnosisId())
                .treatment(form.getTreatment())
                .prescription(form.getPrescription())
                .sickLeave(sickLeave)
                .build();
        examinationService.create(request);
        redirectAttributes.addFlashAttribute("success", "Прегледът е добавен успешно");
        return "redirect:/examinations";
    }

    @GetMapping("/examinations/edit/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String editExaminationForm(@PathVariable Long id, Model model) {
        ExaminationDto.Response exam = examinationService.getById(id);
        ExaminationForm form = new ExaminationForm();
        form.setId(exam.getId());
        form.setExaminationDate(exam.getExaminationDate());
        form.setPatientId(exam.getPatient().getId());
        form.setDoctorId(exam.getDoctor().getId());
        form.setDiagnosisId(exam.getDiagnosis() != null ? exam.getDiagnosis().getId() : null);
        form.setTreatment(exam.getTreatment());
        form.setPrescription(exam.getPrescription());

        model.addAttribute("examination", form);
        model.addAttribute("patients", patientService.getAll());
        model.addAttribute("doctors", doctorService.getAll());
        model.addAttribute("diagnoses", diagnosisService.getAll());

        if (exam.getSickLeave() != null) {
            model.addAttribute("sickLeaveStartDate", exam.getSickLeave().getStartDate());
            model.addAttribute("sickLeaveNumberOfDays", exam.getSickLeave().getNumberOfDays());
        }

        return "examinations/form";
    }

    @PostMapping("/examinations/edit/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String updateExamination(@PathVariable Long id,
                                     @ModelAttribute ExaminationForm form,
                                     @RequestParam(required = false) LocalDate sickLeaveStartDate,
                                     @RequestParam(required = false) Integer sickLeaveNumberOfDays,
                                     RedirectAttributes redirectAttributes) {
        SickLeaveDto.Request sickLeave = null;
        if (sickLeaveStartDate != null && sickLeaveNumberOfDays != null && sickLeaveNumberOfDays > 0) {
            sickLeave = SickLeaveDto.Request.builder()
                    .startDate(sickLeaveStartDate)
                    .numberOfDays(sickLeaveNumberOfDays)
                    .build();
        }

        ExaminationDto.Request request = ExaminationDto.Request.builder()
                .examinationDate(form.getExaminationDate())
                .patientId(form.getPatientId())
                .doctorId(form.getDoctorId())
                .diagnosisId(form.getDiagnosisId())
                .treatment(form.getTreatment())
                .prescription(form.getPrescription())
                .sickLeave(sickLeave)
                .build();
        examinationService.update(id, request);
        redirectAttributes.addFlashAttribute("success", "Прегледът е обновен успешно");
        return "redirect:/examinations";
    }

    @GetMapping("/examinations/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteExamination(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        examinationService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Прегледът е изтрит успешно");
        return "redirect:/examinations";
    }

    @GetMapping("/examinations/{id}")
    @PreAuthorize("isAuthenticated()")
    public String viewExamination(@PathVariable Long id, Model model) {
        model.addAttribute("examination", examinationService.getById(id));
        return "examinations/view";
    }

    // Reports
    @GetMapping("/reports/patients-by-diagnosis")
    @PreAuthorize("isAuthenticated()")
    public String patientsByDiagnosis(Model model, @RequestParam(required = false) Long diagnosisId) {
        model.addAttribute("diagnoses", diagnosisService.getAll());
        if (diagnosisId != null) {
            model.addAttribute("patients", patientService.getPatientsByDiagnosis(diagnosisId));
            model.addAttribute("selectedDiagnosis", diagnosisId);
        }
        return "reports/patients-by-diagnosis";
    }

    @GetMapping("/reports/frequent-diagnoses")
    @PreAuthorize("isAuthenticated()")
    public String frequentDiagnoses(Model model) {
        model.addAttribute("frequencies", diagnosisService.getMostFrequentDiagnoses());
        return "reports/frequent-diagnoses";
    }

    @GetMapping("/reports/patients-per-gp")
    @PreAuthorize("isAuthenticated()")
    public String patientsPerGp(Model model) {
        model.addAttribute("counts", doctorService.getPatientCountPerGp());
        return "reports/patients-per-gp";
    }

    @GetMapping("/reports/examinations-per-doctor")
    @PreAuthorize("isAuthenticated()")
    public String examinationsPerDoctor(Model model) {
        model.addAttribute("counts", doctorService.getExaminationCountPerDoctor());
        return "reports/examinations-per-doctor";
    }

    @GetMapping("/reports/sick-leaves")
    @PreAuthorize("isAuthenticated()")
    public String sickLeaves(Model model) {
        model.addAttribute("monthCounts", sickLeaveService.getMonthsWithMostSickLeaves());
        model.addAttribute("doctorCounts", doctorService.getDoctorsWithMostSickLeaves());
        return "reports/sick-leaves";
    }

    @GetMapping("/reports/examinations-by-period")
    @PreAuthorize("isAuthenticated()")
    public String examinationsByPeriod(Model model,
                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if (startDate != null && endDate != null) {
            model.addAttribute("examinations", examinationService.getAllInPeriod(startDate, endDate));
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
        }
        return "reports/examinations-by-period";
    }

    // Form classes for Thymeleaf binding
    @lombok.Data
    public static class DoctorForm {
        private Long id;
        private String uin;
        private String name;
        private boolean gp;
        private Set<Long> specialtyIds = new HashSet<>();
        private String username;
        private String password;
    }

    @lombok.Data
    public static class PatientForm {
        private Long id;
        private String name;
        private String egn;
        private Long gpId;
        private LocalDate lastInsurancePayment;
        private String username;
        private String password;
    }

    @lombok.Data
    public static class DiagnosisForm {
        private Long id;
        private String code;
        private String name;
        private String description;
    }

    @lombok.Data
    public static class ExaminationForm {
        private Long id;
        private LocalDate examinationDate;
        private Long patientId;
        private Long doctorId;
        private Long diagnosisId;
        private String treatment;
        private String prescription;
    }
}
