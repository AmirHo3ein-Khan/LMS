package ir.lms.dto.exam;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExamDTO {
    private String title;
    private String description;
    private Integer examTime;
    private LocalDateTime examStartTime;
    private LocalDateTime examEndTime;

}
