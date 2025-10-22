package ir.lms.util.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExamQuestionDTO {
    @NotNull(message = "Exam ID is required")
    @Min(value = 1, message = "Exam ID must be positive")
    private Long examId;

    @NotNull(message = "Question ID is required")
    @Min(value = 1, message = "Question ID must be positive")
    private Long questionId;

    @DecimalMin(value = "0.0", inclusive = true, message = "Score must be zero or positive")
    private double score;
}
