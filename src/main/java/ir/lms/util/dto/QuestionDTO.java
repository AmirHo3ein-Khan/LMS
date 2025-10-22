package ir.lms.util.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDTO {
    @NotBlank(message = "Question type is required")
    private String questionType;

    @NotBlank(message = "Title cannot be empty")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @NotBlank(message = "Question text cannot be empty")
    @Size(max = 500, message = "Question text should not exceed 500 characters")
    private String questionText;

    @NotNull(message = "Default score is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Score cannot be negative")
    private Double defaultScore;

    @NotBlank(message = "Course name is required")
    private String courseName;

    @NotBlank(message = "Major name is required")
    private String majorName;

    private List<OptionDTO> options;


    @AssertTrue(message = "Test questions must have at least one correct option")
    public boolean isOptionsValid() {
        if (!"test".equalsIgnoreCase(questionType)) {
            return true;
        }
        if (options == null || options.isEmpty()) {
            return false;
        }
        return options.stream().anyMatch(OptionDTO::isCorrect);
    }
}
