package ir.lms.util.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OfferedCourseDTO {
    @NotNull(message = "Day of week is required")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Class start time is required")
    private LocalTime classStartTime;

    @NotNull(message = "Class end time is required")
    private LocalTime classEndTime;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    @NotBlank(message = "Class location cannot be empty")
    @Size(max = 50, message = "Class location should not exceed 50 characters")
    private String classLocation;

    @NotNull(message = "Course ID is required")
    @Min(value = 1, message = "Course ID must be positive")
    private Long courseId;

    @NotNull(message = "Term ID is required")
    @Min(value = 1, message = "Term ID must be positive")
    private Long termId;

    @NotNull(message = "Teacher ID is required")
    @Min(value = 1, message = "Teacher ID must be positive")
    private Long teacherId;

    @AssertTrue(message = "Class end time must be after class start time")
    public boolean isEndTimeAfterStartTime() {
        if (classStartTime == null || classEndTime == null) return true;
        return classEndTime.isAfter(classStartTime);
    }
}
