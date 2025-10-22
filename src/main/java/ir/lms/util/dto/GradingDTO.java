package ir.lms.util.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GradingDTO {
    private Long examId;
    private Long studentId;
    private Long questionId;
    private Double score;
}
