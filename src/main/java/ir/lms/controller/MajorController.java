package ir.lms.controller;

import ir.lms.service.MajorService;
import ir.lms.util.dto.major.MajorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/major")
public class MajorController {
    private final MajorService majorService;

    public MajorController(MajorService majorService) {
        this.majorService = majorService;
    }

    @PostMapping
    public ResponseEntity<MajorDTO> createMajor(@RequestBody MajorDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(majorService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MajorDTO> updateMajor(@PathVariable Long id, @RequestBody MajorDTO dto) {
        return ResponseEntity.ok(majorService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMajor(@PathVariable Long id) {
        majorService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MajorDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(majorService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<MajorDTO>> findAllMajor() {
        return ResponseEntity.ok(majorService.findAll());
    }

}
