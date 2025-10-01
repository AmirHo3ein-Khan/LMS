package ir.lms.controller;

import ir.lms.dto.ApiResponseDTO;
import ir.lms.dto.major.MajorDTO;
import ir.lms.mapper.MajorMapper;
import ir.lms.model.Major;
import ir.lms.service.MajorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MajorDTO> save(@RequestBody MajorDTO dto) {
        Major major = majorService.persist(majorMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(majorMapper.toDto(major));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{majorId}")
    public ResponseEntity<MajorDTO> update(@PathVariable Long majorId, @RequestBody MajorDTO dto) {
        Major foundedMajor = majorService.findById(majorId);
        foundedMajor.setMajorName(dto.getMajorName());
        Major major = majorService.persist(foundedMajor);
        return ResponseEntity.ok(majorMapper.toDto(major));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO> delete(@PathVariable Long id) {
        majorService.delete(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseDTO("Major deleted success.", true));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<MajorDTO> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(majorMapper.toDto(majorService.findById(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<MajorDTO>> findAll() {
        List<MajorDTO> majors = new ArrayList<>();
        for (Major m : majorService.findAll()) {
            majors.add(majorMapper.toDto(m));
        }
        return ResponseEntity.status(HttpStatus.OK).body(majors);
    }
}
