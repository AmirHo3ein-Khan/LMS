package ir.lms.util.dto;

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
