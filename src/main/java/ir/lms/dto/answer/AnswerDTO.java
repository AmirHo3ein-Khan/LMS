package ir.lms.dto.answer;

import ir.lms.dto.option.OptionDTO;
import ir.lms.model.Answer;
import ir.lms.model.Option;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnswerDTO {
    private String type;
    private Long examId;
    private Long questionId;
    private Long optionId;
    private String answerText;
}

