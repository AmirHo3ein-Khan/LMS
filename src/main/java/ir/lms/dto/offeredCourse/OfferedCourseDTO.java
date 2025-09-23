package ir.lms.dto.offeredCourse;

import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OfferedCourseDTO {
    private Instant startTime;
    private Instant endTime;
    private Integer capacity;
    private String classLocation;
    private Long courseId;
    private Long termId;
    private Long teacherId;
}
