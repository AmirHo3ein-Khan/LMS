package ir.lms.dto.exam;

import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExamDTO {
    private String title;
    private String description;
    private Instant examStartTime;
    private Instant examEndTime;
    private Long courseId;

}
