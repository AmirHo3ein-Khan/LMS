package ir.lms.controller;

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
    public ResponseEntity<MajorDTO> save(@Valid @RequestBody MajorDTO dto) {
        Major major = majorService.persist(majorMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(majorMapper.toDto(major));
    }

    @PreAuthorize(ADMIN)
    @PutMapping("/{majorId}")
    public ResponseEntity<MajorDTO> update(@PathVariable Long majorId, @Valid @RequestBody MajorDTO dto) {
        Major updated = majorService.update(majorId, majorMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.OK).body(majorMapper.toDto(updated));
    }

    @PreAuthorize(ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        majorService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize(ADMIN)
    @GetMapping("/{id}")
    public ResponseEntity<MajorDTO> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(majorMapper.toDto(majorService.findById(id)));
    }

    @GetMapping
    public ResponseEntity<List<MajorDTO>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(majorService.findAll().stream()
                .map(majorMapper::toDto)
                .toList());
    }
}
