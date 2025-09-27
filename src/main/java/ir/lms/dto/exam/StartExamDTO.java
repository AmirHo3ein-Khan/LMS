package ir.lms.dto.exam;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StartExamDTO {
    private Long studentId;
    private Long examId;
}
