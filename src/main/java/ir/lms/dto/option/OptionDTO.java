package ir.lms.dto.option;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OptionDTO {
    private String optionText;
    private boolean correct;
}
