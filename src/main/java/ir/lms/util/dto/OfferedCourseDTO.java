package ir.lms.util.dto;

import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OfferedCourseDTO {
    private DayOfWeek dayOfWeek;
    private LocalTime classStartTime;
    private LocalTime classEndTime;
    private Integer capacity;
    private String classLocation;
    private Long courseId;
    private Long termId;
    private Long teacherId;
}
