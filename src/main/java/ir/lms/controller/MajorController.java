package ir.lms.controller;

import io.swagger.v3.oas.annotations.Operation;
import ir.lms.util.dto.*;
import ir.lms.util.dto.mapper.MajorMapper;
import ir.lms.model.Major;
import ir.lms.service.MajorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/major")
public class MajorController {

    private final MajorService majorService;
    private final MajorMapper majorMapper;

    public MajorController(MajorService majorService, MajorMapper majorMapper) {
        this.majorService = majorService;
        this.majorMapper = majorMapper;
    }

    private static final String ADMIN = "hasRole('ADMIN')";

    @PreAuthorize(ADMIN)
    @PostMapping
    public ResponseEntity<ApiResponse<MajorDTO>> save(@Valid @RequestBody MajorDTO dto) {
        Major major = majorService.persist(majorMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<MajorDTO>builder()
                        .success(true)
                        .message("major.creation.success")
                        .data(majorMapper.toDto(major))
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }

    @PreAuthorize(ADMIN)
    @PutMapping("/{majorId}")
    public ResponseEntity<ApiResponse<MajorDTO>> update(@PathVariable Long majorId,@Valid @RequestBody MajorDTO dto) {
        Major updated = majorService.update(majorId, majorMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<MajorDTO>builder()
                        .success(true)
                        .message("major.update.success")
                        .data(majorMapper.toDto(updated))
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }

    @PreAuthorize(ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO> delete(@PathVariable Long id) {
        majorService.delete(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseDTO("Major deleted success.", true));
    }

    @PreAuthorize(ADMIN)
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MajorDTO>> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<MajorDTO>builder()
                        .success(true)
                        .message("major.get.success")
                        .data(majorMapper.toDto(majorService.findById(id)))
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }

    @PreAuthorize(ADMIN)
    @GetMapping
    public ResponseEntity<ApiResponse<List<MajorDTO>>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<List<MajorDTO>>builder()
                        .success(true)
                        .message("majors.get.success")
                        .data(majorService.findAll().stream()
                                .map(majorMapper::toDto)
                                .toList())
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }
}
