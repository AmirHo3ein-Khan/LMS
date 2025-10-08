package ir.lms.util.dto;

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
