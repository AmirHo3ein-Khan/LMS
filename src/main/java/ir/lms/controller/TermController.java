package ir.lms.controller;

import ir.lms.model.Term;
import ir.lms.service.TermService;
import ir.lms.util.dto.*;
import ir.lms.util.dto.mapper.CalenderMapper;
import ir.lms.util.dto.mapper.TermMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/term")
public class TermController {

    private final TermService termService;
    private final TermMapper termMapper;
    private final CalenderMapper calenderMapper;

    public TermController(TermService termService, TermMapper termMapper, CalenderMapper calenderMapper) {
        this.calenderMapper = calenderMapper;
        this.termService = termService;
        this.termMapper = termMapper;
    }

    private static final String ADMIN_OR_MANAGER = "hasAnyRole('ADMIN' , 'MANAGER')";
    private static final String ALL_AUTHENTICATED = "hasAnyRole('ADMIN','MANAGER','STUDENT','TEACHER')";


    @PreAuthorize(ADMIN_OR_MANAGER)
    @PostMapping
    public ResponseEntity<ApiResponse<TermDTO>> save(@Valid @RequestBody TermDTO termDTO) {
        Term persist = termService.persist(termMapper.toEntity(termDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<TermDTO>builder()
                        .success(true)
                        .message("term.creation.success")
                        .data(termMapper.toDto(persist))
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }

    @PreAuthorize(ADMIN_OR_MANAGER)
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TermDTO>> update(@PathVariable Long id, @Valid @RequestBody TermDTO dto) {
        Term updated = termService.update(id, termMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<TermDTO>builder()
                        .success(true)
                        .message("course.creation.success")
                        .data(termMapper.toDto(updated))
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }

    @PreAuthorize(ADMIN_OR_MANAGER)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO> delete(@PathVariable Long id) {
        termService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDTO("Term deleted success.", true));
    }

    @PreAuthorize(ADMIN_OR_MANAGER)
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TermDTO>> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<TermDTO>builder()
                        .success(true)
                        .message("course.creation.success")
                        .data(termMapper.toDto(termService.findById(id)))
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }

    @PreAuthorize(ADMIN_OR_MANAGER)
    @GetMapping
    public ResponseEntity<ApiResponse<List<TermDTO>>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<List<TermDTO>>builder()
                        .success(true)
                        .message("courses.get.success")
                        .data(termService.findAll().stream()
                                .map(termMapper::toDto)
                                .toList())
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }

    @PreAuthorize(ALL_AUTHENTICATED)
    @GetMapping("/academic-calender/{termId}")
    public ResponseEntity<ApiResponse<AcademicCalenderDTO>> findTermCalender(@PathVariable Long termId) {
        termService.findTermCalenderByTermId(termId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<AcademicCalenderDTO>builder()
                        .success(true)
                        .message("calender.get.success")
                        .data(calenderMapper.toDto(termService.findTermCalenderByTermId(termId)))
                        .build()

        );
    }
}
