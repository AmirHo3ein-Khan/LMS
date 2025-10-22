package ir.lms.util.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Instant;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExamDTO {
    @NotBlank(message = "Exam title cannot be empty")
    @Size(min = 3, max = 50, message = "Title must be between 3 and 50 characters")
    private String title;

    @Size(max = 200, message = "Description should not exceed 200 characters")
    private String description;

    @NotNull(message = "Exam start time is required")
    private Instant examStartTime;

    @NotNull(message = "Exam end time is required")
    private Instant examEndTime;

    @NotNull(message = "Course ID is required")
    @Min(value = 1, message = "Course ID must be positive")
    private Long courseId;

    @AssertTrue(message = "Exam end time must be after exam start time")
    public boolean isEndTimeAfterStartTime() {
        if (examStartTime == null || examEndTime == null) return true;
        return examEndTime.isAfter(examStartTime);
    }
}
