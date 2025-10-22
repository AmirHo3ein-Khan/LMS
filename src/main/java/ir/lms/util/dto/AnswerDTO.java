package ir.lms.util.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnswerDTO {
    @NotBlank(message = "Question type is required (e.g., TEXT, MULTIPLE_CHOICE)")
    private String type;

    @NotNull(message = "Exam ID is required")
    @Min(value = 1, message = "Exam ID must be a positive number")
    private Long examId;

    @NotNull(message = "Question ID is required")
    @Min(value = 1, message = "Question ID must be a positive number")
    private Long questionId;

    private Long optionId;

    private String answerText;


    @AssertTrue(message = "For descriptive questions, answerText must be provided")
    public boolean isTextAnswerValid() {
        if (type == null) return true;
        if (type.equalsIgnoreCase("descriptive")) {
            return answerText != null && !answerText.isBlank();
        }
        return true;
    }

    @AssertTrue(message = "For test questions, optionId must be provided")
    public boolean isMultipleChoiceValid() {
        if (type == null) return true;
        if (type.equalsIgnoreCase("test")) {
            return optionId != null && optionId > 0;
        }
        return true;
    }


    @AssertTrue(message = "Answer cannot have both text and option selected")
    public boolean isExclusiveAnswerValid() {
        if (answerText != null && !answerText.isBlank() && optionId != null) {
            return false;
        }
        return true;
    }
}

