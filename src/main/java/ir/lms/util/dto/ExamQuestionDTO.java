package ir.lms.util.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExamQuestionDTO {
    private Long examId;
    private Long questionId;
    private double score;
}
