package ir.lms.util.dto;

import lombok.*;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDTO {
    private String questionType;
    private String title;
    private String questionText;
    private Double defaultScore;
    private String courseName;
    private String majorName;
    private List<OptionDTO> options;
}
