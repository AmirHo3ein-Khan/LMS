package ir.lms.util.dto;

import lombok.*;

import java.time.Instant;

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
